package com.ssakura49.sakuratinker.common.tinkering.modifiers.special;

import com.ssakura49.sakuratinker.common.tools.capability.ToolBulletSlotCapability;
import com.ssakura49.sakuratinker.generic.BaseModifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.tools.nbt.IToolContext;
import slimeknights.tconstruct.library.tools.nbt.ToolDataNBT;

public class RevolverBulletModifier extends BaseModifier {
    @Override
    public boolean shouldDisplay(boolean advanced) {
        return false;
    }
    @Override
    public void addVolatileData(IToolContext context, ModifierEntry modifier, ToolDataNBT volatileData) {
        volatileData.putBoolean(ToolBulletSlotCapability.KEY_MODIFIABLE,true);
    }
}
