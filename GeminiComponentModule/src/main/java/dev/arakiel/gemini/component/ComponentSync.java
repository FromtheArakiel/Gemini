package dev.arakiel.gemini.component;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;

public final class ComponentSync {
    private ComponentSync() {
    }

    @FunctionalInterface
    public interface PacketWriter {
        @Contract(mutates = "param1")
        void writeSyncPacket(RegistryFriendlyByteBuf buffer, ServerPlayer recipient);
    }

    public interface PlayerPredicate {
        @Contract(pure = true)
        boolean shouldSyncWith(ServerPlayer player);

        default boolean isRequiredOnClient() {
            return true;
        }

        static PlayerPredicate all() {
            return player -> true;
        }

        static PlayerPredicate only(Player player) {
            return recipient -> recipient == player;
        }
    }

    public interface AutoSyncedComponent extends Component, PacketWriter, PlayerPredicate {
        @Override
        default boolean shouldSyncWith(ServerPlayer player) {
            return true;
        }

        @Contract(mutates = "param1")
        @Override
        default void writeSyncPacket(RegistryFriendlyByteBuf buffer, ServerPlayer recipient) {
            CompoundTag tag = new CompoundTag();
            writeToNbt(tag, buffer.registryAccess());
            buffer.writeNbt(tag);
        }

        default void applySyncPacket(RegistryFriendlyByteBuf buffer) {
            CompoundTag tag = buffer.readNbt();
            if (tag != null) {
                readFromNbt(tag, buffer.registryAccess());
            }
        }
    }

    @FunctionalInterface
    @ApiStatus.Experimental
    public interface C2SPacketWriter {
        C2SPacketWriter EMPTY = buffer -> {
        };

        @Contract(mutates = "param")
        void writeC2SPacket(RegistryFriendlyByteBuf buffer);
    }

    @ApiStatus.Experimental
    public interface SelfMessaging extends Component {
        void handleC2SMessage(RegistryFriendlyByteBuf buffer);
    }
}
