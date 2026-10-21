package dev.xkmc.l2menustacker.screen.base;

public enum ClientCloseResult {
   FAIL(-2),
   POP_ALL(-1),
   REMAIN(0);

   public final int id;

   ClientCloseResult(int id) {
      this.id = id;
   }
}
