package com.ssakura49.sakuratinker.network.s2c;

import com.ssakura49.sakuratinker.api.entity.buff.ClientBuffManager;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public record ClientboundSyncBuffPacket(
        int entityId,
        ResourceLocation buffId,
        int duration,
        int level,
        boolean isRemoved
) {
    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(entityId);
        buf.writeResourceLocation(buffId);
        buf.writeInt(duration);
        buf.writeInt(level);
        buf.writeBoolean(isRemoved);
    }

    public static ClientboundSyncBuffPacket decode(FriendlyByteBuf buf) {
        return new ClientboundSyncBuffPacket(
                buf.readInt(), buf.readResourceLocation(),
                buf.readInt(), buf.readInt(), buf.readBoolean());
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ClientBuffManager.handlePacket(this);
        });
        ctx.get().setPacketHandled(true);
    }
}
