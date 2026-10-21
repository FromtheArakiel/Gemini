package com.tterrag.registrate.builders;

import com.google.common.annotations.Beta;
import com.google.common.base.Preconditions;
import com.tterrag.registrate.AbstractRegistrate;
import com.tterrag.registrate.providers.ProviderType;
import com.tterrag.registrate.providers.RegistrateLangProvider;
import com.tterrag.registrate.util.OneTimeEventReceiver;
import com.tterrag.registrate.util.RegistrateDistExecutor;
import com.tterrag.registrate.util.entry.FluidEntry;
import com.tterrag.registrate.util.entry.RegistryEntry;
import com.tterrag.registrate.util.nullness.NonNullBiConsumer;
import com.tterrag.registrate.util.nullness.NonNullBiFunction;
import com.tterrag.registrate.util.nullness.NonNullConsumer;
import com.tterrag.registrate.util.nullness.NonNullFunction;
import com.tterrag.registrate.util.nullness.NonNullSupplier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.function.ToIntFunction;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.client.model.generators.BlockModelBuilder;
import net.neoforged.neoforge.common.util.Lazy;
import net.neoforged.neoforge.fluids.BaseFlowingFluid;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.fluids.BaseFlowingFluid.Flowing;
import net.neoforged.neoforge.fluids.BaseFlowingFluid.Properties;
import net.neoforged.neoforge.fluids.BaseFlowingFluid.Source;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.NeoForgeRegistries.Keys;

public class FluidBuilder<T extends BaseFlowingFluid, P> extends AbstractBuilder<Fluid, T, P, FluidBuilder<T, P>> {
   private final String sourceName;
   private final String bucketName;
   private final ResourceLocation stillTexture;
   private final ResourceLocation flowingTexture;
   private final NonNullFunction<Properties, T> fluidFactory;
   @Nullable
   private final NonNullSupplier<FluidType> fluidType;
   @Nullable
   private Boolean defaultSource;
   @Nullable
   private Boolean defaultBlock;
   @Nullable
   private Boolean defaultBucket;
   private NonNullConsumer<net.neoforged.neoforge.fluids.FluidType.Properties> typeProperties = $ -> {};
   private NonNullConsumer<Properties> fluidProperties;
   @Nullable
   private Supplier<Supplier<RenderType>> layer = null;
   private boolean registerType;
   @Nullable
   private NonNullSupplier<? extends BaseFlowingFluid> source;
   private final List<TagKey<Fluid>> tags = new ArrayList<>();

   public static <P> FluidBuilder<Flowing, P> create(
      AbstractRegistrate<?> owner, P parent, String name, BuilderCallback callback, ResourceLocation stillTexture, ResourceLocation flowingTexture
   ) {
      return create(owner, parent, name, callback, stillTexture, flowingTexture, FluidBuilder::defaultFluidType, Flowing::new);
   }

   public static <P> FluidBuilder<Flowing, P> create(
      AbstractRegistrate<?> owner,
      P parent,
      String name,
      BuilderCallback callback,
      ResourceLocation stillTexture,
      ResourceLocation flowingTexture,
      FluidBuilder.FluidTypeFactory typeFactory
   ) {
      return create(owner, parent, name, callback, stillTexture, flowingTexture, typeFactory, Flowing::new);
   }

   public static <P> FluidBuilder<Flowing, P> create(
      AbstractRegistrate<?> owner,
      P parent,
      String name,
      BuilderCallback callback,
      ResourceLocation stillTexture,
      ResourceLocation flowingTexture,
      NonNullSupplier<FluidType> fluidType
   ) {
      return create(owner, parent, name, callback, stillTexture, flowingTexture, fluidType, Flowing::new);
   }

   public static <T extends BaseFlowingFluid, P> FluidBuilder<T, P> create(
      AbstractRegistrate<?> owner,
      P parent,
      String name,
      BuilderCallback callback,
      ResourceLocation stillTexture,
      ResourceLocation flowingTexture,
      NonNullFunction<Properties, T> fluidFactory
   ) {
      return create(owner, parent, name, callback, stillTexture, flowingTexture, FluidBuilder::defaultFluidType, fluidFactory);
   }

