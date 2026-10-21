package dev.xkmc.l2modularblock.mult;

import dev.xkmc.l2modularblock.type.MultipleBlockMethod;
import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.TooltipContext;

public interface ToolTipBlockMethod extends MultipleBlockMethod {
   void appendHoverText(ItemStack var1, TooltipContext var2, List<Component> var3, TooltipFlag var4);
}
