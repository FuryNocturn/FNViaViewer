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
  <img src="https://img.shields.io/badge/clients-1.21.7_to_26.2-blue" alt="Clients 1.21.7 to 26.2">
  <img src="https://img.shields.io/badge/servers-26.3-brightgreen" alt="Servers 26.3">
  <img src="https://img.shields.io/badge/loader-Fabric-dbd0b4" alt="Fabric">
  <img src="https://img.shields.io/badge/license-GPL--3.0-red" alt="License GPL-3.0">
  <img src="https://img.shields.io/badge/version-0.4.0-orange" alt="Version">
</p>

---

**FNViaViewer** is a client-side Fabric mod for Minecraft **1.21.7 to 26.2** that lets you join **26.3** servers and see their **new blocks, items, sounds and mobs as they really are**, instead of the older look-alikes you normally get.

| Your version | Jar | What it adds |
| :--- | :--- | :--- |
| 26.2 | `fnviaviewer-…+26.2.jar` | 26.3's content |
| 26.1, 26.1.1, 26.1.2 | `fnviaviewer-…+26.1.jar` | 26.2's and 26.3's (sulfur cube, sulfur, cinnabar…) |
| 1.21.11 | `fnviaviewer-…+1.21.11.jar` | 26.1 to 26.3, plus the new babies |
| 1.21.9, 1.21.10 | `fnviaviewer-…+1.21.10.jar` | All of the above, plus 1.21.11's items and mobs (spears, nautilus, camel husk, parched) |
| 1.21.7, 1.21.8 | `fnviaviewer-…+1.21.8.jar` | All of the above, plus 1.21.9's (copper, shelves, copper golem) |

Don't want to pick? `fnviaviewer-…-universal.jar` has all five inside, and Fabric loads only the one for your version.

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
* **🪧 Blocks that work:** poplar signs show their text, the shelf shows its items, and you sleep in the straw bed in the right pose.
* **✨ Particles and geysers:** falling poplar leaves, and geysers with bubbles, gas and eruptions that push you up, like in 26.3.
* **⛵ Poplar boats and cushions** as they really look.
* **🟨 Sulfur cube:** with its see-through shell, its core, its baby size and the block it has swallowed shown inside. It swells and flashes when primed, and drops goo when it bounces.
* **🐣 26.x babies:** around 40 baby animals and monsters (cow, pig, wolf, cat, villager, zombie…) with their new models and textures (up to 1.21.11; 26.1 and newer already have them).
* **🐚 Mobs your version doesn't have**, with their real shape and animations: nautilus, zombie nautilus, camel husk, parched and copper golem, depending on the version.
* **🛡️ Leaves everything else alone:** on servers running your own version, the game is exactly vanilla.

### 📦 Content included

| Client | Blocks (states) | Items | Sounds | Particles |
| :--- | ---: | ---: | ---: | ---: |
| 26.2 | 90 (3,357) | 121 | 23 | 3 |
| 26.1.x | 118 (5,850) | 152 | 89 | 11 |
| 1.21.11 | 120 (5,852) | 153 | 153 | 13 |
| 1.21.9–1.21.10 | 120 (5,852) | 170 | 220 | 13 |
| 1.21.7–1.21.8 | 166 (7,135) | 227 | 263 | 14 |

Among them: the full poplar wood set, cinnabar and sulfur building blocks, wool and concrete slabs and stairs in every color, the golden dandelion, red shrub, shelf mushroom, straw bed, cushions and the new explorer maps. On older versions, also the spears and nautilus armor (1.21.11), and the copper torches, chains, bars, lanterns and lightning rods and the shelves (1.21.9). Plus the entities: poplar boats, cushions, the sulfur cube and the mobs above.

---

## 🎨 Textures and sounds come from Mojang

