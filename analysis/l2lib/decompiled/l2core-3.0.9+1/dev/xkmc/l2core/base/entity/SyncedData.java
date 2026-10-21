package dev.xkmc.l2core.base.entity;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.UUIDUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.network.syncher.SynchedEntityData.Builder;
import org.jetbrains.annotations.Nullable;

public class SyncedData {
   public static final SyncedData.Serializer<Integer> INT = new SyncedData.Simple<>(EntityDataSerializers.INT, Codec.INT);
   public static final SyncedData.Serializer<BlockPos> BLOCK_POS = new SyncedData.Simple<>(EntityDataSerializers.BLOCK_POS, BlockPos.CODEC);
   public static final SyncedData.Serializer<String> STRING = new SyncedData.Simple<>(EntityDataSerializers.STRING, Codec.STRING);
   public static final SyncedData.Serializer<Optional<UUID>> UUID = new SyncedData.Opt<>(EntityDataSerializers.OPTIONAL_UUID, UUIDUtil.CODEC);
   private final SyncedData.Definer cls;
   private final List<SyncedData.Data<?>> list = new ArrayList<>();
   @Nullable
   private final SyncedData parent;

   public SyncedData(SyncedData.Definer cls) {
      this.cls = cls;
      this.parent = null;
   }

   public SyncedData(SyncedData.Definer cls, SyncedData parent) {
      this.cls = cls;
      this.parent = parent;
   }

   public void register(Builder data) {
      for (SyncedData.Data<?> entry : this.list) {
         entry.register(data);
      }

      if (this.parent != null) {
         this.parent.register(data);
      }
   }

   public <T> EntityDataAccessor<T> define(SyncedData.Serializer<T> ser, T init, @Nullable String name) {
      SyncedData.Data<T> data = new SyncedData.Data<>(ser, init, name);
      this.list.add(data);
      return data.data;
   }

   public void write(RegistryAccess pvd, CompoundTag tag, SynchedEntityData data) {
      for (SyncedData.Data<?> entry : this.list) {
         entry.write(pvd, tag, data);
      }

      if (this.parent != null) {
         this.parent.write(pvd, tag, data);
      }
   }

   public void read(RegistryAccess pvd, CompoundTag tag, SynchedEntityData data) {
      for (SyncedData.Data<?> entry : this.list) {
         entry.read(pvd, tag, data);
      }

      if (this.parent != null) {
         this.parent.read(pvd, tag, data);
      }
   }

   private class Data<T> {
      private final SyncedData.Serializer<T> ser;
      private final EntityDataAccessor<T> data;
      private final T init;
      private final String name;

      private Data(SyncedData.Serializer<T> ser, T init, @Nullable String name) {
         this.ser = ser;
         this.data = SyncedData.this.cls.define(ser.data());
         this.init = init;
         this.name = name;
      }

      private void register(Builder data) {
         data.define(this.data, this.init);
      }

      public void write(RegistryAccess pvd, CompoundTag tag, SynchedEntityData entityData) {
         if (this.name != null) {
            Tag ans = this.ser.write(pvd, (T)entityData.get(this.data));
            if (ans != null) {
               tag.put(this.name, ans);
            }
         }
      }

      public void read(RegistryAccess pvd, CompoundTag tag, SynchedEntityData entityData) {
         if (this.name != null) {
            Tag in = tag.get(this.name);
            entityData.set(this.data, Optional.ofNullable(in).map(e -> this.ser.read(pvd, e)).orElse(this.init));
         }
      }
   }

   public interface Definer {
      <T> EntityDataAccessor<T> define(EntityDataSerializer<T> var1);
   }

   public record Opt<T>(EntityDataSerializer<Optional<T>> data, Codec<T> codec) implements SyncedData.Serializer<Optional<T>> {
      @Nullable
      public Tag write(RegistryAccess pvd, Optional<T> val) {
         return val.<Tag>map(e -> (Tag)this.codec.encodeStart(pvd.createSerializationContext(NbtOps.INSTANCE), e).getOrThrow()).orElse(null);
      }

      public Optional<T> read(RegistryAccess pvd, Tag tag) {
         return this.codec.decode(pvd.createSerializationContext(NbtOps.INSTANCE), tag).result().map(Pair::getFirst);
      }
   }

   public interface Serializer<T> {
      EntityDataSerializer<T> data();

      @Nullable
      Tag write(RegistryAccess var1, T var2);

      @Nullable
      T read(RegistryAccess var1, Tag var2);
   }

   public record Simple<T>(EntityDataSerializer<T> data, Codec<T> codec) implements SyncedData.Serializer<T> {
      @Nullable
      @Override
      public Tag write(RegistryAccess pvd, T t) {
         return (Tag)this.codec.encodeStart(pvd.createSerializationContext(NbtOps.INSTANCE), t).getOrThrow();
      }

      @Nullable
      @Override
      public T read(RegistryAccess pvd, Tag tag) {
         return (T)((Pair)this.codec.decode(pvd.createSerializationContext(NbtOps.INSTANCE), tag).getOrThrow()).getFirst();
      }
   }
}
