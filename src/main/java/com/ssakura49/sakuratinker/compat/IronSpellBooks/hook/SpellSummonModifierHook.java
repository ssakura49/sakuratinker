package com.ssakura49.sakuratinker.compat.IronSpellBooks.hook;

import net.minecraft.world.entity.Entity;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import java.util.Collection;

public interface SpellSummonModifierHook {
    void onSummon(IToolStackView tool, ModifierEntry modifier, Entity summoned);
    record AllMerger(Collection<SpellSummonModifierHook> modules) implements SpellSummonModifierHook {
        @Override
        public void onSummon(IToolStackView tool, ModifierEntry modifier, Entity summoned) {
            for (SpellSummonModifierHook module : modules) {
                module.onSummon(tool, modifier, summoned);
            }
        }
    }

}
