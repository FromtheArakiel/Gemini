package com.tterrag.registrate.providers.loot;

import com.tterrag.registrate.AbstractRegistrate;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Stream;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.loot.EntityLootSubProvider;
import net.minecraft.data.loot.packs.VanillaEntityLoot;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.FrogVariant;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.LootTable.Builder;

public class RegistrateEntityLootTables extends VanillaEntityLoot implements RegistrateLootTables {
   private final AbstractRegistrate<?> parent;
   private final Consumer<RegistrateEntityLootTables> callback;

   public RegistrateEntityLootTables(Provider p_346214_, AbstractRegistrate<?> parent, Consumer<RegistrateEntityLootTables> callback) {
      super(p_346214_);
      this.parent = parent;
      this.callback = callback;
   }

   public void generate() {
      this.callback.accept(this);
   }

   protected Stream<EntityType<?>> getKnownEntityTypes() {
      return this.parent.getAll(Registries.ENTITY_TYPE).stream().map(Supplier::get);
   }

   public Provider getRegistries() {
      return this.registries;
   }

   public static Builder createSheepTable(ItemLike p_249422_) {
      return EntityLootSubProvider.createSheepTable(p_249422_);
   }

   public boolean canHaveLootTable(EntityType<?> p_249029_) {
      return super.canHaveLootTable(p_249029_);
   }

   public net.minecraft.world.level.storage.loot.predicates.LootItemCondition.Builder killedByFrogVariant(ResourceKey<FrogVariant> p_335676_) {
      return super.killedByFrogVariant(p_335676_);
   }

   public void add(EntityType<?> p_248740_, Builder p_249440_) {
      super.add(p_248740_, p_249440_);
   }

   public void add(EntityType<?> p_252130_, ResourceKey<LootTable> p_335943_, Builder p_249357_) {
      super.add(p_252130_, p_335943_, p_249357_);
   }
}
