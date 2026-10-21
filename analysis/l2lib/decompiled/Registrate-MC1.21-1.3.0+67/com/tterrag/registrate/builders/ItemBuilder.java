package com.tterrag.registrate.builders;

import com.google.common.collect.Maps;
import com.tterrag.registrate.AbstractRegistrate;
import com.tterrag.registrate.providers.DataGenContext;
import com.tterrag.registrate.providers.ProviderType;
import com.tterrag.registrate.providers.RegistrateItemModelProvider;
import com.tterrag.registrate.providers.RegistrateRecipeProvider;
import com.tterrag.registrate.util.CreativeModeTabModifier;
import com.tterrag.registrate.util.OneTimeEventReceiver;
import com.tterrag.registrate.util.RegistrateDistExecutor;
import com.tterrag.registrate.util.entry.ItemEntry;
import com.tterrag.registrate.util.entry.RegistryEntry;
import com.tterrag.registrate.util.nullness.NonNullBiConsumer;
import com.tterrag.registrate.util.nullness.NonNullFunction;
import com.tterrag.registrate.util.nullness.NonNullSupplier;
import com.tterrag.registrate.util.nullness.NonNullUnaryOperator;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.level.ItemLike;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.datamaps.builtin.Compostable;
import net.neoforged.neoforge.registries.datamaps.builtin.FurnaceFuel;
import net.neoforged.neoforge.registries.datamaps.builtin.NeoForgeDataMaps;

public class ItemBuilder<T extends Item, P> extends AbstractBuilder<Item, T, P, ItemBuilder<T, P>> {
   private final NonNullFunction<Properties, T> factory;
   private NonNullSupplier<Properties> initialProperties = Properties::new;
   private NonNullFunction<Properties, Properties> propertiesCallback = NonNullUnaryOperator.identity();
   @Nullable
   private NonNullSupplier<Supplier<ItemColor>> colorHandler;
   private Map<ResourceKey<CreativeModeTab>, NonNullBiConsumer<DataGenContext<Item, T>, CreativeModeTabModifier>> creativeModeTabs = Maps.newLinkedHashMap();
   @Nullable
   private Function<T, NonNullSupplier<Supplier<IClientItemExtensions>>> clientExtensionFunc;

   public static <T extends Item, P> ItemBuilder<T, P> create(
      AbstractRegistrate<?> owner, P parent, String name, BuilderCallback callback, NonNullFunction<Properties, T> factory
   ) {
      return new ItemBuilder<T, P>(owner, parent, name, callback, factory).defaultModel().defaultLang();
   }

   protected ItemBuilder(AbstractRegistrate<?> owner, P parent, String name, BuilderCallback callback, NonNullFunction<Properties, T> factory) {
      super(owner, parent, name, callback, Registries.ITEM);
      this.factory = factory;
      this.onRegister(
         item -> {
            this.creativeModeTabs
               .forEach(
                  (creativeModeTab, consumer) -> owner.modifyCreativeModeTab(
                     (ResourceKey<CreativeModeTab>)creativeModeTab, modifier -> consumer.accept(DataGenContext.from(this), modifier)
                  )
               );
            this.creativeModeTabs.clear();
         }
      );
   }

   public ItemBuilder<T, P> properties(NonNullUnaryOperator<Properties> func) {
      this.propertiesCallback = this.propertiesCallback.andThen(func);
      return this;
   }

   public ItemBuilder<T, P> initialProperties(NonNullSupplier<Properties> properties) {
      this.initialProperties = properties;
      return this;
   }

   @Deprecated
   public ItemBuilder<T, P> tab(ResourceKey<CreativeModeTab> tab, Consumer<CreativeModeTabModifier> modifier) {
      return this.tab(tab, ($, m) -> modifier.accept(m));
   }

   public ItemBuilder<T, P> tab(ResourceKey<CreativeModeTab> tab, NonNullBiConsumer<DataGenContext<Item, T>, CreativeModeTabModifier> modifier) {
      this.creativeModeTabs.put(tab, modifier);
      return this;
   }

