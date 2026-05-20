package com.ssakura49.sakuratinker.data.generator.enums;

import com.ssakura49.sakuratinker.compat.IronSpellBooks.ISSCompat;
import slimeknights.tconstruct.library.materials.definition.MaterialId;
import slimeknights.tconstruct.tools.data.material.MaterialIds;

import static com.ssakura49.sakuratinker.data.generator.enums.EnumTconMaterialModifier.*;

public enum EnumTconMaterial {
    bone(EnumTconExtraStat.bone, MaterialIds.bone),
    cobalt(EnumTconExtraStat.cobalt, MaterialIds.cobalt, cobalt_m),
    copper(EnumTconExtraStat.copper, MaterialIds.copper),
    manyullyn(EnumTconExtraStat.manyullyn, MaterialIds.manyullyn),
    wood(EnumTconExtraStat.wood, MaterialIds.wood),
    skyslime_vine(EnumTconExtraStat.skyslime_vine, MaterialIds.skyslimeVine),
    nahualt(EnumTconExtraStat.nahualt, MaterialIds.nahuatl),
    iron(EnumTconExtraStat.iron, MaterialIds.iron),
    string(EnumTconExtraStat.string, MaterialIds.string),
    bamboo(EnumTconExtraStat.bamboo, MaterialIds.bamboo),
    obsidian(EnumTconExtraStat.obsidian, MaterialIds.obsidian),
    gold(EnumTconExtraStat.gold, MaterialIds.gold),
    blazingBone(EnumTconExtraStat.blazingBone,MaterialIds.blazingBone),
    blazewood(EnumTconExtraStat.blazewood,MaterialIds.blazewood),
    ice(EnumTconExtraStat.ice,MaterialIds.ice,ice_m),
    witherBone(EnumTconExtraStat.witherBone,MaterialIds.necroticBone,witherBone_m),
    venomBone(EnumTconExtraStat.venomBone,MaterialIds.venombone,venomBone_m)
    ;
    public final EnumTconExtraStat stats;
    public final EnumTconMaterialModifier[] modifiers;
    public final MaterialId id;

    EnumTconMaterial(EnumTconExtraStat stats, MaterialId id, EnumTconMaterialModifier... modifiers) {
        this.stats = stats;
        this.modifiers = modifiers;
        this.id = id;
    }
}
