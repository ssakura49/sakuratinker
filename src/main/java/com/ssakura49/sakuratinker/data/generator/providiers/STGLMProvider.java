package com.ssakura49.sakuratinker.data.generator.providiers;

import com.mojang.serialization.Codec;
import com.ssakura49.sakuratinker.SakuraTinker;
import com.ssakura49.sakuratinker.data.loot.TimeKillLootModifier;
import com.ssakura49.sakuratinker.register.STItems;
import com.ssakura49.sakuratinker.utils.data.LootTableTemplate;
import net.minecraft.data.PackOutput;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraftforge.common.data.GlobalLootModifierProvider;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class STGLMProvider extends GlobalLootModifierProvider {
    public STGLMProvider(PackOutput output) {
        super(output, SakuraTinker.MODID);
    }

    public static final DeferredRegister<Codec<? extends IGlobalLootModifier>> LOOT_MODIFIERS =
            DeferredRegister.create(ForgeRegistries.Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS, SakuraTinker.MODID);
    public static final RegistryObject<Codec<TimeKillLootModifier>> LOOT_TIME_KILL = LOOT_MODIFIERS.register("loot_time_kill", () -> TimeKillLootModifier.CODEC);;

    public static void register(IEventBus bus) {
        LOOT_MODIFIERS.register(bus);
    }

    @Override
    protected void start() {
        add("time_kill_enderman",
                new TimeKillLootModifier(
                        new LootItemCondition[]{LootTableTemplate.byPlayer().build()},
                        3000L,
                        5000L,
                        STItems.wu_yu.get(),
                        EntityType.ENDERMAN,
                        Items.NETHERITE_SWORD
                )
        );
    }
}
