package dev.xkmc.l2tabs.compat.api;

import javax.annotation.Nullable;
import net.minecraft.world.entity.LivingEntity;

public abstract class IAccessoriesWrapper {
   public final LivingEntity entity;
   public int total;
   public int page;

   public IAccessoriesWrapper(LivingEntity entity) {
      this.entity = entity;
   }

   public abstract int getSize();

   public abstract int getRows();

   @Nullable
   public abstract IAccessoriesSlotWrapper getSlotAtPosition(int var1);
}
