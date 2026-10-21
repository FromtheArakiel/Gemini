package com.tterrag.registrate.providers.loot;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Multimap;
import com.tterrag.registrate.AbstractRegistrate;
import com.tterrag.registrate.providers.ProviderType;
import com.tterrag.registrate.providers.RegistrateProvider;
import com.tterrag.registrate.util.nullness.NonNullConsumer;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import net.minecraft.core.Holder;
import net.minecraft.core.WritableRegistry;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.data.loot.LootTableProvider.SubProviderEntry;
import net.minecraft.data.loot.packs.VanillaLootTableProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ProblemReporter.Collector;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.LootTable.Builder;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.neoforged.fml.LogicalSide;
import net.neoforged.fml.util.ObfuscationReflectionHelper;
import org.apache.commons.lang3.function.TriFunction;

public class RegistrateLootTableProvider extends LootTableProvider implements RegistrateProvider {
   private static final Map<String, RegistrateLootTableProvider.LootType<?>> LOOT_TYPES = new HashMap<>();
   private final AbstractRegistrate<?> parent;
   private final Multimap<RegistrateLootTableProvider.LootType<?>, Consumer<? super RegistrateLootTables>> specialLootActions = HashMultimap.create();
   private final Multimap<LootContextParamSet, Consumer<BiConsumer<ResourceKey<LootTable>, Builder>>> lootActions = HashMultimap.create();
   private final Set<RegistrateLootTables> currentLootCreators = new HashSet<>();
   private CompletableFuture<Provider> provider;
   private static final BiMap<ResourceLocation, LootContextParamSet> SET_REGISTRY = (BiMap<ResourceLocation, LootContextParamSet>)ObfuscationReflectionHelper.getPrivateValue(
      LootContextParamSets.class, null, "REGISTRY"
   );

   public RegistrateLootTableProvider(AbstractRegistrate<?> parent, PackOutput packOutput, CompletableFuture<Provider> provider) {
      super(packOutput, Set.of(), VanillaLootTableProvider.create(packOutput, provider).getTables(), provider);
      this.parent = parent;
      this.provider = provider;
   }

   public Provider getProvider() {
      return this.provider.getNow(null);
   }

   public <T> Holder<T> resolve(ResourceKey<T> key) {
      return this.getProvider().lookupOrThrow(key.registryKey()).getOrThrow(key);
   }

   @Override
   public LogicalSide getSide() {
      return LogicalSide.SERVER;
   }

   protected void validate(WritableRegistry<LootTable> writableregistry, ValidationContext validationcontext, Collector problemreporter$collector) {
      this.currentLootCreators.forEach(c -> c.validate(writableregistry, validationcontext));
   }

   public <T extends RegistrateLootTables> void addLootAction(RegistrateLootTableProvider.LootType<T> type, NonNullConsumer<T> action) {
      this.specialLootActions.put(type, action);
   }

   public void addLootAction(LootContextParamSet set, Consumer<BiConsumer<ResourceKey<LootTable>, Builder>> action) {
      this.lootActions.put(set, action);
   }

   private LootTableSubProvider getLootCreator(Provider provider, AbstractRegistrate<?> parent, RegistrateLootTableProvider.LootType<?> type) {
      RegistrateLootTables creator = type.getLootCreator(provider, parent, cons -> this.specialLootActions.get(type).forEach(c -> c.accept(cons)));
      this.currentLootCreators.add(creator);
      return creator;
   }

   public List<SubProviderEntry> getTables() {
      this.parent.genData(ProviderType.LOOT, this);
      this.currentLootCreators.clear();
      com.google.common.collect.ImmutableList.Builder<SubProviderEntry> builder = ImmutableList.builder();

      for (RegistrateLootTableProvider.LootType<?> type : LOOT_TYPES.values()) {
         builder.add(new SubProviderEntry(provider -> this.getLootCreator(provider, this.parent, type), type.getLootSet()));
      }

      for (LootContextParamSet set : SET_REGISTRY.values()) {
         builder.add(new SubProviderEntry(provider -> callback -> this.lootActions.get(set).forEach(a -> a.accept(callback)), set));
      }

      return builder.build();
   }

   public interface LootType<T extends RegistrateLootTables> {
      RegistrateLootTableProvider.LootType<RegistrateBlockLootTables> BLOCK = register("block", LootContextParamSets.BLOCK, RegistrateBlockLootTables::new);
      RegistrateLootTableProvider.LootType<RegistrateEntityLootTables> ENTITY = register("entity", LootContextParamSets.ENTITY, RegistrateEntityLootTables::new);

      T getLootCreator(Provider var1, AbstractRegistrate<?> var2, Consumer<T> var3);

      LootContextParamSet getLootSet();

      static <T extends RegistrateLootTables> RegistrateLootTableProvider.LootType<T> register(
         String name, final LootContextParamSet set, final TriFunction<Provider, AbstractRegistrate, Consumer<T>, T> factory
      ) {
         RegistrateLootTableProvider.LootType<T> type = new RegistrateLootTableProvider.LootType<T>() {
            @Override
            public T getLootCreator(Provider provider, AbstractRegistrate<?> parent, Consumer<T> callback) {
               return (T)factory.apply(provider, parent, callback);
            }

            @Override
            public LootContextParamSet getLootSet() {
               return set;
            }
         };
         RegistrateLootTableProvider.LOOT_TYPES.put(name, type);
         return type;
      }
   }
}
