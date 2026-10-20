package dev.arakiel.gemini.mixin.fancy;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import dev.arakiel.gemini.api.creative.FancyTabSections;
import dev.arakiel.gemini.api.creative.FancyTabSectionsInternals;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;

import java.util.List;

@Mixin(CreativeModeTab.class)
public class CreativeModeTabMixin {
    @WrapMethod(method = "buildContents")
    private void gemini$buildContents(CreativeModeTab.ItemDisplayParameters parameters, Operation<Void> original) {
        CreativeModeTab tab = (CreativeModeTab) (Object) this;
        ResourceLocation id = BuiltInRegistries.CREATIVE_MODE_TAB.getKey(tab);
        if (!FancyTabSections.hasSections(id)) {
            original.call(parameters);
            return;
        }
        ((CreativeModeTabAccessor) tab).gemini$setDisplayItems(List.of(Items.DIAMOND.getDefaultInstance()));
        FancyTabSectionsInternals.applyItems(tab);
    }
}
