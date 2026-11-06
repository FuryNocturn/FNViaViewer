# Changelog

## 0.3.0 (beta)

- The sulfur cube is drawn as it really looks: see-through shell, core, baby size and the swallowed block inside. It swells and flashes when primed, like TNT, and drops its goo particles when it bounces. Its push and bounce come from the server, so they already match 26.3.
- Around 40 baby mobs use their 26.x models and textures. Their animation is still 1.21.11's, so some parts may move differently.
- New particles from the golden dandelion on babies (pause and reset growth).
- New setting: "New babies". The sulfur cube goes with "New entities".
- Babies only change on servers translated by Via; on servers running your own version they stay vanilla.

## 0.2.0 (beta)

- Poplar signs and hanging signs show their text. The 26.3 sign model is used, and only the text is drawn on top.
- The poplar shelf shows its items, and the straw bed works as a bed (sleeping pose).
- New particles: falling poplar leaves, and the sulfur geyser's bubbles, noxious gas and eruptions. Geysers also push you up while erupting, like in 26.3. Geyser effects are re-implemented on the client and may not be identical.
- Poplar boats (with and without chest) and cushions are drawn as they really look, including the cushion color.
- New settings: "New particles" and "New entities". The settings screen now uses two columns.
- The asset cache now also keeps the 26.3 particle definitions; existing caches are updated on the next launch.
- For maintainers: a GitHub Actions workflow checks for new ViaVersion/ViaBackwards releases every day, runs the mapping checks and the build, and leaves a draft release.

## 0.1.1 (beta)

- Fixed disconnects when switching servers behind a proxy (e.g. lobby → game mode). The proxy can still send game packets while the client is already in the configuration phase; vanilla ignores them and Via tried to translate them as configuration packets ("UPDATE_TAGS" error). They are now dropped and only logged, like vanilla does. Translation errors outside that phase still disconnect.

## 0.1.0 (first beta)

- Join 26.3 servers from a 1.21.11 client, with or without ViaVersion on the server.
- 26.3 blocks (120), items (153) and sounds (153) are shown as they really are, instead of as older look-alikes.
- Collision, light, hardness and map colors come from the ViaBackwards look-alike, so movement prediction stays in sync with the server.
- 26.3 textures, models, sounds and translations are downloaded from Mojang on first launch. Nothing from Mojang is bundled in the mod.
- Settings screen (Mod Menu): turn translation, new blocks, new items and new sounds on or off, and download the assets again.
- Iris: new blocks use their look-alike's shader ID when the shader pack doesn't know them.
- English and Spanish.
