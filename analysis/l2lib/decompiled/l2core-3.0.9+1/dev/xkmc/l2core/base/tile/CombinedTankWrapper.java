package dev.xkmc.l2core.base.tile;

import com.mojang.datafixers.util.Pair;
import java.util.ArrayList;
import java.util.List;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.IFluidHandler.FluidAction;
import net.neoforged.neoforge.fluids.capability.templates.EmptyFluidHandler;

public class CombinedTankWrapper implements IFluidHandler {
   private final List<Pair<IFluidHandler, CombinedTankWrapper.Type>> list = new ArrayList<>();
   protected int[] baseIndex;
   protected int tankCount;
   protected boolean enforceVariety;

   public CombinedTankWrapper build() {
      this.baseIndex = new int[this.list.size()];
      int index = 0;

      for (int i = 0; i < this.list.size(); i++) {
         index += ((IFluidHandler)this.list.get(i).getFirst()).getTanks();
         this.baseIndex[i] = index;
      }

      this.tankCount = index;
      return this;
   }

   public CombinedTankWrapper add(CombinedTankWrapper.Type type, IFluidHandler... handlers) {
      for (IFluidHandler handler : handlers) {
         this.list.add(Pair.of(handler, type));
      }

      return this;
   }

   protected Iterable<IFluidHandler> fillable() {
      return this.list.stream().filter(e -> e.getSecond() != CombinedTankWrapper.Type.EXTRACT).<IFluidHandler>map(Pair::getFirst).toList();
   }

   protected Iterable<IFluidHandler> drainable() {
      return this.list.stream().filter(e -> e.getSecond() != CombinedTankWrapper.Type.INSERT).<IFluidHandler>map(Pair::getFirst).toList();
   }

   public CombinedTankWrapper enforceVariety() {
      this.enforceVariety = true;
      return this;
   }

   public int getTanks() {
      return this.tankCount;
   }

   public FluidStack getFluidInTank(int tank) {
      int index = this.getIndexForSlot(tank);
      IFluidHandler handler = this.getHandlerFromIndex(index);
      tank = this.getSlotFromIndex(tank, index);
      return handler.getFluidInTank(tank);
   }

   public int getTankCapacity(int tank) {
      int index = this.getIndexForSlot(tank);
      IFluidHandler handler = this.getHandlerFromIndex(index);
      int localSlot = this.getSlotFromIndex(tank, index);
      return handler.getTankCapacity(localSlot);
   }

   public boolean isFluidValid(int tank, FluidStack stack) {
      int index = this.getIndexForSlot(tank);
      IFluidHandler handler = this.getHandlerFromIndex(index);
      int localSlot = this.getSlotFromIndex(tank, index);
      return handler.isFluidValid(localSlot, stack);
   }

   public int fill(FluidStack input, FluidAction action) {
      if (input.isEmpty()) {
         return 0;
      }

      int ans = 0;
      input = input.copy();
      boolean found = false;

      for (boolean searchPass : new boolean[]{true, false}) {
         for (IFluidHandler handler : this.fillable()) {
            for (int i = 0; i < handler.getTanks(); i++) {
               if (searchPass && FluidStack.isSameFluidSameComponents(handler.getFluidInTank(i), input)) {
                  found = true;
               }
            }

            if (!searchPass || found) {
               int filler = handler.fill(input, action);
               input.shrink(filler);
               ans += filler;
               if (input.isEmpty() || found || this.enforceVariety && filler != 0) {
                  return ans;
               }
            }
         }
      }

      return ans;
   }

   public FluidStack drain(FluidStack resource, FluidAction action) {
      if (resource.isEmpty()) {
         return resource;
      }

      FluidStack ans = FluidStack.EMPTY;
      resource = resource.copy();

      for (IFluidHandler handler : this.drainable()) {
         FluidStack drained = handler.drain(resource, action);
         int amount = drained.getAmount();
         resource.shrink(amount);
         if (!drained.isEmpty() && (ans.isEmpty() || FluidStack.isSameFluidSameComponents(drained, ans))) {
            ans = new FluidStack(drained.getFluidHolder(), amount + ans.getAmount(), drained.getComponentsPatch());
         }

         if (resource.isEmpty()) {
            break;
         }
      }

      return ans;
   }

   public FluidStack drain(int maxDrain, FluidAction action) {
      FluidStack ans = FluidStack.EMPTY;

      for (IFluidHandler iFluidHandler : this.drainable()) {
         FluidStack drained = iFluidHandler.drain(maxDrain, action);
         int amount = drained.getAmount();
         maxDrain -= amount;
         if (!drained.isEmpty() && (ans.isEmpty() || FluidStack.isSameFluidSameComponents(drained, ans))) {
            ans = new FluidStack(drained.getFluidHolder(), amount + ans.getAmount(), drained.getComponentsPatch());
         }

         if (maxDrain == 0) {
            break;
         }
      }

      return ans;
   }

   protected int getIndexForSlot(int slot) {
      if (slot < 0) {
         return -1;
      }

      for (int i = 0; i < this.baseIndex.length; i++) {
         if (slot - this.baseIndex[i] < 0) {
            return i;
         }
      }

      return -1;
   }

   protected IFluidHandler getHandlerFromIndex(int index) {
      return (IFluidHandler)(index >= 0 && index < this.list.size() ? (IFluidHandler)this.list.get(index).getFirst() : EmptyFluidHandler.INSTANCE);
   }

   protected int getSlotFromIndex(int slot, int index) {
      return index > 0 && index < this.baseIndex.length ? slot - this.baseIndex[index - 1] : slot;
   }

   public enum Type {
      INSERT,
      EXTRACT,
      ALL;
   }
}
