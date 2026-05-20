package com.ssakura49.sakuratinker.agent.HealthMethod;

import net.minecraft.world.entity.Entity;

import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;

public class AgentHealthMethodHelper {
    private static final Map<Object, HealthData> HEALTH_MAP = Collections.synchronizedMap(new WeakHashMap<>());

    public static void forceSetHealth(Entity target, float health, float maxHealth) {
        if (target != null) {
            HEALTH_MAP.put(target, new HealthData(health, maxHealth));
        }
    }

    public static float agentGetHealth(Entity target) {
        if (target == null) {
            return -1.0F;
        } else {
            HealthData data = (HealthData)HEALTH_MAP.get(target);
            return data != null ? data.health : -1.0F;
        }
    }

    public static float agentGetMaxHealth(Entity target) {
        if (target == null) {
            return -1.0F;
        } else {
            HealthData data = (HealthData)HEALTH_MAP.get(target);
            return data != null ? data.maxHealth : -1.0F;
        }
    }

    public static void forceSetHealth(Object target, float health, float maxHealth) {
        if (target != null) {
            HEALTH_MAP.put(target, new HealthData(health, maxHealth));
        }
    }

    public static float agentGetHealth(Object target) {
        if (target == null) {
            return -1.0F;
        } else {
            HealthData data = (HealthData)HEALTH_MAP.get(target);
            return data != null ? data.health : -1.0F;
        }
    }

    public static float agentGetMaxHealth(Object target) {
        if (target == null) {
            return -1.0F;
        } else {
            HealthData data = (HealthData)HEALTH_MAP.get(target);
            return data != null ? data.maxHealth : -1.0F;
        }
    }

    public static boolean isModified(Object target) {
        return HEALTH_MAP.containsKey(target);
    }

    public static void clear(Object target) {
        if (target != null) {
            HEALTH_MAP.remove(target);
        }

    }

    public static void clearAll() {
        HEALTH_MAP.clear();
    }

    public static class HealthData {
        public float health;
        public float maxHealth;

        public HealthData(float health, float maxHealth) {
            this.health = health;
            this.maxHealth = maxHealth;
        }
    }
}
