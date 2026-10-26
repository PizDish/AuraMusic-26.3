package dev.auramusic.client.config;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Properties;
import java.util.Set;

/** Stores settings in config/auramusic.properties without a JSON dependency. */
public final class PlayerPreferences {
    private final Path file;
    private final Set<String> favorites = new LinkedHashSet<>();
    private boolean shuffle;
    private int repeatMode; // 0 = off, 1 = all, 2 = one
    private int volume = 75;

    public PlayerPreferences(Path configDir) {
        file = configDir.resolve("auramusic.properties");
        load();
    }

    public boolean isFavorite(String id) { return favorites.contains(id); }
    public Set<String> favorites() { return Collections.unmodifiableSet(favorites); }
    public boolean isShuffle() { return shuffle; }
    public int repeatMode() { return repeatMode; }
    public int volume() { return volume; }

    public void toggleFavorite(String id) {
        if (!favorites.add(id)) favorites.remove(id);
        save();
    }

    public void toggleShuffle() { shuffle = !shuffle; save(); }
    public void cycleRepeat() { repeatMode = (repeatMode + 1) % 3; save(); }
    public void changeVolume(int delta) { volume = Math.clamp(volume + delta, 0, 100); save(); }

    private void load() {
        if (!Files.isRegularFile(file)) return;
        Properties props = new Properties();
        try (InputStream stream = Files.newInputStream(file)) {
            props.load(stream);
            shuffle = Boolean.parseBoolean(props.getProperty("shuffle", "false"));
            repeatMode = Math.clamp(Integer.parseInt(props.getProperty("repeat", "0")), 0, 2);
            volume = Math.clamp(Integer.parseInt(props.getProperty("volume", "75")), 0, 100);
            String stored = props.getProperty("favorites", "");
            if (!stored.isBlank()) Collections.addAll(favorites, stored.split("\\|", -1));
        } catch (IOException | NumberFormatException ignored) {
            // Invalid config must never crash the game.
        }
    }

    public void save() {
        Properties props = new Properties();
        props.setProperty("shuffle", Boolean.toString(shuffle));
        props.setProperty("repeat", Integer.toString(repeatMode));
        props.setProperty("volume", Integer.toString(volume));
        props.setProperty("favorites", String.join("|", favorites));
        try {
            Files.createDirectories(file.getParent());
            try (OutputStream stream = Files.newOutputStream(file)) {
                props.store(stream, "AuraMusic player preferences");
            }
        } catch (IOException ignored) {
            // Save failure should not terminate the player.
        }
    }
}
