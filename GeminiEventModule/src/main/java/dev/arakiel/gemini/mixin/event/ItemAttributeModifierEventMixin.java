package dev.arakiel.gemini.mixin.event;

import dev.arakiel.gemini.event.GeminiEvents;
import dev.arakiel.gemini.event.entity.player.ItemAttributeModifierEvent;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.BiConsumer;

@Mixin(ItemStack.class)
public abstract class ItemAttributeModifierEventMixin {
    @Inject(
            method = "forEachModifier(Lnet/minecraft/world/entity/EquipmentSlot;Ljava/util/function/BiConsumer;)V",
            at = @At("HEAD"),
            cancellable = true
    )
    private void gemini$itemAttributes(
            EquipmentSlot slot,
            BiConsumer<Holder<Attribute>, AttributeModifier> consumer,
            CallbackInfo callback
    ) {
        ItemStack stack = (ItemStack) (Object) this;
        Map<Holder<Attribute>, AttributeModifier> modifiers = new LinkedHashMap<>();
        ItemAttributeModifiers component =
                stack.getOrDefault(DataComponents.ATTRIBUTE_MODIFIERS, ItemAttributeModifiers.EMPTY);
        if (!component.modifiers().isEmpty()) {
            component.forEach(slot, modifiers::put);
        }
        EnchantmentHelper.forEachModifier(stack, slot, modifiers::put);

        ItemAttributeModifierEvent event = new ItemAttributeModifierEvent(stack, slot, modifiers);
        GeminiEvents.post(event);
        event.getModifiers().forEach(consumer);
        callback.cancel();
    }
}
