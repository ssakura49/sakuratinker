/*
package com.ssakura49.sakuratinker.common.entity.deprecated;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.ForgeRegistries;

@Deprecated(forRemoval = true,since = "1.21")
public record CelestialBladeConfig(Holder<Item> item,
                                   int color,
                                   double longAxis,
                                   double shortAxis,
                                   double scale,
                                   int lifespan,
                                   double trailWidth) {
    @SuppressWarnings("deprecation")
    public static final Codec<CelestialBladeConfig> CODEC = RecordCodecBuilder.create((instance) -> instance.group(
            BuiltInRegistries.ITEM.holderByNameCodec().fieldOf("item").forGetter(CelestialBladeConfig::item),
            Codec.INT.fieldOf("color").forGetter(CelestialBladeConfig::color),
            Codec.DOUBLE.fieldOf("longAxis").forGetter(CelestialBladeConfig::longAxis),
            Codec.DOUBLE.fieldOf("shortAxis").forGetter(CelestialBladeConfig::shortAxis),
            Codec.DOUBLE.fieldOf("scale").forGetter(CelestialBladeConfig::scale),
            Codec.INT.fieldOf("lifespan").forGetter(CelestialBladeConfig::lifespan),
            Codec.DOUBLE.fieldOf("trail_width").forGetter(CelestialBladeConfig::trailWidth)
    ).apply(instance, CelestialBladeConfig::new));

    public static CelestialBladeConfig fromNetWork(FriendlyByteBuf buf) {
        String item = buf.readUtf();
        int color = buf.readInt();
        double longAxis = buf.readDouble();
        double shortAxis = buf.readDouble();
        double scale = buf.readDouble();
        int lifespan = buf.readInt();
        double trailWidth = buf.readDouble();
        return new CelestialBladeConfig(ForgeRegistries.ITEMS.getDelegateOrThrow(ResourceLocation.parse(item)),color,longAxis,shortAxis,scale,lifespan,trailWidth);
    }
    @SuppressWarnings("deprecation")
    public void toNetwork(FriendlyByteBuf buf) {
        buf.writeResourceLocation(BuiltInRegistries.ITEM.getKey(item.value()));
        buf.writeInt(color);
        buf.writeDouble(longAxis);
        buf.writeDouble(shortAxis);
        buf.writeDouble(scale);
        buf.writeInt(lifespan);
        buf.writeDouble(trailWidth);
    }
}*/
