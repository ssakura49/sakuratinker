package com.ssakura49.sakuratinker.compat.IronSpellBooks.hook;

import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import java.util.Collection;

public interface SpellLevelModifierHook {
    int getSpellLevel(IToolStackView tool, ModifierEntry modifier, int baseLevel, int currentLevel);
    record AllMerger(Collection<SpellLevelModifierHook> modules) implements SpellLevelModifierHook {
        @Override
        public int getSpellLevel(IToolStackView tool, ModifierEntry modifier, int baseLevel, int currentLevel) {
            for (SpellLevelModifierHook module : modules) {
                currentLevel = module.getSpellLevel(tool, modifier, baseLevel, currentLevel);
            }
            return currentLevel;
        }
    }

}
