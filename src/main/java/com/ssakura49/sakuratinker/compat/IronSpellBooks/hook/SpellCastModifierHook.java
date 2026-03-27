package com.ssakura49.sakuratinker.compat.IronSpellBooks.hook;

import com.ssakura49.sakuratinker.compat.IronSpellBooks.context.SpellAttackContext;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import java.util.Collection;

public interface SpellCastModifierHook {
    boolean onPreCast(IToolStackView tool, ModifierEntry modifier, SpellAttackContext context);
    record AllMerger(Collection<SpellCastModifierHook> modules) implements SpellCastModifierHook {
        @Override
        public boolean onPreCast(IToolStackView tool, ModifierEntry modifier, SpellAttackContext context) {
            for (SpellCastModifierHook module : modules) {
                if (!module.onPreCast(tool, modifier, context))
                    return false;
            }
            return true;
        }
    }

}
