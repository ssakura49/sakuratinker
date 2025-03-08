package com.ssakura49.sakuratinker.common.tinkering.modifiers;

import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import slimeknights.mantle.client.TooltipKey;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.armor.EquipmentChangeModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.armor.ModifyDamageModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.armor.OnAttackedModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.behavior.AttributesModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.behavior.ProcessLootModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.build.ModifierRemovalHook;
import slimeknights.tconstruct.library.modifiers.hook.build.ToolStatsModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.combat.DamageDealtModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.combat.MeleeDamageModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.combat.MeleeHitModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.display.TooltipModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.interaction.EntityInteractionModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.interaction.InteractionSource;
import slimeknights.tconstruct.library.modifiers.hook.interaction.InventoryTickModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.interaction.KeybindInteractModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.mining.BlockBreakModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.mining.BreakSpeedModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.ranged.BowAmmoModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.ranged.ProjectileHitModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.ranged.ProjectileLaunchModifierHook;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.context.EquipmentChangeContext;
import slimeknights.tconstruct.library.tools.context.EquipmentContext;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.context.ToolHarvestContext;
import slimeknights.tconstruct.library.tools.definition.module.mining.MiningSpeedToolHook;
import slimeknights.tconstruct.library.tools.nbt.IToolContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;
import slimeknights.tconstruct.library.tools.nbt.ModifierNBT;
import slimeknights.tconstruct.library.tools.stat.ModifierStatsBuilder;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Predicate;

