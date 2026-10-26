package dev.arakiel.gemini.client;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import dev.arakiel.gemini.Gemini;
import dev.arakiel.gemini.config.ConfigHandle;
import dev.arakiel.gemini.config.GeminiConfig;
import dev.arakiel.gemini.config.client.GeminiConfigClient;
import java.util.List;
import net.minecraft.client.gui.screens.Screen;

public final class GeminiModMenu implements ModMenuApi {
    public GeminiModMenu() {
    }

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return GeminiModMenu::createGeminiScreen;
    }

    private static Screen createGeminiScreen(Screen parent) {
        List<ConfigHandle<?>> configs = GeminiConfig.all().stream()
                .filter(handle -> Gemini.MOD_ID.equals(handle.id().getNamespace()))
                .toList();
        if (configs.size() == 1) {
            return GeminiConfigClient.createScreen(parent, configs.getFirst());
        }
        return GeminiConfigClient.createHubScreen(parent, configs);
    }
}
