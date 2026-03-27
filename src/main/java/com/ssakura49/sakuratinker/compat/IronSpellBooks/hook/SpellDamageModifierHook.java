package com.ssakura49.sakuratinker.compat.IronSpellBooks.hook;

import com.ssakura49.sakuratinker.compat.IronSpellBooks.context.SpellAttackContext;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import java.util.Collection;

public interface SpellDamageModifierHook {
    float getSpellDamage(IToolStackView tool, ModifierEntry modifier, SpellAttackContext context, float baseDamage, float damage);

    record AllMerger(Collection<SpellDamageModifierHook> modules) implements SpellDamageModifierHook {
        @Override
        public float getSpellDamage(IToolStackView tool, ModifierEntry modifier, SpellAttackContext context, float baseDamage, float damage) {
            for (SpellDamageModifierHook module : modules) {
                damage = module.getSpellDamage(tool, modifier, context, baseDamage, damage);
            }
            return damage;
        }
    }
}
