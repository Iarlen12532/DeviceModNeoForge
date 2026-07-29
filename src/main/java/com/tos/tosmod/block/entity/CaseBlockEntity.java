package com.tos.tosmod.block.entity;

import com.tos.tosmod.block.CaseBlock;
import com.tos.tosmod.component.CaseDefinition;
import com.tos.tosmod.component.ComponentCategory;
import com.tos.tosmod.component.ComponentStats;
import com.tos.tosmod.component.CrashCause;
import com.tos.tosmod.component.PowerState;
import com.tos.tosmod.component.SlotType;
import com.tos.tosmod.computer.LuaComputer;
import com.tos.tosmod.computer.MainThreadBridge;
import com.tos.tosmod.computer.NetworkApi;
import com.tos.tosmod.computer.ClusterApi;
import com.tos.tosmod.computer.FsApi;
import com.tos.tosmod.computer.PrinterApi;
import com.tos.tosmod.computer.RedstoneApi;
import com.tos.tosmod.computer.UsbApi;
import com.tos.tosmod.component.NetworkUtils;
import com.tos.tosmod.registry.ModBlockEntities;
import com.tos.tosmod.registry.ModDataComponents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.ItemStackHandler;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Set;

/**
 * BlockEntity genérica pra QUALQUER modelo visual de Case (notebook gamer, notebook fino,
 * torre, all-in-one, servidor). O que muda entre elas é só a CaseDefinition passada no
 * construtor do CaseBlock - essa classe nunca precisa ser reescrita pra um novo visual.
 *
 * Fase 1: guarda os componentes inseridos e calcula se a máquina liga ou não.
 * Fase 2: cálculo de temperatura em tempo real (calor dos componentes vs. refrigeração da case),
 * com throttling visual (aviso) e crash por superaquecimento, no mesmo estilo do crash de PSU.
 * Fase 3: terminal Lua cru (LuaComputer) - só roda enquanto a máquina está ligada; some junto
 * com o resto do estado quando ela desliga/crasha (igual RAM de um PC real).
 * Fase 4: histórico do terminal sincronizado com o cliente (getUpdatePacket/getUpdateTag),
 * usado pela TerminalScreen pra mostrar a saída do Lua e mandar comandos de volta.
 * Fase 5: teclado/mouse com dependência cruzada (hasKeyboard()/hasMouse()) - notebooks já
 * vêm com os dois embutidos (portable()); cases fixas precisam dos itens instalados.
 * Fase 6: instalação do TOS via pendrive (instantâneo) ou por download pela rede (velocidade
 * calculada a partir do roteador mais forte ao alcance - ver findBestRouterLink/NetworkUtils),
 * mais a API Lua "network" (status()/installTOS()) exposta via MainThreadBridge.
 * Fase 7: cabo de rede (ignora distância, sem senha), senha de rede sem fio, PC-a-PC
 * (sendOsTo), impressora (só por cabo) e pen drive (slot USB próprio, usb.read()/write()).
 * Fase 8: monitor externo pra cases sem tela integrada (torre/servidor - ver
 * CaseDefinition.hasIntegratedScreen), e antena de redstone controlada via WIFI
 * (redstone.read()/send()/pulse() - reachableWirelessly() usa o mesmo alcance de roteador
 * de tudo mais, mas sem exigir cabo, diferente da impressora).
 * Fase 9: sistema de arquivos virtual (fs.save/load/list/delete - espaço derivado da
 * capacidade de storage instalada) e a DesktopScreen (dock com os apps salvos, por cima
 * do terminal cru - ver client/screen/TosScreens, que decide qual tela abrir).
 */
public class CaseBlockEntity extends BlockEntity {

    private final CaseDefinition definition;
    private final ItemStackHandler inventory;
    /** Índice do slot -> tipo de slot, calculado uma vez a partir da CaseDefinition. */
    private final List<SlotType> slotLayout;

    private PowerState powerState = PowerState.NO_CPU;
    private CrashCause lastCrashCause = CrashCause.NONE;

    /** Quantos ticks faltam pra travar quando a PSU não aguenta o consumo total, ou quando está superaquecendo. */
    private int crashTicksRemaining = 0;

    // 30 segundos até crashar por PSU insuficiente.
    private static final int UNSTABLE_DURATION_TICKS = 30 * 20;
    // 15 segundos até crashar depois de bater a temperatura crítica - mais rápido que a PSU
    // porque superaquecimento é mais "urgente" (dano de verdade ao hardware).
    private static final int OVERHEAT_DURATION_TICKS = 15 * 20;

    // --- Temperatura ---
    private static final int AMBIENT_TEMPERATURE = 20;   // temperatura de "repouso", tipo ambiente
    private static final int WARNING_TEMPERATURE = 70;   // a partir daqui, é só aviso (throttling visual futuro)
    private static final int CRITICAL_TEMPERATURE = 100; // a partir daqui, começa a contagem de crash
    private static final int MAX_TEMPERATURE = 120;       // teto - não sobe infinitamente
    private static final int HEAT_RISE_DIVISOR = 4;       // quanto maior, mais devagar esquenta
    private static final int COOL_RATE_ON = 1;            // quanto esfria por tick quando ligado e sem excesso de calor
    private static final int COOL_RATE_OFF = 2;           // quanto esfria por tick quando desligado (mais rápido)

    private int temperature = AMBIENT_TEMPERATURE;

    /** Terminal Lua cru (Fase 3) - só roda enquanto powerState.isOn(). */
    private final LuaComputer luaComputer = new LuaComputer();

