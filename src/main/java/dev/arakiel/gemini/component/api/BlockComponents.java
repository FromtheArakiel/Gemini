package dev.arakiel.gemini.component.api;

import java.util.function.BiFunction;
import net.fabricmc.fabric.api.lookup.v1.block.BlockApiLookup;
import net.minecraft.world.level.block.entity.BlockEntityType;

public final class BlockComponents {
   public static <C extends Component> BlockApiLookup<C, Void> getApiLookup(ComponentKey<C> key) {
      return BlockApiLookup.get(key.getId(), key.getComponentClass(), Void.class);
   }

   public static <A, T> void exposeApi(ComponentKey<? extends A> key, BlockApiLookup<A, T> apiLookup) {
      apiLookup.registerFallback((world, pos, state, blockEntity, context) -> blockEntity != null ? key.getNullable(blockEntity) : null);
   }

   public static <A, T, C extends Component> void exposeApi(
      ComponentKey<C> key, BlockApiLookup<A, T> apiLookup, BiFunction<? super C, ? super T, ? extends A> mapper
   ) {
      apiLookup.registerFallback((world, pos, state, blockEntity, context) -> {
         if (blockEntity != null) {
            C ret = key.getNullable(blockEntity);
            if (ret != null) {
               return mapper.apply(ret, (T)context);
            }
         }

         return null;
      });
   }

   public static <A, T, C extends Component> void exposeApi(
      ComponentKey<C> key, BlockApiLookup<A, T> apiLookup, BiFunction<? super C, ? super T, ? extends A> mapper, BlockEntityType<?>... types
   ) {
      apiLookup.registerForBlockEntities((blockEntity, context) -> {
         C component = key.getNullable(blockEntity);
         return component == null ? null : mapper.apply(component, (T)context);
      }, types);
   }
}
