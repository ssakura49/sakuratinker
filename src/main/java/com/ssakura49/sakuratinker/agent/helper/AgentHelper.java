package com.ssakura49.sakuratinker.agent.helper;

public class AgentHelper {
    public static boolean blackClass(String className) {
        if (className == null) return true;

        if (className.startsWith("java/") ||
                className.startsWith("javax/") ||
                className.startsWith("sun/") ||
                className.startsWith("com/sun/") ||
                className.startsWith("jdk/")) {
            return true;
        }

        if (className.startsWith("net/irisshaders/") ||
                className.startsWith("com/jozufozu/flywheel/") ||
                className.contains("oculus") ||
                className.contains("iris")) {
            return true;
        }

        if (className.startsWith("org/bukkit/") ||
                className.startsWith("org/spigotmc/") ||
                className.startsWith("com/destroystokyo/paper/")) {
            return true;
        }

        if (className.startsWith("com/mohistmc/") || className.startsWith("red/mohist/")) {
            return true;
        }

        if (className.startsWith("net/minecraft/server/") && className.contains("Craft")) {
            return true;
        }

        if (className.startsWith("net/minecraftforge/") && !className.contains("event")) {
            return true;
        }

        if (className.startsWith("com/ssakura49/")) {
            return true;
        }

        return false;
    }
}
