package com.ssakura49.sakuratinker.library.tinkering.tools;

import com.ssakura49.sakuratinker.SakuraTinker;
import com.ssakura49.sakuratinker.library.hooks.armor.WearerDamagePreHook;
import com.ssakura49.sakuratinker.library.hooks.armor.WearerDamageTakeHook;
import com.ssakura49.sakuratinker.library.hooks.armor.WearerKnockBackHook;
import com.ssakura49.sakuratinker.library.hooks.armor.WearerTakeHealHook;
import com.ssakura49.sakuratinker.library.hooks.builder.ReplaceMaterialModifierHook;
import com.ssakura49.sakuratinker.library.hooks.bullet.BulletAmmoModifierHook;
import com.ssakura49.sakuratinker.library.hooks.click.KeyPressModifierHook;
import com.ssakura49.sakuratinker.library.hooks.click.LeftClickModifierHook;
import com.ssakura49.sakuratinker.library.hooks.combat.*;
import com.ssakura49.sakuratinker.library.hooks.curio.armor.CurioEquipmentChangeModifierHook;
import com.ssakura49.sakuratinker.library.hooks.curio.armor.CurioTakeDamagePreModifierHook;
import com.ssakura49.sakuratinker.library.hooks.curio.behavior.*;
import com.ssakura49.sakuratinker.library.hooks.curio.combat.*;
import com.ssakura49.sakuratinker.library.hooks.curio.mining.CurioBreakSpeedModifierHook;
import com.ssakura49.sakuratinker.library.hooks.curio.interation.CurioInventoryTickModifierHook;
import com.ssakura49.sakuratinker.library.hooks.curio.ranged.CurioArrowHitHook;
import com.ssakura49.sakuratinker.library.hooks.curio.ranged.CurioArrowShootHook;
import com.ssakura49.sakuratinker.library.hooks.curio.armor.CurioTakeDamagePostModifierHook;
import slimeknights.mantle.data.registry.IdAwareComponentRegistry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.module.ModuleHook;

import javax.swing.plaf.PanelUI;

public class STHooks {
    public static final IdAwareComponentRegistry<ModuleHook<?>> LOADER = new IdAwareComponentRegistry<>("Modifier Hook");
    public static final ModuleHook<WearerDamagePreHook> WEARER_DAMAGE_PRE;
    public static final ModuleHook<WearerKnockBackHook> WEARER_KNOCK_BACK;
    public static final ModuleHook<WearerDamageTakeHook> WEARER_DAMAGE_TAKE;
    public static final ModuleHook<WearerTakeHealHook> WEARER_TAKE_HEAL;
    public static final ModuleHook<GenericCombatModifierHook> GENERIC_COMBAT;
    public static final ModuleHook<MeleeCooldownModifierHook> MELEE_COOLDOWN;
    public static final ModuleHook<HolderDamageTakeModifierHook> HOLDER_DAMAGE_TAKE;
    public static final ModuleHook<ShieldBlockingModifierHook> SHIELD_BLOCKING;
    public static final ModuleHook<CurioFortuneModifierHook> CURIO_FORTUNE;
    public static final ModuleHook<CurioEquipmentChangeModifierHook> CURIO_EQUIPMENT_CHANGE;
    public static final ModuleHook<CurioInventoryTickModifierHook> CURIO_TICK;
    public static final ModuleHook<CurioLootingModifierHook> CURIO_LOOTING;
    public static final ModuleHook<CurioAttributeModifierHook> CURIO_ATTRIBUTE;
    public static final ModuleHook<CurioGetToolDamageModifierHook> CURIO_TOOL_DAMAGE;
    public static final ModuleHook<CurioTakeHealModifierHook> CURIO_TAKE_HEAL;
    public static final ModuleHook<CurioBreakSpeedModifierHook> CURIO_BREAK_SPEED;
    public static final ModuleHook<CurioTakeDamagePreModifierHook> CURIO_TAKE_DAMAGE_PRE;
    public static final ModuleHook<CurioTakeDamagePostModifierHook> CURIO_TAKE_DAMAGE_POST;
    public static final ModuleHook<CurioArrowShootHook> CURIO_ARROW_SHOOT;
    public static final ModuleHook<CurioArrowHitHook> CURIO_ARROW_HIT;
    public static final ModuleHook<CurioDamageTargetPreModifierHook> CURIO_DAMAGE_TARGET_PRE;
    public static final ModuleHook<CurioDamageTargetPostModifierHook> CURIO_DAMAGE_TARGET_POST;
    public static final ModuleHook<CurioCalculateDamageModifierHook> CURIO_CALCULATE_DAMAGE;
    public static final ModuleHook<CurioKillTargetModifierHook> CURIO_KILL_TARGET;
    public static final ModuleHook<ModifyDamageSourceModifierHook> MODIFY_DAMAGE_SOURCE;
    public static final ModuleHook<LeftClickModifierHook> LEFT_CLICK;
    public static final ModuleHook<CriticalAttackModifierHook> CRITICAL_ATTACK;
    public static final ModuleHook<CauseDamageModifierHook> CAUSE_DAMAGE;
    public static final ModuleHook<ReplaceMaterialModifierHook> REPLACE_MATERIAL_MODIFIER;
    public static final ModuleHook<KeyPressModifierHook> KEY_PRESS;
    public static final ModuleHook<CurioDropRuleModifierHook> CURIO_DROP_RULE;

