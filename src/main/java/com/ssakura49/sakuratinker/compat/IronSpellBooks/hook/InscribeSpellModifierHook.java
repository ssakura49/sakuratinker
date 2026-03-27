package com.ssakura49.sakuratinker.compat.IronSpellBooks.hook;

import io.redspace.ironsspellbooks.api.events.InscribeSpellEvent;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import java.util.Collection;

public interface InscribeSpellModifierHook {
    default void onInscribeSpell(IToolStackView tool, ModifierEntry modifier, InscribeSpellEvent event){}
    record AllMerger(Collection<InscribeSpellModifierHook> modules) implements InscribeSpellModifierHook {
        @Override
        public void onInscribeSpell(IToolStackView tool, ModifierEntry modifier, InscribeSpellEvent event) {
            for (InscribeSpellModifierHook module : modules) {
                module.onInscribeSpell(tool, modifier, event);
            }
        }
    }
}

