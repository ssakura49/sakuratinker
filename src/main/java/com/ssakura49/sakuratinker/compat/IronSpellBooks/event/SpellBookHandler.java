package com.ssakura49.sakuratinker.compat.IronSpellBooks.event;

import com.ssakura49.sakuratinker.compat.IronSpellBooks.ISSCompat;
import com.ssakura49.sakuratinker.compat.IronSpellBooks.ISSHooks;
import com.ssakura49.sakuratinker.compat.IronSpellBooks.ISSToolStats;
import com.ssakura49.sakuratinker.compat.IronSpellBooks.context.SpellAttackContext;
import com.ssakura49.sakuratinker.compat.IronSpellBooks.item.base.ModifiableSpellBookItem;
import com.ssakura49.sakuratinker.utils.SafeClassUtil;
import io.redspace.ironsspellbooks.api.events.InscribeSpellEvent;
import io.redspace.ironsspellbooks.api.events.SpellDamageEvent;
import io.redspace.ironsspellbooks.api.events.SpellOnCastEvent;
import io.redspace.ironsspellbooks.api.events.SpellPreCastEvent;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.spells.ISpellContainer;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.damage.SpellDamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.Event;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;


public class SpellBookHandler {
    public SpellBookHandler() {
    }

public static void init() {
        MinecraftForge.EVENT_BUS.addListener(SpellBookHandler::onSpellDamage);
        MinecraftForge.EVENT_BUS.addListener(SpellBookHandler::onPreCast);
        MinecraftForge.EVENT_BUS.addListener(SpellBookHandler::onSpellCast);
        MinecraftForge.EVENT_BUS.addListener(SpellBookHandler::onInscribeSpell);
        MinecraftForge.EVENT_BUS.addListener(SpellBookHandler::addSpellSlots);
}

    public static void addSpellSlots(PlayerEvent.ItemCraftedEvent event) {
        ItemStack itemStack = event.getCrafting();
        if (!itemStack.isEmpty() && itemStack.getItem() instanceof ModifiableSpellBookItem) {
            ToolStack toolStack = ToolStack.from(itemStack);
            int spellSlots = toolStack.getStats().getInt(ISSToolStats.SPELL_SLOT);
            ISpellContainer container = ISpellContainer.get(itemStack);
            if (container.getMaxSpellCount() < spellSlots) {
                var result = itemStack.copy();
                var newBook = ISpellContainer.get(result).mutableCopy();
                newBook.setMaxSpellCount(spellSlots);
                ISpellContainer.set(result, newBook.toImmutable());
            }
        }
    }

