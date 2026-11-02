package es.furynocturn.viaviewer.assets;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import javax.imageio.ImageIO;
import net.minecraft.client.Minecraft;

/**
 * En 26.2+ la tabla del cartel es modelo de bloque y su textura (block/X_sign.png, 32x32) tiene otra
 * distribucion. La pantalla de editar cartel de 1.21.11 sigue pidiendo la textura de entidad
 * (entity/signs/X.png, 64x32): se genera en la cache recolocando cada cara. Solo en el equipo del jugador.
 */
public final class SignTextures {

    /** {xOrigen, yOrigen, ancho, alto, xDestino, yDestino}: caras del modelo de 26.3 -> cubos del modelo de entidad. */
    private static final int[][] FACES = {
        // tabla (24x12x2)
        {0, 0, 24, 2, 2, 0},     // arriba
        {0, 28, 24, 2, 26, 0},   // abajo
        {24, 16, 2, 12, 0, 2},   // canto oeste
        {0, 2, 24, 12, 2, 2},    // cara con el texto
        {24, 2, 2, 12, 26, 2},   // canto este
        {0, 16, 24, 12, 28, 2},  // cara de atras
        // palo (2x14x2); la punta de arriba no se ve en 26.3: se repite la de abajo
        {28, 30, 2, 2, 2, 14},
        {28, 30, 2, 2, 4, 14},
        {30, 16, 2, 14, 0, 16},
        {28, 0, 2, 14, 2, 16},
        {30, 0, 2, 14, 4, 16},
        {28, 16, 2, 14, 6, 16},
    };

    private static final java.util.Set<String> AVAILABLE = java.util.concurrent.ConcurrentHashMap.newKeySet();

    private SignTextures() {
    }

    /** Devuelve true si ha escrito alguna textura nueva. */
    static boolean generate(final Path packDir) throws IOException {
        final Path blocks = packDir.resolve("assets/minecraft/textures/block");
        if (!Files.isDirectory(blocks)) {
            return false;
        }
        boolean written = false;
        try (DirectoryStream<Path> signs = Files.newDirectoryStream(blocks, "*_sign.png")) {
            for (final Path source : signs) {
                final String name = source.getFileName().toString();
                if (name.endsWith("_hanging_sign.png")) {
                    continue; // los colgantes ya traen su textura de pantalla (gui/hanging_signs)
                }
                final String wood = name.substring(0, name.length() - "_sign.png".length());
                final Path target = packDir.resolve("assets/minecraft/textures/entity/signs/" + wood + ".png");
                if (Files.exists(target) || Minecraft.class.getResource("/assets/minecraft/textures/entity/signs/" + wood + ".png") != null) {
                    continue;
                }
                final BufferedImage in = ImageIO.read(source.toFile());
                if (in == null || in.getWidth() != 32 || in.getHeight() != 32) {
                    continue; // distribucion desconocida: se queda la de abedul de MixinSheets
                }
                final BufferedImage out = new BufferedImage(64, 32, BufferedImage.TYPE_INT_ARGB);
                for (final int[] f : FACES) {
                    out.setRGB(f[4], f[5], f[2], f[3], in.getRGB(f[0], f[1], f[2], f[3], null, 0, f[2]), 0, f[2]);
                }
                Files.createDirectories(target.getParent());
                ImageIO.write(out, "png", target.toFile());
                written = true;
            }
        }
        return written;
    }

    /** Si la cache ya tiene la textura de pantalla de esa madera (si no, se usa la de abedul). */
    public static boolean available(final String wood) {
        return AVAILABLE.contains(wood);
    }

    static void scan(final Path packDir) throws IOException {
        final Path signs = packDir.resolve("assets/minecraft/textures/entity/signs");
        AVAILABLE.clear();
        if (Files.isDirectory(signs)) {
            try (DirectoryStream<Path> files = Files.newDirectoryStream(signs, "*.png")) {
                files.forEach(file -> AVAILABLE.add(file.getFileName().toString().replace(".png", "")));
            }
        }
    }
}
