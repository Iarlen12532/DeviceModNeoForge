package com.tos.tosmod.block.entity;

import com.tos.tosmod.component.ComponentStats;
import com.tos.tosmod.registry.ModBlockEntities;
import com.tos.tosmod.registry.ModDataComponents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.ItemStackHandler;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * BlockEntity do roteador. Só tem 1 slot (o processador de rede) - a força desse
 * processador define alcance, nº de dispositivos e velocidade pra todo mundo conectado
 * (ver component/NetworkUtils pras fórmulas).
 *
 * Fase 7: ganhou nome (pra identificar qual roteador é qual) e senha (protege a conexão
 * SEM FIO - conexão por cabo não precisa de senha, é física/direta).
 *
 * Mantém um registro estático leve (por dimensão) de todos os roteadores carregados,
 * pra um computador achar "o roteador mais próximo" em O(nº de roteadores) em vez de
 * varrer o mundo bloco a bloco - importante pra não pesar no seu ambiente Android.
 * Esse registro NÃO é salvo em disco - ele se reconstrói sozinho conforme os chunks
 * com roteador carregam de novo (cada roteador se registra no primeiro tick dele).
 */
public class RouterBlockEntity extends BlockEntity {

    private static final Map<ResourceKey<Level>, Set<BlockPos>> ACTIVE_ROUTERS = new HashMap<>();
    private static final int MAX_NAME_LENGTH = 32;
    private static final int MAX_PASSWORD_LENGTH = 32;

    private final ItemStackHandler inventory = new ItemStackHandler(1) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }

        @Override
        public int getSlotLimit(int slot) {
            return 1;
        }
    };

    private boolean registered = false;
    private String routerName = "Roteador";
    private String password = ""; // vazio = sem senha, qualquer computador sem fio pode usar

    public RouterBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.ROUTER_BLOCK_ENTITY.get(), pos, state);
    }

    public ItemStackHandler getInventory() {
        return inventory;
    }

    /** Força do roteador = performance do processador de rede instalado, ou 0 se vazio. */
    public int getPower() {
        ComponentStats stats = inventory.getStackInSlot(0).get(ModDataComponents.COMPONENT_STATS);
        return stats != null ? stats.performance() : 0;
    }

    public String getRouterName() {
        return routerName;
    }

    public String getPassword() {
        return password;
    }

    public void setRouterConfig(String name, String password) {
        this.routerName = name.isBlank() ? "Roteador" : name.substring(0, Math.min(name.length(), MAX_NAME_LENGTH));
        this.password = password.substring(0, Math.min(password.length(), MAX_PASSWORD_LENGTH));
        setChanged();
        if (level != null) {
            level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
        }
    }

    /** Cabo não usa senha nenhuma (conexão física/direta) - só a conexão sem fio verifica isso. */
    public boolean checkWirelessPassword(String attempt) {
        return password.isEmpty() || password.equals(attempt);
    }

    public void tick() {
        if (!registered && level != null) {
            registerSelf();
        }
    }

    private void registerSelf() {
        registered = true;
        ACTIVE_ROUTERS
                .computeIfAbsent(level.dimension(), key -> new HashSet<>())
                .add(getBlockPos());
    }

    private void unregisterSelf() {
        if (level == null) return;
        Set<BlockPos> routers = ACTIVE_ROUTERS.get(level.dimension());
        if (routers != null) {
            routers.remove(getBlockPos());
        }
    }

    @Override
    public void setRemoved() {
        super.setRemoved();
        unregisterSelf();
    }

    @Override
    public void onChunkUnloaded() {
        super.onChunkUnloaded();
        unregisterSelf();
        registered = false; // se o chunk recarregar, tick() se registra de novo
    }

    /** Todas as posições de roteador atualmente carregadas nessa dimensão. */
    public static Set<BlockPos> getActiveRouters(ResourceKey<Level> dimension) {
        return Collections.unmodifiableSet(ACTIVE_ROUTERS.getOrDefault(dimension, Collections.emptySet()));
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.put("inventory", inventory.serializeNBT(registries));
        tag.putString("router_name", routerName);
        tag.putString("password", password);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        if (tag.contains("inventory")) {
            inventory.deserializeNBT(registries, tag.getCompound("inventory"));
        }
        if (tag.contains("router_name")) {
            routerName = tag.getString("router_name");
        }
        if (tag.contains("password")) {
            password = tag.getString("password");
        }
    }

    // Sincroniza nome/senha/inventário pro cliente (usado pela RouterScreen).
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return saveWithoutMetadata(registries);
    }
}
