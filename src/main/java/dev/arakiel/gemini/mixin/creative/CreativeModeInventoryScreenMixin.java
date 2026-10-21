package dev.arakiel.gemini.mixin.creative;

import dev.arakiel.gemini.creative.client.FancyTabSections;
import dev.arakiel.gemini.creative.client.Section;
import dev.arakiel.gemini.creative.client.BannerRenderer;
import dev.arakiel.gemini.creative.client.FancyTabSectionsInternals;
import dev.arakiel.gemini.creative.client.IndexPanel;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(CreativeModeInventoryScreen.class)
public abstract class CreativeModeInventoryScreenMixin {
    @Shadow
    private static CreativeModeTab selectedTab;

    @Shadow
    private float scrollOffs;

    @Inject(method = "renderBg", at = @At("TAIL"))
    private void gemini$renderSections(GuiGraphics graphics, float partialTick, int mouseX, int mouseY, CallbackInfo callback) {
        CreativeModeInventoryScreen screen = (CreativeModeInventoryScreen) (Object) this;
        ResourceLocation tab = BuiltInRegistries.CREATIVE_MODE_TAB.getKey(selectedTab);
        if (tab == null) {
            return;
        }
        if (BannerRenderer.CURRENT_TAB == null || !BannerRenderer.CURRENT_TAB.equals(tab)) {
            BannerRenderer.CURRENT_TAB = tab;
            BannerRenderer.CURRENT_ROW = 0;
            FancyTabSectionsInternals.applyItems(selectedTab);
            ((CreativeModeInventoryScreenInvoker) screen).gemini$refreshCurrentTabContents(selectedTab.getDisplayItems());
        }
        if (!FancyTabSections.hasSections(tab)) {
            return;
        }
        List<Section<?>> sections = FancyTabSections.getSections(tab);
        BannerRenderer.render(screen, graphics, sections, mouseX, mouseY);
        float scroll = IndexPanel.tick();
        if (scroll >= 0.0F) {
            scrollOffs = scroll;
            screen.getMenu().scrollTo(scroll);
        }
        IndexPanel.render(screen, graphics, sections, mouseX, mouseY);
    }

    @Inject(method = "mouseClicked", at = @At("HEAD"), cancellable = true)
    private void gemini$mouseClicked(double mouseX, double mouseY, int button, CallbackInfoReturnable<Boolean> callback) {
        if (button != 0) {
            return;
        }
        CreativeModeInventoryScreen screen = (CreativeModeInventoryScreen) (Object) this;
        ResourceLocation tab = BuiltInRegistries.CREATIVE_MODE_TAB.getKey(selectedTab);
        if (tab == null || !FancyTabSections.hasSections(tab)) {
            return;
        }
        List<Section<?>> sections = FancyTabSections.getSections(tab);
        if (IndexPanel.mouseClicked(screen, sections, scrollOffs, mouseX, mouseY, button)) {
            callback.setReturnValue(true);
            return;
        }
        if (BannerRenderer.isInBanner(screen, mouseX, mouseY) && !screen.getMenu().getCarried().isEmpty()) {
            screen.getMenu().setCarried(ItemStack.EMPTY);
            callback.cancel();
            return;
        }
        if (!screen.getMenu().getCarried().isEmpty()) {
            return;
        }
        for (Section<?> section : sections) {
            if (!section.collapsible() || !BannerRenderer.isInToggle(screen, section, mouseX, mouseY)) {
                continue;
            }
            if (Screen.hasShiftDown()) {
                boolean collapsed = FancyTabSectionsInternals.isCollapsed(section);
                for (Section<?> other : sections) {
                    if (!other.collapsible()) {
                        continue;
                    }
                    if (collapsed) {
                        FancyTabSectionsInternals.expand(other);
                    } else {
                        FancyTabSectionsInternals.collapse(other);
                    }
                }
            } else {
                FancyTabSectionsInternals.toggle(section);
            }
            FancyTabSectionsInternals.applyItems(selectedTab);
            ((CreativeModeInventoryScreenInvoker) screen).gemini$refreshCurrentTabContents(selectedTab.getDisplayItems());
            callback.setReturnValue(true);
            return;
        }
    }

    @ModifyArg(
            method = "renderLabels",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/GuiGraphics;drawString(Lnet/minecraft/client/gui/Font;Lnet/minecraft/network/chat/Component;IIIZ)I"),
            index = 2
    )
    private int gemini$shiftTitleForIndexPanel(int x) {
        ResourceLocation tab = BuiltInRegistries.CREATIVE_MODE_TAB.getKey(selectedTab);
        return IndexPanel.active(FancyTabSections.getSections(tab)) ? IndexPanel.titleX() : x;
    }

    @Inject(method = "mouseScrolled", at = @At("HEAD"), cancellable = true)
    private void gemini$mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY, CallbackInfoReturnable<Boolean> callback) {
        CreativeModeInventoryScreen screen = (CreativeModeInventoryScreen) (Object) this;
        ResourceLocation tab = BuiltInRegistries.CREATIVE_MODE_TAB.getKey(selectedTab);
        if (tab == null) {
            return;
        }
        if (IndexPanel.mouseScrolled(screen, FancyTabSections.getSections(tab), mouseX, mouseY, scrollY)) {
            callback.setReturnValue(true);
        }
    }
}
