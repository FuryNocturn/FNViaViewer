package es.furynocturn.viaviewer.assets;

import es.furynocturn.viaviewer.FNViaViewer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.PackLocationInfo;
import net.minecraft.server.packs.PackSelectionConfig;
import net.minecraft.server.packs.PathPackResources;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackCompatibility;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.server.packs.repository.RepositorySource;
import net.minecraft.world.flag.FeatureFlagSet;

/**
 * Un resource pack obligatorio por cada version con cache bajada. Va abajo del todo: solo trae
 * ficheros que la nativa no tiene (y claves de idioma/sonidos, que se mezclan), y asi los packs
 * del jugador pueden sobrescribirlo.
 */
public final class CachePackSource implements RepositorySource {

    @Override
    public void loadPacks(final Consumer<Pack> consumer) {
        for (final String version : FNViaViewer.CONTENT_VERSIONS) {
            final Path dir = AssetCache.packDir(version);
            if (!Files.isDirectory(dir.resolve("assets"))) {
                continue;
            }
            final PackLocationInfo location = new PackLocationInfo(FNViaViewer.MOD_ID + "/" + version,
                Component.translatable("fnviaviewer.pack.title", version), PackSource.BUILT_IN, Optional.empty());
            final Pack.Metadata metadata = new Pack.Metadata(Component.translatable("fnviaviewer.pack.description", version),
                PackCompatibility.COMPATIBLE, FeatureFlagSet.of(), List.of());
            consumer.accept(new Pack(location, new PathPackResources.PathResourcesSupplier(dir), metadata,
                new PackSelectionConfig(true, Pack.Position.BOTTOM, false)));
        }
    }
}
