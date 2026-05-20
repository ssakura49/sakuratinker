package com.ssakura49.sakuratinker.register;

import com.ssakura49.sakuratinker.SakuraTinker;
import com.ssakura49.sakuratinker.common.entity.item.CelestialBladePart;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class STEntityDataSerializers {
    public static final DeferredRegister<EntityDataSerializer<?>> ENTITY_DATA_SERIALIZERS = DeferredRegister.create(ForgeRegistries.Keys.ENTITY_DATA_SERIALIZERS, SakuraTinker.MODID);

    public static final RegistryObject<EntityDataSerializer<CelestialBladePart>> CELESTIAL_BLADE_PART = ENTITY_DATA_SERIALIZERS.register("celestial_blade_part_item", () -> EntityDataSerializer.simple((byteBuf, part) -> part.toNetwork(byteBuf), CelestialBladePart::fromNetwork));

}
