package dev.xkmc.l2core.serial.config;

import com.tterrag.registrate.AbstractRegistrate;
import com.tterrag.registrate.providers.ProviderType;
import com.tterrag.registrate.providers.RegistrateProvider;
import com.tterrag.registrate.providers.ProviderType.Context;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.neoforged.fml.LogicalSide;

public class RegistrateNestedProvider implements RegistrateProvider {
   public static final ProviderType<RegistrateNestedProvider> TYPE = ProviderType.registerProvider("l2_custom", RegistrateNestedProvider::new);
   private final List<DataProvider> list = new ArrayList<>();
   private final AbstractRegistrate<?> reg;
   private final DataGenerator gen;
   private final CompletableFuture<Provider> pvd;

   public RegistrateNestedProvider(Context<RegistrateNestedProvider> ctx) {
      this.reg = ctx.parent();
      this.gen = ctx.event().getGenerator();
      this.pvd = ctx.provider();
   }

   public LogicalSide getSide() {
      return LogicalSide.SERVER;
   }

   public RegistrateNestedProvider add(BiFunction<DataGenerator, CompletableFuture<Provider>, DataProvider> factory) {
      this.list.add(factory.apply(this.gen, this.pvd));
      return this;
   }

   public CompletableFuture<?> run(CachedOutput cachedOutput) {
      this.reg.genData(TYPE, this);
      return CompletableFuture.allOf(this.list.stream().map(e -> e.run(cachedOutput)).toArray(CompletableFuture[]::new));
   }

   public String getName() {
      return "Custom Registrate Provider";
   }
}
