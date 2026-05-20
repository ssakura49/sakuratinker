package com.ssakura49.sakuratinker.common.capability;

import com.ssakura49.sakuratinker.SakuraTinker;
import com.ssakura49.sakuratinker.common.capability.entity.PlayerCapability;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.NotNull;

@Mod.EventBusSubscriber(modid = SakuraTinker.MODID)
public class CapabilityAttach {
    private static final ResourceLocation CAP_ID = SakuraTinker.getResource("st_cap");

    @SubscribeEvent
    public static void attachCap(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof Player) {
            PlayerCapability instance = new PlayerCapability();
            ICapabilitySerializable<CompoundTag> provider = new ICapabilitySerializable<CompoundTag>() {
                private final LazyOptional<PlayerCapability> handler = LazyOptional.of(() -> instance);
                @Override
                public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, Direction side) {
                    return CapabilityRegistry.PLAYER_CAP.orEmpty(cap, handler);
                }
                @Override
                public CompoundTag serializeNBT() {
                    CompoundTag nbt = new CompoundTag();
                    instance.saveNBT(nbt);
                    return nbt;
                }
                @Override
                public void deserializeNBT(CompoundTag nbt) {
                    instance.loadNBT(nbt);
                }
            };
            event.addCapability(CAP_ID, provider);
        }
    }
}
