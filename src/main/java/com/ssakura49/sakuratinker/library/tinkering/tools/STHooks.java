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
import slimeknights.mantle.data.registry.IdAwareComponentRegistry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.module.ModuleHook;

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
    public static final ModuleHook<ModifyDamageSourceModifierHook> MODIFY_DAMAGE_SOURCE;
    public static final ModuleHook<LeftClickModifierHook> LEFT_CLICK;
    public static final ModuleHook<CriticalAttackModifierHook> CRITICAL_ATTACK;
    public static final ModuleHook<CauseDamageModifierHook> CAUSE_DAMAGE;
    public static final ModuleHook<ReplaceMaterialModifierHook> REPLACE_MATERIAL_MODIFIER;
    public static final ModuleHook<KeyPressModifierHook> KEY_PRESS;

    public static final ModuleHook<BulletAmmoModifierHook> BULLET_AMMO;

    public STHooks(){}

    static {
        WEARER_DAMAGE_PRE = ModifierHooks.register(SakuraTinker.getResource("wearer_damage_pre"), WearerDamagePreHook.class, WearerDamagePreHook.AllMerger::new, new WearerDamagePreHook() {});
        WEARER_KNOCK_BACK = ModifierHooks.register(SakuraTinker.getResource("wearer_knock_back"), WearerKnockBackHook.class, WearerKnockBackHook.AllMerger::new, new WearerKnockBackHook() {});
        WEARER_DAMAGE_TAKE = ModifierHooks.register(SakuraTinker.getResource("wearer_damage_take"), WearerDamageTakeHook.class, WearerDamageTakeHook.AllMerger::new, new WearerDamageTakeHook() {});
        WEARER_TAKE_HEAL = ModifierHooks.register(SakuraTinker.getResource("wearer_take_heal"), WearerTakeHealHook.class, WearerTakeHealHook.AllMerger::new, new WearerTakeHealHook() {});
        GENERIC_COMBAT = ModifierHooks.register(SakuraTinker.getResource("generic_combat"), GenericCombatModifierHook.class, GenericCombatModifierHook.AllMerge::new, new GenericCombatModifierHook() {});
        MELEE_COOLDOWN = ModifierHooks.register(SakuraTinker.getResource("melee_cooldown"), MeleeCooldownModifierHook.class, MeleeCooldownModifierHook.AllMerger::new, new MeleeCooldownModifierHook() {});
        HOLDER_DAMAGE_TAKE = ModifierHooks.register(SakuraTinker.getResource("holder_damage_take"), HolderDamageTakeModifierHook.class, HolderDamageTakeModifierHook.AllMerge::new, new HolderDamageTakeModifierHook() {});
        SHIELD_BLOCKING = ModifierHooks.register(SakuraTinker.getResource("shield_blocking"), ShieldBlockingModifierHook.class, ShieldBlockingModifierHook.AllMerge::new, new ShieldBlockingModifierHook() {});
        MODIFY_DAMAGE_SOURCE = ModifierHooks.register(SakuraTinker.getResource("modify_damage_source"), ModifyDamageSourceModifierHook.class, ModifyDamageSourceModifierHook.AllMerger::new, new ModifyDamageSourceModifierHook() {});
        LEFT_CLICK = ModifierHooks.register(SakuraTinker.getResource("left_click"), LeftClickModifierHook.class, LeftClickModifierHook.AllMerger::new, new LeftClickModifierHook() {});
        CRITICAL_ATTACK = ModifierHooks.register(SakuraTinker.getResource("critical_attack"), CriticalAttackModifierHook.class, CriticalAttackModifierHook.FirstMerger::new,(tool, entry, attacker, hand, target, sourceSlot, isFullyCharged, isExtraAttack, isCritical)->isCritical);
        CAUSE_DAMAGE = ModifierHooks.register(SakuraTinker.getResource("cause_damage"), CauseDamageModifierHook.class, CauseDamageModifierHook.AllMerger::new, (tool, modifier, event, attacker, target, baseDamage, currentDamage) -> currentDamage);
        REPLACE_MATERIAL_MODIFIER = ModifierHooks.register(SakuraTinker.getResource("replace_material_modifier"), ReplaceMaterialModifierHook.class, ReplaceMaterialModifierHook.AllMerger::new, ((context, inputIndex, secondary) -> false));
        KEY_PRESS = ModifierHooks.register(SakuraTinker.getResource("key_press"), KeyPressModifierHook.class, KeyPressModifierHook.AllMerger::new, new KeyPressModifierHook() {});
        BULLET_AMMO = ModifierHooks.register(SakuraTinker.getResource("bullet_ammo"), BulletAmmoModifierHook.class, BulletAmmoModifierHook.EMPTY);

    }
}
