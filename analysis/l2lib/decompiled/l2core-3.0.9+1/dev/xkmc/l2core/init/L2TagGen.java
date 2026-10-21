package dev.xkmc.l2core.init;

import com.tterrag.registrate.providers.ProviderType;
import com.tterrag.registrate.providers.RegistrateTagsProvider.Impl;
import com.tterrag.registrate.providers.RegistrateTagsProvider.IntrinsicImpl;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.item.enchantment.Enchantment;

public class L2TagGen {
   public static final ProviderType<IntrinsicImpl<MobEffect>> EFF_TAGS = getProvider(Registries.MOB_EFFECT, BuiltInRegistries.MOB_EFFECT);
   public static final ProviderType<IntrinsicImpl<Attribute>> ATTR_TAGS = getProvider(Registries.ATTRIBUTE, BuiltInRegistries.ATTRIBUTE);
   public static final ProviderType<Impl<Enchantment>> ENCH_TAGS = getProvider(Registries.ENCHANTMENT);
   public static final TagKey<MobEffect> TRACKED_EFFECTS = effectTag(ResourceLocation.fromNamespaceAndPath("l2core", "tracked_effects"));

   public static <T> ProviderType<IntrinsicImpl<T>> getProvider(ResourceKey<Registry<T>> id, Registry<T> reg) {
      String name = id.location().getPath();
      return ProviderType.registerIntrinsicTag("tags/" + name, name, id, ench -> (ResourceKey)reg.getResourceKey(ench).orElseThrow());
   }

   public static <T> ProviderType<Impl<T>> getProvider(ResourceKey<Registry<T>> id) {
      String name = id.location().getPath();
      return ProviderType.registerDynamicTag("tags/" + name, name, id);
   }

   public static void onEffectTagGen(IntrinsicImpl<MobEffect> pvd) {
      pvd.addTag(TRACKED_EFFECTS);
   }

   public static TagKey<MobEffect> effectTag(ResourceLocation id) {
      return TagKey.create(Registries.MOB_EFFECT, id);
   }
}
