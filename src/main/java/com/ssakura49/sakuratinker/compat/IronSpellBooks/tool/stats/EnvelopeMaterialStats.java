package com.ssakura49.sakuratinker.compat.IronSpellBooks.tool.stats;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.ssakura49.sakuratinker.SakuraTinker;
import com.ssakura49.sakuratinker.compat.IronSpellBooks.ISSToolStats;
import com.ssakura49.sakuratinker.compat.IronSpellBooks.helper.SchoolTypeLoadable;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.spells.SchoolType;
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
import java.util.function.Supplier;

//封皮
public record EnvelopeMaterialStats(SchoolType stat, float school_bonus) implements IMaterialStats {
    public static final MaterialStatsId ID = new MaterialStatsId(SakuraTinker.MODID, "envelope");

    public static final MaterialStatType<EnvelopeMaterialStats> TYPE = new MaterialStatType<>(ID,
            new EnvelopeMaterialStats(null,0),
            RecordLoadable.create(
                    SchoolTypeLoadable.SCHOOL.defaultField("school_type", null, true, EnvelopeMaterialStats::stat),
                    FloatLoadable.ANY.defaultField("school_bonus", 0F, EnvelopeMaterialStats::school_bonus),
                    EnvelopeMaterialStats::new
            ));

    private static final String SCHOOL_BONUS =
            IMaterialStats.makeTooltipKey(SakuraTinker.getResource("school_bonus"));

    private static final List<Component> DESCRIPTION = ImmutableList.of(
            IMaterialStats.makeTooltip(SakuraTinker.getResource("school_bonus"))
    );

    @Override
    public @NotNull MaterialStatType<?> getType() {
        return TYPE;
    }

    @Override
    public @NotNull List<Component> getLocalizedInfo() {
        List<Component> info = Lists.newArrayList();

        info.add(Component.literal(stat.getId().toString()));
        info.add(IToolStat.formatColoredBonus(SCHOOL_BONUS, this.school_bonus));

        return info;
    }

    @Override
    public @NotNull List<Component> getLocalizedDescriptions() {
        return DESCRIPTION;
    }

    @Override
    public void apply(@NotNull ModifierStatsBuilder builder, float scale) {
        ISSToolStats.SCHOOL_BONUS.update(builder, school_bonus * scale);
    }


    public SchoolType getSchool() {
        return this.stat;
    }
    public float school_bonus() {
        return this.school_bonus;
    }
}
