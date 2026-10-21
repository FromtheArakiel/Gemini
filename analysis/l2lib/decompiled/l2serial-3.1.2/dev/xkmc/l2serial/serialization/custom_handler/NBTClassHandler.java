package dev.xkmc.l2serial.serialization.custom_handler;

import net.minecraft.nbt.Tag;

public interface NBTClassHandler<R extends Tag, T> {
   T fromTag(Tag var1);

   R toTag(Object var1);
}