    public static void onSpellDamage(SpellDamageEvent event) {
        if (SafeClassUtil.ISSLoaded) {
            if (event.getSpellDamageSource().getEntity() instanceof Player player) {
                LivingEntity livingTarget = event.getEntity();
                Entity target = event.getEntity();
                ItemStack itemStack = Utils.getPlayerSpellbookStack(player);
                if (itemStack != null && !itemStack.isEmpty() && itemStack.getItem() instanceof ModifiableSpellBookItem) {
                    ToolStack toolStack = ToolStack.from(itemStack);
                    if (toolStack.getModifierLevel(ISSCompat.ArcaneTinkering.get()) > 0) {
                        float baseDamage = event.getAmount();
                        float spellDamage = toolStack.getStats().get(ISSToolStats.SPELL_DAMAGE);
                        float percentBonus = toolStack.getStats().get(ISSToolStats.PERCENT_BONUS);

                        SpellDamageSource source = event.getSpellDamageSource();
                        AbstractSpell spell = source.spell();
                        SpellAttackContext spellAttackContext = new SpellAttackContext(
                                player,
                                player,
                                target,
                                livingTarget,
                                source,
                                spell.getSpellId(),
                                spell.getSchoolType()
                        );
                        float damage = (baseDamage + spellDamage) * percentBonus;
                        for (ModifierEntry entry : toolStack.getModifierList()) {
                            damage = entry.getHook(ISSHooks.SPELL_DAMAGE).getSpellDamage(toolStack, entry, spellAttackContext, baseDamage, damage);
                        }
                        if (damage <= 0) {
                            event.setCanceled(true);
                            return;
                        }

                        if (event.getResult() == Event.Result.DENY) {
                            for (ModifierEntry entry : toolStack.getModifierList()) {
                                entry.getHook(ISSHooks.SPELL_HIT).failedSpellHit(toolStack, entry, spellAttackContext, damage);
                            }
                        }

                        for (ModifierEntry entry : toolStack.getModifierList()) {
                            entry.getHook(ISSHooks.SPELL_HIT).beforeSpellHit(toolStack, entry, spellAttackContext, damage, 0, 0);
                        }
                        event.setAmount(damage);

                        for (ModifierEntry entry : toolStack.getModifierList()) {
                            entry.getHook(ISSHooks.SPELL_HIT).afterSpellHit(toolStack, entry, spellAttackContext, damage);
                        }
                    }
                }
            }
        }
    }

//    public static void onCastSpell(SpellOnCastEvent event) {
//        ItemStack bookStack = Utils.getPlayerSpellbookStack(event.getEntity());
//        if (bookStack != null && !bookStack.isEmpty() && bookStack.getItem() instanceof IModifiable) {
//            ToolStack toolStack = ToolStack.from(bookStack);
//            float reduce = toolStack.getStats().get(STToolStats.SPELL_REDUCE);
//            if (reduce > 0) {
//                int originalMana = event.getManaCost();
//                int reducedMana = Math.max(0, Math.round(originalMana * (1.0f - reduce)));
//                event.setManaCost(reducedMana);
//            }
//        }
//    }

    public static void onInscribeSpell(InscribeSpellEvent event) {
        ItemStack bookStack = Utils.getPlayerSpellbookStack(event.getEntity());
        if (bookStack != null && !bookStack.isEmpty() && bookStack.getItem() instanceof ModifiableSpellBookItem) {
            IToolStackView toolStack = ToolStack.from(bookStack);
            for (ModifierEntry entry : toolStack.getModifierList()) {
                entry.getHook(ISSHooks.INSCRIBE_SPELL).onInscribeSpell(toolStack, entry, event);
            }
        }
    }

    public static void onPreCast(SpellPreCastEvent event) {
        Player player = event.getEntity();
        ItemStack itemStack = Utils.getPlayerSpellbookStack(player);
        if (itemStack != null && !itemStack.isEmpty() && itemStack.getItem() instanceof ModifiableSpellBookItem) {
            IToolStackView toolStack = ToolStack.from(itemStack);
            SpellAttackContext context = new SpellAttackContext.Builder()
                    .caster(player)
                    .spell(event.getSpellId(), event.getSchoolType(), event.getSpellLevel())
                    .build();

            for (ModifierEntry entry : toolStack.getModifierList()) {
                if (!entry.getHook(ISSHooks.SPELL_CAST).onPreCast(toolStack, entry, context)) {
                    event.setCanceled(true);
                    return;
                }
            }
        }
    }

    public static void onSpellCast(SpellOnCastEvent event) {
            ItemStack itemStack = Utils.getPlayerSpellbookStack(event.getEntity());
            if (itemStack != null && !itemStack.isEmpty() && itemStack.getItem() instanceof ModifiableSpellBookItem) {
                IToolStackView toolStack = ToolStack.from(itemStack);
                int originalCost = event.getManaCost();
                int currentCost = originalCost;
                int reduce = toolStack.getStats().getInt(ISSToolStats.MANA_REDUCE);
                for (ModifierEntry entry : toolStack.getModifierList()) {
                    currentCost = entry.getHook(ISSHooks.MANA_COST).getManaCost(toolStack, entry, originalCost, currentCost);
                }
                event.setManaCost(currentCost-reduce);
            }
    }


}
