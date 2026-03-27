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

public record AlchemicalCoreMaterialStats(float fluidDamage, float cooldown) implements IMaterialStats {
    public static final MaterialStatsId ID = new MaterialStatsId(SakuraTinker.MODID, "alchemical_core");
    public static final MaterialStatType<AlchemicalCoreMaterialStats> TYPE;
    private static final String FLUID_DAMAGE_PREFIX;
    private static final String COOLDOWN_PREFIX;
    private static final List<Component> DESCRIPTION;

    public AlchemicalCoreMaterialStats(float fluidDamage, float cooldown) {
        this.fluidDamage = fluidDamage;
        this.cooldown = cooldown;
    }

    public @NotNull MaterialStatType<?> getType() {
        return TYPE;
    }

    public @NotNull List<Component> getLocalizedInfo() {
        List<Component> info = Lists.newArrayList();
        info.add(IToolStat.formatColoredBonus(FLUID_DAMAGE_PREFIX, this.fluidDamage));
        info.add(IToolStat.formatColoredBonus(COOLDOWN_PREFIX, this.cooldown));
        return info;
    }

    public @NotNull List<Component> getLocalizedDescriptions() {
        return DESCRIPTION;
    }

    public void apply(ModifierStatsBuilder builder, float scale) {
        STToolStats.FLUID_DAMAGE.update(builder, this.fluidDamage * scale);
        STToolStats.COOLDOWN.update(builder, this.cooldown * scale);
    }
    public float fluidDamage() {
        return this.fluidDamage;
    }
    public float cooldown(){
        return this.cooldown;
    }

    static {
        TYPE = new MaterialStatType<>(ID, new AlchemicalCoreMaterialStats(0.0f, 0), RecordLoadable.create(
                FloatLoadable.ANY.defaultField("fluid_damage", 0.0f, true, AlchemicalCoreMaterialStats::fluidDamage),
                FloatLoadable.ANY.defaultField("cooldown", 0.0f, true, AlchemicalCoreMaterialStats::cooldown),
                AlchemicalCoreMaterialStats::new
        ));
        FLUID_DAMAGE_PREFIX = IMaterialStats.makeTooltipKey(SakuraTinker.getResource("fluid_damage"));
        COOLDOWN_PREFIX = IMaterialStats.makeTooltipKey(SakuraTinker.getResource("cooldown"));
        DESCRIPTION = ImmutableList.of(IMaterialStats.makeTooltip(SakuraTinker.getResource("alchemical_core.fluid_damage.description")),
                IMaterialStats.makeTooltip(SakuraTinker.getResource("alchemical_core.cooldown.description")));
    }
}
