package com.ssakura49.sakuratinker.library.tinkering.tools;

import com.ssakura49.sakuratinker.SakuraTinker;
import com.ssakura49.sakuratinker.common.tools.stats.*;
import com.ssakura49.sakuratinker.compat.IronSpellBooks.tool.stats.ManuScriptMaterialStats;
import slimeknights.tconstruct.library.materials.IMaterialRegistry;
import slimeknights.tconstruct.library.materials.MaterialRegistry;
import slimeknights.tconstruct.library.materials.stats.MaterialStatsId;

public class STMaterialStats {
    public static final MaterialStatsId CHARM = new MaterialStatsId(SakuraTinker.getResource("charm"));
    public static final MaterialStatsId LASER_GUN = new MaterialStatsId(SakuraTinker.getResource("laser_gun"));
    public static final MaterialStatsId TINKER_ARROW = new MaterialStatsId(SakuraTinker.getResource("tinker_arrow"));
    //public static final MaterialStatsId TINKER_SPELL_BOOK = new MaterialStatsId(SakuraTinker.getResource("tinker_spell_book"));
    public static final MaterialStatsId POWER_BANK = new MaterialStatsId(SakuraTinker.getResource("power_bank"));
    public static final MaterialStatsId BATTLE_FLAG = new MaterialStatsId(SakuraTinker.getResource("battle_flag"));
//    public static final MaterialStatsId FOX_MASK = new MaterialStatsId(SakuraTinker.location("fox_mask"));
    public static final MaterialStatsId FIRST_FRACTAL =new MaterialStatsId(SakuraTinker.getResource("first_fractal"));
    public static final MaterialStatsId YO_YO = new MaterialStatsId(SakuraTinker.getResource("yo_yo"));
    public static final MaterialStatsId TINKER_WAND = new MaterialStatsId(SakuraTinker.getResource("tinker_wand"));
    public static final MaterialStatsId ALCHEMICAL_GLOVES = new MaterialStatsId(SakuraTinker.getResource("alchemical_gloves"));
    public static final MaterialStatsId RANGE = new MaterialStatsId(SakuraTinker.getResource("range"));

    public STMaterialStats(){}

    public static void init() {
        IMaterialRegistry registry = MaterialRegistry.getInstance();
        registry.registerStatType(CharmChainMaterialStats.TYPE, CHARM);
        registry.registerStatType(STStatlessMaterialStats.CHARM_CORE.getType(), CHARM);
        registry.registerStatType(LaserMediumMaterialStats.TYPE, LASER_GUN);
        registry.registerStatType(EnergyUnitMaterialStats.TYPE, LASER_GUN);
        registry.registerStatType(FletchingMaterialStats.TYPE, TINKER_ARROW);
        //registry.registerStatType(ManuScriptMaterialStats.TYPE, TINKER_SPELL_BOOK);
//        for(MaterialStatType<?> type : EmbeddedArmorMaterialStats.TYPES) {
//            registry.registerStatType(type, MaterialRegistry.ARMOR);
//        }

        registry.registerStatType(STStatlessMaterialStats.SHELL.getType(), POWER_BANK);
        //registry.registerStatType(STStatlessMaterialStats.GUTTER.getType(), POWER_BANK);

        registry.registerStatType(BattleFlagMaterialStats.TYPE, BATTLE_FLAG);
//        registry.registerStatType(STStatlessMaterialStats.FOX_MASK_MAIN.getType(), FOX_MASK);
//        registry.registerStatType(STStatlessMaterialStats.FOX_MASK_CORE.getType(), FOX_MASK);
        registry.registerStatType(PhantomCoreMaterialStats.TYPE, FIRST_FRACTAL);
        registry.registerStatType(YoYoRingMaterialStats.TYPE, YO_YO);
        registry.registerStatType(AxleMaterialStats.TYPE, YO_YO);
        registry.registerStatType(ChordMaterialStats.TYPE, YO_YO);
        registry.registerStatType(SoulGathererMaterialStats.TYPE, TINKER_WAND);
        registry.registerStatType(AlchemicalCoreMaterialStats.TYPE, ALCHEMICAL_GLOVES);
        registry.registerStatType(RangeMaterialStats.TYPE, RANGE);
    }
}
