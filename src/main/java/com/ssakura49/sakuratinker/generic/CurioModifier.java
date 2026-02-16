package com.ssakura49.sakuratinker.generic;

import com.ssakura49.sakuratinker.library.hooks.curio.armor.CurioEquipmentChangeModifierHook;
import com.ssakura49.sakuratinker.library.hooks.curio.armor.CurioTakeDamagePreModifierHook;
import com.ssakura49.sakuratinker.library.hooks.curio.behavior.CurioAttributeModifierHook;
import com.ssakura49.sakuratinker.library.hooks.curio.behavior.CurioTakeHealModifierHook;
import com.ssakura49.sakuratinker.library.hooks.curio.combat.*;
import com.ssakura49.sakuratinker.library.hooks.curio.interation.CurioInventoryTickModifierHook;
import com.ssakura49.sakuratinker.library.hooks.curio.mining.CurioBreakSpeedModifierHook;
import com.ssakura49.sakuratinker.library.hooks.curio.ranged.CurioArrowHitHook;
import com.ssakura49.sakuratinker.library.hooks.curio.ranged.CurioArrowShootHook;
import com.ssakura49.sakuratinker.library.hooks.curio.behavior.CurioGetToolDamageModifierHook;
import com.ssakura49.sakuratinker.library.hooks.curio.behavior.CurioFortuneModifierHook;
import com.ssakura49.sakuratinker.library.hooks.curio.armor.CurioTakeDamagePostModifierHook;
import com.ssakura49.sakuratinker.library.tinkering.tools.STHooks;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.capability.TinkerDataCapability;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import top.theillusivec4.curios.api.SlotContext;

public abstract class CurioModifier extends BaseModifier implements
        CurioEquipmentChangeModifierHook,
        CurioTakeDamagePreModifierHook,
        CurioTakeDamagePostModifierHook,
        CurioAttributeModifierHook,
        CurioFortuneModifierHook,
        CurioGetToolDamageModifierHook,
        CurioTakeHealModifierHook,
        CurioCalculateDamageModifierHook,
        CurioDamageTargetPreModifierHook,
        CurioDamageTargetPostModifierHook,
        CurioKillTargetModifierHook,
        CurioLootingModifierHook,
        CurioInventoryTickModifierHook,
        CurioBreakSpeedModifierHook,
        CurioArrowHitHook,
        CurioArrowShootHook
{
    public CurioModifier(){}

    @Override
    protected void registerHooks(ModuleHookMap.Builder builder) {
        super.registerHooks(builder);
        builder.addHook(this,
                STHooks.CURIO_EQUIPMENT_CHANGE,
                STHooks.CURIO_TAKE_DAMAGE_PRE,
                STHooks.CURIO_TAKE_DAMAGE_POST,
                STHooks.CURIO_ATTRIBUTE,
                STHooks.CURIO_FORTUNE,
                STHooks.CURIO_TOOL_DAMAGE,
                STHooks.CURIO_TAKE_HEAL,
                STHooks.CURIO_CALCULATE_DAMAGE,
                STHooks.CURIO_DAMAGE_TARGET_PRE,
                STHooks.CURIO_DAMAGE_TARGET_POST,
                STHooks.CURIO_KILL_TARGET,
                STHooks.CURIO_LOOTING,
                STHooks.CURIO_TICK,
                STHooks.CURIO_BREAK_SPEED,
                STHooks.CURIO_ARROW_HIT,
                STHooks.CURIO_ARROW_SHOOT
        );
    }

    @Override
    public void onCurioEquip(IToolStackView curio, ModifierEntry entry, SlotContext context, LivingEntity entity, ItemStack prevStack, ItemStack stack) {
        this.onUseKeyEquip(entity, entry, false);
    }

    @Override
    public void onCurioUnequip(IToolStackView curio, ModifierEntry entry, SlotContext context, LivingEntity entity, ItemStack newStack, ItemStack stack) {
        this.onUseKeyUnequip(entity);
    }

    public TinkerDataCapability.TinkerDataKey<Integer> useKey() {
        return null;
    }

    public void onUseKeyEquip(LivingEntity entity, ModifierEntry entry, boolean toAdd) {
        if (this.useKey() != null) {
            entity.getCapability(TinkerDataCapability.CAPABILITY).ifPresent((holder) -> {
                int def = (Integer)holder.get(this.useKey(), 0);
                int level = entry.getLevel();
                if (def < level && !toAdd) {
                    holder.put(this.useKey(), level);
                } else {
                    holder.put(this.useKey(), def + level);
                }
            });
        }
    }

    public void onUseKeyUnequip(LivingEntity entity) {
        if (this.useKey() != null) {
            entity.getCapability(TinkerDataCapability.CAPABILITY).ifPresent((holder) -> holder.remove(this.useKey()));
        }
    }
}
