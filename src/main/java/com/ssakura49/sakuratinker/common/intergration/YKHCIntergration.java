package com.ssakura49.sakuratinker.common.intergration;

import com.ssakura49.sakuratinker.SakuraTinker;
import com.ssakura49.sakuratinker.common.tinkering.modifiers.modifiers.BlurredModifier;
import com.ssakura49.sakuratinker.common.tinkering.modifiers.modifiers.ClearStrikeModifier;
import com.ssakura49.sakuratinker.common.tinkering.modifiers.modifiers.armor.YoukaifiedModifier;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import slimeknights.tconstruct.library.modifiers.util.ModifierDeferredRegister;
import slimeknights.tconstruct.library.modifiers.util.StaticModifier;

public class YKHCIntergration  {
    public static ModifierDeferredRegister MODIFIERS = ModifierDeferredRegister.create(SakuraTinker.MODID);

    public static final StaticModifier<YoukaifiedModifier> Youkaified = MODIFIERS.register("youkaified", YoukaifiedModifier::new);
    public static final StaticModifier<BlurredModifier> Blurred = MODIFIERS.register("blurred", BlurredModifier::new);
    public static final StaticModifier<ClearStrikeModifier> ClearStrike = MODIFIERS.register("clear_strike", ClearStrikeModifier::new);

    public static void Init() {
        MODIFIERS.register(FMLJavaModLoadingContext.get().getModEventBus());
    }
}
