package com.ssakura49.sakuratinker.common.register;

import com.ssakura49.sakuratinker.SakuraTinkerModule;
import com.ssakura49.sakuratinker.common.effects.ImmortalityEffect;
import com.ssakura49.sakuratinker.common.effects.MortalWoundEffect;
import net.minecraft.world.effect.MobEffect;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import static com.ssakura49.sakuratinker.SakuraTinker.MODID;


public class EffectsRegister extends SakuraTinkerModule {
    public static final DeferredRegister<MobEffect> EFFECT = DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, MODID);

    public static final RegistryObject<MobEffect> IMMORTALITY = EFFECT.register("immortality", ImmortalityEffect::new);
    public static final RegistryObject<MobEffect> MORTAL_WOUND = EFFECT.register("mortal_wound", MortalWoundEffect::new);
    //public static final RegistryObject<MobEffect> UniversalBarrier = EFFECT.register("universal_barrier", UniversalBarrierEffect::new);

    public EffectsRegister() {
        EFFECT.register(FMLJavaModLoadingContext.get().getModEventBus());
    }
}
