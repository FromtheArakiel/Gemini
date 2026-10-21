package dev.xkmc.l2core.serial.recipe;

import com.mojang.serialization.MapCodec;
import dev.xkmc.l2serial.serialization.codec.CodecAdaptor;
import dev.xkmc.l2serial.serialization.codec.MapCodecAdaptor;
import dev.xkmc.l2serial.util.Wrappers;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeSerializer;

public class RecSerializer<R extends Recipe<I>, I extends RecipeInput> implements RecipeSerializer<R> {
   public final Class<R> cls;
   private final MapCodecAdaptor<R> codec;
   private final StreamCodec<RegistryFriendlyByteBuf, R> stream;

   public RecSerializer(Class<R> cls) {
      this.cls = cls;
      this.codec = MapCodecAdaptor.of(cls);
      this.stream = new CodecAdaptor(cls).toNetwork();
   }

   public MapCodec<R> codec() {
      return this.codec;
   }

   public StreamCodec<RegistryFriendlyByteBuf, R> streamCodec() {
      return this.stream;
   }

   public R blank() {
      return (R)Wrappers.get(() -> this.cls.getConstructor().newInstance());
   }
}
