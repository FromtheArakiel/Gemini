package dev.arakiel.gemini.component.internal.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.level.block.entity.BlockEntityType;

public record BlockEntityAddress(BlockEntityType<?> beType, BlockPos bePos) {
   public static final StreamCodec<RegistryFriendlyByteBuf, BlockEntityAddress> CODEC = StreamCodec.composite(
      ByteBufCodecs.idMapper(BuiltInRegistries.BLOCK_ENTITY_TYPE),
      BlockEntityAddress::beType,
      BlockPos.STREAM_CODEC,
      BlockEntityAddress::bePos,
      BlockEntityAddress::new
   );
}
