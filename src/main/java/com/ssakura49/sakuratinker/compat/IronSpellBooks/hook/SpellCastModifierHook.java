package com.ssakura49.sakuratinker.compat.IronSpellBooks.hook;

import com.ssakura49.sakuratinker.compat.IronSpellBooks.context.SpellAttackContext;
import io.redspace.ironsspellbooks.api.events.SpellPreCastEvent;
import io.redspace.ironsspellbooks.api.spells.CastSource;
import io.redspace.ironsspellbooks.api.spells.SchoolType;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import java.util.Collection;

public interface SpellCastModifierHook {
    default void onPreCast(IToolStackView tool, ModifierEntry modifier, SpellAttackContext context, CastSource source, int spellLevel){}
    default void onCast(IToolStackView tool, ModifierEntry entry, SpellAttackContext context, CastSource source){}
    record AllMerger(Collection<SpellCastModifierHook> modules) implements SpellCastModifierHook {
        @Override
        public void onPreCast(IToolStackView tool, ModifierEntry modifier, SpellAttackContext context, CastSource source, int spellLevel) {
            for (SpellCastModifierHook module : modules) {
                module.onPreCast(tool,modifier,context,source,spellLevel);
            }
        }
        @Override
        public void onCast(IToolStackView tool, ModifierEntry modifier, SpellAttackContext context, CastSource source) {
            for (SpellCastModifierHook module : modules) {
                module.onCast(tool,modifier,context,source);
            }
        }
    }
}
