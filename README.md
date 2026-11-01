# 👁️ FNViaViewer
### Lo nuevo sin actualizar: contenido de versiones nuevas de Minecraft en un cliente antiguo

<p align="center">
  <a href="README.md"><b>Español 🇪🇸</b></a> |
  <a href="README_EN.md"><b>English 🇺🇸</b></a> |
  <a href="https://modrinth.com/mod/fnviaviewer"><b>Modrinth</b></a> |
  <a href="https://www.curseforge.com/minecraft/mc-mods/fnviaviewer"><b>CurseForge</b></a> |
  <a href="https://github.com/FuryNocturn/FNViaViewer/releases"><b>Descargas 📥</b></a>
</p>

<p align="center">
  <img src="https://img.shields.io/badge/cliente-1.21.11-blue" alt="Cliente 1.21.11">
  <img src="https://img.shields.io/badge/servidores-26.3-brightgreen" alt="Servidores 26.3">
  <img src="https://img.shields.io/badge/loader-Fabric-dbd0b4" alt="Fabric">
  <img src="https://img.shields.io/badge/licencia-GPL--3.0-red" alt="Licencia GPL-3.0">
  <img src="https://img.shields.io/badge/versión-0.1.0--beta-orange" alt="Versión">
</p>

---

**FNViaViewer** es un mod de Fabric para el cliente de Minecraft **1.21.11** que te deja entrar en servidores **26.3** y ver sus **bloques, items y sonidos nuevos tal como son**, en vez de los parecidos antiguos que salen normalmente.

> *Sin relación con Mojang Studios ni con el equipo de ViaVersion.*

---

## ❓ El problema

Los traductores de protocolo como ViaVersion/ViaBackwards dejan entrar con un cliente antiguo en un servidor más nuevo. Lo que no existe en tu versión se cambia por algo parecido: los tablones de álamo salen como abedul, el cinabrio como granito y el azufre puntiagudo como caliza puntiaguda.

---

## ✨ Características

* **🌐 Entra en servidores más nuevos por sí mismo:** ViaVersion y ViaBackwards funcionan dentro de tu cliente, así que sirve en cualquier servidor 26.3, tenga Via o no. El servidor no necesita nada.
* **🧱 Muestra el contenido real:** los bloques, items y sonidos de 26.3 se añaden a tu cliente, y la traducción los deja pasar en vez de sustituirlos.
* **🏃 Movimiento fluido:** cada bloque nuevo toma la colisión, la forma, la luz, la dureza y el color del mapa del bloque que habría puesto ViaBackwards. Así lo que predice tu cliente coincide con el servidor y no hay tirones.
* **🔊 Sonidos nuevos** donde 26.3 los tiene: cinabrio, azufre, hojas de álamo y otros.
* **🛡️ No toca nada más:** en servidores de tu misma versión el juego es exactamente vanilla.

### 📦 Contenido de 26.3 incluido

**120 bloques** (5.852 estados), **153 items** y **153 sonidos**. Entre ellos: todo el juego de madera de álamo, los bloques de construcción de cinabrio y azufre, losas y escaleras de lana y hormigón de todos los colores, el diente de león dorado, el arbusto rojo, el políporo, la cama de paja, los cojines y los nuevos mapas de exploración.

---

## 🎨 Las texturas y los sonidos vienen de Mojang

El mod **no lleva ningún asset de Mojang**. Al arrancar por primera vez baja de los servidores oficiales de Mojang el `client.jar` y el índice de assets de 26.3, comprueba el SHA-1 de cada fichero y se queda solo con lo que 1.21.11 no tiene: unos 640 KB de texturas y modelos, más los sonidos nuevos. Todo se guarda en `.minecraft/fnviaviewer/cache/`.

Ese contenido se carga como un resource pack interno colocado por debajo de los tuyos, así que tus resource packs pueden sobrescribirlo. Si la primera vez no tienes internet, te sale un aviso y los bloques nuevos se ven como sus parecidos antiguos hasta que la descarga funcione.

---

## ⚙️ Configuración

