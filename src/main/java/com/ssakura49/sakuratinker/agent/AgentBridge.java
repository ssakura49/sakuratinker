package com.ssakura49.sakuratinker.agent;

import java.lang.instrument.Instrumentation;

public class AgentBridge {
    private static volatile Instrumentation instrumentation;
    private static volatile boolean agentReady = false;

    public static void setInstrumentation(Instrumentation inst) {
        instrumentation = inst;
        agentReady = true;
        System.setProperty("sakura.agent.ready", "true");
        System.out.println("[SakuraAgent] Instrumentation Done");
    }

    public static Instrumentation getInstrumentation() {
        return instrumentation;
    }

    public static boolean agentLoad() {
        return instrumentation != null;
    }

    public static boolean isAgentReady() {
        return agentReady;
    }

    public static void retransformTargets() {
        if (instrumentation == null) return;
        for (Class<?> clazz : instrumentation.getAllLoadedClasses()) {
            String name = clazz.getName();
            if (name.equals("net.minecraft.world.entity.LivingEntity") ||
                    name.equals("net.minecraft.world.entity.player.Player")) {
                try {
                    System.out.println("[SakuraAgent] 正在重转换目标类: " + name);
                    instrumentation.retransformClasses(clazz);
                } catch (Exception e) {
                    System.err.println("[SakuraAgent] 重转换失败: " + name);
                    e.printStackTrace();
                }
            }
        }
    }
}