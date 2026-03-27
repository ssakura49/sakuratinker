package com.ssakura49.sakuratinker.common.tools.stats;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.ssakura49.sakuratinker.SakuraTinker;
import com.ssakura49.sakuratinker.library.tinkering.tools.STToolStats;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import slimeknights.mantle.data.loadable.primitive.FloatLoadable;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.tconstruct.library.materials.stats.IMaterialStats;
import slimeknights.tconstruct.library.materials.stats.MaterialStatType;
import slimeknights.tconstruct.library.materials.stats.MaterialStatsId;
import slimeknights.tconstruct.library.tools.stat.IToolStat;
import slimeknights.tconstruct.library.tools.stat.ModifierStatsBuilder;

import java.util.List;

public record RangeMaterialStats(float range) implements IMaterialStats {
    public static final MaterialStatsId ID = new MaterialStatsId(SakuraTinker.MODID, "range_core");
    public static final MaterialStatType<RangeMaterialStats> TYPE = new MaterialStatType<>(ID, new RangeMaterialStats(0.0f), RecordLoadable.create(
            FloatLoadable.ANY.defaultField("range", 1.0f, true, RangeMaterialStats::range),
            RangeMaterialStats::new
    ));
    private static final String RANGE_PREFIX = IMaterialStats.makeTooltipKey(SakuraTinker.getResource("range"));
    private static final List<Component> DESCRIPTION = ImmutableList.of(IMaterialStats.makeTooltip(SakuraTinker.getResource("tool.raneg_stats.description")));

    public @NotNull MaterialStatType<?> getType() {
        return TYPE;
    }

    public @NotNull List<Component> getLocalizedInfo() {
        List<Component> info = Lists.newArrayList();
        info.add(IToolStat.formatColoredBonus(RANGE_PREFIX, this.range));
        return info;
    }

    public @NotNull List<Component> getLocalizedDescriptions() {
        return DESCRIPTION;
    }

    @Override
    public void apply(ModifierStatsBuilder builder, float scale) {
        STToolStats.RANGE.update(builder, this.range * scale);
    }

    public float range(){
        return this.range;
    }

}