    public static final ModuleHook<BulletAmmoModifierHook> BULLET_AMMO;

    public STHooks(){}

    static {
        WEARER_DAMAGE_PRE = ModifierHooks.register(SakuraTinker.location("wearer_damage_pre"), WearerDamagePreHook.class, WearerDamagePreHook.AllMerger::new, new WearerDamagePreHook() {});
        WEARER_KNOCK_BACK = ModifierHooks.register(SakuraTinker.location("wearer_knock_back"), WearerKnockBackHook.class, WearerKnockBackHook.AllMerger::new, new WearerKnockBackHook() {});
        WEARER_DAMAGE_TAKE = ModifierHooks.register(SakuraTinker.location("wearer_damage_take"), WearerDamageTakeHook.class, WearerDamageTakeHook.AllMerger::new, new WearerDamageTakeHook() {});
        WEARER_TAKE_HEAL = ModifierHooks.register(SakuraTinker.location("wearer_take_heal"), WearerTakeHealHook.class, WearerTakeHealHook.AllMerger::new, new WearerTakeHealHook() {});
        GENERIC_COMBAT = ModifierHooks.register(SakuraTinker.location("generic_combat"), GenericCombatModifierHook.class, GenericCombatModifierHook.AllMerge::new, new GenericCombatModifierHook() {});
        MELEE_COOLDOWN = ModifierHooks.register(SakuraTinker.location("melee_cooldown"), MeleeCooldownModifierHook.class, MeleeCooldownModifierHook.AllMerger::new, new MeleeCooldownModifierHook() {});
        HOLDER_DAMAGE_TAKE = ModifierHooks.register(SakuraTinker.location("holder_damage_take"), HolderDamageTakeModifierHook.class, HolderDamageTakeModifierHook.AllMerge::new, new HolderDamageTakeModifierHook() {});
        SHIELD_BLOCKING = ModifierHooks.register(SakuraTinker.location("shield_blocking"), ShieldBlockingModifierHook.class, ShieldBlockingModifierHook.AllMerge::new, new ShieldBlockingModifierHook() {});
        CURIO_FORTUNE = ModifierHooks.register(SakuraTinker.location("curio_fortune"), CurioFortuneModifierHook.class, CurioFortuneModifierHook.AllMerger::new, new CurioFortuneModifierHook() {});
        CURIO_EQUIPMENT_CHANGE = ModifierHooks.register(SakuraTinker.location("curio_equipment_change"), CurioEquipmentChangeModifierHook.class, CurioEquipmentChangeModifierHook.AllMerger::new, new CurioEquipmentChangeModifierHook() {});
        CURIO_TICK = ModifierHooks.register(SakuraTinker.location("curio_tick"), CurioInventoryTickModifierHook.class, CurioInventoryTickModifierHook.AllMerger::new, new CurioInventoryTickModifierHook() {});
        CURIO_LOOTING = ModifierHooks.register(SakuraTinker.location("curio_looting"), CurioLootingModifierHook.class, CurioLootingModifierHook.AllMerger::new, new CurioLootingModifierHook() {});
        CURIO_TOOL_DAMAGE = ModifierHooks.register(SakuraTinker.location("curio_tool_damage"), CurioGetToolDamageModifierHook.class, CurioGetToolDamageModifierHook.AllMerger::new, new CurioGetToolDamageModifierHook() {});
        CURIO_TAKE_HEAL = ModifierHooks.register(SakuraTinker.location("curio_take_heal"), CurioTakeHealModifierHook.class, CurioTakeHealModifierHook.AllMerger::new, new CurioTakeHealModifierHook() {});
        CURIO_BREAK_SPEED = ModifierHooks.register(SakuraTinker.location("curio_break_speed"), CurioBreakSpeedModifierHook.class, CurioBreakSpeedModifierHook.AllMerger::new, new CurioBreakSpeedModifierHook() {});
        CURIO_TAKE_DAMAGE_PRE = ModifierHooks.register(SakuraTinker.location("curio_take_damage_pre"), CurioTakeDamagePreModifierHook.class, CurioTakeDamagePreModifierHook.AllMerger::new, new CurioTakeDamagePreModifierHook() {});
        CURIO_TAKE_DAMAGE_POST = ModifierHooks.register(SakuraTinker.location("curio_take_damage_post"), CurioTakeDamagePostModifierHook.class, CurioTakeDamagePostModifierHook.AllMerger::new, new CurioTakeDamagePostModifierHook() {});
        CURIO_ARROW_SHOOT = ModifierHooks.register(SakuraTinker.location("curio_arrow_shoot"), CurioArrowShootHook.class, CurioArrowShootHook.AllMerger::new, new CurioArrowShootHook() {});
        CURIO_ARROW_HIT = ModifierHooks.register(SakuraTinker.location("curio_arrow_hit"), CurioArrowHitHook.class, CurioArrowHitHook.AllMerger::new, new CurioArrowHitHook() {});
        CURIO_ATTRIBUTE = ModifierHooks.register(SakuraTinker.location("curio_attribute"), CurioAttributeModifierHook.class, CurioAttributeModifierHook.AllMerger::new, new CurioAttributeModifierHook() {});
        CURIO_DAMAGE_TARGET_PRE = ModifierHooks.register(SakuraTinker.location("curio_damage_target_pre"), CurioDamageTargetPreModifierHook.class, CurioDamageTargetPreModifierHook.AllMerger::new, new CurioDamageTargetPreModifierHook() {});
        CURIO_DAMAGE_TARGET_POST = ModifierHooks.register(SakuraTinker.location("curio_damage_target_post"), CurioDamageTargetPostModifierHook.class, CurioDamageTargetPostModifierHook.AllMerger::new, new CurioDamageTargetPostModifierHook() {});
        CURIO_CALCULATE_DAMAGE = ModifierHooks.register(SakuraTinker.location("curio_calculate_damage"), CurioCalculateDamageModifierHook.class, CurioCalculateDamageModifierHook.AllMerger::new, new CurioCalculateDamageModifierHook() {});
        CURIO_KILL_TARGET = ModifierHooks.register(SakuraTinker.location("curio_kill_target"), CurioKillTargetModifierHook.class, CurioKillTargetModifierHook.AllMerger::new, new CurioKillTargetModifierHook() {});
        CURIO_DROP_RULE = ModifierHooks.register(SakuraTinker.location("curio_drop_rule"),CurioDropRuleModifierHook.class,CurioDropRuleModifierHook.AllMerger::new, new CurioDropRuleModifierHook(){});
        MODIFY_DAMAGE_SOURCE = ModifierHooks.register(SakuraTinker.location("modify_damage_source"), ModifyDamageSourceModifierHook.class, ModifyDamageSourceModifierHook.AllMerger::new, new ModifyDamageSourceModifierHook() {});
        LEFT_CLICK = ModifierHooks.register(SakuraTinker.location("left_click"), LeftClickModifierHook.class, LeftClickModifierHook.AllMerger::new, new LeftClickModifierHook() {});
        CRITICAL_ATTACK = ModifierHooks.register(SakuraTinker.location("critical_attack"), CriticalAttackModifierHook.class, CriticalAttackModifierHook.FirstMerger::new,(tool, entry, attacker, hand, target, sourceSlot, isFullyCharged, isExtraAttack, isCritical)->isCritical);
        CAUSE_DAMAGE = ModifierHooks.register(SakuraTinker.location("cause_damage"), CauseDamageModifierHook.class, CauseDamageModifierHook.AllMerger::new, (tool, modifier, event, attacker, target, baseDamage, currentDamage) -> currentDamage);
        REPLACE_MATERIAL_MODIFIER = ModifierHooks.register(SakuraTinker.location("replace_material_modifier"), ReplaceMaterialModifierHook.class, ReplaceMaterialModifierHook.AllMerger::new, ((context, inputIndex, secondary) -> false));
        KEY_PRESS = ModifierHooks.register(SakuraTinker.location("key_press"), KeyPressModifierHook.class, KeyPressModifierHook.AllMerger::new, new KeyPressModifierHook() {});
        BULLET_AMMO = ModifierHooks.register(SakuraTinker.location("bullet_ammo"), BulletAmmoModifierHook.class, BulletAmmoModifierHook.EMPTY);

    }
}
