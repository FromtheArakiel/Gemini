package dev.arakiel.gemini.event.entity.living;

import dev.arakiel.gemini.event.Cancelable;
import dev.arakiel.gemini.event.entity.LivingEvent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

import java.util.Objects;

@Cancelable
public class LivingSwapItemsEvent extends LivingEvent {
    private ItemStack itemSwappedToMainHand;
    private ItemStack itemSwappedToOffHand;

    public LivingSwapItemsEvent(LivingEntity entity) {
        super(entity);
        this.itemSwappedToMainHand = entity.getOffhandItem().copy();
        this.itemSwappedToOffHand = entity.getMainHandItem().copy();
    }

    public ItemStack getItemSwappedToMainHand() {
        return this.itemSwappedToMainHand;
    }

    public void setItemSwappedToMainHand(ItemStack itemSwappedToMainHand) {
        this.itemSwappedToMainHand = Objects.requireNonNull(itemSwappedToMainHand, "itemSwappedToMainHand");
    }

    public ItemStack getItemSwappedToOffHand() {
        return this.itemSwappedToOffHand;
    }

    public void setItemSwappedToOffHand(ItemStack itemSwappedToOffHand) {
        this.itemSwappedToOffHand = Objects.requireNonNull(itemSwappedToOffHand, "itemSwappedToOffHand");
    }
}
