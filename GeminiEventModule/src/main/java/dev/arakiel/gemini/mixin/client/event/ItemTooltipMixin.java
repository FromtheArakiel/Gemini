package dev.arakiel.gemini.mixin.client.event;

import dev.arakiel.gemini.event.GeminiEvents;
import dev.arakiel.gemini.event.client.ItemTooltipEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;

@Mixin(ItemStack.class)
public abstract class ItemTooltipMixin {
    @Inject(method = "getTooltipLines", at = @At("RETURN"), cancellable = true)
    private void gemini$tooltip(
            Item.TooltipContext context,
            @Nullable Player player,
            TooltipFlag flags,
            CallbackInfoReturnable<List<Component>> callback
    ) {
        List<Component> lines = new ArrayList<>(callback.getReturnValue());
        ItemTooltipEvent event = new ItemTooltipEvent(
                (ItemStack) (Object) this,
                player,
                flags,
                lines
        );
        GeminiEvents.post(event);
        callback.setReturnValue(event.isCanceled() ? List.of() : List.copyOf(lines));
    }
}
