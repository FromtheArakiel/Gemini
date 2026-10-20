package dev.arakiel.gemini.component.internal.base;

import dev.arakiel.gemini.component.Component;
import dev.arakiel.gemini.component.ComponentContainer;
import dev.arakiel.gemini.component.ComponentKey;
import dev.arakiel.gemini.component.ComponentRegistry;
import dev.arakiel.gemini.component.CopyableComponent;
import java.util.Iterator;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;

@SuppressWarnings({"unchecked", "deprecation"})
public abstract class AbstractComponentContainer implements ComponentContainer {
   public static final String NBT_KEY = "gemini_components";

   @Override
   public void copyFrom(ComponentContainer other, Provider registryLookup) {
      for (ComponentKey<?> key : this.keys()) {
         Component theirs = key.getInternal(other);
         Component ours = key.getInternal(this);

         assert ours != null;

         if (theirs != null && !ours.equals(theirs)) {
            if (ours instanceof CopyableComponent<?> copyable) {
               ((CopyableComponent) copyable).copyFrom(theirs, registryLookup);
            } else {
               CompoundTag tag = new CompoundTag();
               theirs.writeToNbt(tag, registryLookup);
               ours.readFromNbt(tag, registryLookup);
            }
         }
      }
   }

   @Override
   public void fromTag(CompoundTag tag, Provider registryLookup) {
      if (tag.contains("gemini_components", 9)) {
         ListTag componentList = tag.getList("gemini_components", 10);

         for (int i = 0; i < componentList.size(); i++) {
            CompoundTag nbt = componentList.getCompound(i);
            ComponentKey<?> type = ComponentRegistry.get(ResourceLocation.parse(nbt.getString("componentId")));
            if (type != null) {
               Component component = type.getInternal(this);
               if (component != null) {
                  component.readFromNbt(nbt, registryLookup);
               }
            }
         }
      } else if (tag.contains("gemini_components", 10)) {
         CompoundTag componentMap = tag.getCompound("gemini_components");

         for (ComponentKey<?> key : this.keys()) {
            String keyId = key.getId().toString();
            if (componentMap.contains(keyId, 10)) {
               Component component = key.getInternal(this);

               assert component != null;

               component.readFromNbt(componentMap.getCompound(keyId), registryLookup);
               componentMap.remove(keyId);
            }
         }

         ComponentsInternals.logDeserializationWarnings(componentMap.getAllKeys());
      }
   }

   @Override
   public CompoundTag toTag(CompoundTag tag, Provider registryLookup) {
      if (this.hasComponents()) {
         CompoundTag componentMap = null;
         CompoundTag componentTag = new CompoundTag();

         for (ComponentKey<?> type : this.keys()) {
            Component component = type.getFromContainer(this);
            component.writeToNbt(componentTag, registryLookup);
            if (!componentTag.isEmpty()) {
               if (componentMap == null) {
                  componentMap = new CompoundTag();
                  tag.put("gemini_components", componentMap);
               }

               componentMap.put(type.getId().toString(), componentTag);
               componentTag = new CompoundTag();
            }
         }
      }

      return tag;
   }

   @Override
   public String toString() {
      Iterator<ComponentKey<?>> i = this.keys().iterator();
      if (!i.hasNext()) {
         return "{}";
      } else {
         StringBuilder sb = new StringBuilder();
         sb.append('{');

         while (true) {
            ComponentKey<?> key = i.next();
            Component value = key.getInternal(this);
            sb.append(key);
            sb.append('=');
            sb.append(value);
            if (!i.hasNext()) {
               return sb.append('}').toString();
            }

            sb.append(',').append(' ');
         }
      }
   }
}
