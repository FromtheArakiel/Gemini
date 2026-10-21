package com.tterrag.registrate.builders;

import com.tterrag.registrate.AbstractRegistrate;
import com.tterrag.registrate.providers.DataGenContext;
import com.tterrag.registrate.providers.ProviderType;
import com.tterrag.registrate.providers.RegistrateProvider;
import com.tterrag.registrate.util.entry.RegistryEntry;
import com.tterrag.registrate.util.nullness.NonNullBiConsumer;
import com.tterrag.registrate.util.nullness.NonNullConsumer;
import com.tterrag.registrate.util.nullness.NonNullFunction;
import com.tterrag.registrate.util.nullness.NonNullSupplier;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.neoforged.neoforge.common.conditions.ICondition;
import net.neoforged.neoforge.registries.datamaps.DataMapType;

public interface Builder<R, T extends R, P, S extends Builder<R, T, P, S>> extends NonNullSupplier<RegistryEntry<R, T>> {
   RegistryEntry<R, T> register();

   AbstractRegistrate<?> getOwner();

   P getParent();

   String getName();

   ResourceKey<? extends Registry<R>> getRegistryKey();

   default RegistryEntry<R, T> get() {
      return this.getOwner().get(this.getName(), this.getRegistryKey());
   }

   default T getEntry() {
      return this.get().get();
   }

   NonNullSupplier<T> asSupplier();

   default <D extends RegistrateProvider> S setData(ProviderType<? extends D> type, NonNullBiConsumer<DataGenContext<R, T>, D> cons) {
      this.getOwner().setDataGenerator(this, type, prov -> cons.accept(DataGenContext.from(this), (D)prov));
      return (S)this;
   }

   default <D extends RegistrateProvider> S addMiscData(ProviderType<? extends D> type, NonNullConsumer<? extends D> cons) {
      this.getOwner().addDataGenerator(type, cons);
      return (S)this;
   }

   default <D> S dataMap(DataMapType<R, D> type, D val) {
      this.getOwner().addDataGenerator(ProviderType.DATA_MAP, e -> e.builder(type).add(DataGenContext.from(this).getId(), val, false, new ICondition[0]));
      return (S)this;
   }

   default <D> S dataMap(DataMapType<R, D> type, NonNullFunction<DataGenContext<R, T>, D> factory) {
      this.getOwner().addDataGenerator(ProviderType.DATA_MAP, e -> {
         DataGenContext<R, T> ctx = DataGenContext.from(this);
         e.builder(type).add(ctx.getId(), factory.apply(ctx), false, new ICondition[0]);
      });
      return (S)this;
   }

   default S onRegister(NonNullConsumer<? super T> callback) {
      this.getOwner().addRegisterCallback(this.getName(), this.getRegistryKey(), callback);
      return (S)this;
   }

   default <OR> S onRegisterAfter(ResourceKey<? extends Registry<OR>> dependencyType, NonNullConsumer<? super T> callback) {
      return this.onRegister(e -> {
         if (this.getOwner().isRegistered((ResourceKey<? extends Registry<R>>)dependencyType)) {
            callback.accept(e);
         } else {
            this.getOwner().addRegisterCallback((ResourceKey<? extends Registry<R>>)dependencyType, () -> callback.accept((T)e));
         }
      });
   }

   default <R2, T2 extends R2, P2, S2 extends Builder<R2, T2, P2, S2>> S2 transform(NonNullFunction<S, S2> func) {
      return func.apply((S)this);
   }

   default P build() {
      this.register();
      return this.getParent();
   }
}
