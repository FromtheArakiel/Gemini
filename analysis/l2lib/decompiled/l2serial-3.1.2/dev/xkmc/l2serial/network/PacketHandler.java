package dev.xkmc.l2serial.network;

import dev.xkmc.l2serial.serialization.codec.CodecAdaptor;
import dev.xkmc.l2serial.util.ModContainerHack;
import dev.xkmc.l2serial.util.Wrappers;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ChunkPos;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.handling.IPayloadHandler;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PacketHandler {
   public static final Logger LOGGER = LogManager.getLogger();
   public final String modid;
   public final int ver;
   public final String verStr;
   private final Function<PacketHandler, PacketHandler.PacketConfiguration<?>>[] values;
   private final Map<Class<?>, PacketHandler.PacketConfiguration<?>> map = new LinkedHashMap<>();

   @SafeVarargs
   public PacketHandler(String id, int version, Function<PacketHandler, PacketHandler.PacketConfiguration<?>>... values) {
      this.modid = id;
      this.ver = version;
      this.verStr = String.valueOf(this.ver);
      this.values = values;
      ModContainer cont = ModContainerHack.getMod(id);
      IEventBus bus = cont.getEventBus();
      if (bus != null) {
         bus.addListener(this::register);
      } else {
         throw new IllegalStateException("Event bus is null for " + id);
      }
   }

   private ResourceLocation of(Class<?> cls) {
      String name = cls.getSimpleName();
      StringBuilder builder = new StringBuilder();

      for (int i = 0; i < name.length(); i++) {
         char ch = name.charAt(i);
         if ((ch < 'a' || ch > 'z') && (ch < '0' || ch > '9')) {
            if (ch >= 'A' && ch <= 'Z') {
               builder.append((char)(ch - 'A' + 97));
            }
         } else {
            builder.append(ch);
         }
      }

      return ResourceLocation.fromNamespaceAndPath(this.modid, builder.toString());
   }

   private <T extends SimplePacketBase> PacketHandler.PacketConfiguration<T> of(
      Class<T> cls, StreamCodec<RegistryFriendlyByteBuf, T> factory, PacketHandler.NetDir dir
   ) {
      return new PacketHandler.PacketConfiguration<>(new Type(this.of(cls)), cls, factory, dir);
   }

   public <T extends SimplePacketBase> PacketHandler.PacketConfiguration<T> create(
      Class<T> type, StreamCodec<RegistryFriendlyByteBuf, T> factory, PacketHandler.NetDir dir
   ) {
      return this.of(type, factory, dir);
   }

   public <T extends Record & SerialPacketBase<T>> PacketHandler.PacketConfiguration<T> create(Class<T> type, PacketHandler.NetDir dir) {
      return this.of(type, new CodecAdaptor<>(type).toNetwork(), dir);
   }

   private <T extends SimplePacketBase> PacketHandler.BasePayload<T> get(T val) {
      return new PacketHandler.BasePayload<>(Wrappers.cast(this.map.get(val.getClass())), val);
   }

   public void toServer(SimplePacketBase packet) {
      PacketDistributor.sendToServer(this.get(packet), new CustomPacketPayload[0]);
   }

   public void toTrackingPlayers(SimplePacketBase packet, Entity e) {
      PacketDistributor.sendToPlayersTrackingEntityAndSelf(e, this.get(packet), new CustomPacketPayload[0]);
   }

   public void toTrackingOnly(SimplePacketBase packet, Entity e) {
      PacketDistributor.sendToPlayersTrackingEntity(e, this.get(packet), new CustomPacketPayload[0]);
   }

   public void toClientPlayer(SimplePacketBase packet, ServerPlayer e) {
      PacketDistributor.sendToPlayer(e, this.get(packet), new CustomPacketPayload[0]);
   }

   public void toAllClient(SimplePacketBase packet) {
      PacketDistributor.sendToAllPlayers(this.get(packet), new CustomPacketPayload[0]);
   }

   public void toTrackingChunk(ServerLevel sl, ChunkPos pos, SimplePacketBase packet) {
      PacketDistributor.sendToPlayersTrackingChunk(sl, pos, this.get(packet), new CustomPacketPayload[0]);
   }

   public void sendToNear(ServerLevel world, BlockPos pos, int range, SimplePacketBase packet) {
      PacketDistributor.sendToPlayersNear(world, null, pos.getX(), pos.getY(), pos.getZ(), range, this.get(packet), new CustomPacketPayload[0]);
   }

   private void register(RegisterPayloadHandlersEvent event) {
      PayloadRegistrar reg = event.registrar(this.modid).versioned(this.verStr).optional();

      for (Function<PacketHandler, PacketHandler.PacketConfiguration<?>> packet : this.values) {
         PacketHandler.PacketConfiguration<?> config = packet.apply(this);
         config.register(reg);
         this.map.put(config.cls, config);
      }
   }

   public record BasePayload<T extends SimplePacketBase>(PacketHandler.PacketConfiguration<T> config, T packet) implements CustomPacketPayload {
      public Type<? extends CustomPacketPayload> type() {
         return this.config.id;
      }
   }

   public enum NetDir {
      PLAY_TO_CLIENT,
      PLAY_TO_SERVER;

      public <T extends SimplePacketBase> void register(PayloadRegistrar reg, PacketHandler.PacketConfiguration<T> p) {
         switch (this) {
            case PLAY_TO_CLIENT:
               reg.playToClient(p.id, p.wrapCodec(), p);
               break;
            case PLAY_TO_SERVER:
               reg.playToServer(p.id, p.wrapCodec(), p);
         }
      }
   }

   public record PacketConfiguration<T extends SimplePacketBase>(
      Type<PacketHandler.BasePayload<T>> id, Class<T> cls, StreamCodec<RegistryFriendlyByteBuf, T> codec, PacketHandler.NetDir dir
   ) implements IPayloadHandler<PacketHandler.BasePayload<T>> {
      private void register(PayloadRegistrar reg) {
         this.dir.register(reg, this);
      }

      public void handle(PacketHandler.BasePayload<T> payload, IPayloadContext context) {
         payload.packet().handle(context);
      }

      public StreamCodec<RegistryFriendlyByteBuf, PacketHandler.BasePayload<T>> wrapCodec() {
         return this.codec.map(t -> new PacketHandler.BasePayload<>(this, (T)t), p -> p.packet);
      }
   }
}