   public static <T extends BaseFlowingFluid, P> FluidBuilder<T, P> create(
      AbstractRegistrate<?> owner,
      P parent,
      String name,
      BuilderCallback callback,
      ResourceLocation stillTexture,
      ResourceLocation flowingTexture,
      FluidBuilder.FluidTypeFactory typeFactory,
      NonNullFunction<Properties, T> fluidFactory
   ) {
      return new FluidBuilder<T, P>(owner, parent, name, callback, stillTexture, flowingTexture, typeFactory, fluidFactory)
         .defaultLang()
         .defaultSource()
         .defaultBlock()
         .defaultBucket();
   }

   public static <T extends BaseFlowingFluid, P> FluidBuilder<T, P> create(
      AbstractRegistrate<?> owner,
      P parent,
      String name,
      BuilderCallback callback,
      ResourceLocation stillTexture,
      ResourceLocation flowingTexture,
      NonNullSupplier<FluidType> fluidType,
      NonNullFunction<Properties, T> fluidFactory
   ) {
      return new FluidBuilder<T, P>(owner, parent, name, callback, stillTexture, flowingTexture, fluidType, fluidFactory)
         .defaultLang()
         .defaultSource()
         .defaultBlock()
         .defaultBucket();
   }

   public FluidBuilder(
      AbstractRegistrate<?> owner,
      P parent,
      String name,
      BuilderCallback callback,
      ResourceLocation stillTexture,
      ResourceLocation flowingTexture,
      FluidBuilder.FluidTypeFactory typeFactory,
      NonNullFunction<Properties, T> fluidFactory
   ) {
      super(owner, parent, "flowing_" + name, callback, Registries.FLUID);
      this.sourceName = name;
      this.bucketName = name + "_bucket";
      this.stillTexture = stillTexture;
      this.flowingTexture = flowingTexture;
      this.fluidFactory = fluidFactory;
      this.fluidType = NonNullSupplier.lazy(() -> typeFactory.create(this.makeTypeProperties(), this.stillTexture, this.flowingTexture));
      this.registerType = true;
      String bucketName = this.bucketName;
      this.fluidProperties = p -> p.bucket(() -> (Item)owner.get(bucketName, Registries.ITEM).get())
         .block(() -> (LiquidBlock)owner.get(name, Registries.BLOCK).get());
   }

   public FluidBuilder(
      AbstractRegistrate<?> owner,
      P parent,
      String name,
      BuilderCallback callback,
      ResourceLocation stillTexture,
      ResourceLocation flowingTexture,
      NonNullSupplier<FluidType> fluidType,
      NonNullFunction<Properties, T> fluidFactory
   ) {
      super(owner, parent, "flowing_" + name, callback, Registries.FLUID);
      this.sourceName = name;
      this.bucketName = name + "_bucket";
      this.stillTexture = stillTexture;
      this.flowingTexture = flowingTexture;
      this.fluidFactory = fluidFactory;
      this.fluidType = fluidType;
      this.registerType = false;
      String bucketName = this.bucketName;
      this.fluidProperties = p -> p.bucket(() -> (Item)owner.get(bucketName, Registries.ITEM).get())
         .block(() -> (LiquidBlock)owner.get(name, Registries.BLOCK).get());
   }

   public FluidBuilder<T, P> properties(NonNullConsumer<net.neoforged.neoforge.fluids.FluidType.Properties> cons) {
      this.typeProperties = this.typeProperties.andThen(cons);
      return this;
   }

   public FluidBuilder<T, P> fluidProperties(NonNullConsumer<Properties> cons) {
      this.fluidProperties = this.fluidProperties.andThen(cons);
      return this;
   }

   public FluidBuilder<T, P> defaultLang() {
      return this.lang(f -> f.getFluidType().getDescriptionId(), RegistrateLangProvider.toEnglishName(this.sourceName));
   }

   public FluidBuilder<T, P> lang(String name) {
      return this.lang(f -> f.getFluidType().getDescriptionId(), name);
   }

   public FluidBuilder<T, P> renderType(Supplier<Supplier<RenderType>> layer) {
      RegistrateDistExecutor.unsafeRunWhenOn(
         Dist.CLIENT, () -> () -> Preconditions.checkArgument(RenderType.chunkBufferLayers().contains(layer.get().get()), "Invalid render type: " + layer)
      );
      if (this.layer == null) {
         this.onRegister(this::registerRenderType);
      }

      this.layer = layer;
      return this;
   }

