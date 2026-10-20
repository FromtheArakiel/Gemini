package dev.arakiel.gemini;

import dev.arakiel.gemini.api.GeminiApi;
import dev.arakiel.gemini.api.GeminiEvents;
import dev.arakiel.gemini.api.GeminiRegistration;
import net.fabricmc.api.ModInitializer;

import net.minecraft.resources.ResourceLocation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Gemini implements ModInitializer {
	public static final String MOD_ID = "gemini";

	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		GeminiEvents.init();
		GeminiRegistration.init();
		GeminiApi.init();
	}

	public static ResourceLocation id(String path) {
		return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
	}
}
