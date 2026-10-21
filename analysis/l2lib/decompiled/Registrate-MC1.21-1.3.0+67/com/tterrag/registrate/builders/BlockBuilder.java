package com.tterrag.registrate.builders;

import com.google.common.base.Preconditions;
import com.google.gson.JsonElement;
import com.tterrag.registrate.AbstractRegistrate;
import com.tterrag.registrate.providers.DataGenContext;
import com.tterrag.registrate.providers.ProviderType;
import com.tterrag.registrate.providers.RegistrateBlockstateProvider;
import com.tterrag.registrate.providers.RegistrateRecipeProvider;
import com.tterrag.registrate.providers.loot.RegistrateBlockLootTables;
import com.tterrag.registrate.providers.loot.RegistrateLootTableProvider;
import com.tterrag.registrate.util.OneTimeEventReceiver;
import com.tterrag.registrate.util.RegistrateDistExecutor;
import com.tterrag.registrate.util.entry.BlockEntry;
import com.tterrag.registrate.util.entry.RegistryEntry;
import com.tterrag.registrate.util.nullness.NonNullBiConsumer;
import com.tterrag.registrate.util.nullness.NonNullBiFunction;
import com.tterrag.registrate.util.nullness.NonNullFunction;
import com.tterrag.registrate.util.nullness.NonNullSupplier;
import com.tterrag.registrate.util.nullness.NonNullUnaryOperator;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.client.color.block.BlockColor;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.util.ObfuscationReflectionHelper;
import net.neoforged.neoforge.client.extensions.common.IClientBlockExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider.ConfiguredModelList;
import net.neoforged.neoforge.registries.DeferredHolder;

public class BlockBuilder<T extends Block, P> extends AbstractBuilder<Block, T, P, BlockBuilder<T, P>> {
   private final NonNullFunction<Properties, T> factory;
   private NonNullSupplier<Properties> initialProperties;
   private NonNullFunction<Properties, Properties> propertiesCallback = NonNullUnaryOperator.identity();
   private List<Supplier<Supplier<RenderType>>> renderLayers = new ArrayList<>(1);
   @Nullable
   private NonNullSupplier<Supplier<BlockColor>> colorHandler;
   @Nullable
   private Function<T, NonNullSupplier<Supplier<IClientBlockExtensions>>> clientExtensionFunc;

   public static <T extends Block, P> BlockBuilder<T, P> create(
      AbstractRegistrate<?> owner, P parent, String name, BuilderCallback callback, NonNullFunction<Properties, T> factory
   ) {
      return new BlockBuilder<T, P>(owner, parent, name, callback, factory, () -> Properties.of()).defaultBlockstate().defaultLoot().defaultLang();
   }

   protected BlockBuilder(
      AbstractRegistrate<?> owner,
      P parent,
      String name,
      BuilderCallback callback,
      NonNullFunction<Properties, T> factory,
      NonNullSupplier<Properties> initialProperties
   ) {
      super(owner, parent, name, callback, Registries.BLOCK);
      this.factory = factory;
      this.initialProperties = initialProperties;
   }

   public BlockBuilder<T, P> properties(NonNullUnaryOperator<Properties> func) {
      this.propertiesCallback = this.propertiesCallback.andThen(func);
      return this;
   }

   public BlockBuilder<T, P> initialProperties(NonNullSupplier<? extends Block> block) {
      this.initialProperties = () -> Properties.ofFullCopy((BlockBehaviour)block.get());
      return this;
   }

   @Deprecated(forRemoval = true)
   public BlockBuilder<T, P> addLayer(Supplier<Supplier<RenderType>> layer) {
      RegistrateDistExecutor.unsafeRunWhenOn(
         Dist.CLIENT, () -> () -> Preconditions.checkArgument(RenderType.chunkBufferLayers().contains(layer.get().get()), "Invalid block layer: " + layer)
      );
      if (this.renderLayers.isEmpty()) {
         this.onRegister(this::registerLayers);
      }

      this.renderLayers.add(layer);
      return this;
   }

