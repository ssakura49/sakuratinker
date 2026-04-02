package com.ssakura49.sakuratinker.compat.IronSpellBooks;

import com.ssakura49.sakuratinker.SakuraTinker;
import com.ssakura49.sakuratinker.compat.IronSpellBooks.stat.SchoolToolStat;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.spells.SchoolType;
import slimeknights.tconstruct.library.tools.stat.FloatToolStat;
import slimeknights.tconstruct.library.tools.stat.ToolStatId;
import slimeknights.tconstruct.library.tools.stat.ToolStats;

import java.util.HashMap;
import java.util.Map;

public class ISSToolStats {
    //ok
    public static final SchoolToolStat SCHOOL_STAT = ToolStats.register(new SchoolToolStat(name("bullet_type"), SchoolRegistry.NATURE));
    //ok
    public static final Map<SchoolType, FloatToolStat> SCHOOL_BONUS = new HashMap<>();
    public static void init() {
        for (SchoolType school : SchoolRegistry.REGISTRY.get().getValues()) {
            registerSchool(school);
        }
    }
    private static void registerSchool(SchoolType school) {
        if (SCHOOL_BONUS.containsKey(school)) {
            SCHOOL_BONUS.get(school);
            return;
        }

        FloatToolStat stat = ToolStats.register(
                new FloatToolStat(
                        name("school_bonus_" + school.getId().getPath()),
                        -2661276,
                        0.0F,
                        0.0F,
                        Float.MAX_VALUE
                )
        );

        SCHOOL_BONUS.put(school, stat);
    }
    public static final FloatToolStat SPELL_SLOT = ToolStats.register(new FloatToolStat(name("spell_slot"), -10887823, 1, 1, 15));
    //ok
    public static final FloatToolStat PERCENT_BONUS = ToolStats.register(new FloatToolStat(name("percent_bonus"), -2661276, 0.0F, 0.0F, Float.MAX_VALUE));
    //ok
    public static final FloatToolStat MANA_REDUCE = ToolStats.register(new FloatToolStat(name("mana_reduce"), -2661276, 0.0F, 0.0F, Float.MAX_VALUE));
    //ok
    public static final FloatToolStat MANA_REGEN = ToolStats.register(new FloatToolStat(name("mana_regen"), -2661276, 0.0F, 0.0F, Float.MAX_VALUE));
    //ok
    public static final FloatToolStat CAST_TIME_REDUCE = ToolStats.register(new FloatToolStat(name("cast_time_reduce"), -2661276, 0.0F, 0.0F, Float.MAX_VALUE));
    //ok
    public static final FloatToolStat SPELL_DAMAGE = ToolStats.register(new FloatToolStat(name("spell_damage"), -2661276, 0.0F, 0.0F, Float.MAX_VALUE));
    //ok
    public static final FloatToolStat MANA_VALUE = ToolStats.register(new FloatToolStat(name("mana_value"), -2661276, 0.0F, 0.0F, Float.MAX_VALUE));


    private static ToolStatId name(String name) {
        return new ToolStatId(SakuraTinker.getResource(name));
    }
}
