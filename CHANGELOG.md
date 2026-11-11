# Changelog

## 0.4.1

- Universal jar: Mod Menu showed FNViaViewer twice (the container and the mod itself). The container is now listed as a library under FNViaViewer, so there is only one entry, with the settings button.

## 0.4.0

- More client versions. Besides 1.21.11, there are now builds for **26.2, 26.1.x (26.1–26.1.2), 1.21.9–1.21.10 and 1.21.7–1.21.8**, all joining 26.3 servers. Use the jar that matches your game version. Each one only adds what that version is missing up to 26.3.
- One universal jar (`fnviaviewer-0.4.0-universal.jar`) with all five inside: Fabric loads only the one for your game version. Via is included once, so it is about the same size as a single jar.
- Versions not yet tested in game are released as alpha; see the release notes for which ones.
- Mobs that older clients don't have are drawn with their real shape instead of ViaBackwards' look-alike:
  - 1.21.10 and 1.21.8: nautilus (and its baby), zombie nautilus (normal and coral), camel husk and parched;
  - 1.21.8: copper golem, with its oxidation stage and glowing eyes.
- Their shapes and animations come from Mojang's official Bedrock samples (`bedrock-samples`), downloaded on first launch like the other assets and checked against their SHA-1. Nothing from Mojang is bundled in the mod.
- 26.3 models and item definitions are converted for older clients: tilted cubes beyond 45° (signs, hanging sign chains), the "on shelf" display context and new armor trim colors.
- 1.21.8: shelves are shown as plain blocks (no items on top), and copper chests and copper golem statues keep ViaBackwards' look-alike (a chest...), because that version has no renderer for them.
- 26.1 and newer already have the new babies, and 26.2 and newer the sulfur cube and the new signs: those features are only added where they are missing.
- For maintainers: one multi-version build (Essential's preprocessor, like EMF/ETF) with a folder per client version in `versions/`, and a content descriptor per client in `content/`.

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
