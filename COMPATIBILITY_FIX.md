# AuraMusic 0.1.1 — Minecraft 26.3 compatibility fix

## Confirmed crash cause

Crash: `NoSuchFieldError: ClientTickEvents.EndTick END_CLIENT_TICK` in `AuraMusicClient.onInitializeClient()`.

The old preview JAR was **not built with real Fabric libraries**. Its fake test API declared `END_CLIENT_TICK` as `ClientTickEvents.EndTick`, but Fabric 26.3 declares it as `Event<ClientTickEvents.EndTick>`.

The Java source was correct; the emitted bytecode was not. This rebuild uses a corrected stub binary descriptor `Lnet/fabricmc/fabric/api/event/Event;` as verified against Fabric 26.3 source.

Other placeholder binary signatures reviewed/corrected for this **experimental** JAR:

- `Minecraft.gui` is `net.minecraft.client.gui.Gui`
- `BuiltInRegistries.SOUND_EVENT` is `net.minecraft.core.Registry`
- `SoundManager.play(SoundInstance)` returns `SoundEngine.PlayResult`
- `SoundManager.stop(SoundInstance)` and `isActive(SoundInstance)` accept `SoundInstance`
- Keyboard scancode is read from `InputConstants.KEY_M` at runtime rather than hardcoded

## Important

The attached compatibility-test JAR uses verified signature-oriented test interfaces and Java 21 bytecode. It is **not a verified official Loom/Fabric build**, so further linkage errors are possible. Java 25 can run Java 21 bytecode. The GitHub project uses Java 25 and Gradle Loom for genuine builds; run the GitHub Action to obtain an actual Gradle-built JAR.

Never put this JAR in `mods` alongside the previous AuraMusic JAR. Keep exactly one `auramusic` mod.

Test in a separate Minecraft 26.3 Fabric profile. If crash persists, provide the newest crash-report (not the old report). 
