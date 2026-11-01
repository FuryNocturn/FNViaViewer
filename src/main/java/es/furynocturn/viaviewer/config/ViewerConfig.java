package es.furynocturn.viaviewer.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import es.furynocturn.viaviewer.content.MappingPatcher;
import es.furynocturn.viaviewer.via.ViaTranslator;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import net.fabricmc.loader.api.FabricLoader;

/**
 * config/fnviaviewer.json. enabled apaga la traduccion entera (cliente vanilla); blocks/items/sounds
 * solo deciden si ese contenido nuevo llega o sale como su sustituto, y se pueden cambiar en marcha.
 */
public final class ViewerConfig {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path PATH = FabricLoader.getInstance().getConfigDir().resolve("fnviaviewer.json");
    private static ViewerConfig instance;

    public volatile boolean enabled = true;
    public volatile boolean blocks = true;
    public volatile boolean items = true;
    public volatile boolean sounds = true;

    public static synchronized ViewerConfig get() {
        if (instance == null) {
            instance = load();
        }
        return instance;
    }

    private static ViewerConfig load() {
        if (Files.exists(PATH)) {
            try {
                final ViewerConfig config = GSON.fromJson(Files.readString(PATH), ViewerConfig.class);
                if (config != null) {
                    return config;
                }
            } catch (final IOException | JsonParseException e) {
                ViaTranslator.LOGGER.warn("Config ilegible, se usan los valores por defecto: {}", e.toString());
            }
        }
        return new ViewerConfig();
    }

    public void save() {
        try {
            Files.writeString(PATH, GSON.toJson(this));
        } catch (final IOException e) {
            ViaTranslator.LOGGER.warn("No se pudo guardar la config", e);
        }
    }

    public MappingPatcher.Toggles toggles() {
        return new MappingPatcher.Toggles(() -> this.blocks, () -> this.items, () -> this.sounds);
    }
}
