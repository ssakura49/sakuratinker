package com.ssakura49.sakuratinker.common.intergration;

import com.ssakura49.sakuratinker.SakuraTinker;
import com.ssakura49.sakuratinker.common.tinkering.modifiers.modifiers.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import slimeknights.tconstruct.library.materials.definition.MaterialId;
import slimeknights.tconstruct.library.modifiers.util.ModifierDeferredRegister;
import slimeknights.tconstruct.library.modifiers.util.StaticModifier;

public class TFIntergration {
    public static ModifierDeferredRegister MODIFIERS = ModifierDeferredRegister.create(SakuraTinker.MODID);

    public static MaterialId steeleaf = createMaterial("steeleaf");
    public static MaterialId naga_scale = createMaterial("naga_scale");
    public static MaterialId knightmetal = createMaterial("knightmetal");
    public static MaterialId fiery = createMaterial("fiery");

    public static final StaticModifier<TwilitModifier> Twilit = MODIFIERS.register("twilit", TwilitModifier::new);
    public static final StaticModifier<PrecipitateModifier> Precipitate = MODIFIERS.register("precipitate", PrecipitateModifier::new);
    public static final StaticModifier<SynergyModifier> Synergy = MODIFIERS.register("synergy", SynergyModifier::new);
    public static final StaticModifier<StalwartModifier> Stalwart = MODIFIERS.register("stalwart", StalwartModifier::new);
    public static final StaticModifier<SuperHeatModifier> SuperHeat = MODIFIERS.register("superheat", SuperHeatModifier::new);

    public static MaterialId createMaterial(String name) {
        return new MaterialId(new ResourceLocation(SakuraTinker.MODID, name));
    }

    public static void Init() {
        MODIFIERS.register(FMLJavaModLoadingContext.get().getModEventBus());
    }
}
