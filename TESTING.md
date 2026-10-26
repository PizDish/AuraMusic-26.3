# Smoke-test checklist

1. Build with GitHub Actions: `Build AuraMusic 26.3` should finish with a green check.
2. In a clean Minecraft 26.3 Fabric client with matching Fabric API, copy the non-sources JAR into `mods`.
3. Open Controls and ensure AuraMusic's `M` binding appears.
4. Enter a test world; press `M` to open the player. Ensure keyboard/mouse responds.
5. Confirm soundtrack/disc tabs have entries and at least one vanilla soundtrack or disc plays.
6. Drop a small, 16-bit PCM WAV file into `.minecraft/auramusic/tracks/`, press `SCAN`, switch to LOCAL WAV and play it.
7. Pause/unpause WAV, seek ±10 seconds, and adjust VOL +/-.
8. Favorite a track, restart Minecraft and verify that FAVORITES remembers it.
9. Test shuffle and all repeat modes on a queue.
10. Try closing the screen while playing; check that the song continues.

Known limitations: no MP3/OGG, native soundtrack pause resumes from the beginning, standalone JavaSound output depends on local audio support.
