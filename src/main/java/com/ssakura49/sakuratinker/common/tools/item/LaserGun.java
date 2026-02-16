package com.ssakura49.sakuratinker.common.tools.item;

import com.ssakura49.sakuratinker.STConfig;
import com.ssakura49.sakuratinker.SakuraTinker;
import com.ssakura49.sakuratinker.common.entity.LaserProjectileEntity;
import com.ssakura49.sakuratinker.library.tinkering.tools.STToolStats;
import com.ssakura49.sakuratinker.register.STSounds;
import com.ssakura49.sakuratinker.utils.tinker.ItemUtil;
import com.ssakura49.sakuratinker.utils.tinker.TooltipUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import slimeknights.mantle.client.TooltipKey;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.build.ConditionalStatModifierHook;
import slimeknights.tconstruct.library.tools.definition.ToolDefinition;
import slimeknights.tconstruct.library.tools.helper.ToolDamageUtil;
import slimeknights.tconstruct.library.tools.helper.TooltipBuilder;
import slimeknights.tconstruct.library.tools.item.ModifiableItem;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;
import slimeknights.tconstruct.library.tools.stat.ToolStats;

import java.util.List;
import java.util.function.Supplier;

import static slimeknights.tconstruct.library.tools.capability.ToolEnergyCapability.*;

public class LaserGun extends ModifiableItem {

    private static final ResourceLocation MODE_KEY = ResourceLocation.fromNamespaceAndPath(SakuraTinker.MODID,"laser_mode"); // 0 = 单发, 1 = 持续
    private static final ResourceLocation CONTINUOUS_TICKS_KEY = ResourceLocation.fromNamespaceAndPath(SakuraTinker.MODID,"continuous_ticks");
    private static final float BASE_DAMAGE_MULTIPLIER = 1.0f;

    public static final Supplier<Integer> BASE_ENERGY = STConfig.Common.LASER_GUN_BASE_ENERGY;
    public static final Supplier<Integer> ENERGY_PER_TICK = STConfig.Common.LASER_GUN_ENERGY_PER_TICK;
    public static final Supplier<Double> DAMAGE_PER_TICK = STConfig.Common.LASER_GUN_DAMAGE_PER_TICK;
    public static final Supplier<Integer> BONUS_TICK = STConfig.Common.LASER_GUN_C_MOD_NORMAL_DAMAGE_BONUS;

    public LaserGun(Item.Properties properties, ToolDefinition toolDefinition) {
        super(properties, toolDefinition);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        ToolStack tool = ToolStack.from(stack);
        ModDataNBT data = tool.getPersistentData();
        boolean creative = player.getAbilities().instabuild;
        if (player.isShiftKeyDown()) {
            int mode = data.getInt(MODE_KEY);
            data.putInt(MODE_KEY, (mode + 1) % 2);
            if (!level.isClientSide) {
                player.displayClientMessage(
                        Component.literal("激光发射模式: " + (data.getInt(MODE_KEY) == 0 ? "单发" : "持续"))
                                .withStyle(ChatFormatting.YELLOW), true
                );
            }
            return InteractionResultHolder.success(stack);
        }

        int mode = data.getInt(MODE_KEY);
        if (mode == 0) {
            int currentEnergy = getEnergy(tool);
            if (!player.getAbilities().instabuild && currentEnergy < BASE_ENERGY.get()) {
                return InteractionResultHolder.fail(stack);
            }
            if (!player.getAbilities().instabuild) {
                setEnergy(tool, currentEnergy - BASE_ENERGY.get());
            }
            float cooldown = ConditionalStatModifierHook.getModifiedStat(tool, player, STToolStats.COOLDOWN, (Float)tool.getStats().get(STToolStats.COOLDOWN) * 20.0F);
            fireLaser(level, player, stack, tool, 1.0f);
            ItemUtil.addCooldown(player, this, Math.round(cooldown));
            return InteractionResultHolder.consume(stack);
        } else {
            player.startUsingItem(hand);
            return InteractionResultHolder.consume(stack);
        }
    }