public abstract class modifiermodule extends Modifier implements MeleeDamageModifierHook, MeleeHitModifierHook, DamageDealtModifierHook,
        BowAmmoModifierHook, ProjectileHitModifierHook, ProjectileLaunchModifierHook, KeybindInteractModifierHook, ProcessLootModifierHook,
        EquipmentChangeModifierHook, InventoryTickModifierHook, OnAttackedModifierHook, TooltipModifierHook, AttributesModifierHook,
        ModifyDamageModifierHook, ModifierRemovalHook, BlockBreakModifierHook, EntityInteractionModifierHook, ToolStatsModifierHook,
        BreakSpeedModifierHook {
    @Override
    protected void registerHooks(ModuleHookMap.Builder builder) {
        super.registerHooks(builder);
        builder.addHook(this,
                ModifierHooks.DAMAGE_DEALT,
                ModifierHooks.MELEE_DAMAGE,
                ModifierHooks.MELEE_HIT,
                ModifierHooks.BOW_AMMO,
                ModifierHooks.PROJECTILE_HIT,
                ModifierHooks.PROJECTILE_LAUNCH,
                ModifierHooks.EQUIPMENT_CHANGE,
                ModifierHooks.INVENTORY_TICK,
                ModifierHooks.ON_ATTACKED,
                ModifierHooks.TOOLTIP,
                ModifierHooks.REMOVE,
                ModifierHooks.MODIFY_DAMAGE,
                ModifierHooks.BLOCK_BREAK,
                ModifierHooks.ENTITY_INTERACT,
                ModifierHooks.TOOL_STATS,
                ModifierHooks.ARMOR_INTERACT,
                ModifierHooks.ATTRIBUTES,
                ModifierHooks.PROCESS_LOOT
        );
    }

    public modifiermodule() {
        MinecraftForge.EVENT_BUS.addListener(this::LivingHurtEvent);
        MinecraftForge.EVENT_BUS.addListener(this::LivingAttackEvent);
        MinecraftForge.EVENT_BUS.addListener(this::LivingDamageEvent);
    }

    public void addAttributes(IToolStackView tool, ModifierEntry modifier, EquipmentSlot slot, BiConsumer<Attribute, AttributeModifier> consumer) {
    }

    public void processLoot(IToolStackView tool, ModifierEntry modifier, List<ItemStack> list, LootContext context) {
    }

    @Override
    public float getMeleeDamage(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float baseDamage, float damage) {
        return this.onGetMeleeDamage(tool, modifier, context, baseDamage, damage);
    }

    @Override
    public void onDamageDealt(IToolStackView tool, ModifierEntry modifier, EquipmentContext context, EquipmentSlot slotType, LivingEntity entity, DamageSource damageSource, float amount, boolean isDirectDamage) {
        this.modifierDamageDealt(tool, modifier, context, slotType, entity, damageSource, amount, isDirectDamage);
    }

    @Override
    public void onBreakSpeed(IToolStackView tool, ModifierEntry modifier, PlayerEvent.BreakSpeed event, Direction sideHit, boolean isEffective, float miningSpeedModifier) {
        this.modifyBreakSpeed(tool, modifier, event, sideHit, isEffective, miningSpeedModifier);
    }

    public void modifyBreakSpeed(IToolStackView tool, ModifierEntry modifier, PlayerEvent.BreakSpeed event, Direction sideHit, boolean isEffective, float miningSpeedModifier) {
    }

    public float onGetMeleeDamage(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float baseDamage, float damage) {
        return damage;
    }

    public void modifierDamageDealt(IToolStackView tool, ModifierEntry modifier, EquipmentContext context, EquipmentSlot slotType, LivingEntity entity, DamageSource damageSource, float amount, boolean isDirectDamage) {
    }

    public boolean isNoLevels() {
        return false;
    }

    public @NotNull Component getDisplayName(int level) {
        return this.isNoLevels() ? super.getDisplayName() : super.getDisplayName(level);
    }

    @Override
    public ItemStack findAmmo(IToolStackView tool, ModifierEntry modifiers, LivingEntity livingEntity, ItemStack itemStack, Predicate<ItemStack> predicate) {
        return this.modifierFindAmmo(tool, modifiers, livingEntity, itemStack, predicate);
    }

    @Override
    public void onProjectileLaunch(IToolStackView tool, ModifierEntry modifiers, LivingEntity livingEntity, Projectile projectile, @Nullable AbstractArrow abstractArrow, ModDataNBT ModDataNBT, boolean primary) {
        this.modifierOnProjectileLaunch(tool, modifiers, livingEntity, projectile, abstractArrow, ModDataNBT, primary);
    }

    @Override
    public boolean onProjectileHitEntity(ModifierNBT modifiers, ModDataNBT persistentData, ModifierEntry modifier, Projectile projectile, EntityHitResult hit, @javax.annotation.Nullable LivingEntity attacker, @javax.annotation.Nullable LivingEntity target) {
        return this.modifierOnProjectileHitEntity(modifiers, persistentData, modifier, projectile, hit, attacker, target);
    }

    @Override
    public void onProjectileHitBlock(ModifierNBT modifiers, ModDataNBT persistentData, ModifierEntry modifier, Projectile projectile, BlockHitResult hit, @javax.annotation.Nullable LivingEntity attacker) {
        this.modifierOnProjectileHitBlock(modifiers, persistentData, modifier, projectile, hit, attacker);
    }

    public ItemStack modifierFindAmmo(IToolStackView tool, ModifierEntry modifiers, LivingEntity livingEntity, ItemStack itemStack, Predicate<ItemStack> predicate) {
        return itemStack;
    }

    public void modifierOnProjectileLaunch(IToolStackView tool, ModifierEntry modifiers, LivingEntity livingEntity, Projectile projectile, @Nullable AbstractArrow abstractArrow, ModDataNBT ModDataNBT, boolean primary) {
    }

    public boolean modifierOnProjectileHitEntity(ModifierNBT modifiers, ModDataNBT persistentData, ModifierEntry modifier, Projectile projectile, EntityHitResult hit, @javax.annotation.Nullable LivingEntity attacker, @javax.annotation.Nullable LivingEntity target) {
        return false;
    }

    public void modifierOnProjectileHitBlock(ModifierNBT modifiers, ModDataNBT persistentData, ModifierEntry modifier, Projectile projectile, BlockHitResult hit, @javax.annotation.Nullable LivingEntity attacker) {
    }

    @Override
    public void onEquip(IToolStackView tool, ModifierEntry modifier, EquipmentChangeContext context) {
        this.modifierOnEquip(tool, modifier, context);
    }

    @Override
    public void onUnequip(IToolStackView tool, ModifierEntry modifier, EquipmentChangeContext context) {
        this.modifierOnUnequip(tool, modifier, context);
    }

    @Override
    public void onInventoryTick(IToolStackView iToolStackView, ModifierEntry modifierEntry, Level level, LivingEntity entity, int index, boolean b, boolean b1, ItemStack itemStack) {
        this.modifierOnInventoryTick(iToolStackView, modifierEntry, level, entity, index, b, b1, itemStack);
    }

    @Override
    public void onAttacked(IToolStackView tool, ModifierEntry modifier, EquipmentContext context, EquipmentSlot slotType, DamageSource source, float amount, boolean isDirectDamage) {
        this.modifierOnAttacked(tool, modifier, context, slotType, source, amount, isDirectDamage);
    }

    @Override
    public float modifyDamageTaken(IToolStackView tool, ModifierEntry modifier, EquipmentContext context, EquipmentSlot slotType, DamageSource source, float amount, boolean isDirectDamage) {
        return this.modifierDamageTaken(tool, modifier, context, slotType, source, amount, isDirectDamage);
    }

    @Override
    public void afterBlockBreak(IToolStackView tool, ModifierEntry modifier, ToolHarvestContext context) {
        this.modifierAfterBlockBreak(tool, modifier, context);
    }

    @Override
    public InteractionResult beforeEntityUse(IToolStackView tool, ModifierEntry modifier, Player player, Entity target, InteractionHand hand, InteractionSource source) {
        return this.modifierBeforeEntityUse(tool, modifier, player, target, hand, source);
    }

    @Override
    public InteractionResult afterEntityUse(IToolStackView tool, ModifierEntry modifier, Player player, LivingEntity target, InteractionHand hand, InteractionSource source) {
        return this.modifierAfterEntityUse(tool, modifier, player, target, hand, source);
    }

    @Override
    public void addToolStats(IToolContext context, ModifierEntry modifier, ModifierStatsBuilder builder) {
        this.modifierAddToolStats(context, modifier, builder);
    }

    @Override
    public void afterMeleeHit(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float damageDealt) {
        this.modifierAfterMeleeHit(tool, modifier, context, damageDealt);
    }

    public void modifierAfterMeleeHit(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float damageDealt) {
    }

    public void modifierOnEquip(IToolStackView tool, ModifierEntry modifier, EquipmentChangeContext context) {
    }

    public void modifierOnUnequip(IToolStackView tool, ModifierEntry modifier, EquipmentChangeContext context) {
    }

    public void modifierOnInventoryTick(IToolStackView tool, ModifierEntry modifier, Level level, LivingEntity holder, int itemSlot, boolean isSelected, boolean isCorrectSlot, ItemStack itemStack) {
    }

    public void modifierOnAttacked(IToolStackView tool, ModifierEntry modifier, EquipmentContext context, EquipmentSlot slotType, DamageSource source, float amount, boolean isDirectDamage) {
    }

    public float modifierDamageTaken(IToolStackView tool, ModifierEntry modifier, EquipmentContext context, EquipmentSlot slotType, DamageSource source, float amount, boolean isDirectDamage) {
        return amount;
    }

    public void modifierAfterBlockBreak(IToolStackView tool, ModifierEntry modifier, ToolHarvestContext context) {
    }

    public InteractionResult modifierBeforeEntityUse(IToolStackView tool, ModifierEntry modifier, Player player, Entity target, InteractionHand hand, InteractionSource source) {
        return InteractionResult.PASS;
    }

    public InteractionResult modifierAfterEntityUse(IToolStackView tool, ModifierEntry modifier, Player player, LivingEntity target, InteractionHand hand, InteractionSource source) {
        return InteractionResult.PASS;
    }

    public void modifierAddToolStats(IToolContext context, ModifierEntry modifier, ModifierStatsBuilder builder) {
    }

    @Override
    public void addTooltip(IToolStackView tool, ModifierEntry modifierEntry, @Nullable Player player, List<Component> tooltip, TooltipKey tooltipKey, TooltipFlag tooltipFlag) {

    }

    @Nullable
    @Override
    public Component onRemoved(IToolStackView iToolStackView, Modifier modifier) {
        this.onModifierRemoved(iToolStackView);
        return null;
    }

    public void onModifierRemoved(IToolStackView tool) {
    }

    public void LivingHurtEvent(LivingHurtEvent target) {
    }

    public void LivingAttackEvent(LivingAttackEvent target) {
    }

    public void LivingDamageEvent(LivingDamageEvent target) {
    }

    @Override
    public boolean startInteract(IToolStackView tool, ModifierEntry modifier, Player player, EquipmentSlot slot, TooltipKey keyModifier) {
        return KeybindInteractModifierHook.super.startInteract(tool, modifier, player, slot, keyModifier);
    }

}

