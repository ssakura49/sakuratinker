package com.ssakura49.sakuratinker.library.logic.helper;

import net.minecraft.advancements.Advancement;
import net.minecraft.server.level.ServerPlayer;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerAdvDataHelper {
    private static final Map<UUID, Integer> COMPLETED = new HashMap<>();

    public static void update(ServerPlayer player) {
        int total = 0;
        for (Advancement adv : player.server.getAdvancements().getAllAdvancements()) {
            if (player.getAdvancements().getOrStartProgress(adv).isDone()) {
                total++;
            }
        }
        COMPLETED.put(player.getUUID(), total);
    }

    public static int get(ServerPlayer player) {
        return COMPLETED.getOrDefault(player.getUUID(), 0);
    }

    public static final class AdvancementCache {
        private static int TOTAL = -1;

        public static int getTotal(ServerPlayer player) {
            if (TOTAL < 0) {
                TOTAL = player.server.getAdvancements().getAllAdvancements().size();
            }
            return Math.max(TOTAL, 1);
        }
    }
}
