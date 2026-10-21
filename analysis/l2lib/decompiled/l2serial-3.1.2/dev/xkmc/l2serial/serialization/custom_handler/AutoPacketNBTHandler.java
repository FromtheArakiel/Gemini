package dev.xkmc.l2serial.serialization.custom_handler;

import java.util.function.Function;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import org.jetbrains.annotations.NotNull;

public class AutoPacketNBTHandler<T> extends ClassHandler<CompoundTag, T> {
   public AutoPacketNBTHandler(@NotNull Class<T> cls, Function<CompoundTag, T> ft, Function<T, Tag> tt, @NotNull Class<?>... others) {
      super(cls, null, null, p -> ft.apply(p.readNbt()), (p, o) -> p.writeNbt(tt.apply(o)), ft, tt, others);
   }
}
