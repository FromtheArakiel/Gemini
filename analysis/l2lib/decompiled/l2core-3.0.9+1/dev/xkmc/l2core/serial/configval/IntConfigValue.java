package dev.xkmc.l2core.serial.configval;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import dev.xkmc.l2core.util.ConfigInit;
import java.util.List;
import java.util.function.Function;
import java.util.function.IntSupplier;
import net.neoforged.neoforge.common.ModConfigSpec.ConfigValue;

public record IntConfigValue(String path, List<String> line) implements IntSupplier {
   public static Codec<IntSupplier> CODEC = Codec.either(Codec.INT, Codec.STRING)
      .xmap(
         e -> (IntSupplier)e.map(x -> () -> x, IntConfigValue::of),
         e -> e instanceof IntConfigValue val ? Either.right(val.toData()) : Either.left(e.getAsInt())
      );

   public static <T extends ConfigInit> IntConfigValue of(T file, Function<T, ConfigValue<Integer>> config) {
      return new IntConfigValue(file.getPath(), config.apply(file).getPath());
   }

   public static IntConfigValue of(String data) {
      int last = data.lastIndexOf(47);
      String[] line = data.substring(last + 1).split("\\.");
      return new IntConfigValue(data.substring(0, last), List.of(line));
   }

   @Override
   public int getAsInt() {
      return AbstractConfigParser.parse(this.path, this.line).map(e -> e instanceof Number val ? val.intValue() : 0).orElse(0);
   }

   public String toData() {
      StringBuilder lines = new StringBuilder();

      for (String e : this.line) {
         if (!lines.isEmpty()) {
            lines.append(".");
         }

         lines.append(e);
      }

      return this.path + "/" + lines;
   }
}
