package dev.xkmc.l2modularblock.core;

import dev.xkmc.l2modularblock.impl.AllDireBlockMethodImpl;
import dev.xkmc.l2modularblock.impl.HorizontalBlockMethodImpl;
import dev.xkmc.l2modularblock.impl.PowerBlockMethodImpl;
import dev.xkmc.l2modularblock.impl.SimpleWaterloggedImpl;
import dev.xkmc.l2modularblock.impl.TriggerBlockMethodImpl;
import dev.xkmc.l2modularblock.type.BlockMethod;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;

public class BlockTemplates {
   public static final BlockMethod POWER = new PowerBlockMethodImpl();
   public static final BlockMethod ALL_DIRECTION = new AllDireBlockMethodImpl();
   public static final BlockMethod HORIZONTAL = new HorizontalBlockMethodImpl();
   public static final BlockMethod TRIGGER = new TriggerBlockMethodImpl(4);
   public static final BlockMethod WATER = new SimpleWaterloggedImpl();
   public static final DirectionProperty FACING = BlockStateProperties.FACING;
   public static final DirectionProperty HORIZONTAL_FACING = BlockStateProperties.HORIZONTAL_FACING;
}
