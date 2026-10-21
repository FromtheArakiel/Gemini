package dev.xkmc.l2core.base.menu.base;

import dev.xkmc.l2serial.util.Wrappers;
import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class BaseContainerMenu<T extends BaseContainerMenu<T>> extends AbstractContainerMenu {
   public final Inventory inventory;
   public final Player player;
   public final Container container;
   public final RegistryAccess access;
   protected int added = 0;
   protected final boolean isVirtual;
   private final SpriteManager sprite;
   private boolean updating = false;
   private final Map<BaseContainerMenu.SlotKey, Slot> slotMap = new TreeMap<>(BaseContainerMenu.SlotKey.COMPARATOR);

   public static void clearSlot(Player pPlayer, Container pContainer, int index) {
      if (pPlayer.isAlive() && (!(pPlayer instanceof ServerPlayer) || !((ServerPlayer)pPlayer).hasDisconnected())) {
         Inventory inventory = pPlayer.getInventory();
         if (inventory.player instanceof ServerPlayer) {
            inventory.placeItemBackInInventory(pContainer.removeItemNoUpdate(index));
         }
      } else {
         pPlayer.drop(pContainer.removeItemNoUpdate(index), false);
      }
   }

   protected BaseContainerMenu(MenuType<?> type, int wid, Inventory plInv, SpriteManager manager, Function<T, SimpleContainer> factory, boolean isVirtual) {
      super(type, wid);
      this.inventory = plInv;
      this.player = plInv.player;
      this.container = (Container)factory.apply((T)Wrappers.cast(this));
      this.sprite = manager;
      this.access = plInv.player.level().registryAccess();
      int x = this.getLayout().getPlInvX();
      int y = this.getLayout().getPlInvY();
      this.bindPlayerInventory(plInv, x, y);
      this.isVirtual = isVirtual;
   }

   public MenuLayoutConfig getLayout() {
      return this.sprite.get(this.access);
   }

   ResourceLocation getLayoutId() {
      return this.sprite.id();
   }

   protected void bindPlayerInventory(Inventory plInv, int x, int y) {
      for (int i = 0; i < 3; i++) {
         for (int j = 0; j < 9; j++) {
            this.addSlot(this.createSlot(plInv, j + i * 9 + 9, x + j * 18, y + i * 18));
         }
      }

      for (int k = 0; k < 9; k++) {
         this.addSlot(this.createSlot(plInv, k, x + k * 18, y + 58));
      }
   }

   protected Slot createSlot(Inventory inv, int slot, int x, int y) {
      return this.shouldLock(inv, slot) ? new SlotLocked(inv, slot, x, y) : new Slot(inv, slot, x, y);
   }

   protected boolean shouldLock(Inventory inv, int slot) {
      return false;
   }

   protected void addSlot(String name, Predicate<ItemStack> pred) {
      this.getLayout().getSlot(name, (x, y) -> new PredSlot(this.container, this.added++, x, y, pred), this::addSlot);
   }

   protected void addSlot(String name, BiPredicate<Integer, ItemStack> pred) {
      int current = this.added;
      this.getLayout().getSlot(name, (x, y) -> {
         int i = this.added - current;
         PredSlot ans = new PredSlot(this.container, this.added, x, y, e -> pred.test(i, e));
         this.added++;
         return ans;
      }, this::addSlot);
   }

   protected void addSlot(String name, Predicate<ItemStack> pred, Consumer<PredSlot> modifier) {
      this.getLayout().getSlot(name, (x, y) -> {
         PredSlot s = new PredSlot(this.container, this.added++, x, y, pred);
         modifier.accept(s);
         return s;
      }, this::addSlot);
   }

   protected void addSlot(String name, BiPredicate<Integer, ItemStack> pred, BiConsumer<Integer, PredSlot> modifier) {
      int current = this.added;
      this.getLayout().getSlot(name, (x, y) -> {
         int i = this.added - current;
         PredSlot ans = new PredSlot(this.container, this.added, x, y, e -> pred.test(i, e));
         modifier.accept(i, ans);
         this.added++;
         return ans;
      }, this::addSlot);
   }

   protected void addSlot(String name, int i, int j, Slot slot) {
      this.slotMap.put(new BaseContainerMenu.SlotKey(name, i, j), slot);
      this.addSlot(slot);
   }

   protected Slot getSlot(String name, int i, int j) {
      return this.slotMap.get(new BaseContainerMenu.SlotKey(name, i, j));
   }

   public PredSlot getAsPredSlot(String name, int i, int j) {
      return (PredSlot)this.getSlot(name, i, j);
   }

   public PredSlot getAsPredSlot(String name) {
      return (PredSlot)this.getSlot(name, 0, 0);
   }

   public ItemStack quickMoveStack(Player pl, int id) {
      ItemStack stack = ((Slot)this.slots.get(id)).getItem();
      int n = this.container.getContainerSize();
      boolean moved;
      if (id >= 36) {
         moved = this.moveItemStackTo(stack, 0, 36, true);
      } else {
         moved = this.moveItemStackTo(stack, 36, 36 + n, false);
      }

      if (moved) {
         ((Slot)this.slots.get(id)).setChanged();
      }

      return ItemStack.EMPTY;
   }

   public boolean stillValid(Player player) {
      return player.isAlive();
   }

   public void removed(Player player) {
      if (this.isVirtual && !player.level().isClientSide()) {
         this.clearContainerFiltered(player, this.container);
      }

      super.removed(player);
   }

   protected boolean shouldClear(Container container, int slot) {
      return this.isVirtual;
   }

   protected void clearContainerFiltered(Player player, Container container) {
      if (!player.isAlive() || player instanceof ServerPlayer && ((ServerPlayer)player).hasDisconnected()) {
         for (int j = 0; j < container.getContainerSize(); j++) {
            if (this.shouldClear(container, j)) {
               player.drop(container.removeItemNoUpdate(j), false);
            }
         }
      } else {
         Inventory inventory = player.getInventory();

         for (int i = 0; i < container.getContainerSize(); i++) {
            if (this.shouldClear(container, i) && inventory.player instanceof ServerPlayer) {
               inventory.placeItemBackInInventory(container.removeItemNoUpdate(i));
            }
         }
      }
   }

   public void slotsChanged(Container cont) {
      if (this.inventory.player.level().isClientSide()) {
         super.slotsChanged(cont);
      } else {
         if (!this.updating) {
            this.updating = true;
            this.securedServerSlotChange(cont);
            this.updating = false;
         }

         super.slotsChanged(cont);
      }
   }

   protected void securedServerSlotChange(Container cont) {
   }

   public static class BaseContainer<T extends BaseContainerMenu<T>> extends SimpleContainer {
      protected final T parent;
      private boolean updating = false;
      private int max = 64;

      public BaseContainer(int size, T menu) {
         super(size);
         this.parent = menu;
      }

      public BaseContainerMenu.BaseContainer<T> setMax(int max) {
         this.max = max;
         return this;
      }

      public int getMaxStackSize() {
         return Math.min(this.max, super.getMaxStackSize());
      }

      public void setChanged() {
         super.setChanged();
         if (!this.updating) {
            this.updating = true;
            this.parent.slotsChanged(this);
            this.updating = false;
         }
      }
   }

   private record SlotKey(String name, int i, int j) {
      private static final Comparator<BaseContainerMenu.SlotKey> COMPARATOR;

      static {
         Comparator<BaseContainerMenu.SlotKey> comp = Comparator.comparing(BaseContainerMenu.SlotKey::name);
         comp = comp.thenComparingInt(BaseContainerMenu.SlotKey::i);
         comp = comp.thenComparingInt(BaseContainerMenu.SlotKey::j);
         COMPARATOR = comp;
      }
   }
}
