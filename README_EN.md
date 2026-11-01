# 👁️ FNViaViewer
### See what's new without updating: newer Minecraft content on an older client

<p align="center">
  <a href="README.md"><b>Español 🇪🇸</b></a> |
  <a href="README_EN.md"><b>English 🇺🇸</b></a> |
  <a href="https://modrinth.com/mod/fnviaviewer"><b>Modrinth</b></a> |
  <a href="https://www.curseforge.com/minecraft/mc-mods/fnviaviewer"><b>CurseForge</b></a> |
  <a href="https://github.com/FuryNocturn/FNViaViewer/releases"><b>Downloads 📥</b></a>
</p>

<p align="center">
  <img src="https://img.shields.io/badge/client-1.21.11-blue" alt="Client 1.21.11">
  <img src="https://img.shields.io/badge/servers-26.3-brightgreen" alt="Servers 26.3">
  <img src="https://img.shields.io/badge/loader-Fabric-dbd0b4" alt="Fabric">
  <img src="https://img.shields.io/badge/license-GPL--3.0-red" alt="License GPL-3.0">
  <img src="https://img.shields.io/badge/version-0.1.0--beta-orange" alt="Version">
</p>

---

**FNViaViewer** is a client-side Fabric mod for Minecraft **1.21.11** that lets you join **26.3** servers and see their **new blocks, items and sounds as they really are**, instead of the older look-alikes you normally get.

> *Not affiliated with Mojang Studios or the ViaVersion team.*

---

## ❓ The problem

Protocol translators like ViaVersion/ViaBackwards let an older client join a newer server. Content that doesn't exist in your version gets swapped for something similar: poplar planks show up as birch, cinnabar as granite, a sulfur spike as pointed dripstone.

---

## ✨ Features

* **🌐 Joins newer servers by itself:** ViaVersion and ViaBackwards run inside your client, so it works on any 26.3 server, with or without Via on the server side. Nothing is needed on the server.
* **🧱 Shows the real content:** the 26.3 blocks, items and sounds are added to your client, and the translation lets them through instead of replacing them.
* **🏃 Smooth movement:** each new block takes its collision, shape, light, hardness and map color from the block ViaBackwards would have used, so what your client predicts matches the server. No rubber-banding.
* **🔊 New sounds** where 26.3 has them: cinnabar, sulfur, poplar leaves and others.
* **🛡️ Leaves everything else alone:** on servers running your own version, the game is exactly vanilla.

### 📦 26.3 content included

**120 blocks** (5,852 states), **153 items** and **153 sounds**. Among them: the full poplar wood set, cinnabar and sulfur building blocks, wool and concrete slabs and stairs in every color, the golden dandelion, red shrub, shelf mushroom, straw bed, cushions and the new explorer maps.

---

## 🎨 Textures and sounds come from Mojang

The mod contains **no Mojang assets**. On first launch it downloads the official 26.3 `client.jar` and asset index from Mojang's servers, checks every file's SHA-1, and keeps only what 1.21.11 doesn't have (about 640 KB of textures and models, plus the new sounds). Everything goes to `.minecraft/fnviaviewer/cache/`.

That content loads as a built-in resource pack placed below your own packs, so a resource pack can still override it. If you're offline the first time, you get a notice and new blocks show as their older look-alikes until the next successful download.

---

## ⚙️ Settings

With [Mod Menu](https://modrinth.com/mod/modmenu) installed, you get a settings screen:

| Option | What it does |
| :--- | :--- |
| **Join newer servers** | Turns translation on or off. When off, the game is vanilla. |
| **New blocks / items / sounds** | Show each kind of new content, or fall back to the older look-alike. |
| **Download assets again** | Clears the cache and downloads it again from Mojang. |

Settings are saved in `config/fnviaviewer.json`.

---

## 🧩 Compatibility

| Mod | Status |
| :--- | :--- |
| **Sodium** | ✅ Tested, works. |
| **Xaero's Minimap / World Map** | ✅ Tested, new blocks get their map colors. |
| **Iris** | 🟡 Supported, not tested much yet. Shader packs made for 1.21.11 don't know the new blocks, so each one uses the shader ID of its look-alike (waving leaves, emissive blocks, water). If a pack already names a new block, its own setting is used. |
| **JourneyMap** | 🟡 Should work (it reads block textures and map colors), not tested yet. Reports welcome. |
| **ViaFabricPlus / ViaFabric** | ❌ Not compatible: they also inject protocol translation, so use one or the other. |

---

## ⚠️ Known limitations (0.1.0)

* Signs and hanging signs are shown, but **their text isn't**. Beds and shelves from 26.3 are visual only.
* **New mobs and entity models** (poplar boats, cushions…) still show as their older replacements.
* One rotation of the hanging sign model can't be represented in 1.21.11 and shows the missing-model texture.
* Only **1.21.11 → 26.3** for now. More versions, both newer and older, are planned.

---

## 📋 Requirements

* Minecraft **1.21.11**, Fabric Loader **0.19+**, [Fabric API](https://modrinth.com/mod/fabric-api)
* Internet access on first launch, to download the 26.3 assets from Mojang
* Optional: [Mod Menu](https://modrinth.com/mod/modmenu), for the settings screen

---

## 🛠️ How it works (for developers)

1. **Connection:** when you connect, the mod pings the server with protocol `-1` to learn its real version. Servers running ViaVersion report your own version on a normal ping. If the server is newer and supported, the client joins as that version and ViaBackwards translates the packets inside the client.
2. **Content descriptor:** `tools/gen_content.py` compares the official data reports of every version in the chain (26.3 → 26.2 → 26.1.2 → 1.21.11). It lists what's new and which IDs each ViaBackwards step would replace. `gradlew fallbackTool` loads Via outside the game, records each new state's original look-alike, and checks the whole patch: new content reaches the client with the right IDs, client-to-server IDs map back, and vanilla content is unchanged.
3. **Mapping patch:** each ViaBackwards step's block state, block, item and sound mappings are wrapped so new IDs pass through an extended range. The last step maps them to the client's real registry IDs. The table sizes grow too, which is what Via uses for the chunk palette bit width.
4. **Client content:** blocks, items and sound events are registered under their real `minecraft:` names, at the end of the registries so vanilla IDs never change.

### Building

```bash
./gradlew build
```

To regenerate the 26.3 content, run the official servers' data generator into `tools/servers/reports-<version>`. Then:

```bash
cd tools && python gen_content.py 26.3 26.2 26.1.2 1.21.11
```

```bash
./gradlew fallbackTool
```

---

## 📜 License

[GPL-3.0-only](LICENSE). FNViaViewer bundles [ViaVersion](https://github.com/ViaVersion/ViaVersion) and [ViaBackwards](https://github.com/ViaVersion/ViaBackwards) (GPLv3). Its connection code is adapted from [ViaFabricPlus](https://github.com/ViaVersion/ViaFabricPlus) (GPLv3). Thanks to the ViaVersion team for their work.

Minecraft is a trademark of Mojang Studios. Game assets are downloaded from Mojang by each player and are never redistributed by this project.
