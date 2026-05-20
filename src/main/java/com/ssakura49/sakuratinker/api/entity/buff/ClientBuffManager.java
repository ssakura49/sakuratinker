package com.ssakura49.sakuratinker.api.entity.buff;

import com.ssakura49.sakuratinker.network.s2c.ClientboundSyncBuffPacket;
import net.minecraft.resources.ResourceLocation;

import java.util.*;

public class ClientBuffManager {
    private static final Map<Integer, List<ClientBuffData>> CLIENT_CACHE = new HashMap<>();

    public record ClientBuffData(ResourceLocation id, int duration, int level) {}

    public static void handlePacket(ClientboundSyncBuffPacket packet) {
        List<ClientBuffData> buffs = CLIENT_CACHE.computeIfAbsent(packet.entityId(), k -> new ArrayList<>());
        if (packet.isRemoved()) {
            buffs.removeIf(b -> b.id().equals(packet.buffId()));
        } else {
            buffs.add(new ClientBuffData(packet.buffId(), packet.duration(), packet.level()));
        }
    }

    public static List<ClientBuffData> getBuffsForEntity(int entityId) {
        return CLIENT_CACHE.getOrDefault(entityId, Collections.emptyList());
    }
}
