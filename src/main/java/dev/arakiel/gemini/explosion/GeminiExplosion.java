package dev.arakiel.gemini.explosion;

import dev.arakiel.gemini.explosion.internal.GeminiExplosionImpl;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

import java.util.Objects;

public final class GeminiExplosion {
    private GeminiExplosion() {
    }

    public static Builder builder(Base base) {
        return new Builder(base);
    }

    public static Builder builder(Level level, double x, double y, double z, float radius) {
        return builder(new Base(level, null, null, x, y, z, radius, false));
    }

    public record Base(
            Level level,
            Entity source,
            DamageSource damageSource,
            double x,
            double y,
            double z,
            float radius,
            boolean fire
    ) {
        public Base {
            level = Objects.requireNonNull(level, "level");
            if (radius < 0.0F) {
                throw new IllegalArgumentException("Explosion radius must be non-negative");
            }
        }
    }

    public interface BlockBehavior {
        Level.ExplosionInteraction interaction();

        default Float blockResistance(
                net.minecraft.world.level.Explosion explosion,
                net.minecraft.world.level.BlockGetter level,
                net.minecraft.core.BlockPos pos,
                net.minecraft.world.level.block.state.BlockState state,
                net.minecraft.world.level.material.FluidState fluid
        ) {
            return null;
        }

        static BlockBehavior standard(Level.ExplosionInteraction interaction) {
            return () -> Objects.requireNonNull(interaction, "interaction");
        }
    }

    @FunctionalInterface
    public interface EntityBehavior {
        boolean shouldDamage(Entity entity);

        default double damageMultiplier(Entity entity) {
            return 1.0D;
        }

        default float knockbackMultiplier(Entity entity) {
            return 1.0F;
        }

        static EntityBehavior all() {
            return entity -> true;
        }
    }

    public record Presentation(
            ParticleOptions smallParticle,
            ParticleOptions largeParticle,
            Holder<SoundEvent> sound
    ) {
        public Presentation {
            smallParticle = Objects.requireNonNull(smallParticle, "smallParticle");
            largeParticle = Objects.requireNonNull(largeParticle, "largeParticle");
            sound = Objects.requireNonNull(sound, "sound");
        }

        public static Presentation standard() {
            return new Presentation(
                    ParticleTypes.EXPLOSION,
                    ParticleTypes.EXPLOSION_EMITTER,
                    SoundEvents.GENERIC_EXPLODE
            );
        }
    }

    public static final class Builder {
        private Base base;
        private BlockBehavior block = BlockBehavior.standard(Level.ExplosionInteraction.BLOCK);
        private EntityBehavior entities = EntityBehavior.all();
        private Presentation presentation = Presentation.standard();

        private Builder(Base base) {
            this.base = Objects.requireNonNull(base, "base");
        }

        public Builder source(Entity source) {
            base = new Base(base.level(), source, base.damageSource(), base.x(), base.y(), base.z(), base.radius(), base.fire());
            return this;
        }

        public Builder damageSource(DamageSource source) {
            base = new Base(base.level(), base.source(), source, base.x(), base.y(), base.z(), base.radius(), base.fire());
            return this;
        }

        public Builder fire(boolean fire) {
            base = new Base(base.level(), base.source(), base.damageSource(), base.x(), base.y(), base.z(), base.radius(), fire);
            return this;
        }

        public Builder block(BlockBehavior block) {
            this.block = Objects.requireNonNull(block, "block");
            return this;
        }

        public Builder interaction(Level.ExplosionInteraction interaction) {
            return block(BlockBehavior.standard(interaction));
        }

        public Builder entities(EntityBehavior entities) {
            this.entities = Objects.requireNonNull(entities, "entities");
            return this;
        }

        public Builder presentation(Presentation presentation) {
            this.presentation = Objects.requireNonNull(presentation, "presentation");
            return this;
        }

        public net.minecraft.world.level.Explosion explode() {
            return GeminiExplosionImpl.explode(base, block, entities, presentation);
        }
    }
}
