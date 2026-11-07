"""
Genera el descriptor de contenido nuevo (content/<cliente>/<version>.json; el build lo mete en el
jar de ese cliente como fnviaviewer/content/<version>.json)
a partir de los reports del generador de datos oficial de cada servidor vanilla.

Uso (desde FNViaViewer/tools):
    python gen_content.py 26.3 26.2 26.1.2 1.21.11
La primera version es la nueva, la ultima la nativa del cliente, y entre medias cada paso de
ViaBackwards en orden. Necesita servers/reports-<v>/reports/{blocks,registries}.json
(java -DbundlerMainClass=net.minecraft.data.Main -jar <v>.jar --reports --output reports-<v>).

Contenido:
- blocks/items/sounds/particles/block_entities: lo que existe (nid = ID en la version nueva; first = su primer estado) en la version nueva y no en la nativa, en orden de ID de la
  nueva. Su posicion en la lista es su "indice D": el cliente lo registra en ese orden.
- steps: por cada paso de Via (from -> to), que IDs de 'from' no existen en 'to' y su indice D.
  Los estados van en rangos [primerEstadoFrom, primerIndiceD, numEstados]; block_ids son IDs de
  registro de bloque (los usan las tags) con el indice del bloque en 'blocks'.
- fallback (lo anade FallbackTool, no este script): sustituto nativo de cada estado segun Via.
"""
import json
import sys
from pathlib import Path

ROOT = Path(__file__).resolve().parent
# Nombres que no se registran (ViaBackwards sigue poniendo su sustituto). Solo cuentan en los clientes
# que no los tienen: a partir de su version son vanilla y no salen en el descriptor.
# - Cofres de cobre y estatuas del golem de cobre (1.21.9): los dibuja solo su entidad de bloque (el
#   modelo de 26.3 es de particulas) y aqui no hay renderer para ellas; el sustituto (cofre...) se ve.
_COPPER = ["", "exposed_", "weathered_", "oxidized_", "waxed_", "waxed_exposed_", "waxed_weathered_", "waxed_oxidized_"]
_BLOCK_ENTITY_ONLY = [f"minecraft:{c}{b}" for c in _COPPER for b in ("copper_chest", "copper_golem_statue")]
EXCLUDED = {
    "blocks": set(_BLOCK_ENTITY_ONLY),
    "block_ids": set(_BLOCK_ENTITY_ONLY),
    "items": set(_BLOCK_ENTITY_ONLY),
    # La estanteria (1.21.9) sin su entidad de bloque: modelo fijo, sin los objetos de encima
    "block_entities": {"minecraft:shelf", "minecraft:copper_golem_statue"},
}
OUT_ROOT = ROOT.parent / "content"


def load(version):
    reports = ROOT / "servers" / f"reports-{version}" / "reports"
    with open(reports / "blocks.json", encoding="utf-8") as f:
        blocks = json.load(f)
    with open(reports / "registries.json", encoding="utf-8") as f:
        registries = json.load(f)

    def registry(name):
        entries = registries[name]["entries"]
        return {key: value["protocol_id"] for key, value in entries.items()}

    return {"blocks": blocks, "block_ids": registry("minecraft:block"), "items": registry("minecraft:item"),
            "sounds": registry("minecraft:sound_event"), "particles": registry("minecraft:particle_type"),
            "block_entities": registry("minecraft:block_entity_type"), "reports": reports}


def state_key(block, state):
    return block, tuple(sorted(state.get("properties", {}).items()))


