package dev.xkmc.l2serial.serialization.custom_handler;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import dev.xkmc.l2serial.serialization.generic_types.AliasCodec;
import dev.xkmc.l2serial.serialization.generic_types.ArrayCodec;
import dev.xkmc.l2serial.serialization.generic_types.CodecReg;
import dev.xkmc.l2serial.serialization.generic_types.EnumCodec;
import dev.xkmc.l2serial.serialization.generic_types.GenericCodec;
import dev.xkmc.l2serial.serialization.generic_types.HolderCodec;
import dev.xkmc.l2serial.serialization.generic_types.HolderReg;
import dev.xkmc.l2serial.serialization.generic_types.HolderSetCodec;
import dev.xkmc.l2serial.serialization.generic_types.ListCodec;
import dev.xkmc.l2serial.serialization.generic_types.MapCodec;
import dev.xkmc.l2serial.serialization.generic_types.RecordCodec;
import dev.xkmc.l2serial.serialization.generic_types.SetCodec;
import dev.xkmc.l2serial.serialization.nulldefer.NullDefer;
import dev.xkmc.l2serial.serialization.nulldefer.PrimitiveNullDefer;
import dev.xkmc.l2serial.serialization.nulldefer.SimpleNullDefer;
import dev.xkmc.l2serial.util.Wrappers;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.ByteArrayTag;
import net.minecraft.nbt.ByteTag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.DoubleTag;
import net.minecraft.nbt.FloatTag;
import net.minecraft.nbt.IntArrayTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.LongArrayTag;
import net.minecraft.nbt.LongTag;
import net.minecraft.nbt.ShortTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.resources.RegistryFixedCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.extensions.IFriendlyByteBufExtension;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.registries.RegistryManager;

public class Handlers {
   public static final Map<Class<?>, JsonClassHandler<?>> JSON_MAP = new ConcurrentHashMap<>();
   public static final Map<Class<?>, NBTClassHandler<?, ?>> NBT_MAP = new ConcurrentHashMap<>();
   public static final Map<Class<?>, PacketClassHandler<?>> PACKET_MAP = new ConcurrentHashMap<>();
   public static final Map<Class<?>, CodecReg<?>> CODEC_MAP = new ConcurrentHashMap<>();
   private static final Map<Class<?>, HolderReg<?>> REGMAP = new ConcurrentHashMap<>();
   public static final List<GenericCodec> LIST = new ArrayList<>();
   public static final Map<Class<?>, NullDefer<?>> NULL_DEFER = new ConcurrentHashMap<>();

   private static <T> void registerReg(Class<T> cls, HolderReg<T> reg) {
      REGMAP.put(cls, reg);
   }

   public static <T> void registerReg(Class<T> cls, ResourceKey<? extends Registry<T>> reg) {
      registerReg(
         cls,
         new HolderReg<>(
            new CodecReg<>(RegistryFixedCodec.create(reg), ByteBufCodecs.holderRegistry(reg)),
            new CodecReg<>(RegistryCodecs.homogeneousList(reg), ByteBufCodecs.holderSet(reg))
         )
      );
   }

   public static <T> HolderReg<T> getReg(Class<T> cls) {
      return Wrappers.cast(REGMAP.get(cls));
   }

   public static <T> void enableVanilla(Class<T> cls, Registry<T> reg) {
      registerReg(cls, reg.key());
      if (RegistryManager.isNonSyncedBuiltInRegistry(reg)) {
         new CodecHandler<>(cls, reg.byNameCodec(), ByteBufCodecs.fromCodecWithRegistries(reg.byNameCodec()));
      } else {
         new CodecHandler<>(cls, reg.byNameCodec(), ByteBufCodecs.registry(reg.key()));
      }
   }

   public static void register() {
   }

