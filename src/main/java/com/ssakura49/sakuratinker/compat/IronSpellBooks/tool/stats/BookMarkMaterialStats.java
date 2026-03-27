package com.ssakura49.sakuratinker.compat.IronSpellBooks.tool.stats;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.ssakura49.sakuratinker.SakuraTinker;
import com.ssakura49.sakuratinker.compat.IronSpellBooks.ISSToolStats;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import slimeknights.mantle.data.loadable.primitive.FloatLoadable;
import slimeknights.mantle.data.loadable.primitive.IntLoadable;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.tconstruct.library.materials.stats.IMaterialStats;
import slimeknights.tconstruct.library.materials.stats.MaterialStatType;
import slimeknights.tconstruct.library.materials.stats.MaterialStatsId;
import slimeknights.tconstruct.library.tools.stat.IToolStat;
import slimeknights.tconstruct.library.tools.stat.ModifierStatsBuilder;

import java.util.List;

//书签
public record BookMarkMaterialStats(float percent_bonus, int spell_slot) implements IMaterialStats {
    public static final MaterialStatsId ID =
            new MaterialStatsId(SakuraTinker.MODID, "book_mark");

    public static final MaterialStatType<BookMarkMaterialStats> TYPE =
            new MaterialStatType<>(ID,
                    new BookMarkMaterialStats(0F, 0),
                    RecordLoadable.create(
                            FloatLoadable.PERCENT.defaultField(
                                    "percent_bonus",
                                    0F,
                                    BookMarkMaterialStats::percent_bonus
                            ),
                            IntLoadable.FROM_ZERO.defaultField(
                                    "spell_slot",
                                    0,
                                    BookMarkMaterialStats::spell_slot
                            ),
                            BookMarkMaterialStats::new
                    )
            );

    private static final String PERCENT_BONUS =
            IMaterialStats.makeTooltipKey(SakuraTinker.getResource("percent_bonus"));

    private static final String SPELL_SLOT =
            IMaterialStats.makeTooltipKey(SakuraTinker.getResource("spell_slot"));

    private static final List<Component> DESCRIPTION = ImmutableList.of(
            IMaterialStats.makeTooltip(SakuraTinker.getResource("percent_bonus")),
            IMaterialStats.makeTooltip(SakuraTinker.getResource("spell_slot"))
    );

    @Override
    public @NotNull MaterialStatType<?> getType() {
        return TYPE;
    }

    @Override
    public @NotNull List<Component> getLocalizedInfo() {
        List<Component> info = Lists.newArrayList();
        info.add(IToolStat.formatColoredPercentBoost(PERCENT_BONUS, this.percent_bonus));
        info.add(IToolStat.formatColoredBonus(SPELL_SLOT, this.spell_slot));
        return info;
    }

    @Override
    public @NotNull List<Component> getLocalizedDescriptions() {
        return DESCRIPTION;
    }

    @Override
    public void apply(@NotNull ModifierStatsBuilder builder, float scale) {
        ISSToolStats.PERCENT_BONUS.update(builder, percent_bonus * scale);
        ISSToolStats.SPELL_SLOT.update(builder, spell_slot * scale);
    }
}
