package dev.arakiel.gemini.client;

import dev.arakiel.gemini.api.GeminiNetworkClient;
import net.fabricmc.api.ClientModInitializer;

public class GeminiClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		GeminiNetworkClient.init();
	}
}
