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
  <img src="https://img.shields.io/badge/clientes-1.21.7_a_26.2-blue" alt="Clientes 1.21.7 a 26.2">
  <img src="https://img.shields.io/badge/servidores-26.3-brightgreen" alt="Servidores 26.3">
  <img src="https://img.shields.io/badge/loader-Fabric-dbd0b4" alt="Fabric">
  <img src="https://img.shields.io/badge/licencia-GPL--3.0-red" alt="Licencia GPL-3.0">
  <img src="https://img.shields.io/badge/versión-0.4.0-orange" alt="Versión">
</p>

---

**FNViaViewer** es un mod de Fabric para clientes de Minecraft **1.21.7 a 26.2** que te deja entrar en servidores **26.3** y ver sus **bloques, items, sonidos y mobs nuevos tal como son**, en vez de los parecidos antiguos que salen normalmente.

| Tu versión | Jar | Qué te añade |
| :--- | :--- | :--- |
| 26.2 | `fnviaviewer-…+26.2.jar` | Lo de 26.3 |
| 26.1, 26.1.1, 26.1.2 | `fnviaviewer-…+26.1.jar` | Lo de 26.2 y 26.3 (sulfur cube, azufre, cinabrio…) |
| 1.21.11 | `fnviaviewer-…+1.21.11.jar` | Lo de 26.1 a 26.3, más las crías nuevas |
| 1.21.9, 1.21.10 | `fnviaviewer-…+1.21.10.jar` | Lo anterior, más los items y mobs de 1.21.11 (lanzas, nautilus, camel husk, parched) |
| 1.21.7, 1.21.8 | `fnviaviewer-…+1.21.8.jar` | Lo anterior, más lo de 1.21.9 (cobre, estanterías, golem de cobre) |

¿No quieres buscar el tuyo? `fnviaviewer-…-universal.jar` lleva los cinco dentro y Fabric carga solo el de tu versión.

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
* **🪧 Bloques que funcionan:** los carteles de álamo muestran su texto, la estantería enseña sus objetos y en la cama de paja se duerme con la postura correcta.
* **✨ Partículas y géiseres:** hojas de álamo que caen, y géiseres con burbujas, gas y erupciones que te lanzan hacia arriba, como en 26.3.
* **⛵ Barcos de álamo y cojines** con su aspecto real.
* **🟨 Sulfur cube:** con su capa transparente, su núcleo, su tamaño de cría y el bloque que lleva tragado dentro. Se hincha y parpadea al encenderse, y suelta sus pegotes al botar.
* **🐣 Crías de 26.x:** unas 40 crías de animales y monstruos (vaca, cerdo, lobo, gato, aldeano, zombi…) con sus modelos y texturas nuevos (hasta 1.21.11; desde 26.1 ya vienen de serie).
* **🐚 Mobs que tu versión no tiene** con su forma y animaciones reales: nautilus, zombie nautilus, camel husk, parched y golem de cobre, según la versión.
* **🛡️ No toca nada más:** en servidores de tu misma versión el juego es exactamente vanilla.

### 📦 Contenido incluido

| Cliente | Bloques (estados) | Items | Sonidos | Partículas |
| :--- | ---: | ---: | ---: | ---: |
| 26.2 | 90 (3.357) | 121 | 23 | 3 |
| 26.1.x | 118 (5.850) | 152 | 89 | 11 |
| 1.21.11 | 120 (5.852) | 153 | 153 | 13 |
| 1.21.9–1.21.10 | 120 (5.852) | 170 | 220 | 13 |
| 1.21.7–1.21.8 | 166 (7.135) | 227 | 263 | 14 |

Entre ellos: todo el juego de madera de álamo, los bloques de construcción de cinabrio y azufre, losas y escaleras de lana y hormigón de todos los colores, el diente de león dorado, el arbusto rojo, el políporo, la cama de paja, los cojines y los nuevos mapas de exploración. En las versiones más viejas, también las lanzas y la armadura de nautilus (1.21.11), y las antorchas, cadenas, barrotes, faroles y pararrayos de cobre y las estanterías (1.21.9). Más las entidades: barcos de álamo, cojines, sulfur cube y los mobs de arriba.

---

## 🎨 Las texturas y los sonidos vienen de Mojang

