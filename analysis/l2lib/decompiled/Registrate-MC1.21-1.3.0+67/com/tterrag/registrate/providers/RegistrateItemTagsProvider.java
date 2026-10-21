package com.tterrag.registrate.providers;

import com.tterrag.registrate.AbstractRegistrate;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.TagsProvider.TagLookup;
import net.minecraft.tags.TagBuilder;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class RegistrateItemTagsProvider extends RegistrateTagsProvider.IntrinsicImpl<Item> {
   private final CompletableFuture<TagLookup<Block>> blockTags;
   private final Map<TagKey<Block>, TagKey<Item>> tagsToCopy = new HashMap<>();

   public RegistrateItemTagsProvider(
      AbstractRegistrate<?> owner,
      ProviderType<RegistrateItemTagsProvider> type,
      String name,
      PackOutput output,
      CompletableFuture<Provider> registriesLookup,
      CompletableFuture<TagLookup<Block>> blockTags,
      ExistingFileHelper existingFileHelper
   ) {
      super(owner, type, name, output, Registries.ITEM, registriesLookup, item -> item.builtInRegistryHolder().key(), existingFileHelper);
      this.blockTags = blockTags;
   }

   public void copy(TagKey<Block> p_206422_, TagKey<Item> p_206423_) {
      this.tagsToCopy.put(p_206422_, p_206423_);
   }

   protected CompletableFuture<Provider> createContentsProvider() {
      return super.createContentsProvider().thenCombineAsync(this.blockTags, (p_274766_, p_274767_) -> {
         this.tagsToCopy.forEach((p_274763_, p_274764_) -> {
            TagBuilder tagbuilder = this.getOrCreateRawBuilder(p_274764_);
            Optional<TagBuilder> optional = (Optional<TagBuilder>)p_274767_.apply(p_274763_);
            optional.orElseThrow(() -> new IllegalStateException("Missing block tag " + p_274764_.location())).build().forEach(tagbuilder::add);
         });
         return (Provider)p_274766_;
      });
   }
}
