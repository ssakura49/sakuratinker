package com.ssakura49.sakuratinker.compat.IronSpellBooks.tool.stats;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.ssakura49.sakuratinker.compat.IronSpellBooks.ISSToolStats;
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

//手稿
public record ManuScriptMaterialStats(float mana_reduce, float mana_regen, float cast_time_reduce, float spell_damage, float mana_value) implements IMaterialStats {
    public static final MaterialStatsId ID = new MaterialStatsId(SakuraTinker.MODID, "mana_script");
    public static final MaterialStatType<ManuScriptMaterialStats> TYPE = new MaterialStatType<>(ID,
            new ManuScriptMaterialStats(0,0,0,0,0),
            RecordLoadable.create(
                    FloatLoadable.ANY.defaultField("mana_reduce", 0F, ManuScriptMaterialStats::mana_reduce),
                    FloatLoadable.ANY.defaultField("mana_regen", 0F, ManuScriptMaterialStats::mana_regen),
                    FloatLoadable.ANY.defaultField("cast_time_reduce", 0F, ManuScriptMaterialStats::cast_time_reduce),
                    FloatLoadable.ANY.defaultField("spell_damage", 0F, ManuScriptMaterialStats::spell_damage),
                    FloatLoadable.ANY.defaultField("mana_value", 0F, ManuScriptMaterialStats::mana_value),
                    ManuScriptMaterialStats::new
            ));

    private static final String MANA_REDUCE = IMaterialStats.makeTooltipKey(SakuraTinker.getResource("mana_reduce"));
    private static final String MANA_REGEN = IMaterialStats.makeTooltipKey(SakuraTinker.getResource("mana_regen"));
    private static final String CAST_TIME_REDUCE = IMaterialStats.makeTooltipKey(SakuraTinker.getResource("cast_time_reduce"));
    private static final String SPELL_DAMAGE = IMaterialStats.makeTooltipKey(SakuraTinker.getResource("spell_damage"));
    private static final String MANA_VALUE = IMaterialStats.makeTooltipKey(SakuraTinker.getResource("mana_value"));

    private static final List<Component> DESCRIPTION = ImmutableList.of(
            IMaterialStats.makeTooltip(SakuraTinker.getResource("mana_reduce")),
            IMaterialStats.makeTooltip(SakuraTinker.getResource("mana_regen")),
            IMaterialStats.makeTooltip(SakuraTinker.getResource("cast_time_reduce")),
            IMaterialStats.makeTooltip(SakuraTinker.getResource("spell_damage")),
            IMaterialStats.makeTooltip(SakuraTinker.getResource("mana_value"))
    );

    @Override
    public @NotNull MaterialStatType<?> getType() {
        return TYPE;
    }

    public @NotNull List<Component> getLocalizedInfo() {
        List<Component> info = Lists.newArrayList();
        info.add(IToolStat.formatColoredBonus(MANA_REDUCE, this.mana_reduce));
        info.add(IToolStat.formatColoredBonus(MANA_REGEN, this.mana_regen));
        info.add(IToolStat.formatColoredBonus(CAST_TIME_REDUCE, this.cast_time_reduce));
        info.add(IToolStat.formatColoredBonus(SPELL_DAMAGE, this.spell_damage));
        info.add(IToolStat.formatColoredBonus(MANA_VALUE, this.mana_value));
        return info;
    }

    @Override
    public List<Component> getLocalizedDescriptions() {
        return DESCRIPTION;
    }

    @Override
    public void apply(@NotNull ModifierStatsBuilder builder, float scale) {
        ISSToolStats.MANA_REDUCE.update(builder, mana_reduce * scale);
        ISSToolStats.MANA_REGEN.update(builder, mana_regen * scale);
        ISSToolStats.CAST_TIME_REDUCE.update(builder, cast_time_reduce * scale);
        ISSToolStats.SPELL_DAMAGE.update(builder, spell_damage * scale);
        ISSToolStats.MANA_VALUE.update(builder, mana_value * scale);
    }


    public float mana_reduce() {
        return this.mana_reduce;
    }
    public float mana_regen() {
        return this.mana_regen;
    }
    public float cast_time_reduce() {
        return this.cast_time_reduce;
    }
    public float spell_damage() {
        return this.spell_damage;
    }
    public float mana_value() {
        return this.mana_value;
    }
}
