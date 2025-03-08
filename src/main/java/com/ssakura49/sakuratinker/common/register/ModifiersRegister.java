package com.ssakura49.sakuratinker.common.register;

import com.ssakura49.sakuratinker.SakuraTinker;
import com.ssakura49.sakuratinker.SakuraTinkerModule;
import com.ssakura49.sakuratinker.common.tinkering.modifiers.modifiers.*;
import com.ssakura49.sakuratinker.common.tinkering.modifiers.modifiers.armor.*;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import slimeknights.tconstruct.library.modifiers.util.ModifierDeferredRegister;
import slimeknights.tconstruct.library.modifiers.util.StaticModifier;

public class ModifiersRegister extends SakuraTinkerModule {
    public static ModifierDeferredRegister MODIFIERS = ModifierDeferredRegister.create(SakuraTinker.MODID);

    public static final StaticModifier<SolidModifier> Solid = MODIFIERS.register("solid", SolidModifier::new);
    public static final StaticModifier<VoidModifier> Void = MODIFIERS.register("void", VoidModifier::new);
    public static final StaticModifier<WitherSymbiosisModifier> Wither_Symbiosis = MODIFIERS.register("wither_symbiosis", WitherSymbiosisModifier::new);
    public static final StaticModifier<CelestialModifier> Celestial = MODIFIERS.register("celestial", CelestialModifier::new);
    public static final StaticModifier<RuinationModifier> Ruination = MODIFIERS.register("ruination", RuinationModifier::new);
    public static final StaticModifier<EternityModifier> Eternity = MODIFIERS.register("eternity", EternityModifier::new);
    public static final StaticModifier<NullAlmightyModifier> Null_Almighty = MODIFIERS.register("null_almighty", NullAlmightyModifier::new);
    public static final StaticModifier<MortalWoundModifier> Mortal_Wound = MODIFIERS.register("mortal_wound", MortalWoundModifier::new);
    public static final StaticModifier<KoboldModifier> Kobold = MODIFIERS.register("kobold", KoboldModifier::new);
    public static final StaticModifier<StormVeilModifier> Storm_Veil = MODIFIERS.register("storm_veil", StormVeilModifier::new);
    public static final StaticModifier<RootednessModifier> Rootedness = MODIFIERS.register("rootedness", RootednessModifier::new);
    public static final StaticModifier<NetherGhostModifier> Nether_Ghost = MODIFIERS.register("nether_ghost", NetherGhostModifier::new);
    public static final StaticModifier<GaleModifier> Gale = MODIFIERS.register("gale", GaleModifier::new);
    public static final StaticModifier<EvilEyeModifier> Evil_Eye = MODIFIERS.register("evil_eye", EvilEyeModifier::new);
    public static final StaticModifier<OmnipotenceModifier> Omnipotence = MODIFIERS.register("omnipotence", OmnipotenceModifier::new);

    public ModifiersRegister() {
        MODIFIERS.register(FMLJavaModLoadingContext.get().getModEventBus());
    }
}
