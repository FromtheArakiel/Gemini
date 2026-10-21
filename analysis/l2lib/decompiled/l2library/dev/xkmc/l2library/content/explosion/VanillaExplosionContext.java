package dev.xkmc.l2library.content.explosion;

import javax.annotation.Nullable;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.ExplosionDamageCalculator;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.Explosion.BlockInteraction;
import net.minecraft.world.level.Level.ExplosionInteraction;
import net.neoforged.neoforge.event.EventHooks;

public record VanillaExplosionContext(
   @Nullable Entity entity, @Nullable DamageSource source, @Nullable ExplosionDamageCalculator calculator, boolean fire, BlockInteraction type
) {
   public VanillaExplosionContext(
      Level level,
      @Nullable Entity entity,
      @Nullable DamageSource source,
      @Nullable ExplosionDamageCalculator calculator,
      boolean fire,
      ExplosionInteraction type
   ) {
      this(entity, source, calculator, fire, getType(level, entity, type));
   }

   private static BlockInteraction getType(Level level, @Nullable Entity entity, ExplosionInteraction type) {
      return switch (type) {
         case NONE -> BlockInteraction.KEEP;
         case BLOCK -> level.getDestroyType(GameRules.RULE_BLOCK_EXPLOSION_DROP_DECAY);
         case MOB -> EventHooks.canEntityGrief(level, entity instanceof LivingEntity le ? le : null)
            ? level.getDestroyType(GameRules.RULE_MOB_EXPLOSION_DROP_DECAY)
            : BlockInteraction.KEEP;
         case TNT -> level.getDestroyType(GameRules.RULE_TNT_EXPLOSION_DROP_DECAY);
         case TRIGGER -> BlockInteraction.TRIGGER_BLOCK;
         default -> throw new MatchException(null, null);
      };
   }
}
