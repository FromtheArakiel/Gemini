package com.tterrag.registrate.util;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.tterrag.registrate.AbstractRegistrate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.event.IModBusEvent;
import net.neoforged.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.neoforged.neoforge.common.NeoForge;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class OneTimeEventReceiver<T extends Event> implements Consumer<T> {
   private static final Logger log = LogManager.getLogger(OneTimeEventReceiver.class);
   private static boolean seenModBus = false;
   private static final Table<AbstractRegistrate<?>, Class<?>, List<Pair<EventPriority, Consumer<?>>>> waitingModListeners = HashBasedTable.create();
   private final IEventBus bus;
   private final Consumer<? super T> listener;
   private final AtomicBoolean consumed = new AtomicBoolean();
   private static final List<Triple<IEventBus, Object, Class<? extends Event>>> toUnregister = new ArrayList<>();

   public static <T extends Event & IModBusEvent> void addModListener(AbstractRegistrate<?> owner, Class<? super T> evtClass, Consumer<? super T> listener) {
      addModListener(owner, EventPriority.NORMAL, evtClass, listener);
   }

   public static <T extends Event & IModBusEvent> void addModListener(
      AbstractRegistrate<?> owner, EventPriority priority, Class<? super T> evtClass, Consumer<? super T> listener
   ) {
      if (owner.getModEventBus() == null) {
         if (!waitingModListeners.contains(owner, evtClass)) {
            waitingModListeners.put(owner, evtClass, new ArrayList());
         }

         ((List)waitingModListeners.get(owner, evtClass)).add(Pair.of(priority, listener));
      } else {
         if (!seenModBus) {
            seenModBus = true;

            for (Entry<Class<?>, List<Pair<EventPriority, Consumer<?>>>> waitingListener : waitingModListeners.row(owner).entrySet()) {
               for (Pair<EventPriority, Consumer<?>> pair : waitingListener.getValue()) {
                  addListener(owner.getModEventBus(), (EventPriority)pair.getKey(), waitingListener.getKey(), (Consumer<?>)pair.getValue());
               }
            }

            addModListener(owner, FMLLoadCompleteEvent.class, OneTimeEventReceiver::onLoadComplete);
         }

         addListener(owner.getModEventBus(), priority, evtClass, listener);
      }
   }

   public static <T extends Event> void addForgeListener(Class<? super T> evtClass, Consumer<? super T> listener) {
      addForgeListener(EventPriority.NORMAL, evtClass, listener);
   }

   public static <T extends Event> void addForgeListener(EventPriority priority, Class<? super T> evtClass, Consumer<? super T> listener) {
      addListener(NeoForge.EVENT_BUS, priority, evtClass, listener);
   }

   @Deprecated
   public static <T extends Event> void addListener(IEventBus bus, Class<? super T> evtClass, Consumer<? super T> listener) {
      addListener(bus, EventPriority.NORMAL, evtClass, listener);
   }

   @Deprecated
   public static <T extends Event> void addListener(IEventBus bus, EventPriority priority, Class<? super T> evtClass, Consumer<? super T> listener) {
      bus.addListener(priority, false, evtClass, new OneTimeEventReceiver<>(bus, listener));
   }

   public void accept(T event) {
      if (this.consumed.compareAndSet(false, true)) {
         this.listener.accept(event);
         unregister(this.bus, this, event);
      }
   }

   private static synchronized void unregister(IEventBus bus, Object listener, Event event) {
      unregister(bus, listener, (Class<? extends Event>)event.getClass());
   }

   public static synchronized void unregister(AbstractRegistrate<?> owner, Object listener, Class<? extends Event> event) {
      unregister(owner.getModEventBus(), listener, event);
   }

   private static synchronized void unregister(IEventBus bus, Object listener, Class<? extends Event> event) {
      toUnregister.add(Triple.of(bus, listener, event));
   }

   private static void onLoadComplete(FMLLoadCompleteEvent event) {
      event.enqueueWork(() -> {
         toUnregister.forEach(t -> ((IEventBus)t.getLeft()).unregister(t.getMiddle()));
         toUnregister.clear();
      });
   }

   public OneTimeEventReceiver(IEventBus bus, Consumer<? super T> listener) {
      this.bus = bus;
      this.listener = listener;
   }
}
