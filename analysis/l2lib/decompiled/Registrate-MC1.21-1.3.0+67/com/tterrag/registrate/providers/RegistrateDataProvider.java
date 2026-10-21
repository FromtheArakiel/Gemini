package com.tterrag.registrate.providers;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Lists;
import com.tterrag.registrate.AbstractRegistrate;
import com.tterrag.registrate.util.DebugMarkers;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.core.Registry;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.resources.ResourceKey;
import net.neoforged.fml.LogicalSide;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RegistrateDataProvider implements DataProvider {
   private static final Logger log = LogManager.getLogger(RegistrateDataProvider.class);
   static final BiMap<String, ProviderType<?>> TYPES = HashBiMap.create();
   static final Map<ResourceKey<? extends Registry<?>>, ProviderType<?>> TAG_TYPES = new ConcurrentHashMap<>();
   private final String mod;
   private final Map<ProviderType<?>, RegistrateProvider> subProviders = new LinkedHashMap<>();
   private final CompletableFuture<Provider> registriesLookup;

   @Nullable
   public static String getTypeName(ProviderType<?> type) {
      return (String)TYPES.inverse().get(type);
   }

   public RegistrateDataProvider(AbstractRegistrate<?> parent, String modid, GatherDataEvent event) {
      this.mod = modid;
      this.registriesLookup = event.getLookupProvider();
      EnumSet<LogicalSide> sides = EnumSet.noneOf(LogicalSide.class);
      if (event.includeServer()) {
         sides.add(LogicalSide.SERVER);
      }

      if (event.includeClient()) {
         sides.add(LogicalSide.CLIENT);
      }

      log.debug(DebugMarkers.DATA, "Gathering providers for sides: {}", sides);
      Map<ProviderType<?>, RegistrateProvider> known = new HashMap<>();

      for (DataProviderInitializer.Sorted sorted : parent.getDataGenInitializer().getSortedProviders()) {
         ProviderType<?> type = sorted.type();
         CompletableFuture<Provider> lookup = this.registriesLookup;
         if (sorted.parent() != null) {
            lookup = ((RegistrateLookupFillerProvider)known.get(sorted.parent())).getFilledProvider();
         }

         RegistrateProvider prov = ProviderType.create((ProviderType<RegistrateProvider>)type, parent, event, known, lookup);
         if (prov instanceof RegistrateTagsProvider<?> tagsProvider && TAG_TYPES.get(tagsProvider.registry()) != type) {
            throw new IllegalStateException("Tag providers must be registered through ProviderType::registerTag");
         }

         known.put(type, prov);
         if (sides.contains(prov.getSide())) {
            log.debug(DebugMarkers.DATA, "Adding provider for type: {}", sorted.id());
            this.subProviders.put(type, prov);
         }
      }
   }

   public CompletableFuture<?> run(CachedOutput cache) {
      return this.registriesLookup.thenCompose(provider -> {
         ArrayList<CompletableFuture<?>> list = Lists.newArrayList();

         for (Entry<ProviderType<?>, RegistrateProvider> e : this.subProviders.entrySet()) {
            log.debug(DebugMarkers.DATA, "Generating data for type: {}", getTypeName(e.getKey()));
            list.add(e.getValue().run(cache));
         }

         return CompletableFuture.allOf(list.toArray(CompletableFuture[]::new));
      });
   }

   public String getName() {
      return "Registrate Provider for "
         + this.mod
         + " ["
         + this.subProviders.values().stream().<CharSequence>map(DataProvider::getName).collect(Collectors.joining(", "))
         + "]";
   }

   public <P extends RegistrateProvider> Optional<P> getSubProvider(ProviderType<P> type) {
      return Optional.ofNullable((P)this.subProviders.get(type));
   }
}
