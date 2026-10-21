package dev.xkmc.l2core.serial.advancements;

import dev.xkmc.l2serial.serialization.marker.SerialClass;
import dev.xkmc.l2serial.util.Wrappers;
import java.util.Optional;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger.SimpleInstance;

@SerialClass
public class BaseCriterionInstance<T extends BaseCriterionInstance<T, R>, R extends BaseCriterion<T, R>> implements SimpleInstance {
   private final R r;

   protected BaseCriterionInstance(R r) {
      this.r = r;
   }

   public Optional<ContextAwarePredicate> player() {
      return Optional.empty();
   }

   public Criterion<T> build() {
      return new Criterion(this.r, (BaseCriterionInstance)Wrappers.cast(this));
   }
}
