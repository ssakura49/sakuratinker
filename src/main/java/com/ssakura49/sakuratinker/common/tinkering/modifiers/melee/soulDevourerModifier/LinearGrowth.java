package com.ssakura49.sakuratinker.common.tinkering.modifiers.melee.soulDevourerModifier;

import com.ssakura49.sakuratinker.STConfig;

import java.util.function.Supplier;

public class LinearGrowth implements SoulGrowthFormula {
    private final Supplier<Double> linearPerKill = STConfig.Common.linearPerKill;

    @Override
    public float computeBonus(int kills, int level) {
        return (float) (kills
                * linearPerKill.get()
                * level);
    }
}