   public ItemBuilder<T, P> tab(ResourceKey<CreativeModeTab> tab) {
      return this.tab(tab, (item, modifier) -> modifier.accept(item));
   }

   public ItemBuilder<T, P> removeTab(ResourceKey<CreativeModeTab> tab) {
      this.creativeModeTabs.remove(tab);
      return this;
   }

   public ItemBuilder<T, P> color(NonNullSupplier<Supplier<ItemColor>> colorHandler) {
      if (this.colorHandler == null) {
         RegistrateDistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> this::registerItemColor);
      }

      this.colorHandler = colorHandler;
      return this;
   }

   protected void registerItemColor() {
      OneTimeEventReceiver.addModListener(this.getOwner(), net.neoforged.neoforge.client.event.RegisterColorHandlersEvent.Item.class, e -> {
         NonNullSupplier<Supplier<ItemColor>> colorHandler = this.colorHandler;
         if (colorHandler != null) {
            e.register(colorHandler.get().get(), new ItemLike[]{(ItemLike)this.getEntry()});
         }
      });
   }

   public ItemBuilder<T, P> defaultModel() {
      return this.model((ctx, prov) -> prov.generated(ctx::getEntry));
   }

   public ItemBuilder<T, P> model(NonNullBiConsumer<DataGenContext<Item, T>, RegistrateItemModelProvider> cons) {
      return this.setData(ProviderType.ITEM_MODEL, cons);
   }

   public ItemBuilder<T, P> defaultLang() {
      return this.lang(Item::getDescriptionId);
   }

   public ItemBuilder<T, P> lang(String name) {
      return this.lang(Item::getDescriptionId, name);
   }

   public ItemBuilder<T, P> recipe(NonNullBiConsumer<DataGenContext<Item, T>, RegistrateRecipeProvider> cons) {
      return this.setData(ProviderType.RECIPE, cons);
   }

   public ItemBuilder<T, P> burnTime(int tick) {
      return this.dataMap(NeoForgeDataMaps.FURNACE_FUELS, new FurnaceFuel(tick));
   }

   public ItemBuilder<T, P> compostable(float chance) {
      return this.dataMap(NeoForgeDataMaps.COMPOSTABLES, new Compostable(chance));
   }

   public ItemBuilder<T, P> clientExtension(NonNullSupplier<Supplier<IClientItemExtensions>> clientExtension) {
      if (this.clientExtensionFunc == null) {
         RegistrateDistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> this::registerClientExtension);
      }

      this.clientExtensionFunc = item -> clientExtension;
      return this;
   }

   @Deprecated(forRemoval = true)
   public ItemBuilder<T, P> clientExtension(Function<T, NonNullSupplier<Supplier<IClientItemExtensions>>> clientExtension) {
      if (this.clientExtensionFunc == null) {
         RegistrateDistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> this::registerClientExtension);
      }

      this.clientExtensionFunc = clientExtension;
      return this;
   }

   protected void registerClientExtension() {
      OneTimeEventReceiver.addModListener(this.getOwner(), RegisterClientExtensionsEvent.class, e -> {
         if (this.clientExtensionFunc != null) {
            NonNullSupplier<Supplier<IClientItemExtensions>> clientExtension = this.clientExtensionFunc.apply(this.getEntry());
            e.registerItem(clientExtension.get().get(), new Item[]{this.getEntry()});
         }
      });
   }

   @SafeVarargs
   public final ItemBuilder<T, P> tag(TagKey<Item>... tags) {
      return this.tag(ProviderType.ITEM_TAGS, tags);
   }

   protected T createEntry() {
      Properties properties = this.initialProperties.get();
      properties = this.propertiesCallback.apply(properties);
      return this.factory.apply(properties);
   }

   @Override
   protected RegistryEntry<Item, T> createEntryWrapper(DeferredHolder<Item, T> delegate) {
      return new ItemEntry<>(this.getOwner(), delegate);
   }

   public ItemEntry<T> register() {
      return (ItemEntry<T>)super.register();
   }
}
