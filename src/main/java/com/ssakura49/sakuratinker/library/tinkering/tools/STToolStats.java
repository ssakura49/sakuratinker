package com.ssakura49.sakuratinker.library.tinkering.tools;

import com.ssakura49.sakuratinker.SakuraTinker;
import slimeknights.tconstruct.library.tools.stat.FloatToolStat;
import slimeknights.tconstruct.library.tools.stat.ToolStatId;
import slimeknights.tconstruct.library.tools.stat.ToolStats;

public class STToolStats {
    public static final FloatToolStat MOVEMENT_SPEED = (FloatToolStat) ToolStats.register(new FloatToolStat(name("movement_speed"), -8871731, 0.0F, 0.0F, 20480.0F));
    public static final FloatToolStat ARMOR = (FloatToolStat)ToolStats.register(new FloatToolStat(name("armor"), -8042548, 0.0F, 0.0F, 300.0F));
    public static final FloatToolStat ARMOR_TOUGHNESS = (FloatToolStat)ToolStats.register(new FloatToolStat(name("armor_toughness"), -8042548, 0.0F, 0.0F, 300.0F));
    public static final FloatToolStat ATTACK_DAMAGE = (FloatToolStat)ToolStats.register(new FloatToolStat(name("damage"), -2661276, 0.0F, 0.0F, 20480.0F));
    public static final FloatToolStat ARROW_DAMAGE = (FloatToolStat)ToolStats.register(new FloatToolStat(name("arrow_damage"), -2661276, 0.0F, 0.0F, 20480.0F));
    public static final FloatToolStat COOLDOWN = (FloatToolStat)ToolStats.register(new FloatToolStat(name("cooldown"), -10887823, 1.0F, 0.0F, (float)Integer.MAX_VALUE));
    public static final FloatToolStat RANGE = (FloatToolStat)ToolStats.register(new FloatToolStat(name("range"), -3135232, 1.0F, 1.0F, (float)Integer.MAX_VALUE));
    public static final FloatToolStat MAX_HEALTH = (FloatToolStat) ToolStats.register(new FloatToolStat(name("health"), -2661276, 0.0F, 0.0F, 20480.0F));
    public static final FloatToolStat ENERGY_STORAGE = (FloatToolStat)ToolStats.register(new FloatToolStat(name("energy_storage"), -3135232, 0.0F, 0.0F, (float)Integer.MAX_VALUE));
    public static final FloatToolStat BASE_SPELL_DAMAGE = (FloatToolStat) ToolStats.register(new FloatToolStat(name("base_spell_damage"), -2661276, 0.0F, 0.0F, (float) Integer.MAX_VALUE));
    public static final FloatToolStat SPELL_POWER = (FloatToolStat) ToolStats.register(new FloatToolStat(name("spell_power"), -2661276, 0.0F, 0.0F, 20480.0F));
    public static final FloatToolStat SPELL_REDUCE = (FloatToolStat) ToolStats.register(new FloatToolStat(name("spell_reduce"), -2661276, 0.0F, 0.0F, 20480.0F));
    public static final FloatToolStat CAST_TIME = (FloatToolStat) ToolStats.register(new FloatToolStat(name("cast_time"), -10887823, 1.0F, 0.0F, (float)Integer.MAX_VALUE));
    public static final FloatToolStat ATTACK_BUFF_TIME = (FloatToolStat) ToolStats.register(new FloatToolStat(name("attack_buff_time"), -8042548, 1.0F, 0.0F, (float)Integer.MAX_VALUE));
    public static final FloatToolStat DEFENCE_BUFF_TIME = (FloatToolStat) ToolStats.register(new FloatToolStat(name("defence_buff_time"), -8042548, 1.0F, 0.0F, (float)Integer.MAX_VALUE));
    public static final FloatToolStat CHARGING_TIME = (FloatToolStat) ToolStats.register(new FloatToolStat(name("charging_time"), -8042548, 1.0F, 0.0F, (float)Integer.MAX_VALUE));
    public static final FloatToolStat PHANTOM_AMOUNT = (FloatToolStat) ToolStats.register(new FloatToolStat(name("phantom_amount"),-3135232, 1.0F, 1.0F, 32.0F));
    public static final FloatToolStat EMBER_STORAGE = (FloatToolStat)ToolStats.register(new FloatToolStat(name("ember_storage"), -2661276, 0.0F, 0.0F, (float)Integer.MAX_VALUE));
    public static final FloatToolStat TIME = (FloatToolStat) ToolStats.register(new FloatToolStat(name("time"), -10887823, 100F, 0.0F, (float) Integer.MAX_VALUE));
    public static final FloatToolStat MAX_COLLECTED = (FloatToolStat) ToolStats.register(new FloatToolStat(name("max_collected"), -8042548, 1, 0, (float) Integer.MAX_VALUE));
    public static final FloatToolStat ATTACK_INTERVAL = (FloatToolStat) ToolStats.register(new FloatToolStat(name("attack_interval"), -2661276, 10.0F, 0.01F, (float) Integer.MAX_VALUE));
    public static final FloatToolStat WEIGHT = (FloatToolStat) ToolStats.register(new FloatToolStat(name("weight"), -8042548, 5.0F, 0.1F, 64.0F));
    public static final FloatToolStat LENGTH = (FloatToolStat) ToolStats.register(new FloatToolStat(name("length"), -8042548, 2.0F, 1.0F, (float) Integer.MAX_VALUE));
    public static final FloatToolStat SOUL_POWER = (FloatToolStat) ToolStats.register(new FloatToolStat(name("soul_power"), -8143881, 0.0F, 0.0F, (float) Integer.MAX_VALUE));
    public static final FloatToolStat SOUL_INCREASE = (FloatToolStat) ToolStats.register(new FloatToolStat(name("soul_increase"), -8143881, 1.0F, 0.0F, (float) Integer.MAX_VALUE));
    public static final FloatToolStat PENETRATION = (FloatToolStat)ToolStats.register(new FloatToolStat(name("penetration"), -2661276, 3.0F, 1.0F, 256.0F));
    public static final FloatToolStat FLUID_DAMAGE = (FloatToolStat)ToolStats.register(new FloatToolStat(name("fluid_damage"), -2661276, 0.0F, 0.0F, 20480.0F));


    public STToolStats() {
    }

    public static void register() {
    }

    private static ToolStatId name(String name) {
        return new ToolStatId(SakuraTinker.MODID, name);
    }
}
