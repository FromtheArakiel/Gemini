package com.tterrag.registrate.providers;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.tterrag.registrate.AbstractRegistrate;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.fml.LogicalSide;
import net.neoforged.neoforge.common.conditions.ICondition;
import net.neoforged.neoforge.common.conditions.WithConditions;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RegistrateAdvancementProvider implements RegistrateProvider, Consumer<AdvancementHolder> {
   private static final Logger log = LogManager.getLogger(RegistrateAdvancementProvider.class);
   private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
   private final AbstractRegistrate<?> owner;
   private final PackOutput packOutput;
   private final CompletableFuture<Provider> registriesLookup;
   private final List<CompletableFuture<?>> advancementsToSave = Lists.newArrayList();
   private Provider provider;
   @Nullable
   private CachedOutput cache;
   private Set<ResourceLocation> seenAdvancements = new HashSet<>();

   public RegistrateAdvancementProvider(AbstractRegistrate<?> owner, PackOutput packOutputIn, CompletableFuture<Provider> registriesLookupIn) {
      this.owner = owner;
      this.packOutput = packOutputIn;
      this.registriesLookup = registriesLookupIn;
   }

   public <T> Holder<T> resolve(ResourceKey<T> key) {
      return this.provider.lookupOrThrow(key.registryKey()).getOrThrow(key);
   }

   @Override
   public LogicalSide getSide() {
      return LogicalSide.SERVER;
   }

   public MutableComponent title(String category, String name, String title) {
      return this.owner.addLang("advancements", ResourceLocation.fromNamespaceAndPath(category, name), "title", title);
   }

   public MutableComponent desc(String category, String name, String desc) {
      return this.owner.addLang("advancements", ResourceLocation.fromNamespaceAndPath(category, name), "description", desc);
   }

   public CompletableFuture<?> run(CachedOutput cache) {
      return this.registriesLookup.thenCompose(lookup -> {
         this.provider = lookup;
         this.advancementsToSave.clear();

         try {
            this.cache = cache;
            this.seenAdvancements.clear();
            this.owner.genData(ProviderType.ADVANCEMENT, this);
         } finally {
            this.cache = null;
         }

         return CompletableFuture.allOf(this.advancementsToSave.toArray(CompletableFuture[]::new));
      });
   }

   public void accept(@Nullable AdvancementHolder holder) {
      this.withConditions(holder, List.of());
   }

   public void withConditions(@Nullable AdvancementHolder holder, List<ICondition> conditions) {
      this.registriesLookup
         .thenAccept(
            lookup -> {
               CachedOutput cache = this.cache;
               if (cache == null) {
                  throw new IllegalStateException("Cannot accept advancements outside of act");
               }

               Objects.requireNonNull(holder, "Cannot accept a null advancement");
               Path path = getPath(this.packOutput.getOutputFolder(), holder);
               if (!this.seenAdvancements.add(holder.id())) {
                  throw new IllegalStateException("Duplicate advancement " + holder.id());
               }

               if (conditions.isEmpty()) {
                  this.advancementsToSave.add(DataProvider.saveStable(cache, lookup, Advancement.CODEC, holder.value(), path));
               } else {
                  this.advancementsToSave
                     .add(
                        DataProvider.saveStable(cache, lookup, Advancement.CONDITIONAL_CODEC, Optional.of(new WithConditions(conditions, holder.value())), path)
                     );
               }
            }
         );
   }

   private static Path getPath(Path pathIn, AdvancementHolder advancementIn) {
      return pathIn.resolve("data/" + advancementIn.id().getNamespace() + "/advancement/" + advancementIn.id().getPath() + ".json");
   }

   public String getName() {
      return "Advancements";
   }

   public Provider getProvider() {
      return this.provider;
   }
}
