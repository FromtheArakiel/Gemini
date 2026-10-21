package com.tterrag.registrate.builders;

import com.tterrag.registrate.AbstractRegistrate;
import com.tterrag.registrate.providers.ProviderType;
import com.tterrag.registrate.providers.loot.RegistrateEntityLootTables;
import com.tterrag.registrate.providers.loot.RegistrateLootTableProvider;
import com.tterrag.registrate.util.OneTimeEventReceiver;
import com.tterrag.registrate.util.RegistrateDistExecutor;
import com.tterrag.registrate.util.entry.EntityEntry;
import com.tterrag.registrate.util.entry.RegistryEntry;
import com.tterrag.registrate.util.nullness.NonNullBiConsumer;
import com.tterrag.registrate.util.nullness.NonNullConsumer;
import com.tterrag.registrate.util.nullness.NonNullFunction;
import com.tterrag.registrate.util.nullness.NonNullSupplier;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.SpawnPlacementType;
import net.minecraft.world.entity.EntityType.EntityFactory;
import net.minecraft.world.entity.SpawnPlacements.SpawnPredicate;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.level.levelgen.Heightmap.Types;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.client.event.EntityRenderersEvent.RegisterRenderers;
import net.neoforged.neoforge.common.DeferredSpawnEggItem;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.event.entity.RegisterSpawnPlacementsEvent;
import net.neoforged.neoforge.event.entity.RegisterSpawnPlacementsEvent.Operation;
import net.neoforged.neoforge.registries.DeferredHolder;

public class EntityBuilder<T extends Entity, P> extends AbstractBuilder<EntityType<?>, EntityType<T>, P, EntityBuilder<T, P>> {
   private final NonNullSupplier<net.minecraft.world.entity.EntityType.Builder<T>> builder;
   private NonNullConsumer<net.minecraft.world.entity.EntityType.Builder<T>> builderCallback = $ -> {};
   @Nullable
   private NonNullSupplier<NonNullFunction<Context, EntityRenderer<? super T>>> renderer;
   private boolean attributesConfigured;
   private boolean spawnConfigured;

   public static <T extends Entity, P> EntityBuilder<T, P> create(
      AbstractRegistrate<?> owner, P parent, String name, BuilderCallback callback, EntityFactory<T> factory, MobCategory classification
   ) {
      return new EntityBuilder<T, P>(owner, parent, name, callback, factory, classification).defaultLang();
   }

   protected EntityBuilder(AbstractRegistrate<?> owner, P parent, String name, BuilderCallback callback, EntityFactory<T> factory, MobCategory classification) {
      super(owner, parent, name, callback, Registries.ENTITY_TYPE);
      this.builder = () -> net.minecraft.world.entity.EntityType.Builder.of(factory, classification);
   }

   public EntityBuilder<T, P> properties(NonNullConsumer<net.minecraft.world.entity.EntityType.Builder<T>> cons) {
      this.builderCallback = this.builderCallback.andThen(cons);
      return this;
   }

   public EntityBuilder<T, P> renderer(NonNullSupplier<NonNullFunction<Context, EntityRenderer<? super T>>> renderer) {
      if (this.renderer == null && FMLEnvironment.dist.isClient()) {
         RegistrateDistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> this::registerRenderer);
      }

      this.renderer = renderer;
      return this;
   }

   protected void registerRenderer() {
      OneTimeEventReceiver.addModListener(this.getOwner(), RegisterRenderers.class, evt -> {
         NonNullSupplier<NonNullFunction<Context, EntityRenderer<? super T>>> renderer = this.renderer;
         if (renderer != null) {
            try {
               NonNullFunction<Context, EntityRenderer<? super T>> provider = renderer.get();
               evt.registerEntityRenderer(this.getEntry(), provider::apply);
            } catch (Exception e) {
               throw new IllegalStateException("Failed to register renderer for Entity " + this.get().getId(), e);
            }
         }
      });
   }

   public EntityBuilder<T, P> attributes(Supplier<net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder> attributes) {
      if (this.attributesConfigured) {
         throw new IllegalStateException("Cannot configure attributes more than once");
      }

      this.attributesConfigured = true;
      OneTimeEventReceiver.addModListener(this.getOwner(), EntityAttributeCreationEvent.class, e -> e.put(this.getEntry(), attributes.get().build()));
      return this;
   }

   public EntityBuilder<T, P> spawnPlacement(SpawnPlacementType type, Types heightmap, SpawnPredicate<T> predicate, Operation operation) {
      if (this.spawnConfigured) {
         throw new IllegalStateException("Cannot configure spawn placement more than once");
      }

      this.spawnConfigured = true;
      this.onRegister(
         t -> OneTimeEventReceiver.addModListener(
            this.getOwner(), RegisterSpawnPlacementsEvent.class, e -> e.register(t, type, heightmap, predicate, operation)
         )
      );
      return this;
   }

   @Deprecated
   public EntityBuilder<T, P> defaultSpawnEgg(int primaryColor, int secondaryColor) {
      return this.spawnEgg(primaryColor, secondaryColor).build();
   }

   @Deprecated
   public ItemBuilder<? extends SpawnEggItem, EntityBuilder<T, P>> spawnEgg(int primaryColor, int secondaryColor) {
      NonNullSupplier<EntityType<T>> sup = this.asSupplier();
      return this.getOwner()
         .item(this, this.getName() + "_spawn_egg", p -> (T)(new DeferredSpawnEggItem(sup, primaryColor, secondaryColor, p)))
         .tab(CreativeModeTabs.SPAWN_EGGS)
         .model((ctx, prov) -> prov.withExistingParent(ctx.getName(), ResourceLocation.withDefaultNamespace("item/template_spawn_egg")));
   }

   public EntityBuilder<T, P> defaultLang() {
      return this.lang(EntityType::getDescriptionId);
   }

   public EntityBuilder<T, P> lang(String name) {
      return this.lang(EntityType::getDescriptionId, name);
   }

   public EntityBuilder<T, P> loot(NonNullBiConsumer<RegistrateEntityLootTables, EntityType<T>> cons) {
      return this.setData(
         ProviderType.LOOT, (ctx, prov) -> prov.addLootAction(RegistrateLootTableProvider.LootType.ENTITY, tb -> cons.accept((T)tb, ctx.getEntry()))
      );
   }

   @SafeVarargs
   public final EntityBuilder<T, P> tag(TagKey<EntityType<?>>... tags) {
      return this.tag(ProviderType.ENTITY_TAGS, tags);
   }

   protected EntityType<T> createEntry() {
      net.minecraft.world.entity.EntityType.Builder<T> builder = this.builder.get();
      this.builderCallback.accept(builder);
      return builder.build(this.getName());
   }

   @Deprecated
   protected void injectSpawnEggType(EntityType<T> entry) {
   }

   @Override
   protected RegistryEntry<EntityType<?>, EntityType<T>> createEntryWrapper(DeferredHolder<EntityType<?>, EntityType<T>> delegate) {
      return new EntityEntry<>(this.getOwner(), delegate);
   }

   public EntityEntry<T> register() {
      return (EntityEntry<T>)super.register();
   }
}
