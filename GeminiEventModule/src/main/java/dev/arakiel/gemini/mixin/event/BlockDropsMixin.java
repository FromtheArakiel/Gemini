package dev.arakiel.gemini.mixin.event;

import dev.arakiel.gemini.event.GeminiEvents;
import dev.arakiel.gemini.event.block.BlockDropsEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;

@Mixin(Block.class)
public abstract class BlockDropsMixin {
    @Inject(
            method = "getDrops(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/entity/BlockEntity;)Ljava/util/List;",
            at = @At("RETURN"),
            cancellable = true
    )
    private static void gemini$blockDrops(
            BlockState state,
            ServerLevel level,
            BlockPos pos,
            BlockEntity blockEntity,
            CallbackInfoReturnable<List<ItemStack>> callback
    ) {
        BlockDropsEvent event = new BlockDropsEvent(
                level,
                pos,
                state,
                null,
                ItemStack.EMPTY,
                new ArrayList<>(callback.getReturnValue()),
                0
        );
        GeminiEvents.post(event);
        callback.setReturnValue(event.isCanceled() ? List.of() : event.getDrops());
    }

    @Inject(
            method = "getDrops(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/entity/BlockEntity;Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/item/ItemStack;)Ljava/util/List;",
            at = @At("RETURN"),
            cancellable = true
    )
    private static void gemini$blockDropsWithTool(
            BlockState state,
            ServerLevel level,
            BlockPos pos,
            BlockEntity blockEntity,
            Entity entity,
            ItemStack tool,
            CallbackInfoReturnable<List<ItemStack>> callback
    ) {
        BlockDropsEvent event = new BlockDropsEvent(
                level,
                pos,
                state,
                entity instanceof ServerPlayer player ? player : null,
                tool,
                new ArrayList<>(callback.getReturnValue()),
                0
        );
        GeminiEvents.post(event);
        callback.setReturnValue(event.isCanceled() ? List.of() : event.getDrops());
    }
}
