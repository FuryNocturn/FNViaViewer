"""Genera src/main/resources/assets/fnviaviewer/icon.png (pixel art propio, sin assets de Mojang).
Ojo con el iris mitad gris piedra (lo antiguo) y mitad cinabrio (lo nuevo). Solo biblioteca estandar."""
import struct
import zlib
from pathlib import Path

SIZE, SCALE = 32, 8
OUT = Path(__file__).resolve().parent.parent / "src" / "main" / "resources" / "assets" / "fnviaviewer" / "icon.png"

BG, BORDER = (27, 31, 46, 255), (52, 60, 88, 255)
WHITE, LID = (232, 232, 240, 255), (160, 165, 190, 255)
OLD, OLD_DARK = (138, 138, 138, 255), (100, 100, 100, 255)
NEW, NEW_DARK = (217, 83, 44, 255), (160, 52, 28, 255)
PUPIL, SHINE = (12, 12, 18, 255), (255, 255, 255, 255)
CLEAR = (0, 0, 0, 0)


def pixel(x, y):
    # cuadrado redondeado
    corner = 4
    cx, cy = min(x, SIZE - 1 - x), min(y, SIZE - 1 - y)
    if cx < corner and cy < corner and (corner - cx - 0.5) ** 2 + (corner - cy - 0.5) ** 2 > corner ** 2:
        return CLEAR
    edge = min(cx, cy) == 0 or (cx < corner and cy < corner and (corner - cx - 0.5) ** 2 + (corner - cy - 0.5) ** 2 > (corner - 1) ** 2)
    color = BORDER if edge else BG

    # ojo almendrado centrado en (15.5, 15.5)
    dx, dy = x - 15.5, y - 15.5
    half_height = 7.5 * (1 - (dx / 12.5) ** 2)
    if half_height > 0 and abs(dy) <= half_height:
        color = LID if abs(dy) > half_height - 1.2 else WHITE
        r = (dx * dx + dy * dy) ** 0.5
        if r <= 6.5:
            dark = r > 5.4
            color = (OLD_DARK if dark else OLD) if dx < 0 else (NEW_DARK if dark else NEW)
        if r <= 2.6:
            color = PUPIL
        if round(dx) == 2 and round(dy) == -3:
            color = SHINE
    return color


def main():
    rows = []
    for y in range(SIZE * SCALE):
        row = bytearray([0])
        for x in range(SIZE * SCALE):
            row.extend(pixel(x // SCALE, y // SCALE))
        rows.append(bytes(row))
    raw = b"".join(rows)

    def chunk(kind, data):
        return struct.pack(">I", len(data)) + kind + data + struct.pack(">I", zlib.crc32(kind + data) & 0xFFFFFFFF)

    width = SIZE * SCALE
    png = b"\x89PNG\r\n\x1a\n" + chunk(b"IHDR", struct.pack(">IIBBBBB", width, width, 8, 6, 0, 0, 0)) \
        + chunk(b"IDAT", zlib.compress(raw, 9)) + chunk(b"IEND", b"")
    OUT.parent.mkdir(parents=True, exist_ok=True)
    OUT.write_bytes(png)
    print(OUT, len(png), "bytes")


if __name__ == "__main__":
    main()
