package com.ssakura49.sakuratinker.common.tinkering.modifiers.melee.soulDevourerModifier;

import com.ssakura49.sakuratinker.STConfig;

import java.util.Map;

public class SoulDevourerFormula {
    private static final Map<STConfig.Common.GrowthMode, SoulGrowthFormula> FORMULAS = Map.of(
            STConfig.Common.GrowthMode.LINEAR, new LinearGrowth(),
            STConfig.Common.GrowthMode.LOG, new LogGrowth(),
            STConfig.Common.GrowthMode.SOFTCAP, new SoftcapGrowth()
    );

    public static SoulGrowthFormula current() {
        return FORMULAS.get(
                STConfig.Common.growthMode.get()
        );
    }
}
