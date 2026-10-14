package com.obqy;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.ClientModInitializer;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenKeyboardEvents;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

import net.minecraft.sounds.SoundSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MusicToggle implements ClientModInitializer {
	public static final String MOD_ID = "music-toggle";

	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitializeClient() {
		LOGGER.info("Initialising!");

		Config.load();

		muteMusicKey = KeyMappingHelper.registerKeyMapping(new KeyMapping(
				"key.music-toggle.mutemusic",
				InputConstants.Type.KEYBOARD,
				InputConstants.KEY_B,
				this.category
		));

		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			while(muteMusicKey.consumeClick()) this.handleMuteMusicKeyPress(client);
		});

		ScreenEvents.BEFORE_INIT.register((client, screen, scaledWidth, scaledHeight) -> {
			if (!(screen instanceof CreativeModeInventoryScreen) && !(screen instanceof TitleScreen)) return;

			ScreenKeyboardEvents.beforeKeyPress(screen).register((s, keyEvent) -> {
				if (muteMusicKey.matches(keyEvent)) {
					this.handleMuteMusicKeyPress(client);
				}
			});
		});
	}
	public static KeyMapping muteMusicKey;

	KeyMapping.Category category = KeyMapping.Category.register(
			MusicToggle.id("options_category")
	);

	private void handleMuteMusicKeyPress(Minecraft client) {
		if (client.options.getSoundSourceOptionInstance(SoundSource.MUSIC).get() > 0.0) {
			Config.INSTANCE.musicVolume = client.options.getSoundSourceOptionInstance(SoundSource.MUSIC).get();
			client.options.getSoundSourceOptionInstance(SoundSource.MUSIC).set(0.0);
		} else if (Config.INSTANCE.musicVolume != -1.0) {
			client.options.getSoundSourceOptionInstance(SoundSource.MUSIC).set(Config.INSTANCE.musicVolume);
			Config.INSTANCE.musicVolume = -1.0;
		} else {
			client.options.getSoundSourceOptionInstance(SoundSource.MUSIC).set(0.1);
			Config.INSTANCE.musicVolume = -1.0;
		}
		Config.save();
		if (client.player != null) {
			client.player.sendOverlayMessage(Component.literal(Config.INSTANCE.musicVolume == -1.0 ? "§a♪ Music Unmuted" : "§c♪ Music Muted"));
		}
	}

	public static Identifier id(String path) {
		return Identifier.fromNamespaceAndPath(MOD_ID, path);
	}
}



