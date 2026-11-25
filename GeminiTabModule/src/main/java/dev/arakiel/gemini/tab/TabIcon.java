package dev.arakiel.gemini.tab;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.Objects;
import java.util.function.Supplier;

public interface TabIcon {
    void render(GuiGraphics graphics, int x, int y);

    static TabIcon item(Item item) {
        Objects.requireNonNull(item, "item");
        return item(() -> item.getDefaultInstance());
    }

    static TabIcon item(Supplier<ItemStack> stack) {
        Objects.requireNonNull(stack, "stack");
        return new ItemIcon(stack);
    }

    static TabIcon sprite(ResourceLocation sprite) {
        Objects.requireNonNull(sprite, "sprite");
        return new SpriteIcon(sprite);
    }

    final class ItemIcon implements TabIcon {
        private final Supplier<ItemStack> stack;

        private ItemIcon(Supplier<ItemStack> stack) {
            this.stack = stack;
        }

        @Override
        public void render(GuiGraphics graphics, int x, int y) {
            ItemStack current = this.stack.get();
            graphics.renderFakeItem(current == null ? Items.AIR.getDefaultInstance() : current, x, y);
        }
    }

    record SpriteIcon(ResourceLocation sprite) implements TabIcon {
        @Override
        public void render(GuiGraphics graphics, int x, int y) {
            graphics.blitSprite(this.sprite, x, y, 16, 16);
        }
    }
}
