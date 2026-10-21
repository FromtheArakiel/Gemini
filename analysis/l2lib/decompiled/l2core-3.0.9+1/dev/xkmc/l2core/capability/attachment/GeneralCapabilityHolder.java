package dev.xkmc.l2core.capability.attachment;

import dev.xkmc.l2serial.util.Wrappers;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;
import java.util.function.Supplier;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.attachment.IAttachmentHolder;

public class GeneralCapabilityHolder<E extends IAttachmentHolder, T extends GeneralCapabilityTemplate<E, T>> extends AttachmentDef<T> {
   public static final Map<ResourceLocation, GeneralCapabilityHolder<?, ?>> INTERNAL_MAP = new ConcurrentHashMap<>();
   public final ResourceLocation id;
   public final Class<E> entity_class;
   private final Predicate<E> pred;

   public GeneralCapabilityHolder(ResourceLocation id, Class<T> holder_class, Supplier<T> sup, Class<E> entity_class, Predicate<E> pred) {
      super(holder_class, sup);
      this.id = id;
      this.entity_class = entity_class;
      this.pred = pred;
      INTERNAL_MAP.put(id, this);
   }

   public T getOrCreate(E e) {
      return (T)e.getData(this.type());
   }

   public Optional<T> getExisting(E e) {
      return e.getExistingData(this.type());
   }

   @Override
   public boolean isFor(IAttachmentHolder holder) {
      return this.entity_class.isInstance(holder) && this.isProper((E)Wrappers.cast(holder));
   }

   public boolean isProper(E entity) {
      return this.pred.test(entity);
   }
}
