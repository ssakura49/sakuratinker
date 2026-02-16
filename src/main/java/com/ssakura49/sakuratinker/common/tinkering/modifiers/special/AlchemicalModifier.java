package com.ssakura49.sakuratinker.common.tinkering.modifiers.special;

import com.ssakura49.sakuratinker.STConfig;
import com.ssakura49.sakuratinker.SakuraTinker;
import com.ssakura49.sakuratinker.generic.BaseModifier;
import com.ssakura49.sakuratinker.library.hooks.click.KeyPressModifierHook;
import com.ssakura49.sakuratinker.library.hooks.click.LeftClickModifierHook;
import com.ssakura49.sakuratinker.library.tinkering.tools.STHooks;
import com.ssakura49.sakuratinker.library.tinkering.tools.STToolStats;
import com.ssakura49.sakuratinker.register.STKeys;
import com.ssakura49.sakuratinker.utils.tinker.ItemUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import slimeknights.mantle.client.TooltipKey;
import slimeknights.mantle.fluid.FluidTransferHelper;
import slimeknights.mantle.fluid.transfer.IFluidContainerTransfer;
import slimeknights.tconstruct.common.Sounds;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.fluid.FluidEffectManager;
import slimeknights.tconstruct.library.modifiers.fluid.FluidEffects;
import slimeknights.tconstruct.library.modifiers.hook.build.ConditionalStatModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.build.ModifierRemovalHook;
import slimeknights.tconstruct.library.modifiers.hook.build.ValidateModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.build.VolatileDataModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.display.DisplayNameModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.display.TooltipModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.interaction.GeneralInteractionModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.interaction.SlotStackModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.ranged.ProjectileLaunchModifierHook;
import slimeknights.tconstruct.library.modifiers.impl.NoLevelsModifier;
import slimeknights.tconstruct.library.modifiers.modules.build.StatBoostModule;
import slimeknights.tconstruct.library.module.HookProvider;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.capability.EntityModifierCapability;
import slimeknights.tconstruct.library.tools.capability.PersistentDataCapability;
import slimeknights.tconstruct.library.tools.capability.fluid.ToolFluidCapability;
import slimeknights.tconstruct.library.tools.capability.fluid.ToolTankHelper;
import slimeknights.tconstruct.library.tools.helper.ModifierUtil;
import slimeknights.tconstruct.library.tools.helper.ToolDamageUtil;
import slimeknights.tconstruct.library.tools.item.ranged.ModifiableLauncherItem;
import slimeknights.tconstruct.library.tools.nbt.*;
import slimeknights.tconstruct.library.tools.stat.ToolStats;
import slimeknights.tconstruct.smeltery.item.TankItem;
import slimeknights.tconstruct.tools.entity.FluidEffectProjectile;

import java.util.List;
import java.util.function.Supplier;

