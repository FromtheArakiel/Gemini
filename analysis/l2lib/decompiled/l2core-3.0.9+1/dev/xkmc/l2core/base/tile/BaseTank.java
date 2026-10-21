package dev.xkmc.l2core.base.tile;

import dev.xkmc.l2serial.serialization.codec.AliasCollection;
import dev.xkmc.l2serial.serialization.marker.SerialClass;
import dev.xkmc.l2serial.serialization.marker.SerialField;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BooleanSupplier;
import java.util.function.Predicate;
import net.minecraft.core.NonNullList;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.IFluidHandler.FluidAction;
import org.jetbrains.annotations.NotNull;

@SerialClass
public class BaseTank implements IFluidHandler, AliasCollection<FluidStack> {
   private final int size;
   private final int capacity;
   private final List<BaseContainerListener> listeners = new ArrayList<>();
   private Predicate<FluidStack> predicate = e -> true;
   private BooleanSupplier allowExtract = () -> true;
   @SerialField
   public NonNullList<FluidStack> list;
   private int click_max;

   public BaseTank(int size, int capacity) {
      this.size = size;
      this.capacity = capacity;
      this.list = NonNullList.withSize(size, FluidStack.EMPTY);
   }

   public BaseTank add(BaseContainerListener listener) {
      this.listeners.add(listener);
      return this;
   }

   public BaseTank setPredicate(Predicate<FluidStack> predicate) {
      this.predicate = predicate;
      return this;
   }

   public BaseTank setExtract(BooleanSupplier allowExtract) {
      this.allowExtract = allowExtract;
      return this;
   }

   public BaseTank setClickMax(int max) {
      this.click_max = max;
      return this;
   }

   public int getTanks() {
      return this.size;
   }

   @NotNull
   public FluidStack getFluidInTank(int tank) {
      return (FluidStack)this.list.get(tank);
   }

   public int getTankCapacity(int tank) {
      return this.capacity;
   }

   public boolean isFluidValid(int tank, @NotNull FluidStack stack) {
      return true;
   }

   public int fill(FluidStack resource, FluidAction action) {
      if (resource.isEmpty()) {
         return 0;
      }

      if (!this.predicate.test(resource)) {
         return 0;
      }

      int to_fill = this.click_max == 0 ? resource.getAmount() : (resource.getAmount() >= this.click_max ? this.click_max : 0);
      if (to_fill == 0) {
         return 0;
      }

      int filled = 0;

      for (int i = 0; i < this.size; i++) {
         FluidStack stack = (FluidStack)this.list.get(i);
         if (FluidStack.isSameFluidSameComponents(stack, resource)) {
            int remain = this.capacity - stack.getAmount();
            int fill = Math.min(to_fill, remain);
            filled += fill;
            to_fill -= fill;
            if (action == FluidAction.EXECUTE) {
               resource.shrink(fill);
               stack.grow(fill);
            }
         } else if (stack.isEmpty()) {
            int fill = Math.min(to_fill, this.capacity);
            filled += fill;
            to_fill -= fill;
            if (action == FluidAction.EXECUTE) {
               FluidStack rep = resource.copy();
               rep.setAmount(fill);
               this.list.set(i, rep);
               resource.shrink(fill);
            }
         }

         if (resource.isEmpty() || to_fill == 0) {
            break;
         }
      }

      if (action == FluidAction.EXECUTE && filled > 0) {
         this.setChanged();
      }

      return filled;
   }

   @NotNull
   public FluidStack drain(FluidStack resource, FluidAction action) {
      if (resource.isEmpty()) {
         return resource;
      }

      if (!this.allowExtract.getAsBoolean()) {
         return FluidStack.EMPTY;
      }

      int to_drain = resource.getAmount();
      if (this.click_max > 0) {
         if (to_drain < this.click_max) {
            return FluidStack.EMPTY;
         }

         to_drain = this.click_max;
      }

      int drained = 0;

      for (int i = 0; i < this.size; i++) {
         FluidStack stack = (FluidStack)this.list.get(i);
         if (FluidStack.isSameFluidSameComponents(stack, resource)) {
            int remain = stack.getAmount();
            int drain = Math.min(to_drain, remain);
            drained += drain;
            to_drain -= drain;
            if (action == FluidAction.EXECUTE) {
               stack.shrink(drain);
            }
         }

         if (to_drain == 0) {
            break;
         }
      }

      if (action == FluidAction.EXECUTE && drained > 0) {
         this.setChanged();
      }

      FluidStack ans = resource.copy();
      ans.setAmount(drained);
      return ans;
   }

   @NotNull
   public FluidStack drain(int maxDrain, FluidAction action) {
      if (!this.allowExtract.getAsBoolean()) {
         return FluidStack.EMPTY;
      }

      FluidStack ans = null;
      int to_drain = maxDrain;
      if (this.click_max > 0) {
         if (to_drain < this.click_max) {
            return FluidStack.EMPTY;
         }

         to_drain = this.click_max;
      }

      int drained = 0;

      for (int i = 0; i < this.size; i++) {
         FluidStack stack = (FluidStack)this.list.get(i);
         if (!stack.isEmpty() && (ans == null || FluidStack.isSameFluidSameComponents(stack, ans))) {
            int remain = stack.getAmount();
            int drain = Math.min(to_drain, remain);
            drained += drain;
            to_drain -= drain;
            if (ans == null) {
               ans = stack.copy();
            }

            if (action == FluidAction.EXECUTE) {
               stack.shrink(drain);
            }
         }

         if (to_drain == 0) {
            break;
         }
      }

      if (action == FluidAction.EXECUTE && drained > 0) {
         this.setChanged();
      }

      if (ans == null) {
         return FluidStack.EMPTY;
      }

      ans.setAmount(drained);
      return ans;
   }

   public void setChanged() {
      this.listeners.forEach(BaseContainerListener::notifyTile);
   }

   public List<FluidStack> getAsList() {
      return this.list;
   }

   public void clear() {
      this.list.clear();
   }

   public void set(int n, int i, FluidStack elem) {
      this.list.set(i, elem);
   }

   public Class<FluidStack> getElemClass() {
      return FluidStack.class;
   }

   public boolean isEmpty() {
      for (FluidStack stack : this.list) {
         if (!stack.isEmpty()) {
            return false;
         }
      }

      return true;
   }
}
