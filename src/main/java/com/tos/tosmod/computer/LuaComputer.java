package com.tos.tosmod.computer;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.ZeroArgFunction;
import org.luaj.vm2.lib.jse.JsePlatform;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Executa código Lua usando LuaJ - um interpretador Lua 100% em Java, sem JNI nem
 * biblioteca nativa nenhuma. Isso é importante especificamente pro seu ambiente
 * (PojavLauncher/Mojo + gl4es em ARM64 Android): não existe risco de crash nativo
 * tipo o que você teve com o Rapier do Sable, porque não tem lib .so nenhuma envolvida.
 *
 * Cada script roda numa THREAD PRÓPRIA (fora da main thread do Minecraft), então um
 * `while true do end` escrito pelo jogador trava só aquele computador virtual, nunca
 * o jogo inteiro. A comunicação de volta pro mundo (BlockEntity) é sempre por uma fila
 * thread-safe (ConcurrentLinkedQueue) - a thread do Lua nunca mexe direto no mundo.
 */
public class LuaComputer {

    // Pool compartilhado entre todos os computadores do mod - threads daemon, então não
    // impedem o processo de fechar, e são reaproveitadas em vez de criar uma nova a cada boot.
    private static final ExecutorService EXECUTOR = Executors.newCachedThreadPool(runnable -> {
        Thread thread = new Thread(runnable, "tosmod-lua-computer");
        thread.setDaemon(true);
        return thread;
    });

    private final ConcurrentLinkedQueue<String> outputBuffer = new ConcurrentLinkedQueue<>();
    private final AtomicBoolean running = new AtomicBoolean(false);
    private Globals globals;
    private volatile Future<?> currentTask;

    /** "Liga" o computador virtual: cria um novo ambiente Lua limpo. */
    public void start() {
        start(null, null, null, null, null, null, null);
    }

