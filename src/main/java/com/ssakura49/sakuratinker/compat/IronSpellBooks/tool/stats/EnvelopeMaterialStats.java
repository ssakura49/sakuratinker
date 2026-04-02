package com.ssakura49.sakuratinker.compat.IronSpellBooks.tool.stats;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.ssakura49.sakuratinker.SakuraTinker;
import com.ssakura49.sakuratinker.compat.IronSpellBooks.ISSToolStats;
import com.ssakura49.sakuratinker.compat.IronSpellBooks.helper.SchoolTypeLoadable;
import com.ssakura49.sakuratinker.compat.IronSpellBooks.stat.SchoolToolStat;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.spells.SchoolType;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import slimeknights.mantle.data.loadable.mapping.MapLoadable;
import slimeknights.mantle.data.loadable.primitive.FloatLoadable;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.tconstruct.library.materials.stats.IMaterialStats;
import slimeknights.tconstruct.library.materials.stats.MaterialStatType;
import slimeknights.tconstruct.library.materials.stats.MaterialStatsId;
import slimeknights.tconstruct.library.tools.stat.IToolStat;
import slimeknights.tconstruct.library.tools.stat.ModifierStatsBuilder;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

//封皮
public record EnvelopeMaterialStats(Map<SchoolType, Float> schoolBonuses) implements IMaterialStats {
    public static final MaterialStatsId ID = new MaterialStatsId(SakuraTinker.MODID, "envelope");

    public static final MaterialStatType<EnvelopeMaterialStats> TYPE =
            new MaterialStatType<>(ID,
                    new EnvelopeMaterialStats(Map.of()),
                    RecordLoadable.create(
                            SchoolTypeLoadable.SCHOOL_BONUS_MAP.defaultField(
                                    "school_bonus",
                                    Map.of(),
                                    EnvelopeMaterialStats::schoolBonuses
                            ),
                            EnvelopeMaterialStats::new
                    )
            );

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
        for (var entry : schoolBonuses.entrySet()) {
            SchoolType school = entry.getKey();
            float bonus = entry.getValue();
            info.add(Component.literal(school.getId().toString()));
            info.add(IToolStat.formatColoredBonus(SCHOOL_BONUS, bonus));
        }

        return info;
    }

    @Override
    public @NotNull List<Component> getLocalizedDescriptions() {
        return DESCRIPTION;
    }

    @Override
    public void apply(@NotNull ModifierStatsBuilder builder, float scale) {
        for (var entry : schoolBonuses.entrySet()) {
            SchoolType school = entry.getKey();
            float bonus = entry.getValue();

            ISSToolStats.SCHOOL_BONUS.get(school)
                    .update(builder, bonus * scale);
        }
    }
    public Map<SchoolType, Float> getSchoolBonuses() {
        return this.schoolBonuses;
    }

    public float getBonus(SchoolType school) {
        return this.schoolBonuses.getOrDefault(school, 0f);
    }
}
