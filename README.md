> **IMPORTANT:** Use a real Fabric Loom build for installation. Previous experimental test JARs were NOT binary compatible. See `FIX_CRASH_AND_UPLOAD_RU.md`.

# AuraMusic 26.3 🎵

An **original**, client-side music player mod for **Minecraft Java 26.3 + Fabric**.

This is a fresh implementation. **It is not Overtune, and it does not include or require Overtune's code or JAR.**

## Features (implemented in source)

- Press **M** (rebindable under Controls) to open the music player.
- Browse Minecraft's registered music and music discs.
- Browse PCM **`.wav`** files from a local folder.
- Play/stop, previous/next, pause/resume, page tabs.
- Favorites playlist saved across sessions.
- Shuffle, repeat off / all / current track.
- Adjustable local WAV volume and WAV seeking (±10 seconds).
- Re-scan tracks from inside the GUI.
- The player continues running when the screen closes.

### Local music folder

Launch the game once, open AuraMusic with `M`, and put your own **PCM WAV** files into:

```text
.minecraft/auramusic/tracks/
```

Reopen AuraMusic or press **SCAN** to refresh. Windows Explorer: `%APPDATA%\.minecraft\auramusic\tracks` for the default launcher directory. Alternative launchers often use their own instance directory.

**No MP3/OGG decoder is bundled.** WAV playback uses the system's Java Sound audio output, which might not be available on every OS/audio device. Local WAV tracks are limited to 60 MB each to avoid excessive memory use. WAV uses a separate audio pipeline from Minecraft's own sound engine, so **the in-game Music volume slider does not control local WAV playback**. The UI volume controls apply to WAV only. Vanilla music pause/restart is approximate: resuming it restarts the sound event, while PCM WAV supports real pause/resume and seeking.

Audio content is **not included** in this repository: use your own legally obtained WAV music files.

## Build

Requirements: Java **25**, Gradle **9.7.1**, network access to Fabric/Maven repositories.

```bash
gradle build
```

Compiled mod JAR will appear under `build/libs/`. Don't install the `-sources.jar` file.

**GitHub Actions**: Upload all project files to the repository **root**, including the hidden `.github` folder. Open **Actions → Build AuraMusic 26.3**. The automatically triggered build will appear after the commit; when it passes, download the `AuraMusic-26.3-JAR` artifact.

### Dependencies at runtime

- Minecraft **26.3** (Java Edition)
- Fabric Loader **0.19.5+**
- Fabric API matching **26.3**
- Java **25**

## Project layout

```text
src/client/java/dev/auramusic/client/
  AuraMusicClient.java          # input + startup
  audio/Track.java             # track metadata
  audio/TrackLibrary.java      # discovery
  audio/PlayerController.java  # sound playback engine
  config/PlayerPreferences.java # persistent favorites and playback prefs
  ui/AuraMusicScreen.java      # GUI
src/main/resources/fabric.mod.json
src/client/resources/assets/auramusic/lang/
.github/workflows/build.yml
```

## Status

**Experimental source release, not yet verified in a running Minecraft client.** The source/JSON/project layout has been inspected. If a compiler issue is reported by GitHub Actions, collect the `Build mod` log and apply fixes before treating the JAR as a tested release. Compiling only establishes compatibility with APIs, not runtime correctness.

## License

MIT for the new source code. Minecraft, Fabric and music content remain with their respective owners.


See [COMPATIBILITY_FIX.md](COMPATIBILITY_FIX.md) for the 26.3 crash fix and known limitations.