    /**
     * Mesmo que start(), mas já registra as tabelas globais "network", "printer", "usb",
     * "redstone", "fs" e "cluster" (Fase 6-10) - as funções chamam de volta a main thread
     * via MainThreadBridge, então são seguras mesmo rodando na thread própria do Lua.
     */
    public void start(MainThreadBridge bridge, NetworkApi networkApi, PrinterApi printerApi, UsbApi usbApi, RedstoneApi redstoneApi, FsApi fsApi, ClusterApi clusterApi) {
        if (running.get()) {
            return;
        }
        running.set(true);
        globals = JsePlatform.standardGlobals();

        // Bloqueia acesso ao sistema de arquivos e SO REAIS do computador do jogador.
        // O "disco" do TOS vai ser simulado dentro do próprio mod (Fase 4), nunca o disco real.
        globals.set("io", LuaValue.NIL);
        globals.set("os", LuaValue.NIL);
        globals.set("dofile", LuaValue.NIL);
        globals.set("loadfile", LuaValue.NIL);
        globals.set("require", LuaValue.NIL);

        if (bridge != null && networkApi != null) {
            LuaTable networkTable = new LuaTable();
            networkTable.set("status", new ZeroArgFunction() {
                @Override
                public LuaValue call() {
                    return LuaValue.valueOf(bridge.call(networkApi::status, "erro: sem resposta da rede"));
                }
            });
            networkTable.set("installTOS", new ZeroArgFunction() {
                @Override
                public LuaValue call() {
                    return LuaValue.valueOf(bridge.call(networkApi::installTos, "erro: sem resposta da rede"));
                }
            });
            networkTable.set("setPassword", new org.luaj.vm2.lib.OneArgFunction() {
                @Override
                public LuaValue call(LuaValue password) {
                    String pwd = password.optjstring("");
                    return LuaValue.valueOf(bridge.call(() -> networkApi.setPassword(pwd), "erro: sem resposta da rede"));
                }
            });
            networkTable.set("sendOsTo", new org.luaj.vm2.lib.ThreeArgFunction() {
                @Override
                public LuaValue call(LuaValue x, LuaValue y, LuaValue z) {
                    int ix = x.optint(0);
                    int iy = y.optint(0);
                    int iz = z.optint(0);
                    return LuaValue.valueOf(bridge.call(() -> networkApi.sendOsTo(ix, iy, iz), "erro: sem resposta da rede"));
                }
            });
            globals.set("network", networkTable);
        }

        if (bridge != null && printerApi != null) {
            LuaTable printerTable = new LuaTable();
            printerTable.set("print", new org.luaj.vm2.lib.VarArgFunction() {
                @Override
                public org.luaj.vm2.Varargs invoke(org.luaj.vm2.Varargs args) {
                    int ix = args.optint(1, 0);
                    int iy = args.optint(2, 0);
                    int iz = args.optint(3, 0);
                    String text = args.optjstring(4, "");
                    return LuaValue.valueOf(bridge.call(() -> printerApi.print(ix, iy, iz, text), "erro: sem resposta da impressora"));
                }
            });
            globals.set("printer", printerTable);
        }

        if (bridge != null && usbApi != null) {
            LuaTable usbTable = new LuaTable();
            usbTable.set("read", new ZeroArgFunction() {
                @Override
                public LuaValue call() {
                    return LuaValue.valueOf(bridge.call(usbApi::read, "erro: sem pen drive inserido"));
                }
            });
            usbTable.set("write", new org.luaj.vm2.lib.OneArgFunction() {
                @Override
                public LuaValue call(LuaValue text) {
                    String content = text.optjstring("");
                    return LuaValue.valueOf(bridge.call(() -> usbApi.write(content), "erro: sem pen drive inserido"));
                }
            });
            globals.set("usb", usbTable);
        }

        if (bridge != null && redstoneApi != null) {
            LuaTable redstoneTable = new LuaTable();
            redstoneTable.set("read", new org.luaj.vm2.lib.ThreeArgFunction() {
                @Override
                public LuaValue call(LuaValue x, LuaValue y, LuaValue z) {
                    int ix = x.optint(0);
                    int iy = y.optint(0);
                    int iz = z.optint(0);
                    return LuaValue.valueOf(bridge.call(() -> redstoneApi.read(ix, iy, iz), "erro: sem resposta"));
                }
            });
            redstoneTable.set("send", new org.luaj.vm2.lib.VarArgFunction() {
                @Override
                public org.luaj.vm2.Varargs invoke(org.luaj.vm2.Varargs args) {
                    int ix = args.optint(1, 0);
                    int iy = args.optint(2, 0);
                    int iz = args.optint(3, 0);
                    int strength = args.optint(4, 0);
                    return LuaValue.valueOf(bridge.call(() -> redstoneApi.send(ix, iy, iz, strength), "erro: sem resposta"));
                }
            });
            redstoneTable.set("pulse", new org.luaj.vm2.lib.VarArgFunction() {
                @Override
                public org.luaj.vm2.Varargs invoke(org.luaj.vm2.Varargs args) {
                    int ix = args.optint(1, 0);
                    int iy = args.optint(2, 0);
                    int iz = args.optint(3, 0);
                    int strength = args.optint(4, 0);
                    int ticks = args.optint(5, 20);
                    return LuaValue.valueOf(bridge.call(() -> redstoneApi.pulse(ix, iy, iz, strength, ticks), "erro: sem resposta"));
                }
            });
            globals.set("redstone", redstoneTable);
        }

        if (bridge != null && fsApi != null) {
            LuaTable fsTable = new LuaTable();
            fsTable.set("save", new org.luaj.vm2.lib.TwoArgFunction() {
                @Override
                public LuaValue call(LuaValue name, LuaValue content) {
                    String n = name.optjstring("");
                    String c = content.optjstring("");
                    return LuaValue.valueOf(bridge.call(() -> fsApi.save(n, c), "erro: sem resposta"));
                }
            });
            fsTable.set("load", new org.luaj.vm2.lib.OneArgFunction() {
                @Override
                public LuaValue call(LuaValue name) {
                    String n = name.optjstring("");
                    return LuaValue.valueOf(bridge.call(() -> fsApi.load(n), "erro: sem resposta"));
                }
            });
            fsTable.set("list", new ZeroArgFunction() {
                @Override
                public LuaValue call() {
                    return LuaValue.valueOf(bridge.call(fsApi::list, "erro: sem resposta"));
                }
            });
            fsTable.set("delete", new org.luaj.vm2.lib.OneArgFunction() {
                @Override
                public LuaValue call(LuaValue name) {
                    String n = name.optjstring("");
                    return LuaValue.valueOf(bridge.call(() -> fsApi.delete(n), "erro: sem resposta"));
                }
            });
            globals.set("fs", fsTable);
        }

        if (bridge != null && clusterApi != null) {
            LuaTable clusterTable = new LuaTable();
            clusterTable.set("status", new ZeroArgFunction() {
                @Override
                public LuaValue call() {
                    return LuaValue.valueOf(bridge.call(clusterApi::status, "erro: sem resposta"));
                }
            });
            clusterTable.set("setResourceManagerActive", new org.luaj.vm2.lib.OneArgFunction() {
                @Override
                public LuaValue call(LuaValue active) {
                    boolean value = active.toboolean();
                    return LuaValue.valueOf(bridge.call(() -> clusterApi.setResourceManagerActive(value), "erro: sem resposta"));
                }
            });
            globals.set("cluster", clusterTable);
        }

        // Redireciona print() pra um buffer que a tela/terminal do jogo vai ler, em vez do
        // console real do servidor.
        globals.STDOUT = new PrintStream(new OutputStream() {
            private final StringBuilder currentLine = new StringBuilder();

            @Override
            public void write(int b) {
                if (b == '\n') {
                    outputBuffer.add(currentLine.toString());
                    currentLine.setLength(0);
                } else {
                    currentLine.append((char) b);
                }
            }
        }, true);
    }

    public boolean isRunning() {
        return running.get();
    }

    /**
     * Executa um bloco de código Lua de forma assíncrona - não bloqueia quem chamou.
     * O resultado (erros, saída de print) aparece depois via pollOutput().
     */
    public void execute(String code) {
        if (!running.get()) {
            outputBuffer.add("erro: computador desligado");
            return;
        }
        currentTask = EXECUTOR.submit(() -> {
            try {
                globals.load(code, "terminal").call();
            } catch (LuaError e) {
                outputBuffer.add("erro lua: " + e.getMessage());
            } catch (Exception e) {
                outputBuffer.add("erro: " + e.getMessage());
            }
        });
    }

    /** Pega e limpa tudo que foi impresso (ou deu erro) desde a última leitura. */
    public List<String> pollOutput() {
        List<String> lines = new ArrayList<>();
        String line;
        while ((line = outputBuffer.poll()) != null) {
            lines.add(line);
        }
        return lines;
    }

    /** "Desliga" o computador: cancela qualquer script em execução e libera o ambiente Lua. */
    public void stop() {
        running.set(false);
        Future<?> task = currentTask;
        if (task != null) {
            task.cancel(true);
        }
        globals = null;
        outputBuffer.clear();
    }
}
