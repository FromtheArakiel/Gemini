package dev.arakiel.gemini.component.entity;

import dev.arakiel.gemini.component.Component;
import net.minecraft.network.RegistryFriendlyByteBuf;
import org.jetbrains.annotations.ApiStatus.Experimental;

@Experimental
public interface C2SSelfMessagingComponent extends Component {
    void handleC2SMessage(RegistryFriendlyByteBuf buf);
}
