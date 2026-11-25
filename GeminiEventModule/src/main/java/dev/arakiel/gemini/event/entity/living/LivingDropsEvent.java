package dev.arakiel.gemini.event.entity.living;

import dev.arakiel.gemini.event.Cancelable;
import dev.arakiel.gemini.event.entity.LivingEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.Objects;

@Cancelable
public class LivingDropsEvent extends LivingEvent {
    private final DamageSource source;
    private final List<ItemStack> drops;
    private final int lootingLevel;
    private final boolean recentlyHit;

    public LivingDropsEvent(
            LivingEntity entity,
            DamageSource source,
            List<ItemStack> drops,
            int lootingLevel,
            boolean recentlyHit
    ) {
        super(entity);
        this.source = Objects.requireNonNull(source, "source");
        this.drops = Objects.requireNonNull(drops, "drops");
        this.lootingLevel = Math.max(0, lootingLevel);
        this.recentlyHit = recentlyHit;
    }

    public DamageSource getSource() {
        return this.source;
    }

    public List<ItemStack> getDrops() {
        return this.drops;
    }

    public int getLootingLevel() {
        return this.lootingLevel;
    }

    public boolean isRecentlyHit() {
        return this.recentlyHit;
    }
}
