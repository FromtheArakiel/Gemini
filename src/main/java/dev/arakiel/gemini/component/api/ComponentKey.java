package dev.arakiel.gemini.component.api;

import dev.arakiel.gemini.component.internal.base.ComponentsInternals;
import dev.arakiel.gemini.component.internal.base.asm.ComponentBootstrap;
import io.netty.buffer.Unpooled;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketSendListener;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.TickTask;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.ApiStatus.Experimental;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.ApiStatus.NonExtendable;

@NonExtendable
public abstract class ComponentKey<C extends Component> {
   private final ResourceLocation id;
   private final Class<C> componentClass;

   public final ResourceLocation getId() {
      return this.id;
   }

   public final Class<C> getComponentClass() {
      return this.componentClass;
   }

   @Contract(
      pure = true
   )
   @Nullable
   public C getNullable(Object provider) {
      return this.getInternal(((ComponentProvider)provider).getComponentContainer());
   }

   public final C get(Object provider) {
      C component = this.getInternal(((ComponentProvider)provider).getComponentContainer());

      assert component == null || this.getComponentClass().isInstance(component);

      if (component == null) {
         try {
            throw new NoSuchElementException(provider + " provides no component of type " + this.getId());
         } catch (NullPointerException var5) {
            NoSuchElementException e1 = new NoSuchElementException(this.getId() + " not available");
            e1.addSuppressed(var5);
            throw e1;
         }
      } else {
         return component;
      }
   }

   public final Optional<C> maybeGet(@Nullable Object provider) {
      return provider instanceof ComponentProvider p ? Optional.ofNullable(this.getInternal(p.getComponentContainer())) : Optional.empty();
   }

   @Contract(
      pure = true
   )
   public boolean isProvidedBy(Object provider) {
      return this.getNullable(provider) != null;
   }

   public void sync(Object provider) {
      if (this.get(provider) instanceof ComponentSync.AutoSyncedComponent synced) {
         this.sync(provider, synced, synced);
      }
   }

   public void sync(Object provider, ComponentSync.PacketWriter packetWriter) {
      if (this.get(provider) instanceof ComponentSync.AutoSyncedComponent synced) {
         this.sync(provider, packetWriter, synced);
      }
   }

   public void sync(Object provider, ComponentSync.PacketWriter packetWriter, ComponentSync.PlayerPredicate predicate) {
      for (ServerPlayer player : ((ComponentProvider)provider).getRecipientsForComponentSync()) {
         this.syncWith(player, (ComponentProvider)provider, packetWriter, predicate);
      }
   }

   @Experimental
   public void syncWith(ServerPlayer player, ComponentProvider provider) {
      if (this.get(provider) instanceof ComponentSync.AutoSyncedComponent synced) {
         this.syncWith(player, provider, synced, synced);
      }
   }

   @Experimental
   public void syncWith(ServerPlayer player, ComponentProvider provider, ComponentSync.PacketWriter writer, ComponentSync.PlayerPredicate predicate) {
      if (predicate.shouldSyncWith(player)) {
         RegistryFriendlyByteBuf buf = new RegistryFriendlyByteBuf(Unpooled.buffer(), player.serverLevel().registryAccess());
         boolean handedOff = false;
         try {
            writer.writeSyncPacket(buf, player);
            CustomPacketPayload payload = provider.toComponentPacket(this, predicate.isRequiredOnClient(), buf);
            if (payload != null) {
               if (ServerPlayNetworking.canSend(player, payload.type())) {
                  ServerPlayNetworking.getSender(player).sendPacket(payload, PacketSendListener.thenRun(buf::release));
                  handedOff = true;
               } else if (predicate.isRequiredOnClient()) {
                  player.getCommandSenderWorld()
                     .getServer()
                     .tell(
                        new TickTask(
                           0,
                           () -> {
                              if (!player.hasDisconnected()) {
                                 player.connection
                                    .disconnect(
                                       net.minecraft.network.chat.Component.literal(
                                          "This server requires Gemini Components (unhandled packet: "
                                             + payload.type().id()
                                             + ")"
                                             + ComponentsInternals.getClientOptionalModAdvice()
                                       )
                                    );
                              }
                           }
                        )
                     );
               }
            }
         } finally {
            if (!handedOff) {
               buf.release();
            }
         }
      }
   }

   @Override
   public final String toString() {
      return this.getClass().getSimpleName() + "[\"" + this.id + "\"]";
   }

   @Internal
   protected ComponentKey(ResourceLocation id, Class<C> componentClass) {
      if (!ComponentBootstrap.INSTANCE.isGenerated(this.getClass())) {
         throw new IllegalStateException();
      } else {
         this.componentClass = componentClass;
         this.id = id;
      }
   }

   @Contract(
      pure = true
   )
   @Internal
   @Nullable
   public abstract C getInternal(ComponentContainer var1);

   @Internal
   public C getFromContainer(ComponentContainer container) {
      return Objects.requireNonNull(this.getInternal(container));
   }
}
