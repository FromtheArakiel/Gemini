package com.tterrag.registrate.providers;

import com.tterrag.registrate.AbstractRegistrate;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.neoforged.fml.LogicalSide;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import org.apache.commons.compress.utils.Lists;
import org.jetbrains.annotations.ApiStatus.Internal;

public final class RegistrateGenericProvider implements RegistrateProvider {
   private final AbstractRegistrate<?> registrate;
   private final PackOutput output;
   private final CompletableFuture<Provider> registries;
   private final ExistingFileHelper existingFileHelper;
   private final LogicalSide side;
   private final ProviderType<RegistrateGenericProvider> providerType;
   private final List<RegistrateGenericProvider.Generator> generators = Lists.newArrayList();

   @Internal
   RegistrateGenericProvider(AbstractRegistrate<?> registrate, GatherDataEvent event, LogicalSide side, ProviderType<RegistrateGenericProvider> providerType) {
      this.registrate = registrate;
      this.side = side;
      this.providerType = providerType;
      this.output = event.getGenerator().getPackOutput();
      this.registries = event.getLookupProvider();
      this.existingFileHelper = event.getExistingFileHelper();
   }

   public RegistrateGenericProvider add(RegistrateGenericProvider.Generator generator) {
      this.generators.add(generator);
      return this;
   }

   @Override
   public LogicalSide getSide() {
      return this.side;
   }

   public CompletableFuture<?> run(CachedOutput cache) {
      this.generators.clear();
      RegistrateGenericProvider.GeneratorData data = new RegistrateGenericProvider.GeneratorData(this.output, this.registries, this.existingFileHelper);
      this.registrate.genData(this.providerType, this);
      return CompletableFuture.allOf(
         this.generators.stream().map(generator -> generator.generate(data)).map(provider -> provider.run(cache)).toArray(CompletableFuture[]::new)
      );
   }

   public String getName() {
      return "generic_%s_provider".formatted(this.side.name().toLowerCase(Locale.ROOT));
   }

   @FunctionalInterface
   public interface Generator {
      DataProvider generate(RegistrateGenericProvider.GeneratorData var1);
   }

   public record GeneratorData(PackOutput output, CompletableFuture<Provider> registries, ExistingFileHelper existingFileHelper) {
   }
}