    /** Histórico de linhas do terminal (Fase 4) - sincronizado pro cliente pra tela mostrar. */
    private final Deque<String> terminalHistory = new ArrayDeque<>();
    private static final int MAX_HISTORY_LINES = 200;
    private boolean wasOnLastTick = false;

    // --- Fase 6: rede e instalação do TOS ---
    private boolean osInstalled = false;
    private String installedOsName = "";

    private final MainThreadBridge networkBridge = new MainThreadBridge();
    private int downloadTotalKb = 0;
    private int downloadRemainingKb = 0;
    /** Posição do roteador usado no download em andamento - re-checado a cada tick
     *  (se o roteador sumir ou a CPU dele for removida, o download falha). */
    private BlockPos downloadRouterPos = null;
    private boolean downloadViaCable = false;

    /** Roteador ligado por CABO (Fase 7) - null = sem cabo, só sem fio. Cabo não precisa de senha. */
    private BlockPos cableLinkedRouterPos = null;
    /** Senha que este computador tenta usar pra entrar em roteadores sem fio com senha. */
    private String networkPassword = "";

    /** Slot de pen drive (Fase 7) - separado dos slots de hardware, toda case tem 1. */
    private final ItemStackHandler usbSlot = new ItemStackHandler(1) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }

        @Override
        public int getSlotLimit(int slot) {
            return 1;
        }
    };

    /**
     * Sistema de arquivos virtual (Fase 9) - nome do "app" -> código Lua. O espaço
     * disponível é derivado da capacidade de armazenamento instalado (ver
     * getStorageBudgetChars()) - quanto mais forte o HD/SSD/NVMe, mais apps cabem.
     */
    private final java.util.LinkedHashMap<String, String> virtualFiles = new java.util.LinkedHashMap<>();
    private static final int MAX_FILE_NAME_LENGTH = 32;
    private static final int MAX_FILES = 64;

    /** Monitor industrial linkado (Fase 10) - se true, dá teclado+mouse mesmo sem os itens. */
    private boolean hasIndustrialMonitor = false;

    /** Servidores ligados diretamente a este (Fase 10) - o cluster é o componente conexo
     *  desse grafo, calculado sob demanda em ClusterApi.status() via busca em largura. */
    private final java.util.Set<BlockPos> clusterLinks = new java.util.HashSet<>();
    private boolean resourceManagerActive = false;

    public CaseBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.CASE_BLOCK_ENTITY.get(), pos, state);
        // A definição de slots vem do próprio bloco - cada modelo visual (CaseBlock) aponta
        // pra uma CaseDefinition; essa classe não precisa saber qual é até este momento.
        this.definition = ((CaseBlock) state.getBlock()).getDefinition();
        this.slotLayout = buildSlotLayout(definition);
        this.inventory = new ItemStackHandler(slotLayout.size()) {
            @Override
            protected void onContentsChanged(int slot) {
                setChanged();
                recalculatePowerState();
            }

            @Override
            public int getSlotLimit(int slot) {
                return 1; // cada slot físico só aceita 1 peça (1 CPU, 1 GPU, etc.)
            }
        };
    }

    private static List<SlotType> buildSlotLayout(CaseDefinition definition) {
        List<SlotType> layout = new ArrayList<>();
        for (SlotType type : SlotType.values()) {
            int count = definition.slotCount(type);
            for (int i = 0; i < count; i++) {
                layout.add(type);
            }
        }
        return layout;
    }

    public ItemStackHandler getInventory() {
        return inventory;
    }

    /** Layout dos slots (índice -> tipo) - usado pela HardwareMenu pra rotular e restringir cada slot. */
    public List<SlotType> getSlotLayout() {
        return slotLayout;
    }

    public CaseDefinition getDefinition() {
        return definition;
    }

    public PowerState getPowerState() {
        return powerState;
    }

    public int getTemperature() {
        return temperature;
    }

    public CrashCause getLastCrashCause() {
        return lastCrashCause;
    }

    public static int getWarningTemperature() {
        return WARNING_TEMPERATURE;
    }

    public static int getCriticalTemperature() {
        return CRITICAL_TEMPERATURE;
    }

    /**
     * A case tem teclado funcionando? Notebooks já vêm com teclado embutido (portable() =
     * true); cases fixas (torre, all-in-one) precisam de um item KEYBOARD instalado no
     * KEYBOARD_SLOT. Isso NÃO afeta se a máquina liga (PowerState) - só se dá pra digitar
     * nela (checado pela TerminalScreen).
     */
    public boolean hasKeyboard() {
        if (definition.portable() || hasIndustrialMonitor) {
            return true;
        }
        return hasInstalledComponent(ComponentCategory.KEYBOARD);
    }

    /** Mesma lógica do hasKeyboard(), mas pro mouse (trackpad embutido em notebooks). */
    public boolean hasMouse() {
        if (definition.portable() || hasIndustrialMonitor) {
            return true;
        }
        return hasInstalledComponent(ComponentCategory.MOUSE);
    }

    public void setIndustrialMonitorLinked(boolean linked) {
        this.hasIndustrialMonitor = linked;
        setChanged();
    }

    private boolean hasInstalledComponent(ComponentCategory category) {
        for (int i = 0; i < inventory.getSlots(); i++) {
            ComponentStats stats = inventory.getStackInSlot(i).get(ModDataComponents.COMPONENT_STATS);
            if (stats != null && stats.category() == category) {
                return true;
            }
        }
        return false;
    }

    /**
     * Reavalia se a case consegue ligar, olhando os componentes inseridos.
     * Chamado toda vez que o inventário muda.
     *
     * Importante: se a máquina já CRASHOU, ela fica travada nesse estado até um restart manual
     * (tryRestart()) - simplesmente trocar um componente não "conserta" sozinho, igual na vida real
     * você precisa desligar e ligar de novo.
     */
    public void recalculatePowerState() {
        if (powerState == PowerState.CRASHED) {
            return;
        }

        List<ComponentStats> installed = new ArrayList<>();
        for (int i = 0; i < inventory.getSlots(); i++) {
            ComponentStats stats = inventory.getStackInSlot(i).get(ModDataComponents.COMPONENT_STATS);
            if (stats != null) {
                installed.add(stats);
            }
        }

        boolean hasCpuOrApu = installed.stream()
                .anyMatch(s -> s.category() == ComponentCategory.CPU || s.category() == ComponentCategory.APU);
        if (!hasCpuOrApu) {
            powerState = PowerState.NO_CPU;
            crashTicksRemaining = 0;
            return;
        }

        boolean hasRam = installed.stream().anyMatch(s -> s.category() == ComponentCategory.RAM);
        if (!hasRam) {
            powerState = PowerState.NO_RAM;
            crashTicksRemaining = 0;
            return;
        }

        boolean hasStorage = installed.stream().anyMatch(s -> s.category() == ComponentCategory.STORAGE);
        if (!hasStorage) {
            powerState = PowerState.NO_STORAGE;
            crashTicksRemaining = 0;
            return;
        }

        if (definition.portable()) {
            boolean hasBattery = installed.stream().anyMatch(s -> s.category() == ComponentCategory.BATTERY);
            if (!hasBattery) {
                powerState = PowerState.NO_BATTERY;
                crashTicksRemaining = 0;
                return;
            }
        } else {
            ComponentStats psu = installed.stream()
                    .filter(s -> s.category() == ComponentCategory.PSU)
                    .findFirst().orElse(null);
            if (psu == null) {
                powerState = PowerState.NO_PSU;
                crashTicksRemaining = 0;
                return;
            }

            int totalDraw = installed.stream()
                    .filter(s -> s.category() != ComponentCategory.PSU) // PSU não consome, ela fornece
                    .mapToInt(ComponentStats::wattDraw)
                    .sum();

            if (psu.wattSupply() < totalDraw) {
                // Liga mesmo assim - mas entra em contagem regressiva pro crash.
                // Se já estava instável, não reinicia a contagem (senão trocar peça sem
                // resolver o problema recomeçaria o timer de graça).
                if (powerState != PowerState.UNSTABLE_PSU) {
                    crashTicksRemaining = UNSTABLE_DURATION_TICKS;
                }
                powerState = PowerState.UNSTABLE_PSU;
                setChanged();
                return;
            }
        }

        // Se estava superaquecendo, esse estado é controlado pela temperatura (em tick()),
        // não por essa checagem de hardware - só o calor esfriando é que tira dele.
        if (powerState != PowerState.OVERHEATING) {
            powerState = PowerState.ON;
            crashTicksRemaining = 0;
        }
    }

    /**
     * Chamado a cada tick do mundo (via BlockEntityTicker). Atualiza a temperatura sempre
     * (mesmo desligado, ela esfria), e conta regressiva até o crash quando a PSU está
     * insuficiente OU quando a temperatura está crítica.
     */
    public void tick() {
        boolean isOnNow = powerState.isOn();

        // Liga/desliga o computador virtual conforme o estado de energia muda - inclui o
        // caso de carregar o chunk com a máquina já ligada (o Lua reinicia do zero, igual
        // um computador real perde o estado de RAM quando desliga/liga de novo).
        if (isOnNow && !luaComputer.isRunning()) {
            luaComputer.start(networkBridge, networkApi, printerApi, usbApi, redstoneApi, fsApi, clusterApi);
            addTerminalLine("--- TOS terminal cru (Fase 4) ---");
            addTerminalLine("Lua pronto. Digite um comando e aperte enter.");
            if (osInstalled) {
                addTerminalLine(installedOsName + " instalado neste disco.");
            }
        } else if (!isOnNow && luaComputer.isRunning()) {
            luaComputer.stop();
        }

        if (!isOnNow && wasOnLastTick) {
            addTerminalLine("--- máquina desligada" + (powerState == PowerState.CRASHED
                    ? " (travou: " + lastCrashCause.getDescription() + ")" : "") + " ---");
        }
        wasOnLastTick = isOnNow;

        boolean hasNewOutput = false;
        for (String line : luaComputer.pollOutput()) {
            addTerminalLine(line);
            hasNewOutput = true;
        }

        updateTemperature();

        networkBridge.processPendingTasks();
        progressDownload();

        if (powerState == PowerState.UNSTABLE_PSU) {
            crashTicksRemaining--;
            if (crashTicksRemaining <= 0) {
                crash(CrashCause.PSU_OVERLOAD);
            }
        } else if (powerState == PowerState.OVERHEATING) {
            crashTicksRemaining--;
            if (crashTicksRemaining <= 0) {
                crash(CrashCause.OVERHEAT);
            }
        }

        // Sincroniza pro cliente só quando algo relevante mudou de verdade, e no máximo a
        // cada 10 ticks - não faz sentido (nem é bom pra rede) mandar isso todo tick.
        if (hasNewOutput || getGameTimeSafe() % 10 == 0) {
            if (level != null && !level.isClientSide()) {
                setChanged();
                level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
            }
        }
    }

    private long getGameTimeSafe() {
        return level != null ? level.getGameTime() : 0L;
    }

    private void addTerminalLine(String line) {
        terminalHistory.addLast(line);
        while (terminalHistory.size() > MAX_HISTORY_LINES) {
            terminalHistory.removeFirst();
        }
    }

    /** Cópia imutável do histórico atual do terminal - usado pela tela (client-side). */
    public List<String> getTerminalHistory() {
        return new ArrayList<>(terminalHistory);
    }

    public boolean isOsInstalled() {
        return osInstalled;
    }

    public String getInstalledOsName() {
        return installedOsName;
    }

    /**
     * Quantos caracteres de "arquivo" cabem no total, derivado da capacidade de storage
     * instalada (cada unidade de capacity = 1000 caracteres, número arbitrário mas
     * consistente com "quanto mais forte o storage, mais espaço pros apps").
     */
    private int getStorageBudgetChars() {
        int totalCapacity = 0;
        for (int i = 0; i < inventory.getSlots(); i++) {
            ComponentStats stats = inventory.getStackInSlot(i).get(ModDataComponents.COMPONENT_STATS);
            if (stats != null && stats.category() == ComponentCategory.STORAGE) {
                totalCapacity += stats.capacity();
            }
        }
        return totalCapacity * 1000;
    }

    public java.util.Map<String, String> getVirtualFiles() {
        return java.util.Collections.unmodifiableMap(virtualFiles);
    }

    private final FsApi fsApi = new FsApi() {
        @Override
        public String save(String name, String content) {
            if (name == null || name.isBlank() || name.length() > MAX_FILE_NAME_LENGTH) {
                return "erro: nome invalido (max " + MAX_FILE_NAME_LENGTH + " caracteres).";
            }
            if (!virtualFiles.containsKey(name) && virtualFiles.size() >= MAX_FILES) {
                return "erro: limite de " + MAX_FILES + " arquivos atingido.";
            }
            int currentUsage = virtualFiles.entrySet().stream()
                    .filter(e -> !e.getKey().equals(name))
                    .mapToInt(e -> e.getValue().length())
                    .sum();
            int budget = getStorageBudgetChars();
            if (currentUsage + content.length() > budget) {
                return "erro: disco cheio (" + currentUsage + "/" + budget + " caracteres).";
            }
            virtualFiles.put(name, content);
            setChanged();
            if (level != null) {
                level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
            }
            return "salvo (" + content.length() + " caracteres).";
        }

        @Override
        public String load(String name) {
            String content = virtualFiles.get(name);
            return content != null ? content : "erro: arquivo nao encontrado.";
        }

        @Override
        public String list() {
            return virtualFiles.isEmpty() ? "" : String.join(",", virtualFiles.keySet());
        }

        @Override
        public String delete(String name) {
            boolean removed = virtualFiles.remove(name) != null;
            if (removed) {
                setChanged();
                if (level != null) {
                    level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
                }
            }
            return removed ? "apagado." : "erro: arquivo nao encontrado.";
        }
    };

    /** Liga este servidor a outro (bidirecional - quem chama também liga o outro lado). */
    public void addClusterLink(BlockPos other) {
        clusterLinks.add(other);
        setChanged();
    }

    /**
     * Implementação da API Lua "cluster" (Fase 10) - soma o poder computacional de TODOS
     * os servidores conectados (busca em largura pelo grafo de clusterLinks), mas só conta
     * servidores que estão LIGADOS (um servidor desligado não contribui pro cluster).
     */
    private final ClusterApi clusterApi = new ClusterApi() {
        @Override
        public String status() {
            if (!"server_rack".equals(definition.id())) {
                return "erro: cluster so funciona a partir de um servidor.";
            }
            ClusterTotals totals = computeClusterTotals();
            return String.format(
                    "servidores: %d | cpu/apu: %d | gpu: %d | ram: %d | storage: %d | gerenciador: %s",
                    totals.serverCount(), totals.cpuPower(), totals.gpuPower(), totals.ramCapacity(),
                    totals.storageCapacity(), resourceManagerActive ? "ativo" : "inativo");
        }

        @Override
        public String setResourceManagerActive(boolean active) {
            resourceManagerActive = active;
            setChanged();
            return "gerenciador de recursos " + (active ? "ativado." : "desativado.")
                    + " (unifica os servidores conectados como um so, pra quem usar cluster.status())";
        }
    };

    private record ClusterTotals(int serverCount, int cpuPower, int gpuPower, int ramCapacity, int storageCapacity) {}

    private ClusterTotals computeClusterTotals() {
        if (level == null) {
            return new ClusterTotals(0, 0, 0, 0, 0);
        }
        java.util.Set<BlockPos> visited = new java.util.HashSet<>();
        java.util.Deque<BlockPos> queue = new java.util.ArrayDeque<>();
        queue.add(getBlockPos());
        visited.add(getBlockPos());

        int serverCount = 0, cpuPower = 0, gpuPower = 0, ramCapacity = 0, storageCapacity = 0;
        while (!queue.isEmpty()) {
            BlockPos current = queue.poll();
            if (!(level.getBlockEntity(current) instanceof CaseBlockEntity server) || !server.getPowerState().isOn()) {
                continue; // servidor desligado (ou chunk descarregado) não soma pro cluster
            }
            serverCount++;
            for (int i = 0; i < server.inventory.getSlots(); i++) {
                ComponentStats stats = server.inventory.getStackInSlot(i).get(ModDataComponents.COMPONENT_STATS);
                if (stats == null) continue;
                switch (stats.category()) {
                    case CPU, APU -> cpuPower += stats.performance();
                    case GPU -> gpuPower += stats.performance();
                    case RAM -> ramCapacity += stats.capacity();
                    case STORAGE -> storageCapacity += stats.capacity();
                    default -> {}
                }
            }
            for (BlockPos next : server.clusterLinks) {
                if (visited.add(next)) {
                    queue.add(next);
                }
            }
        }
        return new ClusterTotals(serverCount, cpuPower, gpuPower, ramCapacity, storageCapacity);
    }

    public void setCableLinkedRouterPos(BlockPos pos) {
        this.cableLinkedRouterPos = pos;
        setChanged();
        addTerminalLine("Cabo de rede conectado.");
    }

    /** Insere um pen drive no slot USB, se estiver vazio. Retorna false se já tem um. */
    public boolean insertUsbDrive(net.minecraft.world.item.ItemStack stack) {
        if (!usbSlot.getStackInSlot(0).isEmpty()) {
            return false;
        }
        usbSlot.setStackInSlot(0, stack);
        return true;
    }

    /** Retira o pen drive do slot USB (ou ItemStack.EMPTY se não tinha nenhum). */
    public net.minecraft.world.item.ItemStack removeUsbDrive() {
        net.minecraft.world.item.ItemStack current = usbSlot.getStackInSlot(0);
        usbSlot.setStackInSlot(0, net.minecraft.world.item.ItemStack.EMPTY);
        return current;
    }

    private final UsbApi usbApi = new UsbApi() {
        @Override
        public String read() {
            net.minecraft.world.item.ItemStack drive = usbSlot.getStackInSlot(0);
            if (drive.isEmpty()) {
                return "erro: nenhum pen drive inserido.";
            }
            String content = drive.get(ModDataComponents.USB_CONTENT);
            return content != null ? content : "";
        }

        @Override
        public String write(String text) {
            net.minecraft.world.item.ItemStack drive = usbSlot.getStackInSlot(0);
            if (drive.isEmpty()) {
                return "erro: nenhum pen drive inserido.";
            }
            drive.set(ModDataComponents.USB_CONTENT, text);
            setChanged();
            return "gravado (" + text.length() + " caracteres).";
        }
    };

    /**
     * Implementação da API Lua "printer" (Fase 7) - a impressora só é encontrada se estiver
     * ligada por CABO num roteador que este computador também alcança (cabo ou sem fio).
     * Impressora não tem WiFi de propósito.
     */
    private final PrinterApi printerApi = new PrinterApi() {
        @Override
        public String print(int x, int y, int z, String text) {
            if (level == null) {
                return "erro: sem nivel.";
            }
            BlockPos printerPos = new BlockPos(x, y, z);
            if (!(level.getBlockEntity(printerPos) instanceof com.tos.tosmod.block.entity.PrinterBlockEntity printer)) {
                return "erro: nao ha impressora nessa posicao.";
            }
            BlockPos printerRouter = printer.getCableLinkedRouterPos();
            if (printerRouter == null) {
                return "erro: impressora sem cabo conectado (ela nao tem wifi).";
            }
            RouterLink link = findBestRouterLink();
            if (link == null || !link.pos.equals(printerRouter)) {
                return "erro: este computador nao alcanca o roteador da impressora.";
            }
            if (text.length() > 2000) {
                return "erro: texto grande demais (max 2000 caracteres).";
            }
            printer.printText(text);
            return "impresso.";
        }
    };

    /**
     * Implementação da API Lua "redstone" (Fase 8) - a antena conecta por WIFI (diferente
     * da impressora, que exige cabo): basta estar dentro do alcance sem fio de QUALQUER
     * roteador que este computador também alcança (não precisa de cabo até a antena).
     */
    private final RedstoneApi redstoneApi = new RedstoneApi() {
        @Override
        public String read(int x, int y, int z) {
            BlockPos targetPos = new BlockPos(x, y, z);
            if (!reachableWirelessly(targetPos)) {
                return "erro: fora de alcance da rede sem fio.";
            }
            if (level.getBlockEntity(targetPos) instanceof com.tos.tosmod.block.entity.RedstoneLinkBlockEntity link) {
                return String.valueOf(link.getIncomingSignal());
            }
            return "erro: nao ha antena de redstone nessa posicao.";
        }

        @Override
        public String send(int x, int y, int z, int strength) {
            BlockPos targetPos = new BlockPos(x, y, z);
            if (!reachableWirelessly(targetPos)) {
                return "erro: fora de alcance da rede sem fio.";
            }
            if (level.getBlockEntity(targetPos) instanceof com.tos.tosmod.block.entity.RedstoneLinkBlockEntity link) {
                link.setOutputStrength(strength);
                return "sinal definido: " + Math.max(0, Math.min(15, strength));
            }
            return "erro: nao ha antena de redstone nessa posicao.";
        }

        @Override
        public String pulse(int x, int y, int z, int strength, int ticks) {
            BlockPos targetPos = new BlockPos(x, y, z);
            if (!reachableWirelessly(targetPos)) {
                return "erro: fora de alcance da rede sem fio.";
            }
            if (level.getBlockEntity(targetPos) instanceof com.tos.tosmod.block.entity.RedstoneLinkBlockEntity link) {
                int cappedTicks = Math.max(1, Math.min(200, ticks)); // teto de 10s por pulso
                link.pulse(strength, cappedTicks);
                return "pulso enviado.";
            }
            return "erro: nao ha antena de redstone nessa posicao.";
        }
    };

    /**
     * Confere se uma posição está dentro do alcance sem fio do MESMO roteador que este
     * computador alcança (usado pela antena de redstone - ela não precisa de cabo, só
     * precisa estar na área de sinal do roteador, igual qualquer dispositivo wifi).
     */
    private boolean reachableWirelessly(BlockPos targetPos) {
        if (level == null) {
            return false;
        }
        RouterLink link = findBestRouterLink();
        if (link == null) {
            return false;
        }
        double targetDistance = Math.sqrt(link.pos.distSqr(targetPos));
        return NetworkUtils.speedKbPerTick(link.power, targetDistance, false) > 0;
    }

    /**
     * Instalação instantânea via pendrive (Fase 6) - chamado pelo TosInstallerUsbItem.
     * Não usa a rede (pendrive físico = rápido, sem depender de roteador nenhum).
     * Exige a máquina ligada (igual instalar um SO de verdade, precisa estar rodando)
     * e pelo menos um armazenamento instalado (já garantido pelo PowerState, mas
     * confere de novo aqui por clareza).
     */
    public boolean installFromUsb() {
        if (!powerState.isOn()) {
            return false;
        }
        osInstalled = true;
        installedOsName = "TOS";
        addTerminalLine("TOS instalado via pendrive.");
        setChanged();
        return true;
    }

    /**
     * Implementação da API Lua "network" (ver LuaComputer/NetworkApi) - roda sempre na
     * main thread, chamada através do networkBridge a partir da thread do Lua.
     */
    private final NetworkApi networkApi = new NetworkApi() {
        @Override
        public String status() {
            RouterLink link = findBestRouterLink();
            if (link == null) {
                return "sem roteador ao alcance (ou senha errada)";
            }
            String viaText = link.viaCable ? "cabo" : "sem fio";
            String base = String.format("roteador '%s' via %s a %.0f blocos, forca %d, %.2f KB/tick",
                    link.name, viaText, link.distance, link.power, link.speedKbPerTick);
            if (downloadRemainingKb > 0) {
                int percent = 100 - (int) (100.0 * downloadRemainingKb / Math.max(1, downloadTotalKb));
                return base + " | transferencia: " + percent + "%";
            }
            return base;
        }

        @Override
        public String installTos() {
            if (osInstalled) {
                return "TOS ja instalado.";
            }
            if (downloadRemainingKb > 0) {
                return "transferencia ja em andamento.";
            }
            RouterLink link = findBestRouterLink();
            if (link == null) {
                return "erro: nenhum roteador ao alcance (confira a senha com network.setPassword).";
            }
            downloadTotalKb = NetworkUtils.TOS_SYSTEM_SIZE_KB;
            downloadRemainingKb = NetworkUtils.TOS_SYSTEM_SIZE_KB;
            downloadRouterPos = link.pos;
            downloadViaCable = link.viaCable;
            addTerminalLine("Baixando TOS pela rede (" + NetworkUtils.TOS_SYSTEM_SIZE_KB + " KB)...");
            return "download iniciado.";
        }

        @Override
        public String setPassword(String password) {
            networkPassword = password == null ? "" : password;
            setChanged();
            return "senha da rede sem fio definida.";
        }

        @Override
        public String sendOsTo(int x, int y, int z) {
            return CaseBlockEntity.this.sendOsTo(new BlockPos(x, y, z));
        }
    };

    /**
     * PC-a-PC (Fase 7): este computador (que já tem o TOS) manda o sistema pra outro
     * computador na mesma rede. Usa EXATAMENTE a mesma velocidade que um download normal
     * teria - o "gargalo" é o lado mais lento entre os dois computadores até o roteador
     * compartilhado (cabo ou sem fio, cada um por conta própria).
     */
    private String sendOsTo(BlockPos targetPos) {
        if (!osInstalled) {
            return "erro: este computador nao tem TOS instalado pra compartilhar.";
        }
        if (level == null) {
            return "erro: sem nivel.";
        }
        if (!(level.getBlockEntity(targetPos) instanceof CaseBlockEntity target) || target == this) {
            return "erro: nao ha outro computador nessa posicao.";
        }
        if (target.osInstalled) {
            return "erro: computador de destino ja tem TOS.";
        }
        if (!target.getPowerState().isOn()) {
            return "erro: computador de destino esta desligado.";
        }

        RouterLink sourceLink = findBestRouterLink();
        if (sourceLink == null) {
            return "erro: este computador nao alcanca nenhum roteador.";
        }
        boolean targetViaCable = targetPos.equals(getBlockPos()) ? false
                : sourceLink.pos.equals(target.cableLinkedRouterPos);
        double targetDistance = Math.sqrt(sourceLink.pos.distSqr(targetPos));
        double targetSpeed = NetworkUtils.speedKbPerTick(sourceLink.power, targetDistance, targetViaCable);
        if (targetSpeed <= 0) {
            return "erro: computador de destino fora de alcance do mesmo roteador ('" + sourceLink.name + "').";
        }

        double transferSpeed = Math.min(sourceLink.speedKbPerTick, targetSpeed);
        target.downloadTotalKb = NetworkUtils.TOS_SYSTEM_SIZE_KB;
        target.downloadRemainingKb = NetworkUtils.TOS_SYSTEM_SIZE_KB;
        target.downloadRouterPos = sourceLink.pos;
        // Guarda a velocidade já calculada evitando recomputar do zero no destino
        // (ele reusa o mesmo progressDownload() de sempre, só que já sabe o "gargalo" certo).
        target.downloadViaCable = targetViaCable && transferSpeed == targetSpeed;
        target.addTerminalLine("Recebendo TOS de outro computador pela rede...");
        target.setChanged();
        return "transferencia iniciada pra " + targetPos.toShortString() + ".";
    }

    /** Resultado de procurar o melhor roteador ao alcance - usado por status()/installTos(). */
    private record RouterLink(BlockPos pos, String name, int power, double distance, double speedKbPerTick, boolean viaCable) {}

    /**
     * Procura primeiro o cabo (se tiver um conectado e o roteador ainda existir - cabo
     * sempre vence, é mais estável e ignora distância). Se não tiver cabo, procura entre
     * todos os roteadores sem fio ao alcance o que dá a MAIOR velocidade, respeitando a
     * senha configurada em network.setPassword() (cabo nunca precisa de senha).
     */
    private RouterLink findBestRouterLink() {
        if (level == null) {
            return null;
        }

        if (cableLinkedRouterPos != null) {
            if (level.getBlockEntity(cableLinkedRouterPos) instanceof RouterBlockEntity router && router.getPower() > 0) {
                double speed = NetworkUtils.speedKbPerTick(router.getPower(), 0, true);
                return new RouterLink(cableLinkedRouterPos, router.getRouterName(), router.getPower(), 0, speed, true);
            }
            // Cabo apontando pra um roteador que sumiu/ficou sem processador - cai pro sem fio.
        }

        Set<BlockPos> routers = RouterBlockEntity.getActiveRouters(level.dimension());
        RouterLink best = null;
        for (BlockPos routerPos : routers) {
            if (!(level.getBlockEntity(routerPos) instanceof RouterBlockEntity router)) {
                continue;
            }
            int power = router.getPower();
            if (power <= 0 || !router.checkWirelessPassword(networkPassword)) {
                continue;
            }
            double distance = Math.sqrt(routerPos.distSqr(getBlockPos()));
            double speed = NetworkUtils.speedKbPerTick(power, distance, false);
            if (speed <= 0) {
                continue; // fora de alcance desse roteador
            }
            if (best == null || speed > best.speedKbPerTick) {
                best = new RouterLink(routerPos, router.getRouterName(), power, distance, speed, false);
            }
        }
        return best;
    }

    /** Avança um download/transferência em andamento, se houver. Cancela sozinho se o roteador sumir. */
    private void progressDownload() {
        if (downloadRemainingKb <= 0 || downloadRouterPos == null) {
            return;
        }
        if (level == null || !(level.getBlockEntity(downloadRouterPos) instanceof RouterBlockEntity router) || router.getPower() <= 0) {
            addTerminalLine("Transferencia falhou: roteador desconectado.");
            downloadRemainingKb = 0;
            downloadTotalKb = 0;
            downloadRouterPos = null;
            return;
        }
        double distance = downloadViaCable ? 0 : Math.sqrt(downloadRouterPos.distSqr(getBlockPos()));
        double speed = NetworkUtils.speedKbPerTick(router.getPower(), distance, downloadViaCable);
        if (speed <= 0) {
            addTerminalLine("Transferencia pausada: fora de alcance do roteador.");
            return;
        }
        downloadRemainingKb -= speed;
        if (downloadRemainingKb <= 0) {
            downloadRemainingKb = 0;
            downloadRouterPos = null;
            osInstalled = true;
            installedOsName = "TOS";
            addTerminalLine("TOS instalado com sucesso pela rede!");
        }
    }

    /**
     * Sobe a temperatura quando o calor gerado excede a refrigeração da case, ou esfria
     * gradualmente quando não excede (ou quando a máquina está desligada). Entra e sai do
     * estado OVERHEATING sozinho, conforme a temperatura cruza o limite crítico.
     */
    private void updateTemperature() {
        if (!powerState.isOn()) {
            if (temperature > AMBIENT_TEMPERATURE) {
                temperature = Math.max(AMBIENT_TEMPERATURE, temperature - COOL_RATE_OFF);
            }
            return;
        }

        int netHeat = getTotalHeatOutput() - definition.baseCoolingCapacity();
        if (netHeat > 0) {
            temperature = Math.min(MAX_TEMPERATURE, temperature + Math.max(1, netHeat / HEAT_RISE_DIVISOR));
        } else if (temperature > AMBIENT_TEMPERATURE) {
            temperature = Math.max(AMBIENT_TEMPERATURE, temperature - COOL_RATE_ON);
        }

        if (temperature >= CRITICAL_TEMPERATURE && powerState == PowerState.ON) {
            powerState = PowerState.OVERHEATING;
            crashTicksRemaining = OVERHEAT_DURATION_TICKS;
            setChanged();
        } else if (temperature < CRITICAL_TEMPERATURE && powerState == PowerState.OVERHEATING) {
            // Esfriou a tempo - volta ao normal (a contagem de crash é cancelada).
            powerState = PowerState.ON;
            crashTicksRemaining = 0;
            setChanged();
        }
    }

    private void crash(CrashCause cause) {
        powerState = PowerState.CRASHED;
        lastCrashCause = cause;
        crashTicksRemaining = 0;
        luaComputer.stop(); // igual um PC real que trava e desliga: qualquer script rodando morre
        setChanged();
    }

    /**
     * Restart manual depois de um crash (ex: jogador clica num botão de força, ou
     * remove e recoloca uma peça de propósito). Sem isso, a máquina fica CRASHED pra sempre.
     */
    public void tryRestart() {
        if (powerState == PowerState.CRASHED) {
            powerState = PowerState.NO_CPU; // força reavaliação completa do zero
            recalculatePowerState();
            setChanged();
        }
    }

    /** Soma o calor de todos os componentes instalados - usado no cálculo de temperatura (updateTemperature()). */
    public int getTotalHeatOutput() {
        int total = 0;
        for (int i = 0; i < inventory.getSlots(); i++) {
            ComponentStats stats = inventory.getStackInSlot(i).get(ModDataComponents.COMPONENT_STATS);
            if (stats != null) {
                total += stats.heatOutput();
            }
        }
        return total;
    }

    /**
     * Executa um comando/script Lua no terminal cru deste computador. Só funciona se a
     * máquina estiver ligada E tiver teclado (notebook sempre tem embutido; case fixa
     * precisa de um item Keyboard instalado) - sem teclado, não tem como "digitar" de verdade,
     * então nem o servidor aceita o comando (checagem dupla, igual o resto do mod).
     */
    public void runLuaCommand(String code) {
        if (!hasKeyboard()) {
            return;
        }
        luaComputer.execute(code);
    }

    /** Pega a saída acumulada do terminal (print, erros) desde a última leitura - usado pela UI. */
    public java.util.List<String> pollLuaOutput() {
        return luaComputer.pollOutput();
    }

    public boolean isLuaRunning() {
        return luaComputer.isRunning();
    }

    /**
     * Chamado quando o bloco é quebrado ou o chunk descarrega - essencial pra não deixar
     * threads Lua "penduradas" rodando pra sempre em segundo plano.
     */
    @Override
    public void setRemoved() {
        super.setRemoved();
        luaComputer.stop();
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.put("inventory", inventory.serializeNBT(registries));
        tag.putString("power_state", powerState.name());
        tag.putInt("crash_ticks_remaining", crashTicksRemaining);
        tag.putInt("temperature", temperature);
        tag.putString("last_crash_cause", lastCrashCause.name());

        ListTag historyTag = new ListTag();
        for (String line : terminalHistory) {
            historyTag.add(StringTag.valueOf(line));
        }
        tag.put("terminal_history", historyTag);

        tag.putBoolean("os_installed", osInstalled);
        tag.putString("installed_os_name", installedOsName);
        tag.putInt("download_total_kb", downloadTotalKb);
        tag.putInt("download_remaining_kb", downloadRemainingKb);
        if (downloadRouterPos != null) {
            tag.putLong("download_router_pos", downloadRouterPos.asLong());
        }
        tag.putBoolean("download_via_cable", downloadViaCable);

        tag.put("usb_slot", usbSlot.serializeNBT(registries));
        if (cableLinkedRouterPos != null) {
            tag.putLong("cable_linked_router_pos", cableLinkedRouterPos.asLong());
        }
        tag.putString("network_password", networkPassword);

        ListTag filesTag = new ListTag();
        for (var entry : virtualFiles.entrySet()) {
            CompoundTag fileTag = new CompoundTag();
            fileTag.putString("name", entry.getKey());
            fileTag.putString("content", entry.getValue());
            filesTag.add(fileTag);
        }
        tag.put("virtual_files", filesTag);

        tag.putBoolean("has_industrial_monitor", hasIndustrialMonitor);
        tag.putBoolean("resource_manager_active", resourceManagerActive);
        long[] clusterArray = clusterLinks.stream().mapToLong(BlockPos::asLong).toArray();
        tag.putLongArray("cluster_links", clusterArray);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        if (tag.contains("inventory")) {
            inventory.deserializeNBT(registries, tag.getCompound("inventory"));
        }
        if (tag.contains("power_state")) {
            powerState = PowerState.valueOf(tag.getString("power_state"));
        }
        if (tag.contains("crash_ticks_remaining")) {
            crashTicksRemaining = tag.getInt("crash_ticks_remaining");
        }
        if (tag.contains("temperature")) {
            temperature = tag.getInt("temperature");
        }
        if (tag.contains("last_crash_cause")) {
            lastCrashCause = CrashCause.valueOf(tag.getString("last_crash_cause"));
        }
        if (tag.contains("terminal_history")) {
            terminalHistory.clear();
            ListTag historyTag = tag.getList("terminal_history", StringTag.TAG_STRING);
            for (int i = 0; i < historyTag.size(); i++) {
                terminalHistory.addLast(historyTag.getString(i));
            }
        }

        osInstalled = tag.getBoolean("os_installed");
        installedOsName = tag.getString("installed_os_name");
        downloadTotalKb = tag.getInt("download_total_kb");
        downloadRemainingKb = tag.getInt("download_remaining_kb");
        downloadRouterPos = tag.contains("download_router_pos") ? BlockPos.of(tag.getLong("download_router_pos")) : null;
        downloadViaCable = tag.getBoolean("download_via_cable");

        if (tag.contains("usb_slot")) {
            usbSlot.deserializeNBT(registries, tag.getCompound("usb_slot"));
        }
        cableLinkedRouterPos = tag.contains("cable_linked_router_pos") ? BlockPos.of(tag.getLong("cable_linked_router_pos")) : null;
        networkPassword = tag.getString("network_password");

        virtualFiles.clear();
        if (tag.contains("virtual_files")) {
            ListTag filesTag = tag.getList("virtual_files", net.minecraft.nbt.CompoundTag.TAG_COMPOUND);
            for (int i = 0; i < filesTag.size(); i++) {
                CompoundTag fileTag = filesTag.getCompound(i);
                virtualFiles.put(fileTag.getString("name"), fileTag.getString("content"));
            }
        }

        hasIndustrialMonitor = tag.getBoolean("has_industrial_monitor");
        resourceManagerActive = tag.getBoolean("resource_manager_active");
        clusterLinks.clear();
        if (tag.contains("cluster_links")) {
            for (long encoded : tag.getLongArray("cluster_links")) {
                clusterLinks.add(BlockPos.of(encoded));
            }
        }
    }

    /**
     * Habilita a sincronização automática servidor -> cliente (usada pela tela do terminal
     * pra mostrar o histórico e o estado sem precisar de um pacote próprio de leitura).
     * O conteúdo enviado é o mesmo de saveAdditional/loadAdditional acima.
     */
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return saveWithoutMetadata(registries);
    }
}
