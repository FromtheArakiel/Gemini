package dev.xkmc.l2modularblock.one;

import dev.xkmc.l2modularblock.type.SingletonBlockMethod;
import java.util.List;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams.Builder;

public interface SpecialDropBlockMethod extends SingletonBlockMethod {
   List<ItemStack> getDrops(BlockState var1, Builder var2);
}
