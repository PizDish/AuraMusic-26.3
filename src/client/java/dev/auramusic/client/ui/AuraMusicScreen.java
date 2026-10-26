package dev.auramusic.client.ui;

import dev.auramusic.client.audio.PlayerController;
import dev.auramusic.client.audio.Track;
import dev.auramusic.client.audio.TrackLibrary;
import dev.auramusic.client.config.PlayerPreferences;
import java.util.List;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

/** Compact button-first UI for the 26.3 extractor-based GUI API. */
public final class AuraMusicScreen extends Screen {
    private enum Filter { ALL, DISCS, LOCAL, FAVORITES }

    private final TrackLibrary library;
    private final PlayerController player;
    private final PlayerPreferences settings;
    private Filter filter = Filter.ALL;
    private int page;
    private int visibleRows;
    private int left;
    private int top;
    private int panelWidth;

    public AuraMusicScreen(TrackLibrary library, PlayerController player, PlayerPreferences settings) {
        super(Component.literal("AuraMusic"));
        this.library = library;
        this.player = player;
        this.settings = settings;
    }

    @Override
    protected void init() {
        panelWidth = Math.min(390, width - 16);
        left = (width - panelWidth) / 2;
        top = 8;
        visibleRows = Math.clamp((height - 156) / 23, 2, 8);
        int inside = panelWidth - 12;
        int tabsY = top + 28;
        int tabWidth = (inside - 9) / 4;
        String[] titles = {"ALL", "DISCS", "LOCAL WAV", "FAVORITES"};
        Filter[] filters = Filter.values();
        for (int i = 0; i < filters.length; i++) {
            Filter desired = filters[i];
            String display = filter == desired ? "[" + titles[i] + "]" : titles[i];
            addRenderableWidget(Button.builder(Component.literal(display), b -> switchFilter(desired))
                .bounds(left + 6 + i * (tabWidth + 3), tabsY, tabWidth, 20).build());
        }

        List<Track> matching = matchingTracks();
        int pages = Math.max(1, (matching.size() + visibleRows - 1) / visibleRows);
        page = Math.clamp(page, 0, pages - 1);
        int listTop = top + 55;
        for (int i = 0; i < visibleRows; i++) {
            int position = page * visibleRows + i;
            if (position >= matching.size()) break;
            Track track = matching.get(position);
            int y = listTop + i * 23;
            String prefix = track.kind() == Track.Kind.LOCAL ? "♪ " : track.kind() == Track.Kind.DISC ? "♫ " : "· ";
            String label = prefix + shorten(track.title(), inside - 55);
            addRenderableWidget(Button.builder(Component.literal(label), b -> player.play(track, matching))
                .bounds(left + 6, y, inside - 27, 20).build());
            String star = settings.isFavorite(track.id()) ? "*" : "+";
            addRenderableWidget(Button.builder(Component.literal(star), b -> {
                settings.toggleFavorite(track.id());
                rebuild();
            }).bounds(left + 6 + inside - 24, y, 24, 20).build());
        }

        int controls = listTop + visibleRows * 23 + 4;
        addRenderableWidget(Button.builder(Component.literal("< PAGE"), b -> shiftPage(-1))
            .bounds(left + 6, controls, 66, 20).build());
        addRenderableWidget(Button.builder(Component.literal("|<"), b -> player.previous())
            .bounds(left + 77, controls, 38, 20).build());
        addRenderableWidget(Button.builder(Component.literal("PLAY / PAUSE"), b -> player.togglePause())
            .bounds(left + 119, controls, Math.max(65, inside - 232), 20).build());
        addRenderableWidget(Button.builder(Component.literal(">|"), b -> player.next())
            .bounds(left + inside - 108, controls, 38, 20).build());
        addRenderableWidget(Button.builder(Component.literal("PAGE >"), b -> shiftPage(1))
            .bounds(left + inside - 66, controls, 66, 20).build());

        int optionsY = controls + 24;
        addRenderableWidget(Button.builder(Component.literal(settings.isShuffle() ? "SHUFFLE: ON" : "SHUFFLE: OFF"), b -> {
            settings.toggleShuffle(); rebuild();
        }).bounds(left + 6, optionsY, 105, 20).build());
        String repeatText = switch (settings.repeatMode()) {
            case 1 -> "REPEAT: ALL";
            case 2 -> "REPEAT: ONE";
            default -> "REPEAT: OFF";
        };
        addRenderableWidget(Button.builder(Component.literal(repeatText), b -> {
            settings.cycleRepeat(); rebuild();
        }).bounds(left + 115, optionsY, 105, 20).build());
        addRenderableWidget(Button.builder(Component.literal("STOP"), b -> player.stop())
            .bounds(left + 224, optionsY, 50, 20).build());
        addRenderableWidget(Button.builder(Component.literal("SCAN"), b -> {
            library.refresh(); rebuild();
        }).bounds(left + 278, optionsY, Math.max(38, inside - 272), 20).build());

        int volumeY = optionsY + 24;
        addRenderableWidget(Button.builder(Component.literal("VOL -"), b -> {settings.changeVolume(-10);player.applyVolume();rebuild();})
            .bounds(left + 6, volumeY, 65, 20).build());
        addRenderableWidget(Button.builder(Component.literal("VOL +"), b -> {settings.changeVolume(10);player.applyVolume();rebuild();})
            .bounds(left + 75, volumeY, 65, 20).build());
        addRenderableWidget(Button.builder(Component.literal("-10s"), b -> player.seek(-10))
            .bounds(left + 150, volumeY, 65, 20).build());
        addRenderableWidget(Button.builder(Component.literal("+10s"), b -> player.seek(10))
            .bounds(left + 219, volumeY, 65, 20).build());
    }

