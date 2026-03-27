package com.ssakura49.sakuratinker.common.tools.item;

import com.ssakura49.sakuratinker.SakuraTinker;
import com.ssakura49.sakuratinker.api.item.slot.IHasBulletInventory;
import com.ssakura49.sakuratinker.client.menu.RevolverMenu;
import com.ssakura49.sakuratinker.common.entity.BulletEntity;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import slimeknights.mantle.client.TooltipKey;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.display.TooltipModifierHook;
import slimeknights.tconstruct.library.tools.definition.ToolDefinition;
import slimeknights.tconstruct.library.tools.helper.ToolDamageUtil;
import slimeknights.tconstruct.library.tools.helper.TooltipBuilder;
import slimeknights.tconstruct.library.tools.item.ModifiableItem;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;
import slimeknights.tconstruct.library.tools.stat.ToolStats;
import slimeknights.tconstruct.tools.modifiers.ability.interaction.BlockingModifier;

import java.util.*;

public class RevolverItem extends ModifiableItem implements IHasBulletInventory {
    private static final ResourceLocation TAG_CURRENT_CHAMBER = SakuraTinker.getResource("bullet_slot");
    public static final int MAX_BULLETS = 6;

    public RevolverItem(Properties props, ToolDefinition def) {
        super(props, def);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        ToolStack tool = ToolStack.from(stack);
        if (player.isShiftKeyDown()) {
            if (!world.isClientSide) {
                player.openMenu(new SimpleMenuProvider((id, inv, p) -> new RevolverMenu(id, inv, stack), stack.getHoverName()));
            }
            return InteractionResultHolder.sidedSuccess(stack, world.isClientSide);
        }
        LazyOptional<IItemHandler> opt = stack.getCapability(ForgeCapabilities.ITEM_HANDLER);
        if (!opt.isPresent()) return InteractionResultHolder.fail(stack);
        IItemHandler handler = opt.orElseThrow(IllegalStateException::new);
        ModDataNBT nbt = tool.getPersistentData();
        int currentChamber = nbt.getInt(TAG_CURRENT_CHAMBER);
        for (int i = 0; i < MAX_BULLETS; i++) {
            ItemStack bullet = handler.getStackInSlot(currentChamber);
            ToolStack bulletTool = ToolStack.from(bullet);
            if (!bulletTool.isBroken()) {
                player.startUsingItem(hand);
                return InteractionResultHolder.consume(stack);
            }
        }
        return InteractionResultHolder.fail(stack);
    }
    @Override
    public void onUseTick(Level level, LivingEntity entity, ItemStack stack, int timeLeft) {
        if (!(entity instanceof Player player)) return;
        if (level.isClientSide) return;
        ToolStack tool = ToolStack.from(stack);
        if (tool.isBroken()) return;
        ModDataNBT nbt = tool.getPersistentData();
        int currentChamber = nbt.getInt(TAG_CURRENT_CHAMBER);
        LazyOptional<IItemHandler> opt = stack.getCapability(ForgeCapabilities.ITEM_HANDLER);
        if (!opt.isPresent()) return;
        IItemHandler handler = opt.orElseThrow(IllegalStateException::new);
        int chargeTime = this.getUseDuration(stack) - timeLeft;
        if (chargeTime<10) return;
        int intervalTick = 7;
        if (chargeTime % intervalTick != 0) return;
        boolean fired = false;
        for (int i = 0; i < MAX_BULLETS; i++) {
            int chamberIndex = (currentChamber + i) % MAX_BULLETS;
            ItemStack bullet = handler.getStackInSlot(chamberIndex);
            ToolStack bulletTool = ToolStack.from(bullet);
            nbt.putInt(TAG_CURRENT_CHAMBER, (chamberIndex + 1) % MAX_BULLETS);
            if (!bullet.isEmpty() && !bulletTool.isBroken()) {
                fireBullet(level, player, bullet, tool);
                ToolDamageUtil.damageAnimated(bulletTool, 1, player);
                fired = true;
                break;
            }
        }
        if (!fired) {
            player.playSound(SoundEvents.DISPENSER_FAIL, 1.0F, 0.8F + level.random.nextFloat() * 0.4F);
        }
    }

    private static void fireBullet(Level world, Player player, ItemStack bulletStack, ToolStack stack) {
        if (!world.isClientSide) {
            BulletEntity entity = new BulletEntity(world, player);
            ToolStack toolStack = ToolStack.from(bulletStack);
            entity.setPos(player.getX(), player.getEyeY() - 0.1, player.getZ());
            float damage = toolStack.getStats().get(ToolStats.ATTACK_DAMAGE);
            float velocity= stack.getStats().get(ToolStats.VELOCITY);
            entity.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, 3.0F+velocity, 1.0F);
            entity.setTool(bulletStack);
            entity.setDamage(damage);
            entity.setAttacker(player);
            entity.markFired();
            world.addFreshEntity(entity);
        }
    }

    @Override
    public List<Component> getStatInformation(@NotNull IToolStackView tool, @Nullable Player player, List<Component> tooltips, TooltipKey key, TooltipFlag flag) {
        TooltipBuilder builder = new TooltipBuilder(tool, tooltips);
        if (tool.hasTag(TinkerTags.Items.DURABILITY)) {
            builder.addDurability();
        }
        builder.add(ToolStats.ATTACK_DAMAGE);
        builder.add(ToolStats.ATTACK_SPEED);
        builder.add(ToolStats.VELOCITY);
        builder.add(ToolStats.ACCURACY);
        builder.addAllFreeSlots();
        for(ModifierEntry entry : tool.getModifierList()) {
                ((TooltipModifierHook)entry.getHook(ModifierHooks.TOOLTIP)).addTooltip(tool, entry, player, tooltips, key, flag);
        }
        return tooltips;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return BlockingModifier.blockWhileCharging(ToolStack.from(stack), UseAnim.BOW);
    }
    @Override
    public int getUseDuration(ItemStack pStack) {
        return 72000;
    }

    @Override
    public int getDefaultSlots() {
        return MAX_BULLETS;
    }
}
