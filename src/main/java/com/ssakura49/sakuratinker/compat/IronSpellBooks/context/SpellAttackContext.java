package com.ssakura49.sakuratinker.compat.IronSpellBooks.context;

import io.redspace.ironsspellbooks.api.spells.CastSource;
import io.redspace.ironsspellbooks.api.spells.SchoolType;
import io.redspace.ironsspellbooks.damage.SpellDamageSource;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class SpellAttackContext {
    @Nonnull
    private final LivingEntity attacker;
    @Nullable
    private final Player playerAttacker;
    @Nonnull
    private final Entity target;
    @Nullable
    private final LivingEntity livingTarget;

    @Nonnull
    private final SpellDamageSource source;

    private final String spellId;
    private final SchoolType schoolType;


    public SpellAttackContext(@NotNull LivingEntity attacker, @Nullable Player playerAttacker, @NotNull Entity target, @Nullable LivingEntity livingTarget,
                              @Nonnull SpellDamageSource source, String spellId,
                              SchoolType schoolType
    ) {
        this.attacker = attacker;
        this.playerAttacker = playerAttacker;
        this.target = target;
        this.livingTarget = livingTarget;
        this.source = source;
        this.spellId = spellId;
        this.schoolType = schoolType;
    }

    public Level getLevel() {
        return attacker.level();
    }

    @Nullable
    public Player getPlayerAttacker() {
        return playerAttacker;
    }

    @Nonnull
    public Entity getTarget() {
        return target;
    }

    @Nonnull
    public LivingEntity getAttacker(){
        return attacker;
    }

    @Nullable
    public LivingEntity getLivingTarget() {
        return livingTarget;
    }

    @Nonnull
    public SpellDamageSource getSpellDamageSource() {
        return source;
    }

    public String getSpellId() {
        return spellId;
    }

    public SchoolType getSchoolType() {
        return schoolType;
    }

    public static class Builder {
        private LivingEntity caster;
        private Player playerCaster;
        private SpellDamageSource source;
        private Entity targetEntity;
        private LivingEntity target;
        private String spellId;
        private SchoolType schoolType;

        public Builder caster(LivingEntity caster) {
            this.caster = caster;
            this.playerCaster = caster instanceof Player ? (Player)caster : null;
            return this;
        }

        public Builder spellSource(SpellDamageSource source) {
            this.source = source;
            return this;
        }

        public Builder target(LivingEntity target) {
            this.target = target;
            return this;
        }

        public Builder targetEntity(Entity entity) {
            this.targetEntity = entity;
            return this;
        }

        public Builder spell(String spellId, SchoolType schoolType, int spellLevel) {
            this.spellId = spellId;
            this.schoolType = schoolType;
            return this;
        }

        public SpellAttackContext build() {
            return new SpellAttackContext(caster, playerCaster,targetEntity,target,source,
                    spellId, schoolType);
        }
    }

}
