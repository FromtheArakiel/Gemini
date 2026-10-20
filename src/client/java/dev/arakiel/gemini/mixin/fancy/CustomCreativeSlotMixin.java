package dev.arakiel.gemini.mixin.fancy;

import dev.arakiel.gemini.api.creative.BannerRenderer;
import dev.arakiel.gemini.api.creative.FancyTabSectionsInternals;
import net.minecraft.world.inventory.Slot;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(targets = "net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen$CustomCreativeSlot")
public abstract class CustomCreativeSlotMixin extends Slot {
    public CustomCreativeSlotMixin() {
        super(null, 0, 0, 0);
    }

    @Override
    public boolean isActive() {
        int row = BannerRenderer.CURRENT_ROW + this.index / 9;
        return !FancyTabSectionsInternals.isBannerRow(BannerRenderer.CURRENT_TAB, row);
    }

    @Override
    public boolean isHighlightable() {
        int row = BannerRenderer.CURRENT_ROW + this.index / 9;
        return BannerRenderer.CURRENT_TAB == null || !FancyTabSectionsInternals.isBannerRow(BannerRenderer.CURRENT_TAB, row);
    }
}
