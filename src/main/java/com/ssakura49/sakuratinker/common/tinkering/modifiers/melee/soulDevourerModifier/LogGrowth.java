package com.ssakura49.sakuratinker.common.tinkering.modifiers.melee.soulDevourerModifier;

import com.ssakura49.sakuratinker.STConfig;

import java.util.function.Supplier;

public class LogGrowth implements SoulGrowthFormula {
    private final Supplier<Double> logFactor = STConfig.Common.logFactor;

    @Override
    public float computeBonus(int kills, int level) {
        return (float) (kills
                * logFactor.get()
                * level);
    }
}
