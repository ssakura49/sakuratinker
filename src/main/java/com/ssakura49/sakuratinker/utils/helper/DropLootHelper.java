package com.ssakura49.sakuratinker.utils.helper;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;

public class DropLootHelper {
    /**
     * 触发实体的掉落物生成
     * @param entity 目标
     * @param player 攻击者
     * @param forceAllDrops 是否强制掉落所有可能的物品
     */
    public static void dropLoot(Entity entity, Player player, boolean forceAllDrops) {
        Level level = entity.level();
        if (level instanceof ServerLevel serverLevel) {
            DamageSource damageSource = new DamageSource(player.level().registryAccess().registryOrThrow(net.minecraft.core.registries.Registries.DAMAGE_TYPE).getHolderOrThrow(net.minecraft.world.damagesource.DamageTypes.PLAYER_ATTACK), player);
            if (!forceAllDrops) {
                if (entity instanceof LivingEntity le) {
                    ExperienceOrb.award(serverLevel, entity.position(), le.getExperienceReward());
                    le.dropFromLootTable(damageSource, true);
                    le.dropCustomDeathLoot(damageSource, 20, true);
                    le.dropEquipment();
                    le.dropAllDeathLoot(damageSource);
                }
            } else {
                forceDropAllLoot(entity, player, damageSource, serverLevel);
            }
        }
    }

    private static void forceDropAllLoot(Entity entity, Player player, DamageSource damageSource, ServerLevel serverLevel) {
        if (entity instanceof LivingEntity livingEntity) {
            livingEntity.dropFromLootTable(damageSource, true);
        }
        if (entity instanceof Monster) {
            ExperienceOrb.award(serverLevel, entity.position(), 5 + serverLevel.random.nextInt(10));
        }
        forceDropFromLootTable(entity, player, damageSource, serverLevel);
        forceSpecialDrops(entity, player, damageSource, serverLevel);
    }

    /**
     * 手动解析实体的 LootTable（掉落表）并生成物品
     */
    private static void forceDropFromLootTable(Entity entity, Player player, DamageSource damageSource, ServerLevel serverLevel) {
        ResourceLocation lootTableLocation = getLootTableLocation(entity);
        if (lootTableLocation != null) {
            LootParams.Builder lootParamsBuilder = (new LootParams.Builder(serverLevel))
                    .withParameter(LootContextParams.THIS_ENTITY, entity)
                    .withParameter(LootContextParams.ORIGIN, entity.position())
                    .withParameter(LootContextParams.DAMAGE_SOURCE, damageSource)
                    .withOptionalParameter(LootContextParams.KILLER_ENTITY, player)
                    .withOptionalParameter(LootContextParams.DIRECT_KILLER_ENTITY, player)
                    .withParameter(LootContextParams.LAST_DAMAGE_PLAYER, player);
            ItemStack tool = player.getMainHandItem();
            lootParamsBuilder.withOptionalParameter(LootContextParams.TOOL, tool);
            int lootingLevel = tool.getEnchantmentLevel(Enchantments.MOB_LOOTING);
            if (lootingLevel > 0) {
                lootParamsBuilder.withLuck((float)lootingLevel);
            }

            LootParams lootParams = lootParamsBuilder.create(LootContextParamSets.ENTITY);
            LootTable lootTable = serverLevel.getServer().getLootData().getLootTable(lootTableLocation);
            for (ItemStack lootItem : lootTable.getRandomItems(lootParams)) {
                entity.spawnAtLocation(lootItem);
            }
        }
    }

    /**
     * 强制掉落实体身上穿着的装备和手持物品
     */
    private static void forceSpecialDrops(Entity entity, Player player, DamageSource damageSource, ServerLevel serverLevel) {
        if (entity instanceof LivingEntity livingEntity) {
            ItemStack mainHand = livingEntity.getMainHandItem();
            if (!mainHand.isEmpty()) {
                entity.spawnAtLocation(mainHand.copy());
            }
            ItemStack offHand = livingEntity.getOffhandItem();
            if (!offHand.isEmpty()) {
                entity.spawnAtLocation(offHand.copy());
            }
            for (ItemStack armorPiece : livingEntity.getArmorSlots()) { // m_6168_
                if (!armorPiece.isEmpty()) {
                    entity.spawnAtLocation(armorPiece.copy());
                }
            }
        }
    }

    private static ResourceLocation getLootTableLocation(Entity entity) {
        if (entity instanceof LivingEntity livingEntity) {
            return livingEntity.getLootTable();
        }
        return null;
    }
}
