package com.ssakura49.sakuratinker.common.intergration;

import com.ssakura49.sakuratinker.SakuraTinker;
import com.ssakura49.sakuratinker.common.tinkering.modifiers.modifiers.armor.MagicianModifier;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import slimeknights.tconstruct.library.materials.definition.MaterialId;
import slimeknights.tconstruct.library.modifiers.util.ModifierDeferredRegister;
import slimeknights.tconstruct.library.modifiers.util.StaticModifier;

public class ISSIntergration {
    public static ModifierDeferredRegister MODIFIERS = ModifierDeferredRegister.create(SakuraTinker.MODID);

    public static final MaterialId Arcane_Salvage = createMaterial("arcane_salvage");
    public static final StaticModifier<MagicianModifier> Magician = MODIFIERS.register("magician", MagicianModifier::new);

    public static MaterialId createMaterial(String name) {
        return new MaterialId(new ResourceLocation(SakuraTinker.MODID, name));
    }
    public static void Init() {
        MODIFIERS.register(FMLJavaModLoadingContext.get().getModEventBus());
    }
}