    private void fireLaser(Level level, Player player, ItemStack stack, ToolStack tool, float bonusMultiplier) {
        float range = ConditionalStatModifierHook.getModifiedStat(tool, player, STToolStats.RANGE, tool.getStats().getInt(STToolStats.RANGE));
        int penetration = (int) ConditionalStatModifierHook.getModifiedStat(tool, player, STToolStats.PENETRATION, tool.getStats().getInt(STToolStats.PENETRATION));
        Vec3 startPos = player.getEyePosition();
        Vec3 lookVec = player.getLookAngle();
        Vec3 endPos = startPos.add(lookVec.scale(range));
        LaserProjectileEntity laser = new LaserProjectileEntity(level, player);
        laser.setStartPos(startPos);
        laser.setEndPos(endPos);
        laser.setTool(stack.copy());
        laser.setRange(range);
        laser.setOwner(player);
        laser.setPenetrationCount(penetration);
        float baseDamage = tool.getStats().get(ToolStats.ATTACK_DAMAGE) + 0.5f;
        laser.setDamage(baseDamage * bonusMultiplier);
        level.addFreshEntity(laser);
        level.playSound(null, player.getX(), player.getY(), player.getZ(), STSounds.LASER_SHOOT.get(), SoundSource.PLAYERS, 0.1F, 0.9F + level.random.nextFloat() * 0.2F);
        ToolDamageUtil.damageAnimated(tool, 1, player, player.getUsedItemHand());
    }

    @Override
    public void onUseTick(Level level, LivingEntity entity, ItemStack stack, int timeLeft) {
        if (!(entity instanceof Player player)) return;
        ToolStack tool = ToolStack.from(stack);
        ModDataNBT data = tool.getPersistentData();
        if (data.getInt(MODE_KEY) != 1) return;
        int ticks = data.getInt(CONTINUOUS_TICKS_KEY);
        ticks++;
        data.putInt(CONTINUOUS_TICKS_KEY, ticks);
        int rampUpTicks = BONUS_TICK.get();
        float damageMultiplier = (float) (BASE_DAMAGE_MULTIPLIER + (double) ticks / rampUpTicks * DAMAGE_PER_TICK.get());
        int energyCost = BASE_ENERGY.get() + Math.round(ENERGY_PER_TICK.get() * damageMultiplier);
        int currentEnergy = getEnergy(tool);
        if (!player.getAbilities().instabuild && currentEnergy < BASE_ENERGY.get()) {
            player.stopUsingItem();
            data.putInt(CONTINUOUS_TICKS_KEY, 0);
            return;
        }
        fireLaser(player.level, player, stack, tool, damageMultiplier);
        if (!player.getAbilities().instabuild) {
            setEnergy(tool, currentEnergy - energyCost);
        }
    }

    @Override
    public void releaseUsing(ItemStack stack, Level level, LivingEntity entity, int timeLeft) {
        if (!(entity instanceof Player)) return;
        ToolStack tool = ToolStack.from(stack);
        tool.getPersistentData().putInt(CONTINUOUS_TICKS_KEY, 0);
    }

    @Override
    public UseAnim getUseAnimation(ItemStack itemStack) {
        return UseAnim.BOW;
    }

    @Override
    public int getUseDuration(ItemStack pStack) {
        return 72000;
    }


    @Override
    public List<Component> getStatInformation(IToolStackView tool, @Nullable Player player, List<Component> tooltips, TooltipKey key, TooltipFlag tooltipFlag) {
        return getLaserGunStats(tool, player, tooltips, key, tooltipFlag);
    }

    public List<Component> getLaserGunStats(IToolStackView tool, @Nullable Player player,
                                            List<Component> tooltips, TooltipKey key,
                                            TooltipFlag tooltipFlag) {
        TooltipBuilder builder = new TooltipBuilder(tool, tooltips);
        if (tool.hasTag(TinkerTags.Items.DURABILITY)) {
            builder.addDurability();
        }
        if (tool.hasTag(TinkerTags.Items.MELEE)) {
            builder.add(ToolStats.ATTACK_DAMAGE);
            builder.add(ToolStats.ATTACK_SPEED);
        }
        TooltipUtil.addToolStatTooltip(builder, tool, STToolStats.RANGE);
        TooltipUtil.addToolStatTooltip(builder, tool, STToolStats.COOLDOWN);
        int stored = tool.getPersistentData().getInt(ENERGY_KEY);
        int max = tool.getStats().getInt(MAX_STAT) + tool.getStats().getInt(STToolStats.ENERGY_STORAGE);
        int mode = tool.getPersistentData().getInt(MODE_KEY);
        builder.add(Component.literal("能量: " + stored + " / " + max).withStyle(ChatFormatting.BLUE));
        builder.add(Component.literal("激光发射模式: " + (mode == 0 ? "单发" : "持续")).withStyle(ChatFormatting.YELLOW));
        builder.addAllFreeSlots();

        for (ModifierEntry entry : tool.getModifierList()) {
            entry.getHook(ModifierHooks.TOOLTIP).addTooltip(tool, entry, player, tooltips, key, tooltipFlag);
        }

        return tooltips;
    }
}
