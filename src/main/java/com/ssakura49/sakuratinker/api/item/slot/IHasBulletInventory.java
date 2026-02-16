package com.ssakura49.sakuratinker.api.item.slot;

import com.ssakura49.sakuratinker.common.tools.capability.ToolBulletSlotCapability;
import net.minecraft.world.item.ItemStack;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;

public interface IHasBulletInventory {
    int getDefaultSlots();


    default void setSlots(ToolBulletSlotCapability capability) {
        ToolBulletSlotCapability.setSlotCount(capability.tool().get(), getDefaultSlots());
    }
}
