package dev.xkmc.l2serial.util;

public class Lock {
   private boolean recursive = false;

   public void execute(Runnable runnable) {
      if (!this.recursive) {
         this.recursive = true;
         runnable.run();
         this.recursive = false;
      }
   }
}
