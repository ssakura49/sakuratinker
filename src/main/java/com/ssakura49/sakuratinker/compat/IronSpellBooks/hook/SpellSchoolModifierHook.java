package com.ssakura49.sakuratinker.compat.IronSpellBooks.hook;

import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.spells.SchoolType;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import java.util.Collection;

public interface SpellSchoolModifierHook {
    /**
     * @param tool 工具栈
     * @param modifier entry
     * @param spell 法术
     * @param currentSchool 流派
     * @return 修改后的流派
     */
    SchoolType modifySchool(IToolStackView tool, ModifierEntry modifier, AbstractSpell spell, SchoolType currentSchool);

    record AllMerger(Collection<SpellSchoolModifierHook> modules) implements SpellSchoolModifierHook {
        @Override
        public SchoolType modifySchool(IToolStackView tool, ModifierEntry modifier, AbstractSpell spell, SchoolType currentSchool) {
            for (SpellSchoolModifierHook module : modules) {
                currentSchool = module.modifySchool(tool, modifier, spell, currentSchool);
            }
            return currentSchool;
        }
    }
}
