package dev.xkmc.l2core.init.reg.datapack;

import com.mojang.serialization.Codec;
import java.util.Optional;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.registries.DataPackRegistryEvent.NewRegistry;

public record DatapackReg<T>(ResourceKey<Registry<T>> key, Codec<T> codec) {
   public void onRegister(NewRegistry event) {
      event.dataPackRegistry(this.key, this.codec, this.codec);
   }

   @Nullable
   public Holder<T> get(RegistryAccess access, ResourceLocation id) {
      Optional<Registry<T>> reg = access.registry(this.key);
      return reg.isEmpty() ? null : (Holder)reg.get().getHolder(id).orElse(null);
   }

   public Stream<Holder<T>> getAll(RegistryAccess access) {
      Optional<Registry<T>> reg = access.registry(this.key);
      return reg.isEmpty() ? Stream.empty() : reg.get().holders().map(e -> (Holder<T>)e);
   }
}
