package com.ssakura49.sakuratinker.compat.IronSpellBooks.hook;

import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import java.util.Collection;

public interface CooldownModifierHook {
    int getCooldown(IToolStackView tool, ModifierEntry modifier, int baseCooldown, int currentCooldown);
    record AllMerger(Collection<CooldownModifierHook> modules) implements CooldownModifierHook {
        @Override
        public int getCooldown(IToolStackView tool, ModifierEntry modifier, int baseCooldown, int currentCooldown) {
            for (CooldownModifierHook module : modules) {
                currentCooldown = module.getCooldown(tool, modifier, baseCooldown, currentCooldown);
            }
            return currentCooldown;
        }
    }

}