   protected void registerLayers(T entry) {
      RegistrateDistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> OneTimeEventReceiver.addModListener(this.getOwner(), FMLClientSetupEvent.class, $ -> {
         if (this.renderLayers.size() == 1) {
            RenderType layer = this.renderLayers.get(0).get().get();
            ItemBlockRenderTypes.setRenderLayer(entry, layer);
         } else if (this.renderLayers.size() > 1) {
            Set<RenderType> layers = this.renderLayers.stream().map(s -> s.get().get()).collect(Collectors.toSet());
            ItemBlockRenderTypes.setRenderLayer(entry, layers::contains);
         }
      }));
   }

   public BlockBuilder<T, P> simpleItem() {
      return this.item().build();
   }

   public ItemBuilder<BlockItem, BlockBuilder<T, P>> item() {
      return this.item(BlockItem::new);
   }

   public <I extends Item> ItemBuilder<I, BlockBuilder<T, P>> item(NonNullBiFunction<? super T, net.minecraft.world.item.Item.Properties, ? extends I> factory) {
      NonNullSupplier<T> sup = this.asSupplier();
      return (ItemBuilder<I, BlockBuilder<T, P>>)this.getOwner()
         .item(this, this.getName(), p -> factory.apply(this.getEntry(), p))
         .setData(ProviderType.LANG, NonNullBiConsumer.noop())
         .model(
            (ctx, prov) -> {
               Optional<String> model = this.getOwner()
                  .getDataProvider(ProviderType.BLOCKSTATE)
                  .flatMap(p -> p.getExistingVariantBuilder(this.getEntry()))
                  .map(b -> (ConfiguredModelList)b.getModels().get(b.partialState()))
                  .<JsonElement>map(ConfiguredModelList::toJSON)
                  .filter(JsonElement::isJsonObject)
                  .map(j -> j.getAsJsonObject().get("model"))
                  .map(JsonElement::getAsString);
               if (model.isPresent()) {
                  prov.withExistingParent(ctx.getName(), model.get());
               } else {
                  prov.blockItem(sup);
               }
            }
         );
   }

   public <BE extends BlockEntity> BlockBuilder<T, P> simpleBlockEntity(BlockEntityBuilder.BlockEntityFactory<BE> factory) {
      return this.blockEntity(factory).build();
   }

   public <BE extends BlockEntity> BlockEntityBuilder<BE, BlockBuilder<T, P>> blockEntity(BlockEntityBuilder.BlockEntityFactory<BE> factory) {
      return this.getOwner().blockEntity(this, this.getName(), factory).validBlock(this.asSupplier());
   }

   public BlockBuilder<T, P> color(NonNullSupplier<Supplier<BlockColor>> colorHandler) {
      if (this.colorHandler == null) {
         RegistrateDistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> this::registerBlockColor);
      }

      this.colorHandler = colorHandler;
      return this;
   }

   protected void registerBlockColor() {
      OneTimeEventReceiver.addModListener(this.getOwner(), net.neoforged.neoforge.client.event.RegisterColorHandlersEvent.Block.class, e -> {
         NonNullSupplier<Supplier<BlockColor>> colorHandler = this.colorHandler;
         if (colorHandler != null) {
            e.register(colorHandler.get().get(), new Block[]{this.getEntry()});
         }
      });
   }

   public BlockBuilder<T, P> defaultBlockstate() {
      return this.blockstate((ctx, prov) -> prov.simpleBlock(ctx.getEntry()));
   }

   public BlockBuilder<T, P> blockstate(NonNullBiConsumer<DataGenContext<Block, T>, RegistrateBlockstateProvider> cons) {
      return this.setData(ProviderType.BLOCKSTATE, cons);
   }

   public BlockBuilder<T, P> defaultLang() {
      return this.lang(Block::getDescriptionId);
   }

   public BlockBuilder<T, P> lang(String name) {
      return this.lang(Block::getDescriptionId, name);
   }

   public BlockBuilder<T, P> defaultLoot() {
      return this.loot(RegistrateBlockLootTables::dropSelf);
   }

   public BlockBuilder<T, P> loot(NonNullBiConsumer<RegistrateBlockLootTables, T> cons) {
      return this.setData(ProviderType.LOOT, (ctx, prov) -> prov.addLootAction(RegistrateLootTableProvider.LootType.BLOCK, tb -> {
         if (!((Block)ctx.getEntry()).getLootTable().equals(BuiltInLootTables.EMPTY)) {
            cons.accept(tb, (T)ctx.getEntry());
         }
      }));
   }

   public BlockBuilder<T, P> recipe(NonNullBiConsumer<DataGenContext<Block, T>, RegistrateRecipeProvider> cons) {
      return this.setData(ProviderType.RECIPE, cons);
   }

   public BlockBuilder<T, P> clientExtension(NonNullSupplier<Supplier<IClientBlockExtensions>> clientExtension) {
      if (this.clientExtensionFunc == null) {
         RegistrateDistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> this::registerClientExtension);
      }

      this.clientExtensionFunc = block -> clientExtension;
      return this;
   }

   @Deprecated(forRemoval = true)
   public BlockBuilder<T, P> clientExtension(Function<T, NonNullSupplier<Supplier<IClientBlockExtensions>>> clientExtension) {
      if (this.clientExtensionFunc == null) {
         RegistrateDistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> this::registerClientExtension);
      }

      this.clientExtensionFunc = clientExtension;
      return this;
   }

   protected void registerClientExtension() {
      OneTimeEventReceiver.addModListener(this.getOwner(), RegisterClientExtensionsEvent.class, e -> {
         if (this.clientExtensionFunc != null) {
            NonNullSupplier<Supplier<IClientBlockExtensions>> clientExtension = this.clientExtensionFunc.apply(this.getEntry());
            e.registerBlock(clientExtension.get().get(), new Block[]{this.getEntry()});
         }
      });
   }

   @SafeVarargs
   public final BlockBuilder<T, P> tag(TagKey<Block>... tags) {
      return this.tag(ProviderType.BLOCK_TAGS, tags);
   }

   protected T createEntry() {
      Properties properties = this.initialProperties.get();
      ObfuscationReflectionHelper.setPrivateValue(Properties.class, properties, null, "drops");
      properties = this.propertiesCallback.apply(properties);
      return this.factory.apply(properties);
   }

   @Override
   protected RegistryEntry<Block, T> createEntryWrapper(DeferredHolder<Block, T> delegate) {
      return new BlockEntry<>(this.getOwner(), delegate);
   }

   public BlockEntry<T> register() {
      return (BlockEntry<T>)super.register();
   }
}
