package dev.xkmc.l2modularblock.core;

import dev.xkmc.l2modularblock.one.BlockEntityBlockMethod;
import dev.xkmc.l2modularblock.type.BlockMethod;
import dev.xkmc.l2modularblock.type.WaterloggedMethodMarker;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;

@ParametersAreNonnullByDefault
public class DelegateBlock extends Block {
   public static DelegateBlock newBaseBlock(Properties p, BlockMethod... impl) {
      boolean water = false;
      boolean entity = false;

      for (BlockMethod m : impl) {
         if (m instanceof BlockEntityBlockMethod) {
            entity = true;
         }

         if (m instanceof WaterloggedMethodMarker) {
            water = true;
         }
      }

      return water
         ? (entity ? new DelegateEntityBlockSimpleWaterImpl(p, impl) : new DelegateBlockSimpleWaterImpl(p, impl))
         : (entity ? new DelegateEntityBlockImpl(p, impl) : new DelegateBlockImpl(p, impl));
   }

   protected DelegateBlock(Properties props) {
      super(props);
   }
}
