package com.tterrag.registrate.providers;

import com.tterrag.registrate.AbstractRegistrate;
import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.data.PackOutput;
import net.neoforged.fml.LogicalSide;
import net.neoforged.neoforge.common.data.DataMapProvider;

public class RegistrateDataMapProvider extends DataMapProvider implements RegistrateProvider {
   private final AbstractRegistrate<?> parent;

   protected RegistrateDataMapProvider(AbstractRegistrate<?> parent, PackOutput output, CompletableFuture<Provider> pvd) {
      super(output, pvd);
      this.parent = parent;
   }

   @Override
   public LogicalSide getSide() {
      return LogicalSide.SERVER;
   }

   protected void gather() {
      this.parent.genData(ProviderType.DATA_MAP, this);
   }
}
