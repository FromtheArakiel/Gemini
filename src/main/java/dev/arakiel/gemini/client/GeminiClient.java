package dev.arakiel.gemini.client;

import dev.arakiel.gemini.config.client.GeminiConfigCommand;
import dev.arakiel.gemini.integration.GeminiIntegrations;
import dev.arakiel.gemini.network.client.GeminiNetworkClient;
import net.fabricmc.api.ClientModInitializer;

public class GeminiClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		GeminiConfigCommand.init();
		GeminiNetworkClient.init();
		GeminiIntegrations.initClient();
	}
}
