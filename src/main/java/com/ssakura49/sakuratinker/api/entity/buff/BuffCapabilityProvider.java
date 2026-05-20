package com.ssakura49.sakuratinker.api.entity.buff;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public class BuffCapabilityProvider implements ICapabilitySerializable<CompoundTag> {
    public static final Capability<CustomBuffHolder> BUFF_CAP = CapabilityManager.get(new CapabilityToken<>() {});

    private CustomBuffHolder backend = null;
    private final LazyOptional<CustomBuffHolder> optional = LazyOptional.of(this::createContext);

    private CustomBuffHolder createContext() {
        if (this.backend == null) {
            this.backend = new CustomBuffHolder();
        }
        return this.backend;
    }

    @Override
    public <T> @NotNull LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        return BUFF_CAP.orEmpty(cap, optional);
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag nbt = new CompoundTag();
        createContext().saveNBT(nbt);
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        createContext().loadNBT(nbt);
    }
}
