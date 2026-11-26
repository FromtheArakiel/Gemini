package dev.arakiel.gemini.mixin.event;

import dev.arakiel.gemini.event.GeminiEvents;
import dev.arakiel.gemini.event.world.NoteBlockEvent;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.NoteBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(NoteBlock.class)
public abstract class NoteBlockEventMixin {
    @Inject(method = "triggerEvent", at = @At("HEAD"), cancellable = true)
    private void gemini$notePlay(
            BlockState state,
            Level level,
            BlockPos pos,
            int type,
            int data,
            CallbackInfoReturnable<Boolean> callback
    ) {
        NoteBlockEvent event = new NoteBlockEvent(
                level,
                pos,
                NoteBlockEvent.Phase.PLAY,
                state.getValue(NoteBlock.NOTE)
        );
        GeminiEvents.post(event);
        if (event.isCanceled()) {
            callback.setReturnValue(false);
        }
    }

    @Inject(method = "useWithoutItem", at = @At("HEAD"), cancellable = true)
    private void gemini$noteChange(
            BlockState state,
            Level level,
            BlockPos pos,
            Player player,
            BlockHitResult hit,
            CallbackInfoReturnable<InteractionResult> callback
    ) {
        NoteBlockEvent event = new NoteBlockEvent(
                level,
                pos,
                NoteBlockEvent.Phase.CHANGE,
                state.getValue(NoteBlock.NOTE)
        );
        GeminiEvents.post(event);
        if (event.isCanceled()) {
            callback.setReturnValue(InteractionResult.FAIL);
        }
    }
}
