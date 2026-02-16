package com.ssakura49.sakuratinker.event.event.forge;

import com.google.common.collect.Queues;
import com.ssakura49.sakuratinker.SakuraTinker;
import com.ssakura49.sakuratinker.common.tools.item.RevolverItem;
import com.ssakura49.sakuratinker.common.tools.item.RevolverItemBackPack;
import com.ssakura49.sakuratinker.library.interfaces.item.DelayTick;
import com.ssakura49.sakuratinker.library.logic.helper.PlayerAdvDataHelper;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.HashSet;
import java.util.Queue;
import java.util.Set;
import java.util.UUID;

@Mod.EventBusSubscriber(modid = SakuraTinker.MODID,bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ServerTickEvent {
    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }

        Set<UUID> copy = new HashSet<>(PlayerAdvUpdateEvent.uuid);
        PlayerAdvUpdateEvent.uuid.clear();

        for (UUID id : copy) {
            ServerPlayer player = event.getServer().getPlayerList().getPlayer(id);
            if (player != null) {
                PlayerAdvDataHelper.update(player);
            }
        }
    }
}
