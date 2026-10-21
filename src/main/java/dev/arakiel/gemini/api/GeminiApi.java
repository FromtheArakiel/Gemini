package dev.arakiel.gemini.api;

import dev.arakiel.gemini.Gemini;
import dev.arakiel.gemini.func.GeminiCombatImpl;
import dev.arakiel.gemini.func.GeminiCreativeTabsImpl;
import dev.arakiel.gemini.func.GeminiExplosionImpl;
import dev.arakiel.gemini.func.GeminiTweaksImpl;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ItemLike;

import java.util.Collection;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

public final class GeminiApi {
    private GeminiApi() {
    }

    public static void init() {
        GeminiCombatImpl.init();
        GeminiNetwork.init();
        GeminiConfig.init();
    }

    public static AttackEffect attackEffect(Supplier<MobEffectInstance> effect) {
        return new AttackEffect(effect);
    }

    public static AttackEffect attackEffect(
            Item weapon,
            Holder<MobEffect> effect,
            int duration,
            int amplifier
    ) {
        return attackEffect(() -> new MobEffectInstance(effect, duration, amplifier)).withWeapon(weapon);
    }

    public static AttackEffect attackSelf(
            Item weapon,
            Holder<MobEffect> effect,
            int duration,
            int amplifier
    ) {
        return attackEffect(weapon, effect, duration, amplifier).toAttacker();
    }

    public static ResourceKey<CreativeModeTab> registerCreativeTab(
            ResourceLocation id,
            Component title,
            Supplier<ItemStack> icon,
            Consumer<CreativeModeTab.Output> entries
    ) {
        return registerCreativeTab(id, CreativeModeTab.Row.TOP, 0, title, icon, entries);
    }

    public static ResourceKey<CreativeModeTab> registerCreativeTab(
            ResourceLocation id,
            CreativeModeTab.Row row,
            int column,
            Component title,
            Supplier<ItemStack> icon,
            Consumer<CreativeModeTab.Output> entries
    ) {
        return GeminiCreativeTabsImpl.register(id, row, column, title, icon, entries);
    }

    public static ResourceKey<CreativeModeTab> registerCreativeTab(
            String path,
            Component title,
            Supplier<ItemStack> icon,
            Consumer<CreativeModeTab.Output> entries
    ) {
        return registerCreativeTab(Gemini.id(path), title, icon, entries);
    }

    public static ResourceKey<CreativeModeTab> registerCreativeTab(
            String path,
            CreativeModeTab.Row row,
            int column,
            Component title,
            Supplier<ItemStack> icon,
            Consumer<CreativeModeTab.Output> entries
    ) {
        return registerCreativeTab(Gemini.id(path), row, column, title, icon, entries);
    }

    public static void addCreativeTabItems(ResourceKey<CreativeModeTab> tab, ItemLike... items) {
        GeminiCreativeTabsImpl.add(tab, items);
    }

    public static void addCreativeTabStacks(
            ResourceKey<CreativeModeTab> tab,
            Supplier<? extends Collection<ItemStack>> stacks
    ) {
        GeminiCreativeTabsImpl.addStacks(tab, stacks);
    }

    public static void makeLightningSafe(LightningBolt bolt, boolean safe) {
        Objects.requireNonNull(bolt, "bolt");
        GeminiTweaksImpl.makeLightningSafe(bolt, safe);
    }

    public static boolean isLightningSafe(LightningBolt bolt) {
        return GeminiTweaksImpl.isLightningSafe(bolt);
    }

    public static void setArrowDespawnFactor(AbstractArrow arrow, int factor) {
        Objects.requireNonNull(arrow, "arrow");
        GeminiTweaksImpl.setArrowDespawnFactor(arrow, factor);
    }

    public static int getArrowDespawnFactor(AbstractArrow arrow) {
        return GeminiTweaksImpl.getArrowDespawnFactor(arrow);
    }

    public static Explosion.Builder explosion(Level level, double x, double y, double z, float radius) {
        return Explosion.builder(level, x, y, z, radius);
    }

    public interface FastUseItem {
        default boolean isFastUse(ItemStack stack) {
            return true;
        }

        static boolean matches(ItemStack stack) {
            Item item = stack.getItem();
            return item instanceof FastUseItem fast && fast.isFastUse(stack);
        }
    }

    public static final class AttackEffect {
        private final Supplier<MobEffectInstance> effect;
        private Predicate<LivingEntity> attackerFilter = attacker -> true;
        private boolean applyToAttacker;

        private AttackEffect(Supplier<MobEffectInstance> effect) {
            this.effect = Objects.requireNonNull(effect, "effect");
        }

        public AttackEffect withWeapon(Item weapon) {
            Objects.requireNonNull(weapon, "weapon");
            this.attackerFilter = attacker -> attacker.getMainHandItem().is(weapon);
            return this;
        }

        public AttackEffect when(Predicate<LivingEntity> filter) {
            this.attackerFilter = this.attackerFilter.and(Objects.requireNonNull(filter, "filter"));
            return this;
        }

        public AttackEffect toAttacker() {
            this.applyToAttacker = true;
            return this;
        }

        public AttackEffect toTarget() {
            this.applyToAttacker = false;
            return this;
        }

        public void register() {
            GeminiCombatImpl.register(attackerFilter, effect, applyToAttacker);
        }
    }

    public static final class Explosion {
        private Explosion() {
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
                base = new Base(
                        base.level(),
                        source,
                        base.damageSource(),
                        base.x(),
                        base.y(),
                        base.z(),
                        base.radius(),
                        base.fire()
                );
                return this;
            }

            public Builder damageSource(DamageSource source) {
                base = new Base(
                        base.level(),
                        base.source(),
                        source,
                        base.x(),
                        base.y(),
                        base.z(),
                        base.radius(),
                        base.fire()
                );
                return this;
            }

            public Builder fire(boolean fire) {
                base = new Base(
                        base.level(),
                        base.source(),
                        base.damageSource(),
                        base.x(),
                        base.y(),
                        base.z(),
                        base.radius(),
                        fire
                );
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
}
