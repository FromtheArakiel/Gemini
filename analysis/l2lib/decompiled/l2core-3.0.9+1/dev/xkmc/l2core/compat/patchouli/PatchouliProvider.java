package dev.xkmc.l2core.compat.patchouli;

import com.tterrag.registrate.AbstractRegistrate;
import com.tterrag.registrate.providers.RegistrateProvider;
import dev.xkmc.l2core.serial.config.RecordDataProvider;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.data.PackOutput;
import net.neoforged.fml.LogicalSide;

public class PatchouliProvider extends RecordDataProvider implements RegistrateProvider, BiConsumer<String, Record> {
   private final AbstractRegistrate<?> owner;
   private BiConsumer<String, Record> map;

   public PatchouliProvider(AbstractRegistrate<?> owner, PackOutput output, CompletableFuture<Provider> pvd) {
      super(output, pvd, "Patchouli Provider");
      this.owner = owner;
   }

   public void accept(String path, Record rec) {
      if (this.map == null) {
         throw new IllegalStateException("Cannot accept recipes outside of a call to registerRecipes");
      }

      this.map.accept(path, rec);
   }

   public LogicalSide getSide() {
      return LogicalSide.SERVER;
   }

   @Override
   public void add(BiConsumer<String, Record> map) {
      this.map = map;
      this.owner.genData(PatchouliHelper.PATCHOULI, this);
      this.map = null;
   }
}