The mod contains **no Mojang assets**. On first launch it downloads the official 26.3 `client.jar` and asset index from Mojang's servers, checks every file's SHA-1, and keeps only what your version doesn't have: textures, models and the new sounds. On older versions it converts what they wouldn't understand (cubes tilted beyond 45°, new item display contexts, armor trim colors). Mobs your version doesn't have are drawn with the geometry and animations from Mojang's official [Bedrock samples](https://github.com/Mojang/bedrock-samples), downloaded the same way (pinned to one version and checked by SHA-1). Everything goes to `.minecraft/fnviaviewer/cache/`. The only 26.3 data inside the mod are the model measurements of the babies and the sulfur cube (cubes and pivots), copied into its code.

That content loads as a built-in resource pack placed below your own packs, so a resource pack can still override it. If you're offline the first time, you get a notice and new blocks show as their older look-alikes until the next successful download.

---

## ⚙️ Settings

With [Mod Menu](https://modrinth.com/mod/modmenu) installed, you get a settings screen:

| Option | What it does |
| :--- | :--- |
| **Join newer servers** | Turns translation on or off. When off, the game is vanilla. |
| **New blocks / items / sounds / particles / entities / babies** | Show each kind of new content, or fall back to the older look-alike. |
| **Download assets again** | Clears the cache and downloads it again from Mojang. |

Settings are saved in `config/fnviaviewer.json`.

---

## 🧩 Compatibility

| Mod | Status |
| :--- | :--- |
| **Sodium** | ✅ Tested, works. |
| **Xaero's Minimap / World Map** | ✅ Tested, new blocks get their map colors. |
| **Iris** | 🟡 Supported, not tested much yet. Shader packs made for your version don't know the new blocks, so each one uses the shader ID of its look-alike (waving leaves, emissive blocks, water). If a pack already names a new block, its own setting is used. |
| **JourneyMap** | 🟡 Should work (it reads block textures and map colors), not tested yet. Reports welcome. |
| **ViaFabricPlus / ViaFabric** | ❌ Not compatible: they also inject protocol translation, so use one or the other. |

---

## ⚠️ Known limitations (0.4.0)

* Babies use the 26.x geometry and textures, but the **animation** is your version's: for the ones whose parts changed (wolf, bee, rabbit, donkey, axolotl, chicken, strider, hoglin, piglin, turtle) some parts don't move exactly like in 26.x.
* Baby zombie armor keeps its old shape.
* Geyser effects (bubbles, gas, eruptions and the upward push) are **re-implemented** to look like 26.3's: they may not be identical.
* Poplar boats, cushions and sulfur cubes only change their look: on the network and for physics they're still their replacement (birch boat, falling block and slime). The sulfur cube's push and bounce are computed by the server, so they look like in 26.3.
* New mobs on older clients: no nautilus saddle or armor, no items in the copper golem's hand and no chest animations.
* 1.21.7–1.21.8: shelves don't show items, and copper chests and copper golem statues show their look-alike (a chest…), because that version has nothing to draw them with.
* Only **1.21.7 to 26.2** clients and **26.3** servers for now. Older versions are planned.

---

## 📋 Requirements

* Minecraft **1.21.7 to 26.2** (the jar for your version, or the universal one), Fabric Loader **0.19+**, [Fabric API](https://modrinth.com/mod/fabric-api)
* Java 21 (1.21.x) or Java 25 (26.x): whatever your Minecraft version already needs
* Internet access on first launch, to download the 26.3 assets from Mojang
* Optional: [Mod Menu](https://modrinth.com/mod/modmenu), for the settings screen

---

## 🛠️ How it works (for developers)

1. **Connection:** when you connect, the mod pings the server with protocol `-1` to learn its real version. Servers running ViaVersion report your own version on a normal ping. If the server is newer and supported, the client joins as that version and ViaBackwards translates the packets inside the client.
2. **Content descriptor:** `tools/gen_content.py` compares the official data reports of every version in the chain (26.3 → 26.2 → 26.1.2 → 1.21.11 → …, down to the client's). There is one per client in `content/<version>/`. It lists what's new and which IDs each ViaBackwards step would replace. `gradlew fallbackTool` loads Via outside the game, records each new state's original look-alike, and checks the whole patch: new content reaches the client with the right IDs, client-to-server IDs map back, and vanilla content is unchanged.
3. **Mapping patch:** each ViaBackwards step's block state, block, item and sound mappings are wrapped so new IDs pass through an extended range. The last step maps them to the client's real registry IDs. The table sizes grow too, which is what Via uses for the chunk palette bit width.
4. **Client content:** blocks, items and sound events are registered under their real `minecraft:` names, at the end of the registries so vanilla IDs never change.

### Building

One build for every version (Essential's preprocessor, like EMF and ETF): the code in `src/` is written for 1.21.11 and converted for the others with `//#if MC >= …` comments. Each version has its folder in `versions/`. Java 25 is required.

```bash
./gradlew build
```

To regenerate the content, run the official servers' data generator into `tools/servers/reports-<version>`. Then, for each client (the last version in the list):

```bash
cd tools && python gen_content.py 26.3 26.2 26.1.2 1.21.11 1.21.10 1.21.8
```

```bash
./gradlew fallbackTool
```

---

## 📜 License

[GPL-3.0-only](LICENSE). FNViaViewer bundles [ViaVersion](https://github.com/ViaVersion/ViaVersion) and [ViaBackwards](https://github.com/ViaVersion/ViaBackwards) (GPLv3). Its connection code is adapted from [ViaFabricPlus](https://github.com/ViaVersion/ViaFabricPlus) (GPLv3). Thanks to the ViaVersion team for their work.

Minecraft is a trademark of Mojang Studios. Game assets are downloaded from Mojang by each player and are never redistributed by this project.
