package dev.arakiel.gemini.explosion.internal;

import dev.arakiel.gemini.explosion.GeminiExplosion;
import net.minecraft.core.BlockPos;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.ExplosionDamageCalculator;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;

import java.util.Optional;

public final class GeminiExplosionImpl {
    private GeminiExplosionImpl() {
    }

    public static Explosion explode(
            GeminiExplosion.Base base,
            GeminiExplosion.BlockBehavior blocks,
            GeminiExplosion.EntityBehavior entities,
            GeminiExplosion.Presentation presentation
    ) {
        if (base.level().isClientSide) {
            throw new IllegalStateException("Gemini explosions must be created on the server");
        }
        DamageSource damageSource = base.damageSource() == null
                ? base.level().damageSources().explosion(base.source(), base.source())
                : base.damageSource();
        ExplosionDamageCalculator calculator = new ConfiguredCalculator(blocks, entities);
        return base.level().explode(
                base.source(),
                damageSource,
                calculator,
                base.x(),
                base.y(),
                base.z(),
                base.radius(),
                base.fire(),
                blocks.interaction(),
                presentation.smallParticle(),
                presentation.largeParticle(),
                presentation.sound()
        );
    }

    private static final class ConfiguredCalculator extends ExplosionDamageCalculator {
        private final GeminiExplosion.BlockBehavior blocks;
        private final GeminiExplosion.EntityBehavior entities;
        private final ExplosionDamageCalculator vanilla = new ExplosionDamageCalculator();

        private ConfiguredCalculator(
                GeminiExplosion.BlockBehavior blocks,
                GeminiExplosion.EntityBehavior entities
        ) {
            this.blocks = blocks;
            this.entities = entities;
        }

        @Override
        public Optional<Float> getBlockExplosionResistance(
                Explosion explosion,
                BlockGetter level,
                BlockPos pos,
                BlockState state,
                FluidState fluid
        ) {
            Float resistance = blocks.blockResistance(explosion, level, pos, state, fluid);
            return resistance == null
                    ? vanilla.getBlockExplosionResistance(explosion, level, pos, state, fluid)
                    : Optional.of(Math.max(0.0F, resistance));
        }

        @Override
        public boolean shouldDamageEntity(Explosion explosion, Entity entity) {
            return vanilla.shouldDamageEntity(explosion, entity) && entities.shouldDamage(entity);
        }

        @Override
        public float getEntityDamageAmount(Explosion explosion, Entity entity) {
            return (float) (vanilla.getEntityDamageAmount(explosion, entity)
                    * Math.max(0.0D, entities.damageMultiplier(entity)));
        }

        @Override
        public float getKnockbackMultiplier(Entity entity) {
            return vanilla.getKnockbackMultiplier(entity) * Math.max(0.0F, entities.knockbackMultiplier(entity));
        }
    }
}
