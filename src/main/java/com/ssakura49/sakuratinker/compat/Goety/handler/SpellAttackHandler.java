package com.ssakura49.sakuratinker.compat.Goety.handler;

import com.Polarice3.Goety.utils.WandUtil;
import com.fuyun.cloudertinker.register.CloudertinkerModifiers;
import com.ssakura49.sakuratinker.STConfig;
import com.ssakura49.sakuratinker.library.tinkering.tools.STToolStats;
import com.ssakura49.sakuratinker.library.tinkering.tools.item.ModifiableWandItem;
import com.ssakura49.sakuratinker.register.STModifiers;
import com.ssakura49.sakuratinker.utils.tinker.ModifierConfigHelper;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;
import slimeknights.tconstruct.library.tools.stat.ToolStats;

import java.util.function.Supplier;

public class SpellAttackHandler {
    private static final Supplier<Boolean> bonus = STConfig.Common.TINKER_DARK_WAND_ATTACK_BONUS;
    private static final Supplier<Boolean> disabled = STConfig.Common.TinkerDarkWandTriggerModifier;

    public static void onLivingHurt(LivingHurtEvent event) {
        DamageSource source = event.getSource();
        LivingEntity target = event.getEntity();
        if (target.level().isClientSide) return;
        if (source.getEntity() instanceof Player player) {
            ItemStack wandStack = WandUtil.findWand(player);
             if (!wandStack.isEmpty() && wandStack.getItem() instanceof ModifiableWandItem) {
                 ToolStack tool = ToolStack.from(wandStack);
                 if (tool.getModifierLevel(STModifiers.G_SOUL_INFUSION.get())>0) {
                     boolean a = bonus.get();
                     float baseDamage = tool.getStats().get(ToolStats.ATTACK_DAMAGE);
                     float soulPower = tool.getStats().get(STToolStats.SOUL_POWER);
                     float soulIncrease = tool.getStats().get(STToolStats.SOUL_INCREASE);
                     if (!tool.isBroken()) {
                         boolean dis = disabled.get();
                         if (!dis) {
                             ToolAttackContext context = new ToolAttackContext(
                                     player,
                                     player,
                                     InteractionHand.MAIN_HAND,
                                     target,
                                     target,
                                     false,
                                     0,
                                     false
                             );
                             float eventDamage = event.getAmount();
                             float modifiedDamage = (eventDamage + (a ? baseDamage : 0) + soulPower) * (1 + soulIncrease);

                             for (ModifierEntry entry : tool.getModifierList()) {
                                 if (ModifierConfigHelper.isBlacklisted(entry)) {
                                     continue;
                                 }
                                 modifiedDamage = entry.getHook(ModifierHooks.MELEE_DAMAGE).getMeleeDamage(tool, entry, context, baseDamage, modifiedDamage);
                             }
                             event.setAmount(modifiedDamage);

                             for (ModifierEntry entry : tool.getModifierList()) {
                                 entry.getHook(ModifierHooks.MELEE_HIT).beforeMeleeHit(tool, entry, context, modifiedDamage, 0, 0);
                             }
                             for (ModifierEntry entry : tool.getModifierList()) {
                                 entry.getHook(ModifierHooks.MELEE_HIT).afterMeleeHit(tool, entry, context, modifiedDamage);
                             }
                         } else {
                             float eventDamage = event.getAmount();
                             float modifiedDamage = (eventDamage + (a ? baseDamage : 0) + soulPower) * (1 + soulIncrease);
                             event.setAmount(modifiedDamage);
                         }
                     }
                 }
            }
        }
    }
}
