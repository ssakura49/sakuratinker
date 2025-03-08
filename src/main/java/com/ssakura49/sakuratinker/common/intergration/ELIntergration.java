package com.ssakura49.sakuratinker.common.intergration;

import com.ssakura49.sakuratinker.SakuraTinker;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import slimeknights.tconstruct.library.materials.definition.MaterialId;
import slimeknights.tconstruct.library.modifiers.util.ModifierDeferredRegister;


public class ELIntergration {
    public static ModifierDeferredRegister MODIFIERS = ModifierDeferredRegister.create(SakuraTinker.MODID);
    public static final MaterialId etherium = createMaterial("etherium");

    public static MaterialId createMaterial(String name) {
        return new MaterialId(new ResourceLocation(SakuraTinker.MODID, name));
    }

    public static void Init() {
        MODIFIERS.register(FMLJavaModLoadingContext.get().getModEventBus());
    }
}
