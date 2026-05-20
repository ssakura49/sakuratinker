package com.ssakura49.sakuratinker.compat.IronSpellBooks.hook;

import com.ssakura49.sakuratinker.compat.IronSpellBooks.context.SpellAttackContext;
import io.redspace.ironsspellbooks.api.events.SpellHealEvent;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import java.util.Collection;

public interface SpellHealModifierHook {
    default void getSpellHeal(IToolStackView tool, ModifierEntry modifier, SpellAttackContext context, SpellHealEvent event){}
    record AllMerger(Collection<SpellHealModifierHook> modules) implements SpellHealModifierHook {
        @Override
            public void getSpellHeal(IToolStackView tool, ModifierEntry modifier, SpellAttackContext context, SpellHealEvent event) {
            for (SpellHealModifierHook module : modules) {
                module.getSpellHeal(tool, modifier, context, event);
            }
        }
    }

}
