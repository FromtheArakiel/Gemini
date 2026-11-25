package dev.arakiel.gemini.creative.client;

import dev.arakiel.gemini.Gemini;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;

import java.util.List;
import java.util.function.Supplier;

public interface Section<T extends Section<T>> {
    ResourceLocation id();

    default boolean collapsible() {
        return true;
    }

    default Component title() {
        return Component.translatable("section." + id().getNamespace() + "." + id().getPath());
    }

    default ItemStack icon() {
        return items().getStacks().stream()
                .filter(stack -> !stack.isEmpty())
                .findFirst()
                .orElseGet(() -> Items.DIAMOND.getDefaultInstance());
    }

    default void onReload(RegistryAccess registryAccess) {
    }

    ConglomerateOfItems items();

    void render(GuiGraphics graphics, Font font, int topLeftX, int topLeftY);

    @SuppressWarnings("unchecked")
    default T add(Item item) {
        items().add(item);
        return (T) this;
    }

    @SuppressWarnings("unchecked")
    default T add(ItemStack stack) {
        items().add(stack);
        return (T) this;
    }

    @SuppressWarnings("unchecked")
    default T add(ItemLike itemLike) {
        items().add(itemLike);
        return (T) this;
    }

    @SuppressWarnings("unchecked")
    default T add(Supplier<ItemStack> supplier) {
        items().add(supplier);
        return (T) this;
    }

    @SuppressWarnings("unchecked")
    default T add(List<ItemStack> stacks) {
        items().add(stacks);
        return (T) this;
    }

    @SuppressWarnings("unchecked")
    default T add(ConglomerateOfItems.RegistryDependentEntry entry) {
        items().add(entry);
        return (T) this;
    }

    @SuppressWarnings("unchecked")
    default T addItemTag(TagKey<Item> tag) {
        add(registryAccess -> registryAccess.lookup(Registries.ITEM)
                .flatMap(lookup -> lookup.get(tag))
                .map(named -> named.stream().map(holder -> holder.value().getDefaultInstance()).toList())
                .orElseGet(List::of));
        return (T) this;
    }

    default void renderToggle(
            CreativeModeInventoryScreen screen,
            GuiGraphics graphics,
            Section<?> section,
            int x,
            int y,
            int width,
            int bannerWidth,
            int mouseX,
            int mouseY,
            boolean hoveringAny
    ) {
        if (!section.collapsible()) {
            return;
        }
        int tx1 = x + width + 3;
        int tx0 = tx1 - BannerRenderer.ROW_HEIGHT;
        int ty0 = y - 1;
        int ty1 = y + bannerWidth;
        int bx = tx0 + (BannerRenderer.ROW_HEIGHT - 16) / 2 + 1;
        int by = ty0 + (bannerWidth - 16) / 2 + 1;
        boolean hovered = ((hoveringAny && Screen.hasShiftDown())
                || mouseX >= tx0 && mouseX < tx1 && mouseY >= ty0 && mouseY < ty1)
                && screen.getMenu().getCarried().isEmpty();
        boolean collapsed = FancyTabSectionsInternals.isCollapsed(section);
        ResourceLocation texture = collapsed
                ? (hovered ? BannerRenderer.COLLAPSED_BUTTON_HIGHLIGHT : BannerRenderer.COLLAPSED_BUTTON)
                : (hovered ? BannerRenderer.EXPANDED_BUTTON_HIGHLIGHT : BannerRenderer.EXPANDED_BUTTON);
        graphics.blit(texture, bx, by, 0, 0, 16, 16, 16, 16);
    }
}
