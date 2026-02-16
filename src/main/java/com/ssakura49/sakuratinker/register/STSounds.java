package com.ssakura49.sakuratinker.register;

import com.ssakura49.sakuratinker.SakuraTinker;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class STSounds {
    public static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, SakuraTinker.MODID);

    public static final RegistryObject<SoundEvent> CELESTIAL_BLADE = SOUNDS.register("celestial_blade", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(SakuraTinker.MODID, "celestial_blade")));
    public static final RegistryObject<SoundEvent> LASER_SHOOT = SOUNDS.register("laser_shoot", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(SakuraTinker.MODID, "laser_shoot")));
    public static final RegistryObject<SoundEvent> TIME_STOP = SOUNDS.register("time_stop", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(SakuraTinker.MODID, "time_stop")));
    public static final RegistryObject<SoundEvent> THROW = SOUNDS.register("entity.yoyo.throw", ()->  SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(SakuraTinker.MODID,"entity.yoyo.throw")));
    public static final RegistryObject<SoundEvent> ZAP = SOUNDS.register("zap", ()->  SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(SakuraTinker.MODID,"zap")));

}
