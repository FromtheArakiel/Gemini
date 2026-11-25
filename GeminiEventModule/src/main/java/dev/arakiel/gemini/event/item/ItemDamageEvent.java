package dev.arakiel.gemini.event.item;

import dev.arakiel.gemini.event.Cancelable;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

import java.util.Objects;

@Cancelable
public class ItemDamageEvent extends ItemEvent {
    private final LivingEntity entity;
    private final EquipmentSlot slot;
    private int amount;

    public ItemDamageEvent(ItemStack stack, LivingEntity entity, EquipmentSlot slot, int amount) {
        super(stack);
        this.entity = Objects.requireNonNull(entity, "entity");
        this.slot = Objects.requireNonNull(slot, "slot");
        this.amount = amount;
    }

    public LivingEntity getEntity() {
        return entity;
    }

    public EquipmentSlot getSlot() {
        return slot;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }
}
