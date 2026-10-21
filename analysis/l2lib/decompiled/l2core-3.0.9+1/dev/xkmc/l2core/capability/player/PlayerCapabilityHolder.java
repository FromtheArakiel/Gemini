package dev.xkmc.l2core.capability.player;

import dev.xkmc.l2core.capability.attachment.GeneralCapabilityHolder;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

public class PlayerCapabilityHolder<T extends PlayerCapabilityTemplate<T>> extends GeneralCapabilityHolder<Player, T> {
   public static final Map<ResourceLocation, PlayerCapabilityHolder<?>> INTERNAL_MAP = new ConcurrentHashMap<>();
   public final PlayerCapabilityNetworkHandler<T> network;

   public PlayerCapabilityHolder(ResourceLocation id, Class<T> cls, Supplier<T> sup, PlayerCapabilityHolder.NetworkFactory<T> network) {
      super(id, cls, sup, Player.class, e -> true);
      this.network = network.create(this);
      INTERNAL_MAP.put(id, this);
   }

   @Override
   protected boolean copyOnDeath() {
      return true;
   }

   public interface NetworkFactory<T extends PlayerCapabilityTemplate<T>> {
      PlayerCapabilityNetworkHandler<T> create(PlayerCapabilityHolder<T> var1);
   }
}
