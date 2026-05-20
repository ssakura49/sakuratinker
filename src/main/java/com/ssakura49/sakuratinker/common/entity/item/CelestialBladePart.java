package com.ssakura49.sakuratinker.common.entity.item;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;

public record CelestialBladePart(Holder<Item> item, int color, double rotationCenterHeight, double rotation, double scale,
                                 double trailWidth) {
    @SuppressWarnings("deprecation")
    public static final Codec<CelestialBladePart> CODEC = RecordCodecBuilder.create((instance) -> instance.group(
            BuiltInRegistries.ITEM.holderByNameCodec().fieldOf("item").forGetter(CelestialBladePart::item),
            Codec.INT.fieldOf("color").forGetter(CelestialBladePart::color),
            Codec.DOUBLE.fieldOf("rotation_center_height").forGetter(CelestialBladePart::rotationCenterHeight),
            Codec.DOUBLE.fieldOf("rotation").forGetter(CelestialBladePart::rotation),
            Codec.DOUBLE.fieldOf("scale").forGetter(CelestialBladePart::scale),
            Codec.DOUBLE.fieldOf("trail_width").forGetter(CelestialBladePart::trailWidth)
    ).apply(instance, CelestialBladePart::new));

    public static final Codec<List<CelestialBladePart>> LIST_CODEC = CODEC.listOf();

    public static CelestialBladePart fromNetwork(FriendlyByteBuf byteBuf) {
        String item = byteBuf.readUtf();
        int color = byteBuf.readInt();
        double rotationCenterHeight = byteBuf.readDouble();
        double rotation = byteBuf.readDouble();
        double scale = byteBuf.readDouble();
        double trailWidth = byteBuf.readDouble();
        return new CelestialBladePart(ForgeRegistries.ITEMS.getDelegateOrThrow(ResourceLocation.parse(item)), color, rotationCenterHeight, rotation, scale, trailWidth);
    }

    public void toNetwork(FriendlyByteBuf byteBuf) {
        ResourceLocation itemKey = ForgeRegistries.ITEMS.getKey(item().value());
        if (itemKey != null) {
            byteBuf.writeUtf(itemKey.toString());
            byteBuf.writeInt(color());
            byteBuf.writeDouble(rotationCenterHeight());
            byteBuf.writeDouble(rotation());
            byteBuf.writeDouble(scale());
            byteBuf.writeDouble(trailWidth());
        }
    }
}
