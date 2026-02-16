package com.ssakura49.sakuratinker.data.generator.providiers;

import com.ssakura49.sakuratinker.SakuraTinker;
import com.ssakura49.sakuratinker.data.loot.TimeKillCondition;
import com.ssakura49.sakuratinker.register.STItems;
import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.LootTable.Builder;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;

import javax.annotation.Nullable;
import java.util.function.BiConsumer;

public class STEntityLootTables implements LootTableSubProvider {

    @Override
    public void generate(BiConsumer<ResourceLocation, Builder> consumer) {

        LootItemCondition.Builder timeKill = timeKill(3000, 5000, Items.NETHERITE_SWORD, null, TimeKillCondition.MatchMode.MAIN_ONLY);
        LootTable.Builder builder = LootTable.lootTable()
                .withPool(net.minecraft.world.level.storage.loot.LootPool.lootPool()
                        .setRolls(ConstantValue.exactly(1))
                        .when(timeKill)
                        .add(LootItem.lootTableItem(STItems.wu_yu.get()))
                );
        consumer.accept(ResourceLocation.fromNamespaceAndPath(SakuraTinker.MODID, "entities/enderman"), builder);
    }

    public static LootItemCondition.Builder timeKill(long min, long max, @Nullable Item mainhand, @Nullable Item offhand, TimeKillCondition.MatchMode mode) {
        return () -> new TimeKillCondition(min, max, mainhand, offhand, mode);
    }
}