public class AlchemicalModifier extends NoLevelsModifier implements
        ToolFluidCapability.FluidModifierHook,
        VolatileDataModifierHook,
        ValidateModifierHook,
        ModifierRemovalHook,
        SlotStackModifierHook,
        DisplayNameModifierHook,
        TooltipModifierHook,
        KeyPressModifierHook,
        LeftClickModifierHook
{
    public static Supplier<Double> fluidConsume = STConfig.Common.ALCHEMIACAL_FLUID_CONSUME;
    private static final int DEFAULT_TANK_CAPACITY = 5000;

    public static final List<ToolTankHelper> ALL_TANKS = List.of(
            new ToolTankHelper(ToolTankHelper.CAPACITY_STAT, SakuraTinker.location("fluid_1")),
            new ToolTankHelper(ToolTankHelper.CAPACITY_STAT, SakuraTinker.location("fluid_2")),
            new ToolTankHelper(ToolTankHelper.CAPACITY_STAT, SakuraTinker.location("fluid_3")),
            new ToolTankHelper(ToolTankHelper.CAPACITY_STAT, SakuraTinker.location("fluid_4")),
            new ToolTankHelper(ToolTankHelper.CAPACITY_STAT, SakuraTinker.location("fluid_5"))
    );

    public static final ResourceLocation MODE = SakuraTinker.location("mode");

    public static String selectedSlotKeyId = STKeys.SELECTED_SLOT_KEY_ID;

    @Override
    protected void registerHooks(ModuleHookMap.Builder builder) {
        super.registerHooks(builder);
        builder.addHook(this,
                ToolFluidCapability.HOOK,
                ModifierHooks.VOLATILE_DATA,
                ModifierHooks.VALIDATE,
                ModifierHooks.REMOVE,
                ModifierHooks.SLOT_STACK,
                ModifierHooks.DISPLAY_NAME,
                STHooks.KEY_PRESS,
                ModifierHooks.TOOLTIP,
                STHooks.LEFT_CLICK
        );
//        builder.addModule(ToolTankHelper.TANK_HANDLER);
//        builder.addModule((HookProvider) StatBoostModule.add(ToolTankHelper.CAPACITY_STAT).amount(1,5000));


    }

    /** 按键切换模式 */
    @Override
    public void onKeyPress(IToolStackView tool, ModifierEntry modifier, Player player, String key) {
        if (!key.equals(selectedSlotKeyId)) return;
        ModDataNBT dataNBT = tool.getPersistentData();
        int mode = dataNBT.getInt(MODE);
        mode = (mode % 7) + 1; // 1-7 循环
        dataNBT.putInt(MODE, mode);

        player.displayClientMessage(Component.literal("切换到模式 " + mode), true);
    }

    @Override
    public String getKeyId(IToolStackView tool, ModifierEntry modifier){
        return selectedSlotKeyId;
    }


    /** 移除时清空流体 */
    @Nullable
    @Override
    public Component onRemoved(IToolStackView tool, Modifier modifier) {
        for (ToolTankHelper helper : ALL_TANKS) {
            helper.setFluid(tool, FluidStack.EMPTY);
        }
        return null;
    }

    /** 校验流体容量 */
    @Nullable
    @Override
    public Component validate(IToolStackView tool, ModifierEntry modifier) {
        for (ToolTankHelper helper : ALL_TANKS) {
            FluidStack fluid = helper.getFluid(tool);
            int capacity = helper.getCapacity(tool);
            if (fluid.getAmount() > capacity) {
                fluid.setAmount(capacity);
                helper.setFluid(tool, fluid);
            }
        }
        return null;
    }

    @Override
    public void addVolatileData(IToolContext context, ModifierEntry modifier, ToolDataNBT volatileData) {
        ToolFluidCapability.addTanks(modifier, volatileData, this);
    }

    @Override
    public Component getDisplayName(IToolStackView tool, ModifierEntry entry, Component name, @Nullable RegistryAccess access) {
        int mode = tool.getPersistentData().getInt(MODE);
        return name.copy()
                .append(Component.literal(" [模式 " + mode + "]").withStyle(ChatFormatting.GRAY));
    }

    /* ----------------- FluidModifierHook ----------------- */

//    @Override
//    public int getTankCapacity(IToolStackView tool, ModifierEntry modifier, int tank) {
//        return ALL_TANKS.get(tank).getCapacity(tool);
//    }
    @Override
    public int getTankCapacity(IToolStackView tool, ModifierEntry modifier, int tank) {
        int cap = ALL_TANKS.get(tank).getCapacity(tool);
        return cap > 0 ? cap : DEFAULT_TANK_CAPACITY;
    }

    @Override
    public FluidStack getFluidInTank(IToolStackView tool, ModifierEntry modifier, int tank) {
        return ALL_TANKS.get(tank).getFluid(tool);
    }

    @Override
    public int getTanks(IModDataView volatileData, ModifierEntry modifier) {
        return ALL_TANKS.size();
    }



    @Override
    public int fill(IToolStackView tool, ModifierEntry modifier, FluidStack resource, IFluidHandler.FluidAction action) {
        ToolTankHelper helper = getHelperForMode(tool);
        int capacity = helper.getCapacity(tool);
        if (capacity <= 0) capacity = DEFAULT_TANK_CAPACITY;
        int mode = getModeRaw(tool);
        if (mode < 1 || mode > 5) return 0;
        FluidStack current = helper.getFluid(tool);

        if (resource.isEmpty()) return 0;
        if (!current.isEmpty() && !current.isFluidEqual(resource)) {
            return 0; // 只能填相同液体
        }

        int fillAmount = Math.min(capacity - current.getAmount(), resource.getAmount());
        if (fillAmount > 0 && action.execute()) {
            if (current.isEmpty()) {
                FluidStack filled = resource.copy();
                filled.setAmount(fillAmount);
                helper.setFluid(tool, filled);
            } else {
                current.grow(fillAmount);
                helper.setFluid(tool, current);
            }
        }
        return fillAmount;
    }

    @Override
    public FluidStack drain(IToolStackView tool, ModifierEntry modifier, FluidStack resource, IFluidHandler.FluidAction action) {
        ToolTankHelper helper = getHelperForMode(tool);
        FluidStack current = helper.getFluid(tool);
        int mode = getModeRaw(tool);
        if (mode < 1 || mode > 5) return FluidStack.EMPTY;
        if (current.isEmpty() || !current.isFluidEqual(resource)) return FluidStack.EMPTY;

        int drainAmount = Math.min(current.getAmount(), resource.getAmount());
        FluidStack drained = new FluidStack(current, drainAmount);

        if (drainAmount > 0 && action.execute()) {
            current.shrink(drainAmount);
            helper.setFluid(tool, current.isEmpty() ? FluidStack.EMPTY : current);
        }
        return drained;
    }

    @Override
    public FluidStack drain(IToolStackView tool, ModifierEntry modifier, int maxDrain, IFluidHandler.FluidAction action) {
        ToolTankHelper helper = getHelperForMode(tool);
        FluidStack current = helper.getFluid(tool);
        int mode = getModeRaw(tool);
        if (mode < 1 || mode > 5) return FluidStack.EMPTY;
        if (current.isEmpty()) return FluidStack.EMPTY;

        int drainAmount = Math.min(current.getAmount(), maxDrain);
        FluidStack drained = new FluidStack(current, drainAmount);

        if (drainAmount > 0 && action.execute()) {
            current.shrink(drainAmount);
            helper.setFluid(tool, current.isEmpty() ? FluidStack.EMPTY : current);
        }
        return drained;
    }
    private int getModeRaw(IToolStackView tool) {
        return tool.getPersistentData().getInt(MODE);
    }

    /* ----------------- 交互 ----------------- */

    private FluidTank getTank(IToolStackView tool) {
        ToolTankHelper helper = getHelperForMode(tool);
        int cap = helper.getCapacity(tool);
        if (cap <= 0) cap = DEFAULT_TANK_CAPACITY;
        FluidTank tank = new FluidTank(cap);
        tank.setFluid(helper.getFluid(tool));
        return tank;
    }
//    private FluidTank getTank(IToolStackView tool) {
//        ToolTankHelper helper = getHelperForMode(tool);
//        FluidTank tank = new FluidTank(helper.getCapacity(tool));
//        tank.setFluid(helper.getFluid(tool));
//        return tank;
//    }

    private ToolTankHelper getHelperForMode(IToolStackView tool) {
        int mode = tool.getPersistentData().getInt(MODE);
        if (mode >= 1 && mode <= 5) {
            return ALL_TANKS.get(mode - 1);
        }
        return ALL_TANKS.get(0);
    }

    @Override
    public boolean overrideStackedOnOther(IToolStackView heldTool, ModifierEntry modifier, Slot slot, Player player) {
        ItemStack slotStack = slot.getItem();
        if (!slotStack.isEmpty() && TankItem.mayHaveFluid(slotStack)) {
            if (slotStack.getCount() == 1) {
                FluidTank tank = getTank(heldTool);
                IFluidContainerTransfer.TransferResult result = FluidTransferHelper.interactWithStack(tank, slotStack, IFluidContainerTransfer.TransferDirection.REVERSE);
                if (result != null) {
                    if (player.level().isClientSide) {
                        player.playSound(result.getSound());
                    }
                    getHelperForMode(heldTool).setFluid(heldTool, tank.getFluid());
                    slot.set(FluidTransferHelper.getOrTransferFilled(player, slotStack, result.stack()));
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean overrideOtherStackedOnMe(IToolStackView slotTool, ModifierEntry modifier, ItemStack held, Slot slot, Player player, SlotAccess access) {
        if (!held.isEmpty() && TankItem.mayHaveFluid(held)) {
            FluidTank tank = getTank(slotTool);
            IFluidContainerTransfer.TransferResult result = FluidTransferHelper.interactWithStack(tank, held, IFluidContainerTransfer.TransferDirection.AUTO);
            if (result != null) {
                if (player.level().isClientSide) {
                    player.playSound(result.getSound());
                }
                getHelperForMode(slotTool).setFluid(slotTool, tank.getFluid());
                TankItem.updateHeldItem(player, held, result.stack());
            }
            return true;
        }
        return false;
    }

    @Override
    public void onLeftClickEmpty(IToolStackView tool, ModifierEntry entry, Player player, Level level, EquipmentSlot equipmentSlot) {
        if (level.isClientSide) {
            return;
        }

        int mode = tool.getPersistentData().getInt(MODE);

        // 找到要用的槽
        ToolTankHelper helper = null;
        switch (mode) {
            case 1,2,3,4,5 -> helper = ALL_TANKS.get(mode - 1);
            case 6 -> { // 轮询
                int index = tool.getPersistentData().getInt(SakuraTinker.location("round_robin_index"));
                for (int i = 0; i < ALL_TANKS.size(); i++) {
                    int tryIndex = (index + i) % ALL_TANKS.size();
                    if (!ALL_TANKS.get(tryIndex).getFluid(tool).isEmpty()) {
                        helper = ALL_TANKS.get(tryIndex);
                        tool.getPersistentData().putInt(SakuraTinker.location("round_robin_index"), tryIndex + 1);
                        break;
                    }
                }
            }
            case 7 -> { // 随机
                List<ToolTankHelper> nonEmpty = ALL_TANKS.stream()
                        .filter(h -> !h.getFluid(tool).isEmpty())
                        .toList();
                if (!nonEmpty.isEmpty()) {
                    helper = nonEmpty.get(level.random.nextInt(nonEmpty.size()));
                }
            }
        }

        if (helper == null) {
            return; // 当前模式没槽可用
        }

        // 拿流体
        FluidStack fluid = helper.getFluid(tool);
        if (fluid.isEmpty()) {
            return;
        }

        FluidEffects recipe = FluidEffectManager.INSTANCE.find(fluid.getFluid());
        if (!recipe.hasEffects()) {
            return;
        }

        float cooldown = ConditionalStatModifierHook.getModifiedStat(tool, player, STToolStats.COOLDOWN, (Float)tool.getStats().get(STToolStats.COOLDOWN) * 20.0F);
        float power = ConditionalStatModifierHook.getModifiedStat(tool, player, ToolStats.PROJECTILE_DAMAGE);
        int levelCount = entry.intEffectiveLevel();

        int amount = Math.min(fluid.getAmount(),
                (int)(recipe.getAmount(fluid.getFluid()) * power) * levelCount) / levelCount;
        if (amount <= 0) {
            return;
        }

        float velocity = ConditionalStatModifierHook.getModifiedStat(tool, player, ToolStats.VELOCITY) * 3.0f;
        float inaccuracy = ModifierUtil.getInaccuracy(tool, player);

        int shots = 1 + 2 * (levelCount - 1);
        float startAngle = ModifiableLauncherItem.getAngleStart(shots);
        int primaryIndex = shots / 2;

        for (int shotIndex = 0; shotIndex < shots; shotIndex++) {
            FluidEffectProjectile spit = new FluidEffectProjectile(level, player, new FluidStack(fluid, amount), power);

            // 旋转角度
            Vec3 upVector = player.getUpVector(1.0f);
            float angle = startAngle + (10 * shotIndex);
            Vector3f targetVector = player.getViewVector(1.0f).toVector3f()
                    .rotate((new Quaternionf()).setAngleAxis(angle * Math.PI / 180F, upVector.x, upVector.y, upVector.z));

            spit.shoot(targetVector.x(), targetVector.y(), targetVector.z(), velocity, inaccuracy);

            ModDataNBT arrowData = PersistentDataCapability.getOrWarn(spit);

            for (ModifierEntry e : tool.getModifierList()) {
                e.getHook(ModifierHooks.PROJECTILE_LAUNCH).onProjectileLaunch(tool, e, player,
                        ItemStack.EMPTY, spit, null, arrowData, shotIndex == primaryIndex);
            }

            level.addFreshEntity(spit);
            level.playSound(null, player.getX(), player.getY(), player.getZ(),
                    Sounds.SPIT.getSound(), SoundSource.PLAYERS, 1.0F,
                    1.0F / (level.getRandom().nextFloat() * 0.4F + 1.2F) + 0.5F + (angle / 10f));
        }

// 消耗流体和耐久
        fluid.shrink(amount * levelCount);
        helper.setFluid(tool, fluid);
        ItemUtil.addCooldown(player, (ToolStack) tool, Math.round(cooldown));
        ToolDamageUtil.damageAnimated(tool, shots, player, player.getUsedItemHand());
    }

    @Override
    public void addTooltip(IToolStackView tool, ModifierEntry entry, @Nullable Player player,
                           List<Component> tooltip, TooltipKey tooltipKey, TooltipFlag tooltipFlag) {
        if (player == null) return;

        int mode = tool.getPersistentData().getInt(MODE);
        String keyName = getKeyDisplay(tool, entry);

        // 切换提示
        tooltip.add(Component.literal("按 [")
                .append(Component.literal(keyName).withStyle(ChatFormatting.YELLOW))
                .append("] 切换模式 (当前: " + mode + ")")
                .withStyle(ChatFormatting.GRAY));

        // 槽位信息
        for (int i = 0; i < ALL_TANKS.size(); i++) {
            ToolTankHelper helper = ALL_TANKS.get(i);
            FluidStack fluid = helper.getFluid(tool);
            int capacity = helper.getCapacity(tool);
            if (capacity <= 0) capacity = DEFAULT_TANK_CAPACITY;

            Component slotInfo;
            if (fluid.isEmpty()) {
                // 空槽：显示“空 + 最大容量”
                slotInfo = Component.literal("槽位 " + (i+1) + ": ")
                        .append(Component.literal("空").withStyle(ChatFormatting.DARK_GRAY))
                        .append(" / " + capacity + " mB");
            } else {
                // 有流体：显示 “流体名 当前/最大”
                slotInfo = Component.literal("槽位 " + (i+1) + ": ")
                        .append(fluid.getDisplayName().copy().withStyle(ChatFormatting.AQUA))
                        .append(Component.literal(" " + fluid.getAmount() + " / " + capacity + " mB")
                                .withStyle(ChatFormatting.GRAY));
            }
            tooltip.add(slotInfo);
        }
    }
}
