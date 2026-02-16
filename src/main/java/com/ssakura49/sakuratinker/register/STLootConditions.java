package com.ssakura49.sakuratinker.register;

import com.ssakura49.sakuratinker.SakuraTinker;
import com.ssakura49.sakuratinker.data.loot.TimeKillCondition;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegisterEvent;
import net.minecraftforge.registries.RegistryObject;

import javax.annotation.Nullable;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, modid = SakuraTinker.MODID)
public class STLootConditions {
    public static LootItemConditionType TIME_KILL_CONDITION;

    @SubscribeEvent
    public static void registerLoot(RegisterEvent event) {
        ResourceKey<?> key = event.getRegistryKey();

        if (key == Registries.LOOT_CONDITION_TYPE) {
            TIME_KILL_CONDITION = Registry.register(
                    BuiltInRegistries.LOOT_CONDITION_TYPE,
                    ResourceLocation.fromNamespaceAndPath(SakuraTinker.MODID, "time_kill"),
                    new LootItemConditionType(TimeKillCondition.SERIALIZER)
            );
        }
    }


}
