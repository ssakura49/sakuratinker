package com.ssakura49.sakuratinker.api.entity.buff;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.effect.MobEffect;

import java.util.UUID;

public class BuffInstance {
    private final CustomBuff type;
    private int duration;
    private final int level;
    private final CompoundTag data;

    private UUID modifierId;
    private boolean attributesApplied = false;

    public BuffInstance(CustomBuff type, int duration, int level) {
        this(type, duration, level, UUID.randomUUID());
    }

    public BuffInstance(CustomBuff type, int duration, int level, UUID uuid) {
        this.type = type;
        this.duration = duration;
        this.level = level;
        this.data = new CompoundTag();
        this.modifierId = uuid;
    }

    public void tick() { if (duration > 0) duration--; }
    public boolean isExpired() { return duration <= 0; }

    public CustomBuff getType() { return type; }
    public int getDuration() { return duration; }
    public void setDuration(int d) { this.duration = d; }
    public int getLevel() { return level; }

    public CompoundTag getData() {
        return data;
    }

    public UUID getModifierId() {
        return modifierId;
    }

    public boolean isAttributesApplied() {
        return attributesApplied;
    }

    public void setAttributesApplied(boolean state) { this.attributesApplied = state; }
}
