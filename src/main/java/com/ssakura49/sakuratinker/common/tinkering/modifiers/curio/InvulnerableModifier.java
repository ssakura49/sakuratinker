package com.ssakura49.sakuratinker.common.tinkering.modifiers.curio;

import com.ssakura49.sakuratinker.library.tinkering.tools.STToolStats;
import com.ssakura49.sakuratinker.proxy.CommonProxy;
import com.ssakura49.tinkercuriolib.hook.TCLibHooks;
import com.ssakura49.tinkercuriolib.hook.armor.CurioEquipmentChangeModifierHook;
import com.ssakura49.tinkercuriolib.hook.armor.CurioTakeDamagePostModifierHook;
import com.ssakura49.tinkercuriolib.tools.modifier.base.TCLibBaseModifier;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import org.jetbrains.annotations.NotNull;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import top.theillusivec4.curios.api.SlotContext;

public class InvulnerableModifier extends TCLibBaseModifier implements CurioTakeDamagePostModifierHook {

    @Override
    protected void registerHooks(ModuleHookMap.@NotNull Builder builder) {
        super.registerHooks(builder);
        builder.addHook(this, TCLibHooks.CURIO_TAKE_DAMAGE_POST);
    }

    @Override
    public boolean shouldDisplay(boolean advanced) {
        return false;
    }

//    @Override
//    public void onCurioTakeDamagePost(IToolStackView tool, ModifierEntry entry, LivingDamageEvent event, LivingEntity entity, DamageSource source) {
//        if (!entity.level().isClientSide && entity instanceof Player player) {
//
//            CommonProxy.getPlayerCapOptional(player).ifPresent(cap -> {
//                cap.setInvulnerableTick(tool.getStats().getInt(STToolStats.INVULNERABLE_TIME));
//            });
//        }
//    }
}
