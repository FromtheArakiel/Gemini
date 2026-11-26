package dev.arakiel.gemini.event.client;

import com.mojang.datafixers.util.Either;
import dev.arakiel.gemini.event.Cancelable;
import dev.arakiel.gemini.event.GeminiEvent;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipPositioner;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.Objects;

public abstract class RenderTooltipEvent extends GeminiEvent {
    public enum Phase {
        GATHER_COMPONENTS,
        PRE,
        COLOR
    }

    private final ItemStack itemStack;
    private final GuiGraphics graphics;
    private int x;
    private int y;
    private Font font;
    private final List<ClientTooltipComponent> components;
    private final Phase phase;

    protected RenderTooltipEvent(
            ItemStack itemStack,
            GuiGraphics graphics,
            int x,
            int y,
            Font font,
            List<ClientTooltipComponent> components,
            Phase phase
    ) {
        this.itemStack = Objects.requireNonNull(itemStack, "itemStack");
        this.graphics = Objects.requireNonNull(graphics, "graphics");
        this.x = x;
        this.y = y;
        this.font = Objects.requireNonNull(font, "font");
        this.components = List.copyOf(components);
        this.phase = Objects.requireNonNull(phase, "phase");
    }

    public ItemStack getItemStack() {
        return this.itemStack;
    }

    public ItemStack getStack() {
        return this.itemStack;
    }

    public GuiGraphics getGraphics() {
        return this.graphics;
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    public Font getFont() {
        return this.font;
    }

    public List<ClientTooltipComponent> getComponents() {
        return this.components;
    }

    public Phase getPhase() {
        return this.phase;
    }

    @Cancelable
    public static final class GatherComponents extends GeminiEvent {
        private final ItemStack itemStack;
        private final int screenWidth;
        private final int screenHeight;
        private final List<Either<FormattedText, TooltipComponent>> tooltipElements;
        private int maxWidth;

        public GatherComponents(
                ItemStack itemStack,
                int screenWidth,
                int screenHeight,
                List<Either<FormattedText, TooltipComponent>> tooltipElements,
                int maxWidth
        ) {
            this.itemStack = Objects.requireNonNull(itemStack, "itemStack");
            this.screenWidth = screenWidth;
            this.screenHeight = screenHeight;
            this.tooltipElements = Objects.requireNonNull(tooltipElements, "tooltipElements");
            this.maxWidth = maxWidth;
        }

        public ItemStack getItemStack() {
            return this.itemStack;
        }

        public int getScreenWidth() {
            return this.screenWidth;
        }

        public int getScreenHeight() {
            return this.screenHeight;
        }

        public List<Either<FormattedText, TooltipComponent>> getTooltipElements() {
            return this.tooltipElements;
        }

        public int getMaxWidth() {
            return this.maxWidth;
        }

        public void setMaxWidth(int maxWidth) {
            this.maxWidth = maxWidth;
        }
    }

    @Cancelable
    public static final class Pre extends RenderTooltipEvent {
        private final int screenWidth;
        private final int screenHeight;
        private final ClientTooltipPositioner positioner;

        public Pre(
                ItemStack itemStack,
                GuiGraphics graphics,
                int x,
                int y,
                int screenWidth,
                int screenHeight,
                Font font,
                List<ClientTooltipComponent> components,
                ClientTooltipPositioner positioner
        ) {
            super(itemStack, graphics, x, y, font, components, Phase.PRE);
            this.screenWidth = screenWidth;
            this.screenHeight = screenHeight;
            this.positioner = Objects.requireNonNull(positioner, "positioner");
        }

        public int getScreenWidth() {
            return this.screenWidth;
        }

        public int getScreenHeight() {
            return this.screenHeight;
        }

        public ClientTooltipPositioner getTooltipPositioner() {
            return this.positioner;
        }

        public void setX(int x) {
            super.x = x;
        }

        public void setY(int y) {
            super.y = y;
        }

        public void setFont(Font font) {
            super.font = Objects.requireNonNull(font, "font");
        }
    }

    public static final class Color extends RenderTooltipEvent {
        private final int originalBackground;
        private final int originalBorderStart;
        private final int originalBorderEnd;
        private int backgroundStart;
        private int backgroundEnd;
        private int borderStart;
        private int borderEnd;

        public Color(
                ItemStack itemStack,
                GuiGraphics graphics,
                int x,
                int y,
                Font font,
                int background,
                int borderStart,
                int borderEnd,
                List<ClientTooltipComponent> components
        ) {
            super(itemStack, graphics, x, y, font, components, Phase.COLOR);
            this.originalBackground = background;
            this.originalBorderStart = borderStart;
            this.originalBorderEnd = borderEnd;
            this.backgroundStart = background;
            this.backgroundEnd = background;
            this.borderStart = borderStart;
            this.borderEnd = borderEnd;
        }

        public int getBackgroundStart() {
            return this.backgroundStart;
        }

        public int getBackgroundEnd() {
            return this.backgroundEnd;
        }

        public void setBackground(int background) {
            this.backgroundStart = background;
            this.backgroundEnd = background;
        }

        public void setBackgroundStart(int backgroundStart) {
            this.backgroundStart = backgroundStart;
        }

        public void setBackgroundEnd(int backgroundEnd) {
            this.backgroundEnd = backgroundEnd;
        }

        public int getBorderStart() {
            return this.borderStart;
        }

        public void setBorderStart(int borderStart) {
            this.borderStart = borderStart;
        }

        public int getBorderEnd() {
            return this.borderEnd;
        }

        public void setBorderEnd(int borderEnd) {
            this.borderEnd = borderEnd;
        }

        public int getOriginalBackgroundStart() {
            return this.originalBackground;
        }

        public int getOriginalBackgroundEnd() {
            return this.originalBackground;
        }

        public int getOriginalBorderStart() {
            return this.originalBorderStart;
        }

        public int getOriginalBorderEnd() {
            return this.originalBorderEnd;
        }
    }
}
