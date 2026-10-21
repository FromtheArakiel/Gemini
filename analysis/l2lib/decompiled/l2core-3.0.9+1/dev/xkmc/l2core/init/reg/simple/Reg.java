package dev.xkmc.l2core.init.reg.simple;

import com.mojang.serialization.Codec;
import dev.xkmc.l2core.init.reg.datapack.DataMapReg;
import dev.xkmc.l2core.init.reg.datapack.DatapackReg;
import dev.xkmc.l2serial.serialization.codec.CodecAdaptor;
import dev.xkmc.l2serial.util.ModContainerHack;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.datamaps.DataMapType;

public final class Reg {
   private final String modid;
   private final IEventBus bus;

   public Reg(String modid) {
      this.modid = modid;
      ModContainer cont = ModContainerHack.getMod(modid);
      IEventBus bus = cont.getEventBus();
      if (bus != null) {
         this.bus = bus;
      } else {
         throw new IllegalStateException("Event bus is null for " + modid);
      }
   }

   public <T> DeferredRegister<T> make(Registry<T> reg) {
      DeferredRegister<T> ans = DeferredRegister.create(reg, this.modid);
      ans.register(this.bus);
      return ans;
   }

   public <T> DeferredRegister<T> make(ResourceKey<Registry<T>> reg) {
      DeferredRegister<T> ans = DeferredRegister.create(reg, this.modid);
      ans.register(this.bus);
      return ans;
   }

   public <T> DatapackReg<T> dataReg(String id, Codec<T> codec) {
      DatapackReg<T> ans = new DatapackReg<>(ResourceKey.createRegistryKey(this.loc(id)), codec);
      this.bus.addListener(ans::onRegister);
      return ans;
   }

   public <T> DatapackReg<T> dataReg(String id, Class<T> cls) {
      return this.dataReg(id, new CodecAdaptor(cls));
   }

   public <K, V> DataMapReg<K, V> dataMap(DataMapType<K, V> type) {
      DataMapReg<K, V> ans = new DataMapReg<>(type);
      this.bus.addListener(ans::register);
      return ans;
   }

   public <K, V> DataMapReg<K, V> dataMap(String id, ResourceKey<Registry<K>> k, Codec<V> codec, Codec<V> network) {
      return this.dataMap(DataMapType.builder(this.loc(id), k, codec).synced(network, true).build());
   }

   public <K, V> DataMapReg<K, V> dataMap(String id, ResourceKey<Registry<K>> k, Class<V> cls) {
      CodecAdaptor<V> codec = new CodecAdaptor(cls);
      return this.dataMap(id, k, codec, codec);
   }

   public ResourceLocation loc(String id) {
      return ResourceLocation.fromNamespaceAndPath(this.modid, id);
   }
}
