package dev.arakiel.gemini.mixin.creative;

import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.Collection;

@Mixin(CreativeModeInventoryScreen.class)
public interface CreativeModeInventoryScreenInvoker {
    @Invoker("refreshCurrentTabContents")
    void gemini$refreshCurrentTabContents(Collection<ItemStack> items);
}
