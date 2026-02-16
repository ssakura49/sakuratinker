package com.ssakura49.sakuratinker.common.tinkering.modifiers.melee.soulDevourerModifier;

import com.ssakura49.sakuratinker.STConfig;

import java.util.function.Supplier;

public class SoftcapGrowth implements SoulGrowthFormula {
    private final Supplier<Double> capMax = STConfig.Common.capMax;
    private final Supplier<Double> capScale = STConfig.Common.capScale;

    @Override
    public float computeBonus(int kills, int level) {
        double max = capMax.get();
        double scale = capScale.get();
        return (float) (max * level * (1.0 - Math.exp(-kills / scale)));
    }
}