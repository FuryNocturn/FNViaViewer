# Store listing: FNViaViewer 0.1.0

The same text goes on Modrinth and CurseForge. **Nothing is uploaded until you give your OK.**

## Fields

| Field | Value |
|---|---|
| Name | FNViaViewer |
| Slug | `fnviaviewer` (free on Modrinth as of 2026-10-31) |
| Summary (Modrinth max 256, CurseForge max 128) | See what's new without updating: join 26.3 servers from 1.21.11 and see their real new blocks, items and sounds. |
| Version | 0.1.0, channel **beta** |
| Minecraft | 1.21.11 |
| Loader | Fabric |
| Environment | Client: required · Server: unsupported |
| License | GPL-3.0-only |
| Source | https://github.com/FuryNocturn/FNViaViewer |
| Issues | https://github.com/FuryNocturn/FNViaViewer/issues |
| Modrinth categories | Utility, Decoration, Technology *(check which ones fit)* |
| CurseForge categories | Utility & QoL, Cosmetic, Server Utility |
| Required dependency | Fabric API |
| Optional dependency | Mod Menu |
| Incompatible | ViaFabricPlus, ViaFabric |
| File | `build/libs/fnviaviewer-0.1.0.jar` (not the `-sources` jar) |
| Changelog | Contents of `CHANGELOG.md`, section 0.1.0 |

## Screenshots to take (in Limpio, on the 26.3 server)

1. A small build mixing poplar, cinnabar and sulfur (the main image).
2. The same spot with the mod's "New blocks" option turned off, for a before/after (the ViaBackwards look-alike).
3. Inventory or hotbar with new items (cushions, poplar boat, explorer maps).
4. The settings screen (Mod Menu → FNViaViewer).
5. Optional: Xaero's map showing the colors.

## Description (Markdown)

---

**See what's new without updating.**

FNViaViewer is a client-side Fabric mod for Minecraft **1.21.11** that lets you join **26.3** servers and see their **new blocks, items and sounds as they really are**, instead of the older look-alikes you normally get.

### Why?

Protocol translators let an older client join a newer server. Content your version doesn't have gets swapped for something similar: poplar planks become birch, cinnabar becomes granite, a sulfur spike becomes pointed dripstone. FNViaViewer shows you the real thing.

### Features

- 🌐 **Works on any 26.3 server**, whether or not it runs ViaVersion. Nothing is needed on the server.
- 🧱 **120 new blocks, 153 items and 153 sounds**: the full poplar wood set, cinnabar and sulfur building blocks, wool and concrete slabs and stairs, the golden dandelion, cushions, explorer maps and more.
- 🏃 **No rubber-banding**: collision, light and hardness match what the server expects.
- 🔊 **New sounds** where 26.3 has them.
- 🎨 **No Mojang files inside the mod**: the 26.3 textures and sounds are downloaded from Mojang's official servers on first launch (SHA-1 checked), about 640 KB plus sounds.
- ⚙️ **Settings** (with Mod Menu): turn translation, new blocks, new items or new sounds on or off.
- ✅ Tested with **Sodium** and **Xaero's maps**. **Iris** support is built in (new blocks use their look-alike's shader ID).

### Good to know

- Needs internet on first launch (to download the 26.3 assets).
- Not compatible with **ViaFabricPlus** or **ViaFabric**.
- Not done yet: sign text, entity models (poplar boats, cushions) and new mobs.
- Only 1.21.11 → 26.3 for now; more versions are planned.

### Credits

FNViaViewer bundles **ViaVersion** and **ViaBackwards** and adapts connection code from **ViaFabricPlus**, all under GPLv3. Huge thanks to the ViaVersion team.
*Not affiliated with Mojang Studios or the ViaVersion team.*

---
