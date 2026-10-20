package dev.arakiel.gemini.mixin.fancy;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Collection;
import java.util.Set;

@Mixin(CreativeModeTab.class)
public interface CreativeModeTabAccessor {
    @Accessor("displayItems")
    void gemini$setDisplayItems(Collection<ItemStack> items);

    @Accessor("displayItemsSearchTab")
    void gemini$setDisplayItemsSearchTab(Set<ItemStack> items);
}
