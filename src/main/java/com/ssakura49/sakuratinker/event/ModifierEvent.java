package com.ssakura49.sakuratinker.event;

import com.ssakura49.sakuratinker.STConfig;
import com.ssakura49.sakuratinker.SakuraTinker;
import com.ssakura49.sakuratinker.agent.HealthMethod.AgentHealthMethodHelper;
import com.ssakura49.sakuratinker.common.tinkering.modifiers.armor.AbsorptionModifier;
import com.ssakura49.sakuratinker.register.STModifiers;
import com.ssakura49.sakuratinker.utils.entity.EntityUtil;
import com.ssakura49.sakuratinker.utils.helper.DropLootHelper;
import com.ssakura49.sakuratinker.utils.helper.HealthModify;
import com.ssakura49.sakuratinker.utils.tinker.ToolCooldownManager;
import com.ssakura49.sakuratinker.utils.tinker.ToolUtil;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraftforge.entity.PartEntity;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.LivingKnockBackEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;
import slimeknights.tconstruct.library.tools.stat.ToolStats;

@Mod.EventBusSubscriber(modid = SakuraTinker.MODID)
public class ModifierEvent {
    public ModifierEvent(){
    }
    @SubscribeEvent
    public static void onAbsorptionPlayerTick(TickEvent.PlayerTickEvent event) {
        Player player = event.player;
        //在这里清除工具的过期数据
        ToolCooldownManager.tick(event.player);
        if (event.phase != TickEvent.Phase.END || player.level().isClientSide) return;
        if (player.tickCount % STConfig.Common.tickInterval != 0) return;
        float regen = (float) STConfig.Common.absorptionPerTick;
        CompoundTag persistentData = player.getPersistentData();
        float modAbsorption = persistentData.getFloat(AbsorptionModifier.ABSORPTION_KEY);
        float totalAbsorption = player.getAbsorptionAmount();
        int totalLevel = AbsorptionModifier.getTotalModifierLevel(player,STModifiers.Absorption.get());
        if (totalLevel > 0) {
            float maxAbsorption = player.getMaxHealth() * totalLevel;
            float absorptionToAdd = Math.min(regen, maxAbsorption - modAbsorption);
            if (absorptionToAdd > 0) {
                modAbsorption += absorptionToAdd;
                persistentData.putFloat(AbsorptionModifier.ABSORPTION_KEY, modAbsorption);
                player.setAbsorptionAmount(totalAbsorption + absorptionToAdd);
            }
        } else if (modAbsorption > 0) {
            player.setAbsorptionAmount(Math.max(0, totalAbsorption - modAbsorption));
            persistentData.putFloat(AbsorptionModifier.ABSORPTION_KEY, 0);
        }
    }



    @SubscribeEvent
    public static void onNull_AlmightyKnockback(LivingKnockBackEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        int level = ToolUtil.getModifierArmorAllLevel(player, STModifiers.Null_Almighty.get());
//        int level = NullAlmightyModifier.getNullAlmightyLevel(player);
        if (level >= 4) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onPlayerNull_AlmightyHurt(LivingHurtEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        int level = ToolUtil.getModifierArmorAllLevel(player, STModifiers.Null_Almighty.get());
//        int level = NullAlmightyModifier.getNullAlmightyLevel(player);
        DamageSource source = event.getSource();
        if (level >= 4 && !source.is(DamageTypes.FELL_OUT_OF_WORLD)) {
            event.setCanceled(true);
            player.hurtTime = 0;
            return;
        }
        float reduction = 0.25f * level;
        if (reduction > 0) {
            float original = event.getAmount();
            float reduced = original * (1 - Math.min(reduction, 1.0f));
            event.setAmount(reduced);
        }
    }
}
