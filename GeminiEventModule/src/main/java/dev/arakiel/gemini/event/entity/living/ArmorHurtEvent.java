package dev.arakiel.gemini.event.entity.living;

import dev.arakiel.gemini.event.Cancelable;
import dev.arakiel.gemini.event.entity.LivingEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;

@Cancelable
public class ArmorHurtEvent extends LivingEvent {
    private final DamageSource source;
    private final EnumMap<EquipmentSlot, ArmorEntry> armorEntries;

    public ArmorHurtEvent(LivingEntity entity, DamageSource source, ItemStack armor, int damage) {
        this(entity, source, Map.of(EquipmentSlot.CHEST, new ArmorEntry(armor, damage)));
    }

    public ArmorHurtEvent(
            LivingEntity entity,
            DamageSource source,
            Map<EquipmentSlot, ArmorEntry> armorEntries
    ) {
        super(entity);
        this.source = Objects.requireNonNull(source, "source");
        this.armorEntries = new EnumMap<>(EquipmentSlot.class);
        this.armorEntries.putAll(Objects.requireNonNull(armorEntries, "armorEntries"));
    }

    public DamageSource getSource() {
        return this.source;
    }

    public ItemStack getArmor() {
        return this.getArmorItemStack(EquipmentSlot.CHEST);
    }

    public int getDamage() {
        return Math.round(this.getNewDamage(EquipmentSlot.CHEST));
    }

    public void setDamage(int damage) {
        this.setNewDamage(EquipmentSlot.CHEST, Math.max(0, damage));
    }

    public ItemStack getArmorItemStack(EquipmentSlot slot) {
        ArmorEntry entry = this.armorEntries.get(slot);
        return entry == null ? ItemStack.EMPTY : entry.armorItemStack();
    }

    public float getOriginalDamage(EquipmentSlot slot) {
        ArmorEntry entry = this.armorEntries.get(slot);
        return entry == null ? 0.0F : entry.originalDamage();
    }

    public float getNewDamage(EquipmentSlot slot) {
        ArmorEntry entry = this.armorEntries.get(slot);
        return entry == null ? 0.0F : entry.newDamage();
    }

    public void setNewDamage(EquipmentSlot slot, float damage) {
        ArmorEntry entry = this.armorEntries.get(slot);
        if (entry != null) {
            this.armorEntries.put(slot, new ArmorEntry(entry.armorItemStack(), entry.originalDamage(), damage));
        }
    }

    public Map<EquipmentSlot, ArmorEntry> getArmorMap() {
        return Collections.unmodifiableMap(this.armorEntries);
    }

    public record ArmorEntry(ItemStack armorItemStack, float originalDamage, float newDamage) {
        public ArmorEntry {
            Objects.requireNonNull(armorItemStack, "armorItemStack");
            if (originalDamage < 0.0F || newDamage < 0.0F) {
                throw new IllegalArgumentException("Armor damage must not be negative");
            }
        }

        public ArmorEntry(ItemStack armorItemStack, float damage) {
            this(armorItemStack, damage, damage);
        }
    }
}
