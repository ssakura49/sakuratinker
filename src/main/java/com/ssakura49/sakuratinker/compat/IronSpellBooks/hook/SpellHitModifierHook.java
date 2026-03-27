package com.ssakura49.sakuratinker.compat.IronSpellBooks.hook;

import com.ssakura49.sakuratinker.compat.IronSpellBooks.context.SpellAttackContext;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import java.util.Collection;

public interface SpellHitModifierHook {
    default float beforeSpellHit(IToolStackView tool, ModifierEntry modifier, SpellAttackContext context, float damage, float baseKnockback, float knockback) {
        return knockback;
    }

    default void afterSpellHit(IToolStackView tool, ModifierEntry modifier, SpellAttackContext context, float damageDealt) {}

    default void failedSpellHit(IToolStackView tool, ModifierEntry modifier, SpellAttackContext context, float damageAttempted) {}

    record AllMerger(Collection<SpellHitModifierHook> modules) implements SpellHitModifierHook {
        @Override
        public float beforeSpellHit(IToolStackView tool, ModifierEntry modifier, SpellAttackContext context, float damage, float baseKnockback, float knockback) {
            for (SpellHitModifierHook module : modules) {
                knockback = module.beforeSpellHit(tool, modifier, context, damage, baseKnockback, knockback);
            }
            return knockback;
        }

        @Override
        public void afterSpellHit(IToolStackView tool, ModifierEntry modifier, SpellAttackContext context, float damageDealt) {
            for (SpellHitModifierHook module : modules) {
                module.afterSpellHit(tool, modifier, context, damageDealt);
            }
        }

        @Override
        public void failedSpellHit(IToolStackView tool, ModifierEntry modifier, SpellAttackContext context, float damageAttempted) {
            for (SpellHitModifierHook module : modules) {
                module.failedSpellHit(tool, modifier, context, damageAttempted);
            }
        }
    }
}