    private String shorten(String text, int pixels) {
        if (font.width(text) <= pixels) return text;
        while (!text.isEmpty() && font.width(text + "…") > pixels)
            text = text.substring(0, text.length() - 1);
        return text + "…";
    }

    private List<Track> matchingTracks() {
        return library.all().stream().filter(track -> switch (filter) {
            case ALL -> true;
            case DISCS -> track.kind() == Track.Kind.DISC;
            case LOCAL -> track.kind() == Track.Kind.LOCAL;
            case FAVORITES -> settings.isFavorite(track.id());
        }).toList();
    }

    private void switchFilter(Filter next) { filter = next; page = 0; rebuild(); }
    private void shiftPage(int direction) {
        int pages = Math.max(1, (matchingTracks().size() + visibleRows - 1) / visibleRows);
        page = Math.floorMod(page + direction, pages);
        rebuild();
    }
    private void rebuild() {
        AuraMusicScreen next = new AuraMusicScreen(library, player, settings);
        next.filter = filter;
        next.page = page;
        this.minecraft.gui.setScreen(next);
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta) {
        graphics.fill(0, 0, width, height, 0xE80A101C);
        graphics.fill(left - 2, top - 2, left + panelWidth + 2, height - 4, 0xEF121E33);
        graphics.fill(left - 2, top - 2, left + panelWidth + 2, top, 0xFF32C4D2);
        graphics.fill(left - 2, height - 6, left + panelWidth + 2, height - 4, 0xFF32C4D2);
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta) {
        super.extractRenderState(graphics, mouseX, mouseY, delta);
        graphics.centeredText(font, Component.literal("AURA MUSIC  //  26.3"), width / 2, top + 8, 0xFF72E5ED);
        List<Track> matching = matchingTracks();
        int pages = Math.max(1, (matching.size() + visibleRows - 1) / visibleRows);
        graphics.centeredText(font,
            Component.literal(matching.size() + " tracks     page " + (page + 1) + "/" + pages),
            width / 2, top + 19, 0xFF9CAFC2);
        Track current = player.current();
        String state = current == null ? "No track selected" :
            (player.isPaused() ? "PAUSED: " : "PLAYING: ") + current.title();
        String extra = current != null && current.isLocal()
            ? "  " + player.positionSeconds() + " / " + player.lengthSeconds() + "s" : "";
        graphics.centeredText(font, Component.literal(shorten(state + extra, panelWidth - 20)),
            width / 2, top + 55 + visibleRows * 23 + 78, 0xFFF9E9B5);
        String notice = !player.error().isBlank() ? player.error() : library.lastError();
        if (!notice.isBlank()) graphics.centeredText(font,
            Component.literal(shorten(notice, panelWidth - 15)), width / 2,
            top + 55 + visibleRows * 23 + 90, 0xFFFF7F94);
    }

    @Override
    public boolean isPauseScreen() { return false; }
}
