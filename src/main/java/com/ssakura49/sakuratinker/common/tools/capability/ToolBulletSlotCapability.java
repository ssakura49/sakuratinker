package com.ssakura49.sakuratinker.common.tools.capability;

import com.ssakura49.sakuratinker.SakuraTinker;
import com.ssakura49.sakuratinker.api.item.slot.IHasBulletInventory;
import com.ssakura49.sakuratinker.common.entity.item.BulletItem;
import com.ssakura49.sakuratinker.common.tools.item.RevolverItem;
import com.ssakura49.sakuratinker.library.tinkering.tools.item.ModifiableBulletItem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemHandlerHelper;
import org.jetbrains.annotations.NotNull;
import slimeknights.tconstruct.library.tools.capability.TinkerDataCapability;
import slimeknights.tconstruct.library.tools.capability.ToolCapabilityProvider;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;

import javax.annotation.Nonnull;
import java.util.function.Supplier;

public record ToolBulletSlotCapability(Supplier<? extends IToolStackView> tool) implements IItemHandlerModifiable {

    private static final int DEFAULT_SLOTS = 6;
    private static int stackCount = 1;
    private static final ResourceLocation BULLET_SLOT_COUNT = SakuraTinker.location("bullet_slots");

    /** 如果是IModifiable的物品，需要通过addVolatileData将此nbt设为true来让槽位的最大堆叠为1 */
    public static final ResourceLocation KEY_MODIFIABLE = SakuraTinker.location("is_modifiable");

    public ToolBulletSlotCapability(Supplier<? extends IToolStackView> tool) {
        this.tool = tool;
    }

    private static ModDataNBT getData(IToolStackView tool) {
        return tool.getPersistentData();
    }

    public boolean isModifiable(){
        return this.tool().get().getVolatileData().contains(KEY_MODIFIABLE);
    }

    public static int setStackCount(int count) {
        return stackCount = count;
    }

    public static int getStackCount() {
        return stackCount;
    }

    /** 获取当前的槽位数 */
    public static int getSlotCount(IToolStackView tool) {
        ModDataNBT data = getData(tool);
        return data.contains(BULLET_SLOT_COUNT) ? data.getInt(BULLET_SLOT_COUNT) : DEFAULT_SLOTS;
    }

    /** 设置槽位数 */
    public static void setSlotCount(IToolStackView tool, int count) {
        getData(tool).putInt(BULLET_SLOT_COUNT, Math.max(1, count));
    }


    private static ResourceLocation makeKey(int slot) {
        return SakuraTinker.location("bullet_" + slot);
    }

    @Nonnull
    @Override
    public ItemStack getStackInSlot(int slot) {
        int max = getSlotCount(tool.get());
        if (slot < 0 || slot >= max) return ItemStack.EMPTY;
        ModDataNBT data = getData(tool.get());
        ResourceLocation key = makeKey(slot);
        if (data.contains(key)) {
            return ItemStack.of(data.getCompound(key));
        }
        return ItemStack.EMPTY;
    }

    @Override
    public void setStackInSlot(int slot, @Nonnull ItemStack stack) {
        int max = getSlotCount(tool.get());
        if (slot < 0 || slot >= max) return;
        ModDataNBT data = getData(tool.get());
        ResourceLocation key = makeKey(slot);
        if (stack.isEmpty()) {
            data.remove(key);
        } else {
            data.put(key, stack.save(new CompoundTag()));
        }
    }

    @Override
    public int getSlots() {
        return getSlotCount(tool.get());
    }

    @Nonnull
    @Override
    public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
        int max = getSlotCount(tool.get());
        if (stack.isEmpty() || slot < 0 || slot >= max) return stack;
        ItemStack current = getStackInSlot(slot);
        if (!current.isEmpty()) return stack;

        int maxInsert = Math.min(stack.getCount(), getSlotLimit(slot));
        if (!simulate) setStackInSlot(slot, stack.copyWithCount(maxInsert));
        return ItemStack.EMPTY;
    }

    @Override
    public int getSlotLimit(int slot) {
        return isModifiable() ? 1 : 64;
    }

    @Override
    public boolean isItemValid(int slot, @NotNull ItemStack stack) {
        int max = getSlotCount(tool.get());
        if (slot < 0 || slot >= max || stack.isEmpty()) return false;
        return stack.getItem() instanceof BulletItem;
    }

    @Nonnull
    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        int max = getSlotCount(tool.get());
        if (slot < 0 || slot >= max) return ItemStack.EMPTY;
        ItemStack current = getStackInSlot(slot);
        if (current.isEmpty()) return ItemStack.EMPTY;

        int take = Math.min(amount, current.getCount());
        ItemStack result = ItemHandlerHelper.copyStackWithSize(current, take);
        if (!simulate) {
            current.shrink(take);
            setStackInSlot(slot, current.isEmpty() ? ItemStack.EMPTY : current);
        }
        return result;
    }

    public static class Provider implements ToolCapabilityProvider.IToolCapabilityProvider {
        private final LazyOptional<ToolBulletSlotCapability> cap;

        public Provider(Supplier<? extends IToolStackView> tool) {
            this.cap = LazyOptional.of(() -> new ToolBulletSlotCapability(tool));
        }

        @Override
        public <T> @NotNull LazyOptional<T> getCapability(IToolStackView tool, @NotNull Capability<T> capability) {
            if (capability == ForgeCapabilities.ITEM_HANDLER && tool.getItem() instanceof IHasBulletInventory) {
                return cap.cast();
            }
            return LazyOptional.empty();
        }
    }

