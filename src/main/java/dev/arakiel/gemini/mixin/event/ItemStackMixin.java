package dev.arakiel.gemini.mixin.event;

import dev.arakiel.gemini.event.GeminiEvents;
import dev.arakiel.gemini.event.item.ItemDamageEvent;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin {
    @ModifyVariable(
            method = "hurtAndBreak(ILnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/entity/EquipmentSlot;)V",
            at = @At("HEAD"),
            argsOnly = true,
            ordinal = 0
    )
    private int gemini$itemDamage(int value, int amount, LivingEntity entity, EquipmentSlot slot) {
        ItemDamageEvent event = new ItemDamageEvent((ItemStack) (Object) this, entity, slot, value);
        GeminiEvents.post(event);
        return event.isCanceled() ? 0 : event.getAmount();
    }
}
