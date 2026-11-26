package dev.arakiel.gemini.mixin.event;

import dev.arakiel.gemini.event.GeminiEvents;
import dev.arakiel.gemini.event.entity.living.ArmorHurtEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.EnumMap;

@Mixin({LivingEntity.class, Player.class})
public abstract class ArmorHurtEventMixin {
    @Inject(method = "hurtArmor", at = @At("HEAD"), cancellable = true)
    private void gemini$armorHurt(DamageSource source, float damage, CallbackInfo callback) {
        LivingEntity entity = (LivingEntity) (Object) this;
        EnumMap<EquipmentSlot, ArmorHurtEvent.ArmorEntry> entries = new EnumMap<>(EquipmentSlot.class);
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            if (slot.getType() != EquipmentSlot.Type.HUMANOID_ARMOR && slot != EquipmentSlot.BODY) {
                continue;
            }
            ItemStack armor = entity.getItemBySlot(slot);
            if (armor.isEmpty()) {
                continue;
            }
            float originalDamage = armor.getItem() instanceof ArmorItem && armor.canBeHurtBy(source)
                    ? Math.max(0.0F, damage)
                    : 0.0F;
            entries.put(slot, new ArmorHurtEvent.ArmorEntry(armor, originalDamage));
        }

        ArmorHurtEvent event = new ArmorHurtEvent(entity, source, entries);
        GeminiEvents.post(event);
        if (!event.isCanceled()) {
            event.getArmorMap().forEach((slot, entry) ->
                    entry.armorItemStack().hurtAndBreak((int) entry.newDamage(), entity, slot));
        }
        callback.cancel();
    }
}
