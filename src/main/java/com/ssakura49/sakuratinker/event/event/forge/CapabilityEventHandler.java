package com.ssakura49.sakuratinker.event.event.forge;

import com.ssakura49.sakuratinker.SakuraTinker;
import com.ssakura49.sakuratinker.api.entity.buff.BuffCapabilityProvider;
import com.ssakura49.sakuratinker.api.entity.buff.CustomBuffHolder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = SakuraTinker.MODID)
public class CapabilityEventHandler {

    public static final ResourceLocation BUFF_CAP_KEY = SakuraTinker.getResource("custom_buffs");

    @SubscribeEvent
    public static void registerCaps(RegisterCapabilitiesEvent event) {
        event.register(CustomBuffHolder.class);
    }

    @SubscribeEvent
    public static void onAttachCapabilities(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof LivingEntity) {
            event.addCapability(BUFF_CAP_KEY, new BuffCapabilityProvider());
        }
    }
}
