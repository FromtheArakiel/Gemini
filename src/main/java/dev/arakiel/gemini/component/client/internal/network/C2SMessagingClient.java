package dev.arakiel.gemini.component.client.internal.network;

import com.mojang.datafixers.util.Unit;
import dev.arakiel.gemini.component.Component;
import dev.arakiel.gemini.component.ComponentKey;
import dev.arakiel.gemini.component.ComponentProvider;
import dev.arakiel.gemini.component.ComponentSync;
import dev.arakiel.gemini.component.internal.entity.GeminiEntityComponents;
import dev.arakiel.gemini.component.internal.network.ComponentUpdatePayload;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.PacketSendListener;
import net.minecraft.network.RegistryFriendlyByteBuf;
import org.jetbrains.annotations.ApiStatus.Experimental;

import java.util.Objects;

@Experimental
public final class C2SMessagingClient {
    private C2SMessagingClient() {
    }

    public static void send(Component component, ComponentSync.C2SPacketWriter writer) {
        LocalPlayer player = Objects.requireNonNull(Minecraft.getInstance().player);
        ComponentProvider provider = (ComponentProvider) (Object) player;
        ComponentKey<?> key = Objects.requireNonNull(provider.getComponentContainer().getKey(component));
        send(key, writer);
    }

    public static void send(ComponentKey<?> key, ComponentSync.C2SPacketWriter writer) {
        PacketSender sender = ClientPlayNetworking.getSender();
        RegistryFriendlyByteBuf buf = new RegistryFriendlyByteBuf(
                PacketByteBufs.create(),
                Objects.requireNonNull(Minecraft.getInstance().getConnection()).registryAccess()
        );
        writer.writeC2SPacket(buf);
        sender.sendPacket(
                new ComponentUpdatePayload<>(GeminiEntityComponents.C2S_SELF_PACKET_ID, Unit.INSTANCE, true, key.getId(), buf),
                PacketSendListener.thenRun(buf::release)
        );
    }
}
