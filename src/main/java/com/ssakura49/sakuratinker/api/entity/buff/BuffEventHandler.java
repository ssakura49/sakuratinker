package com.ssakura49.sakuratinker.api.entity.buff;

import com.ssakura49.sakuratinker.SakuraTinker;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = SakuraTinker.MODID)
public class BuffEventHandler {

    @SubscribeEvent
    public static void onLivingTick(LivingEvent.LivingTickEvent event) {
        event.getEntity().getCapability(BuffCapabilityProvider.BUFF_CAP).ifPresent(cap -> {
            cap.tickAll(event.getEntity());
        });
    }

    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone event) {
        if (!event.isWasDeath()) return;
        LivingEntity newPlayer = event.getEntity();
        event.getOriginal().getCapability(BuffCapabilityProvider.BUFF_CAP).ifPresent(oldCap -> {
            newPlayer.getCapability(BuffCapabilityProvider.BUFF_CAP).ifPresent(newCap -> {
                oldCap.getActiveBuffs().stream()
                        .filter(i -> i.getType().getPersistence() == CustomBuff.PersistenceLevel.PERSIST_DEATH)
                        .forEach(instance -> newCap.addBuff(newPlayer, instance));
            });
        });
    }
}
