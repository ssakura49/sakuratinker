package com.ssakura49.sakuratinker.compat.IronSpellBooks.hook;

import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import java.util.Collection;

public interface ManaCostModifierHook {
    int getManaCost(IToolStackView tool, ModifierEntry modifier, int baseCost, int currentCost);
    record AllMerger(Collection<ManaCostModifierHook> modules) implements ManaCostModifierHook {
        @Override
        public int getManaCost(IToolStackView tool, ModifierEntry modifier, int baseCost, int currentCost) {
            for (ManaCostModifierHook module : modules) {
                currentCost = module.getManaCost(tool, modifier, baseCost, currentCost);
            }
            return currentCost;
        }
    }

}
