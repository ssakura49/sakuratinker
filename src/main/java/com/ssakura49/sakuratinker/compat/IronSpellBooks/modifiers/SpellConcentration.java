package com.ssakura49.sakuratinker.compat.IronSpellBooks.modifiers;

import com.ssakura49.sakuratinker.SakuraTinker;
import com.ssakura49.tinkercuriolib.tools.module.CurioLevelModule;
import org.jetbrains.annotations.NotNull;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.modifiers.impl.NoLevelsModifier;
import slimeknights.tconstruct.library.modifiers.modules.technical.ArmorLevelModule;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.capability.TinkerDataCapability;

public class SpellConcentration extends NoLevelsModifier {
    @Override
    protected void registerHooks(ModuleHookMap.@NotNull Builder hookBuilder) {
        super.registerHooks(hookBuilder);
        hookBuilder.addModule(new ArmorLevelModule(KEY, false, TinkerTags.Items.MODIFIABLE));
        hookBuilder.addModule(new CurioLevelModule(KEY, true, TinkerTags.Items.MODIFIABLE));
    }

    public static final TinkerDataCapability.TinkerDataKey<Integer> KEY = TinkerDataCapability.TinkerDataKey.of(SakuraTinker.getResource("spell_focus"));

}
