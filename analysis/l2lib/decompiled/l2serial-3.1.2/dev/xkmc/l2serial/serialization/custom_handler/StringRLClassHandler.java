package dev.xkmc.l2serial.serialization.custom_handler;

import java.util.function.Supplier;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;

public class StringRLClassHandler<T> extends StringClassHandler<T> {
   @Deprecated
   public StringRLClassHandler(Class<T> cls, Supplier<Registry<T>> reg) {
      super(cls, s -> (T)reg.get().get(ResourceLocation.parse(s)), t -> reg.get().getKey(t).toString());
   }
}
