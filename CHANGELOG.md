# Changelog

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
