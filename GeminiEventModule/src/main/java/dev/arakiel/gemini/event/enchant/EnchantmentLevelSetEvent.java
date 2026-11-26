package dev.arakiel.gemini.event.enchant;

import dev.arakiel.gemini.event.Cancelable;
import dev.arakiel.gemini.event.GeminiEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

import java.util.Objects;

@Cancelable
public class EnchantmentLevelSetEvent extends GeminiEvent {
    private final Level level;
    private final BlockPos pos;
    private final int enchantRow;
    private final int power;
    private int enchantLevel;

    public EnchantmentLevelSetEvent(Level level, BlockPos pos, int enchantRow, int power, int enchantLevel) {
        this.level = Objects.requireNonNull(level, "level");
        this.pos = Objects.requireNonNull(pos, "pos");
        this.enchantRow = enchantRow;
        this.power = power;
        this.enchantLevel = Math.max(0, enchantLevel);
    }

    public Level getLevel() {
        return this.level;
    }

    public BlockPos getPos() {
        return this.pos;
    }

    public int getEnchantRow() {
        return this.enchantRow;
    }

    public int getPower() {
        return this.power;
    }

    public int getEnchantLevel() {
        return this.enchantLevel;
    }

    public void setEnchantLevel(int enchantLevel) {
        this.enchantLevel = Math.max(0, enchantLevel);
    }
}
