package com.ssakura49.sakuratinker.compat.IronSpellBooks.tool.stats;

import com.ssakura49.sakuratinker.SakuraTinker;
import net.minecraft.network.chat.Component;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.materials.stats.IMaterialStats;
import slimeknights.tconstruct.library.materials.stats.MaterialStatType;
import slimeknights.tconstruct.library.materials.stats.MaterialStatsId;
import slimeknights.tconstruct.library.tools.stat.ModifierStatsBuilder;

import java.util.List;

public enum ISSStatlessMaterialStats implements IMaterialStats {
    GUTTER("gutter");

    private static final List<Component> LOCALIZED = List.of(IMaterialStats.makeTooltip(TConstruct.getResource("extra.no_stats")));
    private static final List<Component> DESCRIPTION = List.of(Component.empty());
    private final MaterialStatType<ISSStatlessMaterialStats> type;

    private ISSStatlessMaterialStats(String name) {
        this.type = MaterialStatType.singleton(new MaterialStatsId(SakuraTinker.getResource(name)), this);
    }

    public MaterialStatType<ISSStatlessMaterialStats> getType() {
        return this.type;
    }

    public List<Component> getLocalizedInfo() {
        return LOCALIZED;
    }

    public List<Component> getLocalizedDescriptions() {
        return DESCRIPTION;
    }

    public void apply(ModifierStatsBuilder builder, float scale) {
    }
}