   protected void registerRenderType(T entry) {
      RegistrateDistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> OneTimeEventReceiver.addModListener(this.getOwner(), FMLClientSetupEvent.class, $ -> {
         if (this.layer != null) {
            RenderType layer = this.layer.get().get();
            ItemBlockRenderTypes.setRenderLayer(entry, layer);
            ItemBlockRenderTypes.setRenderLayer(this.getSource(), layer);
         }
      }));
   }

   public FluidBuilder<T, P> defaultSource() {
      if (this.defaultSource != null) {
         throw new IllegalStateException("Cannot set a default source after a custom source has been created");
      }

      this.defaultSource = true;
      return this;
   }

   public FluidBuilder<T, P> source(NonNullFunction<Properties, ? extends BaseFlowingFluid> factory) {
      this.defaultSource = false;
      this.source = NonNullSupplier.lazy(() -> factory.apply(this.makeProperties()));
      return this;
   }

   public FluidBuilder<T, P> defaultBlock() {
      if (this.defaultBlock != null) {
         throw new IllegalStateException("Cannot set a default block after a custom block has been created");
      }

      this.defaultBlock = true;
      return this;
   }

   public BlockBuilder<LiquidBlock, FluidBuilder<T, P>> block() {
      return this.block(LiquidBlock::new);
   }

   public <B extends LiquidBlock> BlockBuilder<B, FluidBuilder<T, P>> block(
      NonNullBiFunction<T, net.minecraft.world.level.block.state.BlockBehaviour.Properties, ? extends B> factory
   ) {
      if (this.defaultBlock == Boolean.FALSE) {
         throw new IllegalStateException("Only one call to block/noBlock per builder allowed");
      }

      this.defaultBlock = false;
      NonNullSupplier<T> supplier = this.asSupplier();
      Lazy<Integer> lightLevel = Lazy.of(() -> this.fluidType.get().getLightLevel());
      ToIntFunction<BlockState> lightLevelInt = $ -> (Integer)lightLevel.get();
      return this.getOwner()
         .block(this, this.sourceName, p -> (T)factory.apply(supplier.get(), p))
         .properties(p -> net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy(Blocks.WATER).noLootTable())
         .properties(p -> p.lightLevel(lightLevelInt))
         .blockstate(
            (ctx, prov) -> prov.simpleBlock(
               (Block)ctx.getEntry(), ((BlockModelBuilder)prov.models().getBuilder(this.sourceName)).texture("particle", this.stillTexture)
            )
         );
   }

   @Beta
   public FluidBuilder<T, P> noBlock() {
      if (this.defaultBlock == Boolean.FALSE) {
         throw new IllegalStateException("Only one call to block/noBlock per builder allowed");
      }

      this.defaultBlock = false;
      return this;
   }

   public FluidBuilder<T, P> defaultBucket() {
      if (this.defaultBucket != null) {
         throw new IllegalStateException("Cannot set a default bucket after a custom bucket has been created");
      }

      this.defaultBucket = true;
      return this;
   }

   public ItemBuilder<BucketItem, FluidBuilder<T, P>> bucket() {
      return this.bucket(BucketItem::new);
   }

   public <I extends BucketItem> ItemBuilder<I, FluidBuilder<T, P>> bucket(
      NonNullBiFunction<BaseFlowingFluid, net.minecraft.world.item.Item.Properties, ? extends I> factory
   ) {
      if (this.defaultBucket == Boolean.FALSE) {
         throw new IllegalStateException("Only one call to bucket/noBucket per builder allowed");
      } else {
         this.defaultBucket = false;
         NonNullSupplier<? extends BaseFlowingFluid> source = this.source;
         if (source == null) {
            throw new IllegalStateException("Cannot create a bucket before creating a source block");
         } else {
            return this.getOwner()
               .item(this, this.bucketName, p -> (T)factory.apply(source.get(), p))
               .properties(p -> p.craftRemainder(Items.BUCKET).stacksTo(1))
               .model(
                  (ctx, prov) -> prov.generated(ctx::getEntry, ResourceLocation.fromNamespaceAndPath(this.getOwner().getModid(), "item/" + this.bucketName))
               );
         }
      }
   }

   @Beta
   public FluidBuilder<T, P> noBucket() {
      if (this.defaultBucket == Boolean.FALSE) {
         throw new IllegalStateException("Only one call to bucket/noBucket per builder allowed");
      }

      this.defaultBucket = false;
      return this;
   }

   @SafeVarargs
   public final FluidBuilder<T, P> tag(TagKey<Fluid>... tags) {
      FluidBuilder<T, P> ret = this.tag(ProviderType.FLUID_TAGS, tags);
      if (this.tags.isEmpty()) {
         ret.getOwner()
            .setDataGenerator(
               ret.sourceName,
               this.getRegistryKey(),
               ProviderType.FLUID_TAGS,
               prov -> this.tags.stream().map(prov::addTag).forEach(p -> p.add(this.getSource().builtInRegistryHolder().key()))
            );
      }

      this.tags.addAll(Arrays.asList(tags));
      return ret;
   }

   @SafeVarargs
   public final FluidBuilder<T, P> removeTag(TagKey<Fluid>... tags) {
      this.tags.removeAll(Arrays.asList(tags));
      return this.removeTag(ProviderType.FLUID_TAGS, tags);
   }

   private BaseFlowingFluid getSource() {
      NonNullSupplier<? extends BaseFlowingFluid> source = this.source;
      Preconditions.checkNotNull(source, "Fluid has no source block: " + this.sourceName);
      return source.get();
   }

   private Properties makeProperties() {
      NonNullSupplier<? extends BaseFlowingFluid> source = this.source;
      Properties ret = new Properties(this.fluidType, source == null ? null : source::get, this.asSupplier());
      this.fluidProperties.accept(ret);
      return ret;
   }

   private net.neoforged.neoforge.fluids.FluidType.Properties makeTypeProperties() {
      net.neoforged.neoforge.fluids.FluidType.Properties properties = net.neoforged.neoforge.fluids.FluidType.Properties.create();
      Optional<RegistryEntry<Block, Block>> block = this.getOwner().getOptional(this.sourceName, Registries.BLOCK);
      this.typeProperties.accept(properties);
      if (block.isPresent() && block.get().isBound()) {
         properties.descriptionId(block.get().get().getDescriptionId());
         this.setData(ProviderType.LANG, NonNullBiConsumer.noop());
      } else {
         properties.descriptionId(Util.makeDescriptionId("fluid", ResourceLocation.fromNamespaceAndPath(this.getOwner().getModid(), this.sourceName)));
      }

      return properties;
   }

   protected T createEntry() {
      return this.fluidFactory.apply(this.makeProperties());
   }

   public FluidEntry<T> register() {
      if (this.fluidType != null) {
         if (this.registerType) {
            this.getOwner().simple(this, this.sourceName, Keys.FLUID_TYPES, this.fluidType);
         }

         if (this.defaultSource == Boolean.TRUE) {
            this.source(Source::new);
         }

         if (this.defaultBlock == Boolean.TRUE) {
            this.block().register();
         }

         if (this.defaultBucket == Boolean.TRUE) {
            this.bucket().register();
         }

         NonNullSupplier<? extends BaseFlowingFluid> source = this.source;
         if (source != null) {
            this.getCallback().accept(this.sourceName, Registries.FLUID, this, source::get);
            return (FluidEntry<T>)super.register();
         } else {
            throw new IllegalStateException("Fluid must have a source version: " + this.getName());
         }
      } else {
         throw new IllegalStateException("Fluid must have a type: " + this.getName());
      }
   }

   @Override
   protected RegistryEntry<Fluid, T> createEntryWrapper(DeferredHolder<Fluid, T> delegate) {
      return new FluidEntry<>(this.getOwner(), delegate);
   }

   private static FluidType defaultFluidType(
      net.neoforged.neoforge.fluids.FluidType.Properties properties, final ResourceLocation stillTexture, final ResourceLocation flowingTexture
   ) {
      return new FluidType(properties) {
         public void initializeClient(Consumer<IClientFluidTypeExtensions> consumer) {
            consumer.accept(new IClientFluidTypeExtensions() {
               public ResourceLocation getStillTexture() {
                  return stillTexture;
               }

               public ResourceLocation getFlowingTexture() {
                  return flowingTexture;
               }
            });
         }
      };
   }

   @FunctionalInterface
   public interface FluidTypeFactory {
      FluidType create(net.neoforged.neoforge.fluids.FluidType.Properties var1, ResourceLocation var2, ResourceLocation var3);
   }
}