El mod **no lleva ningún asset de Mojang**. Al arrancar por primera vez baja de los servidores oficiales de Mojang el `client.jar` y el índice de assets de 26.3, comprueba el SHA-1 de cada fichero y se queda solo con lo que tu versión no tiene: texturas, modelos y los sonidos nuevos. En las versiones antiguas convierte lo que no entenderían (cubos girados más de 45°, contextos de item nuevos, colores de adorno de armadura). Los mobs que tu versión no tiene se dibujan con la geometría y las animaciones de los [ejemplos oficiales de Bedrock](https://github.com/Mojang/bedrock-samples) de Mojang, que se bajan igual (fijados a una versión y con su SHA-1). Todo se guarda en `.minecraft/fnviaviewer/cache/`. Lo único de 26.3 que va dentro del mod son las medidas de los modelos de las crías y del sulfur cube (cubos y pivotes), copiadas en su código.

Ese contenido se carga como un resource pack interno colocado por debajo de los tuyos, así que tus resource packs pueden sobrescribirlo. Si la primera vez no tienes internet, te sale un aviso y los bloques nuevos se ven como sus parecidos antiguos hasta que la descarga funcione.

---

## ⚙️ Configuración

Con [Mod Menu](https://modrinth.com/mod/modmenu) instalado tienes una pantalla de opciones:

| Opción | Qué hace |
| :--- | :--- |
| **Entrar en servidores más nuevos** | Activa o desactiva la traducción. Desactivada, el juego es vanilla. |
| **Bloques / items / sonidos / partículas / entidades / crías nuevos** | Muestra cada tipo de contenido nuevo, o lo cambia por su parecido antiguo. |
| **Volver a bajar los assets** | Borra la cache y la vuelve a bajar de Mojang. |

La configuración se guarda en `config/fnviaviewer.json`.

---

## 🧩 Compatibilidad

| Mod | Estado |
| :--- | :--- |
| **Sodium** | ✅ Probado, funciona. |
| **Xaero's Minimap / World Map** | ✅ Probado, los bloques nuevos tienen su color en el mapa. |
| **Iris** | 🟡 Soportado, aún poco probado. Los shaderpacks hechos para tu versión no conocen los bloques nuevos, así que cada uno usa el ID de shader de su parecido (hojas que ondean, bloques que brillan, agua). Si un pack ya nombra un bloque nuevo, se respeta su ajuste. |
| **JourneyMap** | 🟡 Debería funcionar (lee las texturas y los colores de mapa), aún sin probar. Se agradecen informes. |
| **ViaFabricPlus / ViaFabric** | ❌ No compatibles: también meten traducción de protocolo, así que usa uno u otro. |

---

## ⚠️ Limitaciones conocidas (0.4.0)

* Las crías usan la geometría y las texturas de 26.x, pero la **animación** es la de tu versión: en las que cambiaron de partes (lobo, abeja, conejo, burro, ajolote, gallina, strider, hoglin, piglin, tortuga) algunas partes no se mueven igual que en 26.x.
* La armadura de las crías zombi sigue con su forma antigua.
* Los efectos del géiser (burbujas, gas, erupción y empuje) están **reprogramados** para parecerse a los de 26.3: pueden no ser idénticos.
* Barcos de álamo, cojines y sulfur cubes solo cambian de aspecto: en la red y en la física siguen siendo su sustituto (barco de abedul, bloque que cae y slime). El empuje y el rebote del sulfur cube los calcula el servidor, así que se ven como en 26.3.
* Mobs nuevos en clientes viejos: sin la silla ni la armadura del nautilus, sin objetos en la mano del golem de cobre y sin sus animaciones con cofres.
* 1.21.7–1.21.8: las estanterías no enseñan objetos, y los cofres de cobre y las estatuas del golem de cobre se ven como su parecido (un cofre…), porque esa versión no tiene con qué dibujarlos.
* Por ahora solo clientes **1.21.7 a 26.2** y servidores **26.3**. Está previsto añadir versiones más antiguas.

---

## 📋 Requisitos

* Minecraft **1.21.7 a 26.2** (el jar de tu versión o el universal), Fabric Loader **0.19+**, [Fabric API](https://modrinth.com/mod/fabric-api)
* Java 21 (1.21.x) o Java 25 (26.x): lo mismo que ya pide tu versión de Minecraft
* Internet la primera vez que arranca, para bajar los assets de 26.3 de Mojang
* Opcional: [Mod Menu](https://modrinth.com/mod/modmenu), para la pantalla de opciones

---

## 🛠️ Cómo funciona (para desarrolladores)

1. **Conexión:** al conectar, el mod hace un ping al servidor con protocolo `-1` para saber su versión real, porque un servidor con ViaVersion responde con tu propia versión en un ping normal. Si el servidor es más nuevo y está soportado, el cliente entra con esa versión y ViaBackwards traduce los paquetes dentro del cliente.
2. **Descriptor de contenido:** `tools/gen_content.py` compara los reports de datos oficiales de cada versión de la cadena (26.3 → 26.2 → 26.1.2 → 1.21.11 → …, hasta la del cliente). Hay uno por cliente en `content/<versión>/`. Saca qué es nuevo y qué IDs sustituiría cada paso de ViaBackwards. `gradlew fallbackTool` carga Via fuera del juego, guarda el parecido original de cada estado nuevo y comprueba el parche entero: lo nuevo llega al cliente con el ID correcto, los IDs vuelven bien del cliente al servidor y lo vanilla no cambia.
3. **Parche de las tablas:** en cada paso de ViaBackwards se envuelven las tablas de estados, bloques, items y sonidos para que los IDs nuevos pasen por una zona ampliada. El último paso los convierte en los IDs reales del registro del cliente. El tamaño de las tablas también crece, que es lo que Via usa para los bits de la paleta de los chunks.
4. **Contenido en el cliente:** los bloques, items y sonidos se registran con su nombre real `minecraft:`, al final de los registros, así que los IDs vanilla nunca cambian.

### Compilar

Un solo build para todas las versiones (preprocesador de Essential, como EMF y ETF): el código de `src/` está escrito para 1.21.11 y se convierte para las demás con comentarios `//#if MC >= …`. Cada versión tiene su carpeta en `versions/`. Hace falta Java 25.

```bash
./gradlew build
```

Para regenerar el contenido, primero saca los reports del generador de datos de los servidores oficiales en `tools/servers/reports-<versión>`. Después, para cada cliente (la última versión de la lista):

```bash
cd tools && python gen_content.py 26.3 26.2 26.1.2 1.21.11 1.21.10 1.21.8
```

```bash
./gradlew fallbackTool
```

---

## 📜 Licencia

[GPL-3.0-only](LICENSE). FNViaViewer incluye [ViaVersion](https://github.com/ViaVersion/ViaVersion) y [ViaBackwards](https://github.com/ViaVersion/ViaBackwards) (GPLv3), y su código de conexión está adaptado de [ViaFabricPlus](https://github.com/ViaVersion/ViaFabricPlus) (GPLv3). Gracias al equipo de ViaVersion por su trabajo.

Minecraft es una marca de Mojang Studios. Cada jugador baja los assets del juego de Mojang; este proyecto nunca los redistribuye.
