package dev.arakiel.gemini;

import dev.arakiel.gemini.attribute.GeminiAttributes;
import dev.arakiel.gemini.combat.internal.GeminiCombatImpl;
import dev.arakiel.gemini.config.GeminiConfig;
import dev.arakiel.gemini.event.GeminiEvents;
import dev.arakiel.gemini.integration.GeminiIntegrations;
import dev.arakiel.gemini.locate.GeminiLocate;
import dev.arakiel.gemini.network.GeminiNetwork;
import dev.arakiel.gemini.registration.GeminiRegistration;
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
		GeminiNetwork.init();
		GeminiConfig.init();
		GeminiAttributes.init();
		GeminiLocate.init();
		GeminiIntegrations.initCommon();
		GeminiCombatImpl.init();
	}

	public static ResourceLocation id(String path) {
		return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
	}
}
