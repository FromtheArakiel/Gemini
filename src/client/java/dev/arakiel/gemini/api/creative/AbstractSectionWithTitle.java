package dev.arakiel.gemini.api.creative;

import net.minecraft.Util;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;

import java.util.function.Consumer;
import java.util.function.Function;

public abstract class AbstractSectionWithTitle<T extends AbstractSectionWithTitle<T>> implements Section<T>, StickySection {
    protected final ResourceLocation id;
    protected Component title;
    protected boolean renderTitle = true;
    protected int titleOffsetX = 5;
    protected int titleOffsetY = 5;
    protected int textColor = 0xFFFFFFFF;
    protected int textOutline;
    protected boolean textShadow = true;
    protected boolean centered;
    protected boolean collapsible = true;
    protected boolean sticky = true;
    protected Function<RegistryAccess, ItemStack> displayItemFunction;
    protected ItemStack displayItem;
    protected Runnable onRender = () -> {
    };
    protected Consumer<T> onRenderConsumer = section -> {
    };
    protected ConglomerateOfItems items = ConglomerateOfItems.create();

    protected AbstractSectionWithTitle(ResourceLocation id) {
        this.id = id;
        this.title = Component.translatable("section." + id.getNamespace() + "." + id.getPath());
    }

    @Override
    @SuppressWarnings("unchecked")
    public void render(GuiGraphics graphics, Font font, int topLeftX, int topLeftY) {
        onRender.run();
        onRenderConsumer.accept((T) this);
        renderTitle(graphics, font, topLeftX, topLeftY);
    }

    protected void renderTitle(GuiGraphics graphics, Font font, int topLeftX, int topLeftY) {
        if (!renderTitle) {
            return;
        }
        int x = topLeftX + titleOffsetX;
        int y = topLeftY + titleOffsetY;
        if (centered) {
            if (textOutline != 0) {
                centeredScrollingText(graphics, font, title, x + 78, x, x + 137, y - 1, textOutline, false);
                centeredScrollingText(graphics, font, title, x + 78, x, x + 137, y + 1, textOutline, textShadow);
                centeredScrollingText(graphics, font, title, x + 79, x + 1, x + 138, y, textOutline, textShadow);
                centeredScrollingText(graphics, font, title, x + 77, x - 1, x + 136, y, textOutline, false);
            }
            centeredScrollingText(graphics, font, title, x + 78, x, x + 137, y, textColor, textShadow);
        } else {
            if (textOutline != 0) {
                scrollingText(graphics, font, title, x, x + 137, y - 1, textOutline, false, 100);
                scrollingText(graphics, font, title, x, x + 137, y + 1, textOutline, textShadow, 100);
                scrollingText(graphics, font, title, x + 1, x + 138, y, textOutline, textShadow, 100);
                scrollingText(graphics, font, title, x - 1, x + 136, y, textOutline, false, 100);
            }
            scrollingText(graphics, font, title, x, x + 137, y, textColor, textShadow, 100);
        }
    }

    @SuppressWarnings("unchecked")
    public T setTitle(Component title) {
        this.title = title;
        return (T) this;
    }

    @SuppressWarnings("unchecked")
    public T setTitle(String title) {
        this.title = Component.literal(title);
        return (T) this;
    }

    @SuppressWarnings("unchecked")
    public T setRenderTitle(boolean renderTitle) {
        this.renderTitle = renderTitle;
        return (T) this;
    }

    @SuppressWarnings("unchecked")
    public T setDisplayItem(Function<RegistryAccess, ItemStack> item) {
        this.displayItemFunction = item;
        return (T) this;
    }

    @Override
    public void onReload(RegistryAccess registryAccess) {
        if (displayItemFunction != null) {
            displayItem = displayItemFunction.apply(registryAccess);
        }
    }

    @Override
    public ItemStack icon() {
        return displayItem == null ? Section.super.icon() : displayItem;
    }

