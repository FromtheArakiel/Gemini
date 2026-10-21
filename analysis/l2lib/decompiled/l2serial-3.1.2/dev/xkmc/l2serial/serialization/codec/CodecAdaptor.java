package dev.xkmc.l2serial.serialization.codec;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import dev.xkmc.l2serial.serialization.type_cache.TypeInfo;
import dev.xkmc.l2serial.serialization.unified_processor.JsonContext;
import dev.xkmc.l2serial.serialization.unified_processor.TagContext;
import dev.xkmc.l2serial.serialization.unified_processor.UnifiedCodec;
import dev.xkmc.l2serial.util.Wrappers;
import java.util.Objects;
import java.util.Map.Entry;
import java.util.function.UnaryOperator;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.EndTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.TagParser;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.RegistryOps;
import net.minecraft.util.Unit;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public record CodecAdaptor<T>(Class<T> cls, UnaryOperator<T> validator) implements Codec<T> {
   public static final Logger LOGGER = LogManager.getLogger();

   public CodecAdaptor(Class<T> cls) {
      this(cls, e -> e);
   }

   public <E> DataResult<Pair<T, E>> decode(DynamicOps<E> ops, E input) {
      if (ops.empty() instanceof JsonElement) {
         DynamicOps<JsonElement> jops = Wrappers.cast(ops);

         try {
            T val = Wrappers.cast(UnifiedCodec.deserializeValue(new JsonContext(jops), (E)((JsonElement)input), TypeInfo.of(this.cls), null));
            return DataResult.success(Pair.of(this.validator.apply(val), input));
         } catch (Exception e) {
            LOGGER.throwing(Level.ERROR, e);
            return DataResult.error(e::getMessage);
         }
      } else if (ops.empty() instanceof Tag) {
         DynamicOps<Tag> jops = Wrappers.cast(ops);

         try {
            T val = Wrappers.cast(UnifiedCodec.deserializeValue(new TagContext(jops, ex -> true), (E)((Tag)input), TypeInfo.of(this.cls), null));
            return DataResult.success(Pair.of(this.validator.apply(val), input));
         } catch (Exception e) {
            LOGGER.throwing(Level.ERROR, e);
            return DataResult.error(e::getMessage);
         }
      } else if (ops instanceof RegistryOps) {
         CompoundTag tag = (CompoundTag)((Pair)TagParser.AS_CODEC.decode(ops, input).getOrThrow()).getFirst();
         Tag ti = tag.get("value");
         DynamicOps<Tag> jops = ((RegistryOps)ops).withParent(NbtOps.INSTANCE);

         try {
            T val = Wrappers.cast(UnifiedCodec.deserializeValue(new TagContext(jops, ex -> true), ti, TypeInfo.of(this.cls), null));
            return DataResult.success(Pair.of(this.validator.apply(val), input));
         } catch (Exception e) {
            LOGGER.throwing(Level.ERROR, e);
            return DataResult.error(e::getMessage);
         }
      } else {
         return DataResult.error(() -> "[CodecAdaptor::decode] Unsupported ops type " + ops.empty() + " and value " + input.getClass().getSimpleName());
      }
   }

   public <E> DataResult<E> encode(T input, DynamicOps<E> ops, E prefix) {
      if (ops.empty() == Unit.INSTANCE) {
         return DataResult.success(Wrappers.cast(Unit.INSTANCE));
      }

      if (ops.empty() == JsonNull.INSTANCE) {
         RegistryOps<JsonElement> jops = Wrappers.cast(ops);

         try {
            JsonElement json = UnifiedCodec.serializeValue(new JsonContext(jops), TypeInfo.of(this.cls), input);
            if (!ops.empty().equals(prefix)) {
               if (!(json instanceof JsonObject a) || !(prefix instanceof JsonObject b)) {
                  return DataResult.error(() -> "[CodecAdaptor::encode] Non-empty prefix for type " + ops.empty());
               }

               for (Entry<String, JsonElement> e : b.entrySet()) {
                  a.add(e.getKey(), e.getValue());
               }
            }

            return DataResult.success(Wrappers.cast(json));
         } catch (Exception e) {
            LOGGER.throwing(Level.ERROR, e);
            return DataResult.error(e::getMessage);
         }
      } else if (ops.empty() == EndTag.INSTANCE) {
         RegistryOps<Tag> tops = Wrappers.cast(ops);

         try {
            Tag tag = UnifiedCodec.serializeValue(new TagContext(tops, f -> true), TypeInfo.of(this.cls), input);
            if (!ops.empty().equals(prefix)) {
               if (!(tag instanceof CompoundTag a) || !(prefix instanceof CompoundTag b)) {
                  return DataResult.error(() -> "[CodecAdaptor::encode] Non-empty prefix for type " + ops.empty());
               }

               for (String e : b.getAllKeys()) {
                  a.put(e, Objects.requireNonNull(b.get(e)));
               }
            }

            return DataResult.success(Wrappers.cast(tag));
         } catch (Exception e) {
            LOGGER.throwing(Level.ERROR, e);
            return DataResult.error(e::getMessage);
         }
      } else if (ops instanceof RegistryOps<E> rops) {
         RegistryOps<Tag> tops = rops.withParent(NbtOps.INSTANCE);

         try {
            Tag tag = UnifiedCodec.serializeValue(new TagContext(tops, f -> true), TypeInfo.of(this.cls), input);
            if (!ops.empty().equals(prefix)) {
               if (!(tag instanceof CompoundTag a) || !(prefix instanceof CompoundTag b)) {
                  return DataResult.error(() -> "[CodecAdaptor::encode] Non-empty prefix for type " + ops.empty());
               }

               for (String e : b.getAllKeys()) {
                  a.put(e, Objects.requireNonNull(b.get(e)));
               }
            }

            CompoundTag ans = new CompoundTag();
            ans.put("value", tag);
            return TagParser.AS_CODEC.encodeStart(ops, ans);
         } catch (Exception e) {
            LOGGER.throwing(Level.ERROR, e);
            return DataResult.error(e::getMessage);
         }
      } else {
         String str = "[CodecAdaptor::encode] Unknown ops type with empty ops: " + ops.empty();
         LOGGER.throwing(Level.ERROR, new IllegalStateException(str));
         return DataResult.error(() -> str);
      }
   }

   public <B extends RegistryFriendlyByteBuf> StreamCodec<B, T> toNetwork() {
      return StreamCodec.of(PacketCodec::to, b -> Objects.requireNonNull(PacketCodec.from(b, this.cls, null)));
   }
}
