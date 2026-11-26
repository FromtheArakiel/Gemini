package dev.arakiel.gemini.event.enchant;

import dev.arakiel.gemini.event.Cancelable;
import dev.arakiel.gemini.event.GeminiEvent;
import net.minecraft.core.Holder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

@Cancelable
public class GetEnchantmentLevelEvent extends GeminiEvent {
    private final ItemStack stack;
    private final Map<Holder<Enchantment>, Integer> levels;

    public GetEnchantmentLevelEvent(ItemStack stack, Map<Holder<Enchantment>, Integer> levels) {
        this.stack = Objects.requireNonNull(stack, "stack");
        this.levels = new LinkedHashMap<>(Objects.requireNonNull(levels, "levels"));
    }

    public ItemStack getStack() {
        return this.stack;
    }

    public Map<Holder<Enchantment>, Integer> getLevels() {
        return this.levels;
    }
}