    @SuppressWarnings("unchecked")
    public T setTitleOffset(int x, int y) {
        this.titleOffsetX = x;
        this.titleOffsetY = y;
        return (T) this;
    }

    @SuppressWarnings("unchecked")
    public T setCentered(boolean centered) {
        this.centered = centered;
        return (T) this;
    }

    @SuppressWarnings("unchecked")
    public T setTextColor(int textColor) {
        this.textColor = textColor;
        return (T) this;
    }

    @SuppressWarnings("unchecked")
    public T setTextOutline(int textOutline) {
        this.textOutline = textOutline;
        return (T) this;
    }

    @SuppressWarnings("unchecked")
    public T setTextShadow(boolean textShadow) {
        this.textShadow = textShadow;
        return (T) this;
    }

    @SuppressWarnings("unchecked")
    public T setCollapsible(boolean collapsible) {
        this.collapsible = collapsible;
        return (T) this;
    }

    @SuppressWarnings("unchecked")
    public T setItems(ConglomerateOfItems items) {
        this.items = items;
        return (T) this;
    }

    @SuppressWarnings("unchecked")
    public T setSticky(boolean sticky) {
        this.sticky = sticky;
        return (T) this;
    }

    @SuppressWarnings("unchecked")
    public T setOnRender(Runnable onRender) {
        this.onRender = onRender;
        return (T) this;
    }

    @SuppressWarnings("unchecked")
    public T setOnRenderConsumer(Consumer<T> consumer) {
        this.onRenderConsumer = consumer;
        return (T) this;
    }

    @Override
    public ResourceLocation id() {
        return id;
    }

    @Override
    public Component title() {
        return title;
    }

    @Override
    public ConglomerateOfItems items() {
        return items;
    }

    @Override
    public boolean collapsible() {
        return collapsible;
    }

    @Override
    public boolean isSticky() {
        return sticky;
    }

    public static void centeredScrollingText(
            GuiGraphics graphics,
            Font font,
            Component text,
            int centerX,
            int minX,
            int maxX,
            int y,
            int color,
            boolean shadow
    ) {
        int width = font.width(text);
        int available = maxX - minX;
        if (width > available) {
            int overflow = width - available;
            double phase = (double) Util.getMillis() / 300.0D;
            double duration = Math.max((double) overflow * 0.5F, 3.0D);
            double wave = Math.sin((Math.PI / 2.0D) * Math.cos((Math.PI * 2.0D) * phase / duration)) / 2.0D + 0.5D;
            double offset = Mth.lerp(wave, 0.0F, overflow);
            graphics.enableScissor(minX, y - 10, maxX, y + 10);
            graphics.drawString(font, text, minX - (int) offset, y, color, shadow);
            graphics.disableScissor();
        } else {
            int x = Mth.clamp(centerX, minX + width / 2, maxX - width / 2);
            graphics.drawString(font, text.getVisualOrderText(), x - font.width(text.getVisualOrderText()) / 2, y, color, shadow);
        }
    }

    public static void scrollingText(
            GuiGraphics graphics,
            Font font,
            Component text,
            int minX,
            int maxX,
            int y,
            int color,
            boolean shadow,
            int speed
    ) {
        int width = font.width(text);
        int available = maxX - minX;
        if (width > available) {
            int overflow = width - available;
            double phase = (double) Util.getMillis() / (double) speed;
            double duration = Math.max((double) overflow * 0.5F, 3.0D);
            double wave = Math.sin((Math.PI / 2.0D) * Math.cos((Math.PI * 2.0D) * phase / duration)) / 2.0D + 0.5D;
            double offset = Mth.lerp(wave, 0.0F, overflow);
            graphics.enableScissor(minX, y - 20, maxX, y + 20);
            graphics.drawString(font, text, minX - (int) offset, y, color, shadow);
            graphics.disableScissor();
        } else {
            graphics.drawString(font, text.getVisualOrderText(), minX, y, color, shadow);
        }
    }
}
