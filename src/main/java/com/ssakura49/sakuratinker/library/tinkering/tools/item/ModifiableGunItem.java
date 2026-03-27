package com.ssakura49.sakuratinker.library.tinkering.tools.item;

import com.ssakura49.sakuratinker.SakuraTinker;
import com.ssakura49.sakuratinker.api.item.slot.IHasBulletInventory;
import com.ssakura49.sakuratinker.common.entity.BulletEntity;
import com.ssakura49.sakuratinker.library.hooks.bullet.BulletAmmoModifierHook;
import com.ssakura49.sakuratinker.library.interfaces.projectile.IProjectileBuild;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.interaction.UsingToolModifierHook;
import slimeknights.tconstruct.library.tools.definition.ToolDefinition;
import slimeknights.tconstruct.library.tools.item.ranged.ModifiableLauncherItem;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;
import slimeknights.tconstruct.library.tools.stat.ToolStats;

import java.util.function.Predicate;

public abstract class ModifiableGunItem extends ModifiableLauncherItem implements IHasBulletInventory {
    private static final ResourceLocation TAG_CURRENT_CHAMBER = SakuraTinker.getResource("bullet_slot");

    public ModifiableGunItem(Properties properties, ToolDefinition toolDefinition) {
        super(properties, toolDefinition);
    }

    @Override
    public int getDefaultSlots() {
        return setBulletSlots();
    }

    public abstract int setBulletSlots();

    public abstract @NotNull Predicate<ItemStack> getAllSupportedProjectiles();

    public abstract int getDefaultProjectileRange();

    public abstract int chargeTime();

    public abstract int intervalTick();

    @Override
    public void onUseTick(Level world, LivingEntity living, ItemStack stack, int timeLeft) {
        if (world.isClientSide) return;
        ToolStack tool = ToolStack.from(stack);
        if (tool.isBroken()) return;
        ModDataNBT nbt = tool.getPersistentData();

        int chargeTime = this.getUseDuration(stack) - timeLeft;
        if (chargeTime<chargeTime()) return;
        if (chargeTime % intervalTick() != 0) return;

        ItemStack ammo = BulletAmmoModifierHook.consumeAmmo(tool, stack, living, getAllSupportedProjectiles());
        if (!ammo.isEmpty()) {
            fireBullet(world, living, ammo, tool);
        } else {
            living.playSound(SoundEvents.DISPENSER_FAIL, 1.0F, 0.8F + world.random.nextFloat() * 0.4F);
        }

        for (ModifierEntry entry : tool.getModifierList()) {
            ((UsingToolModifierHook)entry.getHook(ModifierHooks.TOOL_USING))
                    .onUsingTick(tool, entry, living, getUseDuration(stack), timeLeft, ModifierEntry.EMPTY);
        }
    }

    protected void fireBullet(Level level, LivingEntity entity, ItemStack bulletStack, ToolStack gunTool) {
        if (level.isClientSide()) return;
        if (!(bulletStack.getItem() instanceof IProjectileBuild bulletItem)) return;

        Projectile projectile = bulletItem.createProjectile(bulletStack, level, entity);
        bulletItem.addInfoToProjectile(projectile, bulletStack, level, entity);

        float velocity = gunTool.getStats().get(ToolStats.VELOCITY) + 3f;
        projectile.shootFromRotation(entity, entity.getXRot(), entity.getYRot(), 0f, velocity, 1f);

        if (projectile instanceof BulletEntity bulletEntity) {
            bulletEntity.setAttacker(entity);
        }
        level.addFreshEntity(projectile);
    }
}
