package com.tterrag.registrate.providers;

import com.tterrag.registrate.AbstractRegistrate;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import net.minecraft.core.Registry;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.IntrinsicHolderTagsProvider;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.data.tags.IntrinsicHolderTagsProvider.IntrinsicTagAppender;
import net.minecraft.data.tags.TagsProvider.TagAppender;
import net.minecraft.data.tags.TagsProvider.TagLookup;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.neoforged.fml.LogicalSide;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public interface RegistrateTagsProvider<T> extends RegistrateLookupFillerProvider {
   TagAppender<T> addTag(TagKey<T> var1);

   CompletableFuture<TagLookup<T>> contentsGetter();

   ResourceKey<? extends Registry<T>> registry();

   class Impl<T> extends TagsProvider<T> implements RegistrateTagsProvider<T> {
      private final AbstractRegistrate<?> owner;
      private final ProviderType<? extends RegistrateTagsProvider.Impl<T>> type;
      private final String name;

      public Impl(
         AbstractRegistrate<?> owner,
         ProviderType<? extends RegistrateTagsProvider.Impl<T>> type,
         String name,
         PackOutput packOutput,
         ResourceKey<? extends Registry<T>> registryIn,
         CompletableFuture<Provider> registriesLookup,
         ExistingFileHelper existingFileHelper
      ) {
         super(packOutput, registryIn, registriesLookup, owner.getModid(), existingFileHelper);
         this.owner = owner;
         this.type = type;
         this.name = name;
      }

      public String getName() {
         return "Tags (%s)".formatted(this.name);
      }

      protected void addTags(Provider provider) {
         this.owner.genData(this.type, this);
      }

      @Override
      public LogicalSide getSide() {
         return LogicalSide.SERVER;
      }

      @Override
      public TagAppender<T> addTag(TagKey<T> tag) {
         return super.tag(tag);
      }

      @Override
      public CompletableFuture<Provider> getFilledProvider() {
         return this.createContentsProvider();
      }

      @Override
      public ResourceKey<? extends Registry<T>> registry() {
         return this.registryKey;
      }
   }

   class IntrinsicImpl<T> extends IntrinsicHolderTagsProvider<T> implements RegistrateTagsProvider<T> {
      private final AbstractRegistrate<?> owner;
      private final ProviderType<? extends RegistrateTagsProvider.IntrinsicImpl<T>> type;
      private final String name;

      public IntrinsicImpl(
         AbstractRegistrate<?> owner,
         ProviderType<? extends RegistrateTagsProvider.IntrinsicImpl<T>> type,
         String name,
         PackOutput packOutput,
         ResourceKey<? extends Registry<T>> registryIn,
         CompletableFuture<Provider> registriesLookup,
         Function<T, ResourceKey<T>> keyExtractor,
         ExistingFileHelper existingFileHelper
      ) {
         super(packOutput, registryIn, registriesLookup, keyExtractor, owner.getModid(), existingFileHelper);
         this.owner = owner;
         this.type = type;
         this.name = name;
      }

      public String getName() {
         return "Tags (%s)".formatted(this.name);
      }

      protected void addTags(Provider provider) {
         this.owner.genData(this.type, this);
      }

      @Override
      public LogicalSide getSide() {
         return LogicalSide.SERVER;
      }

      public IntrinsicTagAppender<T> addTag(TagKey<T> tag) {
         return super.tag(tag);
      }

      @Override
      public CompletableFuture<Provider> getFilledProvider() {
         return this.createContentsProvider();
      }

      @Override
      public ResourceKey<? extends Registry<T>> registry() {
         return this.registryKey;
      }
   }
}
