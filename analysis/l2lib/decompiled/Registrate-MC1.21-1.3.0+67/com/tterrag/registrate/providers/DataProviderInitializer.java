package com.tterrag.registrate.providers;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import javax.annotation.Nullable;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.RegistrySetBuilder.RegistryBootstrap;
import net.minecraft.resources.ResourceKey;

public class DataProviderInitializer {
   private final RegistrySetBuilder datapackEntryProvider = new RegistrySetBuilder();
   private final Map<ProviderType<?>, ProviderType<? extends RegistrateLookupFillerProvider>> providerDependencies = new ConcurrentHashMap<>();

   public DataProviderInitializer() {
      this.addDependency(ProviderType.ITEM_TAGS, ProviderType.BLOCK_TAGS);
   }

   protected RegistrySetBuilder getDatapackRegistryProviders() {
      return this.datapackEntryProvider;
   }

   protected List<DataProviderInitializer.Sorted> getSortedProviders() {
      List<DataProviderInitializer.Sorted> ans = new ArrayList<>();
      Set<ProviderType<?>> added = new HashSet<>();
      List<Entry<String, ProviderType<?>>> remain = new ArrayList<>(RegistrateDataProvider.TYPES.entrySet());

      while (!remain.isEmpty()) {
         if (!remain.removeIf(e -> {
            ProviderType<?> type = e.getValue();
            ProviderType<? extends RegistrateLookupFillerProvider> parent = this.providerDependencies.get(type);
            if (parent != null && !added.contains(parent)) {
               return false;
            }

            ans.add(new DataProviderInitializer.Sorted(e.getKey(), type, parent));
            added.add(type);
            return true;
         })) {
            throw new IllegalStateException("Looping dependency detected: " + remain);
         }
      }

      return ans;
   }

   public <T> void add(ResourceKey<Registry<T>> registry, RegistryBootstrap<T> provider) {
      this.datapackEntryProvider.add(registry, provider);
   }

   public void addDependency(ProviderType<?> dependent, ProviderType<? extends RegistrateLookupFillerProvider> parent) {
      ProviderType<? extends RegistrateLookupFillerProvider> old = this.providerDependencies.put(dependent, parent);
      if (old != null) {
         throw new IllegalStateException("Providers can have only 1 prerequisite");
      }
   }

   public record Sorted(String id, ProviderType<?> type, @Nullable ProviderType<? extends RegistrateLookupFillerProvider> parent) {
   }
}
