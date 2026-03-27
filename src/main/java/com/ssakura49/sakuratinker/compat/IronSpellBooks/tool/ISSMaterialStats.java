package com.ssakura49.sakuratinker.compat.IronSpellBooks.tool;

import com.ssakura49.sakuratinker.SakuraTinker;
import com.ssakura49.sakuratinker.common.tools.stats.*;
import com.ssakura49.sakuratinker.compat.IronSpellBooks.tool.stats.BookMarkMaterialStats;
import com.ssakura49.sakuratinker.compat.IronSpellBooks.tool.stats.EnvelopeMaterialStats;
import com.ssakura49.sakuratinker.compat.IronSpellBooks.tool.stats.ISSStatlessMaterialStats;
import com.ssakura49.sakuratinker.compat.IronSpellBooks.tool.stats.ManuScriptMaterialStats;
import slimeknights.tconstruct.library.materials.IMaterialRegistry;
import slimeknights.tconstruct.library.materials.MaterialRegistry;
import slimeknights.tconstruct.library.materials.stats.MaterialStatsId;

public class ISSMaterialStats {
    public static final MaterialStatsId TINKER_SPELL_BOOK = new MaterialStatsId(SakuraTinker.getResource("tinker_spell_book"));
    public ISSMaterialStats(){}

    public static void init() {
        IMaterialRegistry registry = MaterialRegistry.getInstance();
        registry.registerStatType(BookMarkMaterialStats.TYPE, TINKER_SPELL_BOOK);
        registry.registerStatType(EnvelopeMaterialStats.TYPE, TINKER_SPELL_BOOK);
        registry.registerStatType(ManuScriptMaterialStats.TYPE, TINKER_SPELL_BOOK);
        registry.registerStatType(ISSStatlessMaterialStats.GUTTER.getType(), TINKER_SPELL_BOOK);
    }
}
