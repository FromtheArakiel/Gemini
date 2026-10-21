package com.tterrag.registrate.util;

import java.util.function.Supplier;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.loading.FMLEnvironment;

public class RegistrateDistExecutor {
   public static void unsafeRunWhenOn(Dist dist, Supplier<Runnable> toRun) {
      if (dist == FMLEnvironment.dist) {
         toRun.get().run();
      }
   }
}
