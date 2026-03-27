package com.ssakura49.sakuratinker.compat.IronSpellBooks.hook;

import com.ssakura49.sakuratinker.compat.IronSpellBooks.context.SpellAttackContext;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import java.util.Collection;

public interface SpellHealModifierHook {
    float getSpellHeal(IToolStackView tool, ModifierEntry modifier, SpellAttackContext context, float baseHeal, float currentHeal);
    record AllMerger(Collection<SpellHealModifierHook> modules) implements SpellHealModifierHook {
        @Override
        public float getSpellHeal(IToolStackView tool, ModifierEntry modifier, SpellAttackContext context, float baseHeal, float currentHeal) {
            for (SpellHealModifierHook module : modules) {
                currentHeal = module.getSpellHeal(tool, modifier, context, baseHeal, currentHeal);
            }
            return currentHeal;
        }
    }

}
