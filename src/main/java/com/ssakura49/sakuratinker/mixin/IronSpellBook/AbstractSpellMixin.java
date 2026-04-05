package com.ssakura49.sakuratinker.mixin.IronSpellBook;

import com.ssakura49.sakuratinker.compat.IronSpellBooks.modifiers.SpellConcentration;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import slimeknights.tconstruct.library.tools.capability.TinkerDataCapability;

import javax.annotation.Nullable;

@Mixin(AbstractSpell.class)
public abstract class AbstractSpellMixin {
    @Inject(method = "canBeInterrupted",
            at = @At("RETURN"),
            cancellable = true,
            remap = false
    )
    private void sakura$canBeInterrupted(@Nullable Player player, CallbackInfoReturnable<Boolean> cir) {
        if (player == null) return;
        player.getCapability(TinkerDataCapability.CAPABILITY).ifPresent(cap -> {
            int lvl = cap.get(SpellConcentration.KEY, 0);
            if (lvl > 0 && cir.getReturnValue()) {
                cir.setReturnValue(false);
            }
        });
    }
}
