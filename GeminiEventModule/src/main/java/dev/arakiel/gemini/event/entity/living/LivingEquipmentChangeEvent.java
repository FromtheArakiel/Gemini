package dev.arakiel.gemini.event.entity.living;

import dev.arakiel.gemini.event.entity.LivingEvent;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

import java.util.Objects;

public class LivingEquipmentChangeEvent extends LivingEvent {
    private final EquipmentSlot slot;
    private final ItemStack previous;
    private final ItemStack current;

    public LivingEquipmentChangeEvent(LivingEntity entity, EquipmentSlot slot, ItemStack previous, ItemStack current) {
        super(entity);
        this.slot = Objects.requireNonNull(slot, "slot");
        this.previous = Objects.requireNonNull(previous, "previous");
        this.current = Objects.requireNonNull(current, "current");
    }

    public EquipmentSlot getSlot() {
        return slot;
    }

    public ItemStack getPrevious() {
        return previous;
    }

    public ItemStack getCurrent() {
        return current;
    }
}