//    private static final int MAX_BULLETS = 6;
//
//    public static final ResourceLocation BULLET_1 = SakuraTinker.location("bullet_1");
//    public static final ResourceLocation BULLET_2 = SakuraTinker.location("bullet_2");
//    public static final ResourceLocation BULLET_3 = SakuraTinker.location("bullet_3");
//    public static final ResourceLocation BULLET_4 = SakuraTinker.location("bullet_4");
//    public static final ResourceLocation BULLET_5 = SakuraTinker.location("bullet_5");
//    public static final ResourceLocation BULLET_6 = SakuraTinker.location("bullet_6");
//
//    public static final ResourceLocation[] BULLET_KEYS = new ResourceLocation[] {
//            BULLET_1, BULLET_2, BULLET_3, BULLET_4, BULLET_5, BULLET_6
//    };
//
//    private static ModDataNBT getData(IToolStackView tool) {
//        return tool.getPersistentData();
//    }
//
//    /** 获取槽位的子弹 ItemStack */
//    @Nonnull
//    @Override
//    public ItemStack getStackInSlot(int slot) {
//        if (slot < 0 || slot >= MAX_BULLETS) return ItemStack.EMPTY;
//        ModDataNBT data = getData(tool.get());
//        if (data.contains(BULLET_KEYS[slot])) {
//            return ItemStack.of(data.getCompound(BULLET_KEYS[slot]));
//        }
//        return ItemStack.EMPTY;
//    }
//
//    /** 设置槽位的子弹 ItemStack */
//    @Override
//    public void setStackInSlot(int slot, @Nonnull ItemStack stack) {
//        if (slot < 0 || slot >= MAX_BULLETS) return;
//        ModDataNBT data = getData(tool.get());
//        if (stack.isEmpty()) {
//            data.remove(BULLET_KEYS[slot]);
//        } else {
//            data.put(BULLET_KEYS[slot], stack.save(new CompoundTag()));
//        }
//    }
//
//    @Override
//    public int getSlots() {
//        return MAX_BULLETS;
//    }
//
//    @Nonnull
//    @Override
//    public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
//        if (stack.isEmpty() || slot < 0 || slot >= MAX_BULLETS) return stack;
//        ItemStack current = getStackInSlot(slot);
//        if (!current.isEmpty()) return stack; // 只允许空槽插入
//
//        if (!simulate) setStackInSlot(slot, stack.copy());
//        return ItemStack.EMPTY;
//    }
//
//    @Override
//    public int getSlotLimit(int slot) {
//        return 1; // 每格只能放一颗子弹
//    }
//
//    @Override
//    public boolean isItemValid(int slot, @NotNull ItemStack stack) {
//        if (slot < 0 || slot >= MAX_BULLETS || stack.isEmpty()) return false;
//        return stack.getItem() instanceof ModifiableBulletItem;
//    }
//
//    @Nonnull
//    @Override
//    public ItemStack extractItem(int slot, int amount, boolean simulate) {
//        if (slot < 0 || slot >= MAX_BULLETS) return ItemStack.EMPTY;
//        ItemStack current = getStackInSlot(slot);
//        if (current.isEmpty()) return ItemStack.EMPTY;
//
//        int take = Math.min(amount, current.getCount());
//        ItemStack result = ItemHandlerHelper.copyStackWithSize(current, take);
//        if (!simulate) {
//            current.shrink(take);
//            setStackInSlot(slot, current.isEmpty() ? ItemStack.EMPTY : current);
//        }
//        return result;
//    }
//
//    public static class Provider implements ToolCapabilityProvider.IToolCapabilityProvider {
//        private final LazyOptional<RevolverInventoryCapability> cap;
//
//        public Provider(Supplier<? extends IToolStackView> tool) {
//            this.cap = LazyOptional.of(() -> new RevolverInventoryCapability(tool));
//        }
//
//        @Override
//        public <T> @NotNull LazyOptional<T> getCapability(IToolStackView tool, @NotNull Capability<T> capability) {
//            if (capability == ForgeCapabilities.ITEM_HANDLER) {
//                Item item = tool.getItem();
//                if (item instanceof RevolverItem) {
//                    return cap.cast();
//                }
//             }
//            return LazyOptional.empty();
//        }
//    }
//
//    public static int getMaxBullets() {
//        return MAX_BULLETS;
//    }
}
