package com.ssakura49.sakuratinker.library.hooks.bullet;

import com.ssakura49.sakuratinker.SakuraTinker;
import com.ssakura49.sakuratinker.common.tools.capability.ToolBulletSlotCapability;
import com.ssakura49.sakuratinker.library.tinkering.tools.STHooks;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.items.ItemHandlerHelper;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.tools.helper.ToolDamageUtil;
import slimeknights.tconstruct.library.tools.item.IModifiable;
import slimeknights.tconstruct.library.tools.item.IModifiableDisplay;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;

import javax.annotation.Nullable;
import java.util.function.Predicate;

public interface BulletAmmoModifierHook {
    /** 空 Hook */
    BulletAmmoModifierHook EMPTY = (tool, modifier, shooter, capability, predicate) -> ItemStack.EMPTY;

    /** 从弹仓中寻找可用子弹 */
    ItemStack findAmmo(IToolStackView tool, ModifierEntry modifier, LivingEntity shooter,
                       ToolBulletSlotCapability capability, Predicate<ItemStack> predicate);

    /**
     * 默认消耗子弹
     * @param tool 工具
     * @param modifier Modifier entry
     * @param shooter 发射者
     * @param ammo 子弹 ItemStack
     * @param needed 消耗数量
     */
    default void shrinkAmmo(IToolStackView tool, ModifierEntry modifier, LivingEntity shooter, ItemStack ammo, int needed) {
        if (ammo.getItem() instanceof IModifiable) {
            ToolStack ammoStack = ToolStack.from(ammo);
            ToolDamageUtil.damageAnimated(ammoStack, needed, shooter);
        } else {
            ammo.shrink(needed);
        }
    }
    /** 是否有弹药 */
    static boolean hasAmmo(IToolStackView tool, ItemStack gun, LivingEntity shooter, Predicate<ItemStack> predicate) {
        return !getAmmo(tool, gun, shooter, predicate).isEmpty();
    }

    /** 获取当前格的弹药 */
    static ItemStack getAmmo(IToolStackView tool, ItemStack gun, LivingEntity shooter, Predicate<ItemStack> predicate) {
        ToolBulletSlotCapability capability = getCapability(gun);
        if (capability == null) return ItemStack.EMPTY;

        int current = getCurrentChamber(tool, capability);

        for (ModifierEntry entry : tool.getModifierList()) {
            BulletAmmoModifierHook hook = entry.getHook(STHooks.BULLET_AMMO);
            if (hook == EMPTY) continue;

            ItemStack ammo = hook.findAmmo(tool, entry, shooter, capability, predicate);
            if (!ammo.isEmpty()) return ammo;
        }

        ItemStack bullet = capability.getStackInSlot(current);
        if (!bullet.isEmpty() && predicate.test(bullet)) return bullet;
        return ItemStack.EMPTY;
    }

    /** 扣除一发弹药并推进一格 */
    static ItemStack consumeAmmo(IToolStackView tool, ItemStack gun, LivingEntity shooter, Predicate<ItemStack> predicate) {
        ToolBulletSlotCapability capability = getCapability(gun);
        if (capability == null) return ItemStack.EMPTY;

        int current = getCurrentChamber(tool, capability);
        int slotCount = capability.getSlots();

        ItemStack ammo = capability.getStackInSlot(current);

        if (ammo.isEmpty() || !predicate.test(ammo)) {
            rotateChamber(tool, capability, slotCount);
            return ItemStack.EMPTY;
        }

        for (ModifierEntry entry : tool.getModifierList()) {
            BulletAmmoModifierHook hook = entry.getHook(STHooks.BULLET_AMMO);
            if (hook == EMPTY) continue;

            ItemStack customAmmo = hook.findAmmo(tool, entry, shooter, capability, predicate);
            if (!customAmmo.isEmpty()) {
                hook.shrinkAmmo(tool, entry, shooter, customAmmo, 1);
                capability.setStackInSlot(current, customAmmo);
                rotateChamber(tool, capability, slotCount);
                return ItemHandlerHelper.copyStackWithSize(customAmmo, 1);
            }
        }

        ItemStack result = ItemHandlerHelper.copyStackWithSize(ammo, 1);

        if (result.getItem() instanceof IModifiable) {
            ToolStack ammoStack = ToolStack.from(ammo);
            ToolDamageUtil.damageAnimated(ammoStack, 1, shooter);
        } else {
            ammo.shrink(1);
            capability.setStackInSlot(current, ammo);
        }

        rotateChamber(tool, capability, slotCount);
        return result;
    }

    private static @Nullable ToolBulletSlotCapability getCapability(ItemStack gun) {
        if (gun.isEmpty()) return null;
        return gun.getCapability(ForgeCapabilities.ITEM_HANDLER)
                .filter(ToolBulletSlotCapability.class::isInstance)
                .map(ToolBulletSlotCapability.class::cast)
                .orElse(null);
    }

    /** 获取当前弹仓索引（默认 0） */
    private static int getCurrentChamber(IToolStackView tool, ToolBulletSlotCapability cap) {
        return tool.getPersistentData().getInt(SakuraTinker.location("current_chamber"));
    }

    /** 推进弹仓（开火或空仓时调用） */
    private static void rotateChamber(IToolStackView tool, ToolBulletSlotCapability cap, int totalSlots) {
        ModDataNBT data = tool.getPersistentData();
        int current = data.getInt(SakuraTinker.location("current_chamber"));
        current = (current + 1) % totalSlots;
        data.putInt(SakuraTinker.location("current_chamber"), current);
    }
}