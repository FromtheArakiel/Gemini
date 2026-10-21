package dev.xkmc.l2serial.serialization.unified_processor;

import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import dev.xkmc.l2serial.serialization.custom_handler.Handlers;
import dev.xkmc.l2serial.serialization.generic_types.CodecReg;
import dev.xkmc.l2serial.serialization.marker.SerialField;
import dev.xkmc.l2serial.serialization.type_cache.ClassCache;
import dev.xkmc.l2serial.serialization.type_cache.FieldCache;
import dev.xkmc.l2serial.serialization.type_cache.TypeInfo;
import dev.xkmc.l2serial.util.Wrappers;
import java.util.Optional;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.network.RegistryFriendlyByteBuf;

public class PacketContext extends SingletonContext<RegistryFriendlyByteBuf> {
   private final Predicate<SerialField> pred;

   public PacketContext(RegistryFriendlyByteBuf instance, Predicate<SerialField> pred) {
      super(instance);
      this.pred = pred;
   }

   public PacketContext(RegistryFriendlyByteBuf instance) {
      this(instance, e -> true);
   }

   @Override
   public boolean hasSpecialHandling(Class<?> cls) {
      return Handlers.PACKET_MAP.containsKey(cls);
   }

   public Object deserializeSpecial(Class<?> cls, RegistryFriendlyByteBuf self) {
      return Handlers.PACKET_MAP.get(cls).fromPacket(this.instance);
   }

   public RegistryFriendlyByteBuf serializeSpecial(Class<?> cls, Object obj) {
      Handlers.PACKET_MAP.get(cls).toPacket(this.instance, obj);
      return this.instance;
   }

   public Object deserializeCodec(CodecReg<?> cls, RegistryFriendlyByteBuf buf) {
      return cls.stream().decode(this.instance);
   }

   public RegistryFriendlyByteBuf serializeCodec(CodecReg<?> cls, Object e) {
      cls.stream().encode(this.instance, Wrappers.cast(e));
      return this.instance;
   }

   public Optional<Either<Optional<Object>, TypeInfo>> fetchRealClass(@Nullable RegistryFriendlyByteBuf obj, TypeInfo cls) throws Exception {
      byte header = this.instance.readByte();
      if (header == 0) {
         return Optional.of(Either.left(Optional.empty()));
      } else {
         return header == 2 ? Optional.of(Either.right(TypeInfo.of(Class.forName(this.instance.readUtf())))) : Optional.empty();
      }
   }

   @Override
   public Optional<Pair<RegistryFriendlyByteBuf, Optional<ClassCache>>> writeRealClass(TypeInfo cls, @Nullable Object obj) throws Exception {
      if (obj == null) {
         this.instance.writeByte(0);
         return Optional.of(Pair.of(this.instance, Optional.empty()));
      }

      Optional<Wrappers.ExcSup<RegistryFriendlyByteBuf>> special = UnifiedCodec.serializeSpecial(this, cls, obj);
      if (special.isPresent()) {
         this.instance.writeByte(1);
         return Optional.of(Pair.of(special.get().get(), Optional.empty()));
      }

      if (obj.getClass() != cls.getAsClass()) {
         ClassCache cache = ClassCache.get(obj.getClass());
         if (cache.getSerialAnnotation() != null) {
            this.instance.writeByte(2);
            this.instance.writeUtf(obj.getClass().getName());
            return Optional.of(Pair.of(this.instance, Optional.of(cache)));
         }
      }

      this.instance.writeByte(1);
      return Optional.empty();
   }

   public boolean shouldRead(RegistryFriendlyByteBuf obj, FieldCache field) throws Exception {
      return this.pred.test(field.getSerialAnnotation());
   }

   @Override
   public boolean shouldWrite(SerialField sf) {
      return this.pred.test(sf);
   }

   public int getSize(RegistryFriendlyByteBuf self) {
      return this.instance.readInt();
   }

   public String getAsString(RegistryFriendlyByteBuf self) {
      return this.instance.readUtf();
   }

   public RegistryFriendlyByteBuf createList(int size) {
      this.instance.writeInt(size);
      return this.instance;
   }

   public RegistryFriendlyByteBuf fromString(String str) {
      this.instance.writeUtf(str);
      return this.instance;
   }
}
