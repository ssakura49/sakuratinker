package com.ssakura49.sakuratinker.common.capability.entity;

import net.minecraft.nbt.CompoundTag;

public class PlayerCapability {
    private int invulnerableTick;

    public int getInvulnerableTick() { return invulnerableTick; }
    public void setInvulnerableTick(int tick) { this.invulnerableTick = tick; }

    public void saveNBT(CompoundTag nbt) {
        nbt.putInt("invul_tick", invulnerableTick);
    }

    public void loadNBT(CompoundTag nbt) {
        this.invulnerableTick = nbt.getInt("invul_tick");
    }
}
