package dev.arakiel.gemini.client;

import dev.arakiel.gemini.integration.GeminiIntegrations;
import dev.arakiel.gemini.network.client.GeminiNetworkClient;
import net.fabricmc.api.ClientModInitializer;

public class GeminiClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		GeminiNetworkClient.init();
		GeminiIntegrations.initClient();
	}
}
