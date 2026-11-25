package dev.arakiel.gemini.event.client;

import dev.arakiel.gemini.event.Cancelable;
import dev.arakiel.gemini.event.GeminiEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

@Cancelable
public class ItemTooltipEvent extends GeminiEvent {
    private final ItemStack stack;
    private final @Nullable Player player;
    private final TooltipFlag flags;
    private final List<Component> lines;

    public ItemTooltipEvent(
            ItemStack stack,
            @Nullable Player player,
            TooltipFlag flags,
            List<Component> lines
    ) {
        this.stack = Objects.requireNonNull(stack, "stack");
        this.player = player;
        this.flags = Objects.requireNonNull(flags, "flags");
        this.lines = Objects.requireNonNull(lines, "lines");
    }

    public ItemStack getStack() {
        return this.stack;
    }

    public @Nullable Player getPlayer() {
        return this.player;
    }

    public TooltipFlag getFlags() {
        return this.flags;
    }

    public List<Component> getLines() {
        return this.lines;
    }
}
