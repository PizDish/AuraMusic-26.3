package dev.auramusic.client;

import com.mojang.blaze3d.platform.InputConstants;
import dev.auramusic.client.audio.PlayerController;
import dev.auramusic.client.audio.TrackLibrary;
import dev.auramusic.client.config.PlayerPreferences;
import dev.auramusic.client.ui.AuraMusicScreen;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;

/** Client-only entry point. No Overtune code is included. */
public final class AuraMusicClient implements ClientModInitializer {
    private static TrackLibrary library;
    private static PlayerPreferences preferences;
    private static PlayerController player;

    @Override
    public void onInitializeClient() {
        KeyMapping.Category category = KeyMapping.Category.register(
            Identifier.fromNamespaceAndPath("auramusic", "player"));
        KeyMapping shortcut = KeyMappingHelper.registerKeyMapping(new KeyMapping(
            "key.auramusic.open", InputConstants.Type.KEYBOARD, InputConstants.KEY_M, category));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (shortcut.consumeClick()) open(client);
            if (player != null) player.tick();
        });
    }

    public static void open(Minecraft client) {
        if (library == null) library = new TrackLibrary(client.gameDirectory.toPath());
        if (preferences == null) preferences = new PlayerPreferences(FabricLoader.getInstance().getConfigDir());
        if (player == null) player = new PlayerController(client, preferences);
        library.refresh();
        client.gui.setScreen(new AuraMusicScreen(library, player, preferences));
    }
}
