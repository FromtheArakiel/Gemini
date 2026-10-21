package dev.xkmc.l2core.capability.attachment;

import dev.xkmc.l2serial.serialization.codec.TagCodec;
import dev.xkmc.l2serial.util.Wrappers;
import java.util.Objects;
import java.util.function.Supplier;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import net.neoforged.neoforge.attachment.IAttachmentSerializer;
import net.neoforged.neoforge.attachment.AttachmentType.Builder;

public class AttachmentDef<E> implements IAttachmentSerializer<CompoundTag, E> {
   private final Class<E> cls;
   private final Supplier<E> sup;
   private AttachmentType<E> type;

   public AttachmentDef(Class<E> cls, Supplier<E> sup) {
      this.cls = cls;
      this.sup = sup;
   }

   public AttachmentType<E> type() {
      if (this.type != null) {
         return this.type;
      }

      Builder<E> builder = AttachmentType.builder(this.sup);
      builder.serialize(this);
      if (this.copyOnDeath()) {
         builder.copyOnDeath();
      }

      this.type = builder.build();
      return this.type;
   }

   protected boolean copyOnDeath() {
      return false;
   }

   public E read(IAttachmentHolder holder, CompoundTag tag, Provider provider) {
      return Objects.requireNonNull((E)Wrappers.get(() -> new TagCodec(provider).fromTag(tag, this.cls, null)));
   }

   public CompoundTag write(E attachment, Provider provider) {
      return Objects.requireNonNull(new TagCodec(provider).toTag(new CompoundTag(), attachment));
   }

   public Class<E> cls() {
      return this.cls;
   }

   public boolean isFor(IAttachmentHolder holder) {
      return true;
   }
}