def main(chain):
    data = {version: load(version) for version in chain}
    newest, native = data[chain[0]], data[chain[-1]]

    # Bloques nuevos (indice D por estado) en orden de ID de la version nueva
    new_blocks = sorted(
        (name for name in newest["blocks"] if name not in native["blocks"] and name not in EXCLUDED["blocks"]),
        key=lambda name: newest["blocks"][name]["states"][0]["id"],
    )
    block_index = {}
    blocks_out = []
    for name in new_blocks:
        block = newest["blocks"][name]
        states = sorted(block["states"], key=lambda s: s["id"])
        ids = [s["id"] for s in states]
        if ids != list(range(ids[0], ids[0] + len(ids))):
            raise SystemExit(f"{name}: estados no contiguos")
        for state in states:
            block_index[state_key(name, state)] = len(block_index)
        entry = {"id": name, "nid": newest["block_ids"][name], "first": ids[0], "states": len(states)}
        if block.get("properties"):
            entry["properties"] = block["properties"]
        default = next((s for s in states if s.get("default")), None)
        if default and default.get("properties"):
            entry["default"] = default["properties"]
        blocks_out.append(entry)

    def new_entries(kind):
        names = [n for n in newest[kind] if n not in native[kind] and n not in EXCLUDED.get(kind, ())]
        return sorted(names, key=lambda n: newest[kind][n])

    item_names = new_entries("items")
    sounds_out = new_entries("sounds")
    item_index = {n: i for i, n in enumerate(item_names)}
    block_id_index = {entry["id"]: i for i, entry in enumerate(blocks_out)}
    items_out = []
    for name in item_names:
        path = newest["reports"] / "minecraft" / "components" / "item" / (name.split(":")[1] + ".json")
        with open(path, encoding="utf-8") as f:
            components = json.load(f)["components"]
        entry = {"id": name, "nid": newest["items"][name], "stack": components.get("minecraft:max_stack_size", 64)}
        if components.get("minecraft:rarity", "common") != "common":
            entry["rarity"] = components["minecraft:rarity"]
        items_out.append(entry)
    sound_index = {n: i for i, n in enumerate(sounds_out)}
    particles_out = new_entries("particles")
    block_entities_out = new_entries("block_entities")

    steps = []
    for source, target in zip(chain, chain[1:]):
        src, dst = data[source], data[target]
        ranges = []
        for name, block in src["blocks"].items():
            if name in dst["blocks"] or name in EXCLUDED["blocks"]:
                continue
            for state in block["states"]:
                key = state_key(name, state)
                if key not in block_index:
                    raise SystemExit(f"{source}: {name} {key[1]} no esta en {chain[0]} con esas propiedades")
                sid, did = state["id"], block_index[key]
                last = ranges[-1] if ranges else None
                if last and last[0] + last[2] == sid and last[1] + last[2] == did:
                    last[2] += 1
                else:
                    ranges.append([sid, did, 1])
        ranges.sort()

        def registry_step(kind, index):
            out = []
            for name, rid in src[kind].items():
                if name in dst[kind] or name in EXCLUDED.get(kind, ()):
                    continue
                if name not in index:
                    raise SystemExit(f"{source}: {kind} {name} no esta en {chain[0]}")
                out.append([rid, index[name]])
            return sorted(out)

        steps.append({
            "from": source,
            "to": target,
            "blocks": ranges,
            "block_ids": registry_step("block_ids", block_id_index),
            "items": registry_step("items", item_index),
            "sounds": registry_step("sounds", sound_index),
            "particles": registry_step("particles", {n: i for i, n in enumerate(particles_out)}),
            "block_entities": registry_step("block_entities", {n: i for i, n in enumerate(block_entities_out)}),
        })

    out = {
        "version": chain[0],
        "native": chain[-1],
        "blocks": blocks_out,
        "items": items_out,
        "sounds": [{"id": name, "nid": newest["sounds"][name]} for name in sounds_out],
        "particles": [{"id": name, "nid": newest["particles"][name]} for name in particles_out],
        "block_entities": [{"id": name, "nid": newest["block_entities"][name]} for name in block_entities_out],
        "steps": steps,
    }
    OUT = OUT_ROOT / chain[-1]
    OUT.mkdir(parents=True, exist_ok=True)
    path = OUT / f"{chain[0]}.json"
    with open(path, "w", encoding="utf-8", newline="\n") as f:
        json.dump(out, f, separators=(",", ":"))
    print(f"{path}: {len(blocks_out)} bloques ({len(block_index)} estados), "
          f"{len(items_out)} items, {len(sounds_out)} sonidos, {len(particles_out)} particulas, "
          f"{len(block_entities_out)} entidades de bloque")
    for step in steps:
        states = sum(r[2] for r in step["blocks"])
        print(f"  {step['from']} -> {step['to']}: {states} estados, {len(step['block_ids'])} bloques, "
              f"{len(step['items'])} items, {len(step['sounds'])} sonidos, {len(step['particles'])} particulas, "
              f"{len(step['block_entities'])} entidades de bloque")


if __name__ == "__main__":
    if len(sys.argv) < 3:
        raise SystemExit(__doc__)
    main(sys.argv[1:])
