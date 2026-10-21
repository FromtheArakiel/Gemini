package dev.xkmc.l2core.init.reg.datapack;

import com.mojang.datafixers.util.Pair;
import java.util.Optional;
import java.util.stream.Stream;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.neoforged.neoforge.registries.datamaps.DataMapType;
import net.neoforged.neoforge.registries.datamaps.RegisterDataMapTypesEvent;
import org.jetbrains.annotations.Nullable;

public record DataMapReg<K, V>(DataMapType<K, V> reg) {
   public void register(RegisterDataMapTypesEvent event) {
      event.register(this.reg);
   }

   @Nullable
   public V get(RegistryAccess access, Holder<K> key) {
      Optional<Registry<K>> registry = access.registry(this.reg.registryKey());
      if (registry.isEmpty()) {
         return null;
      }

      Optional<ResourceKey<K>> id = key.unwrapKey();
      return (V)(id.isEmpty() ? null : registry.get().getData(this.reg, id.get()));
   }

   public Stream<Pair<Holder<K>, V>> getAll(RegistryAccess access) {
      Optional<Registry<K>> registry = access.registry(this.reg.registryKey());
      return registry.isEmpty()
         ? Stream.empty()
         : registry.get().getDataMap(this.reg).entrySet().stream().map(e -> Pair.of(registry.get().getHolderOrThrow((ResourceKey)e.getKey()), e.getValue()));
   }
}
