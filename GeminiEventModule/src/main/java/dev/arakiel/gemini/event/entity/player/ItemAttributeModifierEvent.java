package dev.arakiel.gemini.event.entity.player;

import dev.arakiel.gemini.event.GeminiEvent;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

public class ItemAttributeModifierEvent extends GeminiEvent {
    private final ItemStack stack;
    private final EquipmentSlot slot;
    private final Map<Holder<Attribute>, AttributeModifier> modifiers = new LinkedHashMap<>();

    public ItemAttributeModifierEvent(
            ItemStack stack,
            EquipmentSlot slot,
            Map<Holder<Attribute>, AttributeModifier> modifiers
    ) {
        this.stack = Objects.requireNonNull(stack, "stack");
        this.slot = Objects.requireNonNull(slot, "slot");
        this.modifiers.putAll(Objects.requireNonNull(modifiers, "modifiers"));
    }

    public ItemStack getStack() {
        return this.stack;
    }

    public EquipmentSlot getSlot() {
        return this.slot;
    }

    public Map<Holder<Attribute>, AttributeModifier> getModifiers() {
        return Map.copyOf(this.modifiers);
    }

    public void addModifier(Holder<Attribute> attribute, AttributeModifier modifier) {
        this.modifiers.put(Objects.requireNonNull(attribute, "attribute"), Objects.requireNonNull(modifier, "modifier"));
    }

    public void removeModifier(Holder<Attribute> attribute) {
        this.modifiers.remove(attribute);
    }
}
