package com.tterrag.registrate.builders;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.tterrag.registrate.AbstractRegistrate;
import com.tterrag.registrate.providers.ProviderType;
import com.tterrag.registrate.providers.RegistrateLangProvider;
import com.tterrag.registrate.providers.RegistrateTagsProvider;
import com.tterrag.registrate.util.entry.LazyRegistryEntry;
import com.tterrag.registrate.util.entry.RegistryEntry;
import com.tterrag.registrate.util.nullness.NonNullBiFunction;
import com.tterrag.registrate.util.nullness.NonNullFunction;
import com.tterrag.registrate.util.nullness.NonNullSupplier;
import com.tterrag.registrate.util.nullness.NonnullType;
import java.util.Arrays;
import net.minecraft.core.Registry;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagEntry;
import net.minecraft.tags.TagKey;
import net.neoforged.neoforge.registries.DeferredHolder;

public abstract class AbstractBuilder<R, T extends R, P, S extends AbstractBuilder<R, T, P, S>> implements Builder<R, T, P, S> {
   private final AbstractRegistrate<?> owner;
   private final P parent;
   private final String name;
   private final BuilderCallback callback;
   private final ResourceKey<? extends Registry<R>> registryKey;
   private final Multimap<ProviderType<? extends RegistrateTagsProvider<?>>, TagKey<?>> tagsByType = HashMultimap.create();
   private final LazyRegistryEntry<R, T> safeSupplier = new LazyRegistryEntry<>(this);
   private boolean isOptional = false;

   @NonnullType
   protected abstract T createEntry();

   @Override
   public RegistryEntry<R, T> register() {
      return this.callback.accept(this.name, this.registryKey, this, this::createEntry, this::createEntryWrapper);
   }

   protected RegistryEntry<R, T> createEntryWrapper(DeferredHolder<R, T> delegate) {
      return new RegistryEntry<>(this.getOwner(), delegate);
   }

   @Override
   public NonNullSupplier<T> asSupplier() {
      return this.safeSupplier;
   }

   @SafeVarargs
   public final <TP extends TagsProvider<R> & RegistrateTagsProvider<R>> S tag(ProviderType<? extends TP> type, TagKey<R>... tags) {
      if (!this.tagsByType.containsKey(type)) {
         this.setData(type, (ctx, prov) -> this.tagsByType.get(type).stream().map(t -> (TagKey)t).map(prov::addTag).forEach(b -> b.add(this.asTag())));
      }

      this.tagsByType.putAll(type, Arrays.asList(tags));
      return (S)this;
   }

   public S asOptional() {
      this.isOptional = true;
      return (S)this;
   }

   protected TagEntry asTag() {
      ResourceLocation id = ResourceLocation.fromNamespaceAndPath(this.getOwner().getModid(), this.getName());
      return this.isOptional ? TagEntry.optionalElement(id) : TagEntry.element(id);
   }

   @SafeVarargs
   public final <TP extends TagsProvider<R> & RegistrateTagsProvider<R>> S removeTag(ProviderType<TP> type, TagKey<R>... tags) {
      if (this.tagsByType.containsKey(type)) {
         for (TagKey<R> tag : tags) {
            this.tagsByType.remove(type, tag);
         }
      }

      return (S)this;
   }

   public S lang(NonNullFunction<T, String> langKeyProvider) {
      return this.lang(langKeyProvider, (p, t) -> p.getAutomaticName(t, this.getRegistryKey()));
   }

   public S lang(NonNullFunction<T, String> langKeyProvider, String name) {
      return this.lang(langKeyProvider, (p, s) -> name);
   }

   private S lang(
      NonNullFunction<T, String> langKeyProvider, NonNullBiFunction<RegistrateLangProvider, NonNullSupplier<? extends T>, String> localizedNameProvider
   ) {
      return this.setData(ProviderType.LANG, (ctx, prov) -> prov.add(langKeyProvider.apply(ctx.getEntry()), localizedNameProvider.apply(prov, ctx::getEntry)));
   }

   public AbstractBuilder(AbstractRegistrate<?> owner, P parent, String name, BuilderCallback callback, ResourceKey<? extends Registry<R>> registryKey) {
      this.owner = owner;
      this.parent = parent;
      this.name = name;
      this.callback = callback;
      this.registryKey = registryKey;
   }

   @Override
   public AbstractRegistrate<?> getOwner() {
      return this.owner;
   }

   @Override
   public P getParent() {
      return this.parent;
   }

   @Override
   public String getName() {
      return this.name;
   }

   protected BuilderCallback getCallback() {
      return this.callback;
   }

   @Override
   public ResourceKey<? extends Registry<R>> getRegistryKey() {
      return this.registryKey;
   }
}
