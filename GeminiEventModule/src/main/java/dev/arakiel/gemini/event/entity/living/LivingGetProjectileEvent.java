package dev.arakiel.gemini.event.entity.living;

import dev.arakiel.gemini.event.Cancelable;
import dev.arakiel.gemini.event.entity.LivingEvent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

import java.util.Objects;

@Cancelable
public class LivingGetProjectileEvent extends LivingEvent {
    private final ItemStack weapon;
    private ItemStack projectile;

    public LivingGetProjectileEvent(LivingEntity entity, ItemStack weapon, ItemStack projectile) {
        super(entity);
        this.weapon = Objects.requireNonNull(weapon, "weapon");
        this.projectile = Objects.requireNonNull(projectile, "projectile");
    }

    public ItemStack getWeapon() {
        return this.weapon;
    }

    public ItemStack getProjectile() {
        return this.projectile;
    }

    public void setProjectile(ItemStack projectile) {
        this.projectile = Objects.requireNonNull(projectile, "projectile");
    }
}
