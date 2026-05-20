package com.ssakura49.sakuratinker.data.generator.enums;

import com.ssakura49.sakuratinker.compat.IronSpellBooks.ISSCompat;
import com.ssakura49.sakuratinker.compat.IronSpellBooks.tool.ISSMaterialRegistry;
import com.ssakura49.sakuratinker.compat.IronSpellBooks.tool.stats.EnvelopeMaterialStats;
import com.ssakura49.sakuratinker.compat.IronSpellBooks.tool.stats.ManuScriptMaterialStats;
import slimeknights.tconstruct.library.materials.stats.MaterialStatsId;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierId;

public enum EnumTconMaterialModifier {
    cobalt_m(ISSMaterialRegistry.TINKER_SPELL_BOOK,entry(ISSCompat.cobaltSpell.getId())),
    ice_m(ISSMaterialRegistry.TINKER_SPELL_BOOK,entry(ISSCompat.SpellFrozen.getId())),
    witherBone_m(ISSMaterialRegistry.TINKER_SPELL_BOOK,entry(ISSCompat.WitherTentacles.getId())),
    manyullyn_m(ISSMaterialRegistry.TINKER_SPELL_BOOK,entry(ISSCompat.GreedyBook.getId())),
    venomBone_m(ISSMaterialRegistry.TINKER_SPELL_BOOK,entry(ISSCompat.ToxicityIntensification.getId()))
    ;
    public final ModifierEntry[] modifiers;
    public final MaterialStatsId statType;
    EnumTconMaterialModifier(MaterialStatsId statType, slimeknights.tconstruct.library.modifiers.ModifierEntry... modifiers){
        this.modifiers = modifiers;
        this.statType = statType;
    }
    public static ModifierEntry entry(ModifierId id, int level){
        return new ModifierEntry(id,level);
    }
    public static ModifierEntry entry(ModifierId id){
        return new ModifierEntry(id,1);
    }
}
