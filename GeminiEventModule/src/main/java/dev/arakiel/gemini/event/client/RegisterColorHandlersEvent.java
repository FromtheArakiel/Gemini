package dev.arakiel.gemini.event.client;

import dev.arakiel.gemini.event.GeminiEvent;
import net.minecraft.client.color.block.BlockColor;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class RegisterColorHandlersEvent extends GeminiEvent {
    private final List<BlockRegistration> blockRegistrations = new ArrayList<>();
    private final List<ItemRegistration> itemRegistrations = new ArrayList<>();

    public void registerBlock(BlockColor provider, Block... blocks) {
        this.blockRegistrations.add(new BlockRegistration(provider, List.of(blocks)));
    }

    public void registerItem(ItemColor provider, Item... items) {
        this.itemRegistrations.add(new ItemRegistration(provider, List.of(items)));
    }

    public List<BlockRegistration> getBlockRegistrations() {
        return List.copyOf(this.blockRegistrations);
    }

    public List<ItemRegistration> getItemRegistrations() {
        return List.copyOf(this.itemRegistrations);
    }

    public record BlockRegistration(BlockColor provider, List<Block> blocks) {
        public BlockRegistration {
            Objects.requireNonNull(provider, "provider");
            blocks = List.copyOf(blocks);
        }
    }

    public record ItemRegistration(ItemColor provider, List<Item> items) {
        public ItemRegistration {
            Objects.requireNonNull(provider, "provider");
            items = List.copyOf(items);
        }
    }
}