Con [Mod Menu](https://modrinth.com/mod/modmenu) instalado tienes una pantalla de opciones:

| Opción | Qué hace |
| :--- | :--- |
| **Entrar en servidores más nuevos** | Activa o desactiva la traducción. Desactivada, el juego es vanilla. |
| **Bloques / items / sonidos nuevos** | Muestra cada tipo de contenido nuevo, o lo cambia por su parecido antiguo. |
| **Volver a bajar los assets** | Borra la cache y la vuelve a bajar de Mojang. |

La configuración se guarda en `config/fnviaviewer.json`.

---

## 🧩 Compatibilidad

| Mod | Estado |
| :--- | :--- |
| **Sodium** | ✅ Probado, funciona. |
| **Xaero's Minimap / World Map** | ✅ Probado, los bloques nuevos tienen su color en el mapa. |
| **Iris** | 🟡 Soportado, aún poco probado. Los shaderpacks hechos para 1.21.11 no conocen los bloques nuevos, así que cada uno usa el ID de shader de su parecido (hojas que ondean, bloques que brillan, agua). Si un pack ya nombra un bloque nuevo, se respeta su ajuste. |
| **JourneyMap** | 🟡 Debería funcionar (lee las texturas y los colores de mapa), aún sin probar. Se agradecen informes. |
| **ViaFabricPlus / ViaFabric** | ❌ No compatibles: también meten traducción de protocolo, así que usa uno u otro. |

---

## ⚠️ Limitaciones conocidas (0.1.0)

* Los carteles y carteles colgantes se ven, pero **sin su texto**. Las camas y estanterías de 26.3 son solo visuales.
* **Los mobs y modelos de entidad nuevos** (barcos de álamo, cojines…) siguen saliendo como su sustituto antiguo.
* Una de las rotaciones del modelo del cartel colgante no se puede representar en 1.21.11 y sale con la textura de "falta modelo".
* Por ahora solo **1.21.11 → 26.3**. Está previsto añadir más versiones, tanto más nuevas como más antiguas.

---

## 📋 Requisitos

* Minecraft **1.21.11**, Fabric Loader **0.19+**, [Fabric API](https://modrinth.com/mod/fabric-api)
* Internet la primera vez que arranca, para bajar los assets de 26.3 de Mojang
* Opcional: [Mod Menu](https://modrinth.com/mod/modmenu), para la pantalla de opciones

---

## 🛠️ Cómo funciona (para desarrolladores)

1. **Conexión:** al conectar, el mod hace un ping al servidor con protocolo `-1` para saber su versión real, porque un servidor con ViaVersion responde con tu propia versión en un ping normal. Si el servidor es más nuevo y está soportado, el cliente entra con esa versión y ViaBackwards traduce los paquetes dentro del cliente.
2. **Descriptor de contenido:** `tools/gen_content.py` compara los reports de datos oficiales de cada versión de la cadena (26.3 → 26.2 → 26.1.2 → 1.21.11). Saca qué es nuevo y qué IDs sustituiría cada paso de ViaBackwards. `gradlew fallbackTool` carga Via fuera del juego, guarda el parecido original de cada estado nuevo y comprueba el parche entero: lo nuevo llega al cliente con el ID correcto, los IDs vuelven bien del cliente al servidor y lo vanilla no cambia.
3. **Parche de las tablas:** en cada paso de ViaBackwards se envuelven las tablas de estados, bloques, items y sonidos para que los IDs nuevos pasen por una zona ampliada. El último paso los convierte en los IDs reales del registro del cliente. El tamaño de las tablas también crece, que es lo que Via usa para los bits de la paleta de los chunks.
4. **Contenido en el cliente:** los bloques, items y sonidos se registran con su nombre real `minecraft:`, al final de los registros, así que los IDs vanilla nunca cambian.

### Compilar

```bash
./gradlew build
```

Para regenerar el contenido de 26.3, primero saca los reports del generador de datos de los servidores oficiales en `tools/servers/reports-<versión>`. Después:

```bash
cd tools && python gen_content.py 26.3 26.2 26.1.2 1.21.11
```

```bash
./gradlew fallbackTool
```

---

## 📜 Licencia

[GPL-3.0-only](LICENSE). FNViaViewer incluye [ViaVersion](https://github.com/ViaVersion/ViaVersion) y [ViaBackwards](https://github.com/ViaVersion/ViaBackwards) (GPLv3), y su código de conexión está adaptado de [ViaFabricPlus](https://github.com/ViaVersion/ViaFabricPlus) (GPLv3). Gracias al equipo de ViaVersion por su trabajo.

Minecraft es una marca de Mojang Studios. Cada jugador baja los assets del juego de Mojang; este proyecto nunca los redistribuye.