   static {
      new ClassHandler<>(
         long.class,
         JsonPrimitive::new,
         JsonElement::getAsLong,
         FriendlyByteBuf::readLong,
         FriendlyByteBuf::writeLong,
         LongTag::getAsLong,
         LongTag::valueOf,
         Long.class
      );
      new ClassHandler<>(
         int.class,
         JsonPrimitive::new,
         JsonElement::getAsInt,
         FriendlyByteBuf::readInt,
         FriendlyByteBuf::writeInt,
         IntTag::getAsInt,
         IntTag::valueOf,
         Integer.class
      );
      new ClassHandler<>(
         short.class,
         JsonPrimitive::new,
         JsonElement::getAsShort,
         FriendlyByteBuf::readShort,
         FriendlyByteBuf::writeShort,
         ShortTag::getAsShort,
         ShortTag::valueOf,
         Short.class
      );
      new ClassHandler<>(
         byte.class,
         JsonPrimitive::new,
         JsonElement::getAsByte,
         FriendlyByteBuf::readByte,
         IFriendlyByteBufExtension::writeByte,
         ByteTag::getAsByte,
         ByteTag::valueOf,
         Byte.class
      );
      new ClassHandler<>(
         boolean.class,
         JsonPrimitive::new,
         JsonElement::getAsBoolean,
         FriendlyByteBuf::readBoolean,
         FriendlyByteBuf::writeBoolean,
         tag -> tag.getAsByte() != 0,
         ByteTag::valueOf,
         Boolean.class
      );
      new ClassHandler<>(
         char.class,
         JsonPrimitive::new,
         JsonElement::getAsCharacter,
         FriendlyByteBuf::readChar,
         FriendlyByteBuf::writeChar,
         t -> (char)t.getAsByte(),
         c -> ByteTag.valueOf((byte)c.charValue()),
         Character.class
      );
      new ClassHandler<>(
         double.class,
         JsonPrimitive::new,
         JsonElement::getAsDouble,
         FriendlyByteBuf::readDouble,
         FriendlyByteBuf::writeDouble,
         DoubleTag::getAsDouble,
         DoubleTag::valueOf,
         Double.class
      );
      new ClassHandler<>(
         float.class,
         JsonPrimitive::new,
         JsonElement::getAsFloat,
         FriendlyByteBuf::readFloat,
         FriendlyByteBuf::writeFloat,
         FloatTag::getAsFloat,
         FloatTag::valueOf,
         Float.class
      );
      new ClassHandler<>(
         String.class, JsonPrimitive::new, JsonElement::getAsString, FriendlyByteBuf::readUtf, FriendlyByteBuf::writeUtf, Tag::getAsString, StringTag::valueOf
      );
      new StringClassHandler<>(ResourceLocation.class, ResourceLocation::parse, ResourceLocation::toString);
      new StringClassHandler<>(UUID.class, UUID::fromString, UUID::toString);
      new CodecHandler<>(ItemStack.class, ItemStack.OPTIONAL_CODEC, ItemStack.OPTIONAL_STREAM_CODEC);
      new CodecHandler<>(FluidStack.class, FluidStack.OPTIONAL_CODEC, FluidStack.OPTIONAL_STREAM_CODEC);
      new CodecHandler<>(Ingredient.class, Ingredient.CODEC, Ingredient.CONTENTS_STREAM_CODEC);
      new CodecHandler<>(MobEffectInstance.class, MobEffectInstance.CODEC, MobEffectInstance.STREAM_CODEC);
      new CodecHandler<>(Component.class, ComponentSerialization.CODEC, ComponentSerialization.STREAM_CODEC);
      new ClassHandler<>(CompoundTag.class, null, null, f -> f.readNbt(), (f, b) -> f.writeNbt(b), e -> e, e -> e);
      new ClassHandler<>(
         ListTag.class,
         null,
         null,
         buf -> (ListTag)buf.readNbt().get("warp"),
         (buf, tag) -> buf.writeNbt((Tag)Util.make(new CompoundTag(), e -> e.put("wrap", tag))),
         e -> e,
         e -> e
      );
      new ClassHandler<>(
         long[].class, null, null, FriendlyByteBuf::readLongArray, FriendlyByteBuf::writeLongArray, LongArrayTag::getAsLongArray, LongArrayTag::new
      );
      new ClassHandler<>(
         int[].class, null, null, FriendlyByteBuf::readVarIntArray, FriendlyByteBuf::writeVarIntArray, IntArrayTag::getAsIntArray, IntArrayTag::new
      );
      new ClassHandler<>(byte[].class, null, null, f -> f.readByteArray(), (f, b) -> f.writeByteArray(b), ByteArrayTag::getAsByteArray, ByteArrayTag::new);
      new ClassHandler<>(
         BlockPos.class,
         null,
         null,
         p -> p.readBlockPos(),
         (p, b) -> p.writeBlockPos(b),
         tag -> new BlockPos(tag.getInt("x"), tag.getInt("y"), tag.getInt("z")),
         obj -> {
            CompoundTag tag = new CompoundTag();
            tag.putInt("x", obj.getX());
            tag.putInt("y", obj.getY());
            tag.putInt("z", obj.getZ());
            return tag;
         }
      );
      new ClassHandler<>(
         Vec3.class,
         null,
         null,
         FriendlyByteBuf::readVec3,
         FriendlyByteBuf::writeVec3,
         tag -> new Vec3(tag.getDouble("x"), tag.getDouble("y"), tag.getDouble("z")),
         obj -> {
            CompoundTag tag = new CompoundTag();
            tag.putDouble("x", obj.x());
            tag.putDouble("y", obj.y());
            tag.putDouble("z", obj.z());
            return tag;
         }
      );
      new RecordCodec();
      new EnumCodec();
      new ArrayCodec();
      new AliasCodec();
      new ListCodec();
      new SetCodec();
      new MapCodec();
      new HolderCodec();
      new HolderSetCodec();
      new SimpleNullDefer<>(ItemStack.class, ItemStack.EMPTY);
      new SimpleNullDefer<>(Ingredient.class, Ingredient.EMPTY);
      new PrimitiveNullDefer<>(Integer.class, 0);
      new PrimitiveNullDefer<>(int.class, 0);
      new PrimitiveNullDefer<>(Long.class, 0L);
      new PrimitiveNullDefer<>(long.class, 0L);
      new PrimitiveNullDefer<>(Short.class, (short)0);
      new PrimitiveNullDefer<>(short.class, (short)0);
      new PrimitiveNullDefer<>(Byte.class, (byte)0);
      new PrimitiveNullDefer<>(byte.class, (byte)0);
      new PrimitiveNullDefer<>(Character.class, '\u0000');
      new PrimitiveNullDefer<>(char.class, '\u0000');
      new PrimitiveNullDefer<>(Double.class, 0.0);
      new PrimitiveNullDefer<>(double.class, 0.0);
      new PrimitiveNullDefer<>(Float.class, 0.0F);
      new PrimitiveNullDefer<>(float.class, 0.0F);
      new PrimitiveNullDefer<>(Boolean.class, false);
      new PrimitiveNullDefer<>(boolean.class, false);
      enableVanilla(Item.class, BuiltInRegistries.ITEM);
      enableVanilla(Block.class, BuiltInRegistries.BLOCK);
      enableVanilla(Potion.class, BuiltInRegistries.POTION);
      enableVanilla(MobEffect.class, BuiltInRegistries.MOB_EFFECT);
      enableVanilla(Attribute.class, BuiltInRegistries.ATTRIBUTE);
      enableVanilla(Wrappers.cast(EntityType.class), BuiltInRegistries.ENTITY_TYPE);
      registerReg(Enchantment.class, Registries.ENCHANTMENT);
      registerReg(DamageType.class, Registries.DAMAGE_TYPE);
   }
}
