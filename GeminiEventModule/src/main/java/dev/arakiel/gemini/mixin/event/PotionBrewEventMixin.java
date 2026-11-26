package dev.arakiel.gemini.mixin.event;

import dev.arakiel.gemini.event.GeminiEvents;
import dev.arakiel.gemini.event.brewing.PotionBrewEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BrewingStandBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BrewingStandBlockEntity.class)
public abstract class PotionBrewEventMixin {
    @Inject(method = "doBrew", at = @At("HEAD"), cancellable = true)
    private static void gemini$potionBrewPre(
            Level level,
            BlockPos pos,
            NonNullList<ItemStack> stacks,
            CallbackInfo callback
    ) {
        PotionBrewEvent event = new PotionBrewEvent(
                PotionBrewEvent.Phase.PRE,
                copyOf(stacks),
                copyOf(stacks)
        );
        GeminiEvents.post(event);
        if (event.isCanceled()) {
            callback.cancel();
            return;
        }
        for (int index = 0; index < stacks.size(); index++) {
            stacks.set(index, event.getItem(index));
        }
    }

    @Inject(method = "doBrew", at = @At("RETURN"))
    private static void gemini$potionBrewPost(
            Level level,
            BlockPos pos,
            NonNullList<ItemStack> stacks,
            CallbackInfo callback
    ) {
        NonNullList<ItemStack> output = copyOf(stacks);
        GeminiEvents.post(new PotionBrewEvent(PotionBrewEvent.Phase.POST, output, output));
    }

    private static NonNullList<ItemStack> copyOf(NonNullList<ItemStack> stacks) {
        NonNullList<ItemStack> copy = NonNullList.withSize(stacks.size(), ItemStack.EMPTY);
        for (int index = 0; index < stacks.size(); index++) {
            copy.set(index, stacks.get(index).copy());
        }
        return copy;
    }
}
