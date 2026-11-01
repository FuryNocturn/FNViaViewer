package es.furynocturn.viaviewer;

import es.furynocturn.viaviewer.assets.AssetCache;
import es.furynocturn.viaviewer.content.NewBlock;
import es.furynocturn.viaviewer.content.NewContent;
import es.furynocturn.viaviewer.via.ViaTranslator;
import java.util.List;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.rendering.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.gui.components.toasts.SystemToast;
import net.minecraft.network.chat.Component;

public final class FNViaViewer implements ClientModInitializer {

    public static final String MOD_ID = "fnviaviewer";
    /** Versiones con descriptor en fnviaviewer/content/. shortcut: lista fija, leerla de los recursos al haber mas de una */
    public static final List<String> CONTENT_VERSIONS = List.of("26.3");

    @Override
    public void onInitializeClient() {
        // Capa de render (cutout para hojas, puertas...) y tinte: los del sustituto de cada estado
        for (final NewBlock block : NewContent.blocks()) {
            BlockRenderLayerMap.putBlock(block, ItemBlockRenderTypes.getChunkRenderType(block.layout().fallbacks()[0]));
            ColorProviderRegistry.BLOCK.register((state, level, pos, tint) ->
                Minecraft.getInstance().getBlockColors().getColor(block.layout().fallback(state), level, pos, tint), block);
        }

        ViaTranslator.init(FabricLoader.getInstance().getConfigDir().resolve(MOD_ID).resolve("via"));
        ClientLifecycleEvents.CLIENT_STARTED.register(mc -> updateAssets(mc, false));
    }

    /** Boton de la config: borra la cache y la baja otra vez. */
    public static void redownloadAssets() {
        updateAssets(Minecraft.getInstance(), true);
    }

    private static void updateAssets(final Minecraft mc, final boolean redownload) {
        final String language = mc.options.languageCode;
        final Thread thread = new Thread(() -> {
            boolean changed = redownload;
            for (final String version : CONTENT_VERSIONS) {
                try {
                    final AssetCache cache = new AssetCache(version, ViaTranslator.NATIVE_VERSION.getName());
                    if (redownload) {
                        cache.clear();
                    }
                    changed |= cache.update(language);
                    if (redownload) {
                        mc.execute(() -> SystemToast.add(mc.getToastManager(), SystemToast.SystemToastId.PERIODIC_NOTIFICATION,
                            Component.translatable("fnviaviewer.toast.download_done.title"),
                            Component.translatable("fnviaviewer.toast.download_done.body", version)));
                    }
                } catch (final Exception e) {
                    // Sin assets el mod sigue: los bloques nuevos salen con su sustituto
                    ViaTranslator.LOGGER.warn("No se pudieron bajar los assets de {}", version, e);
                    mc.execute(() -> SystemToast.add(mc.getToastManager(), SystemToast.SystemToastId.PERIODIC_NOTIFICATION,
                        Component.translatable("fnviaviewer.toast.download_failed.title"),
                        Component.translatable("fnviaviewer.toast.download_failed.body", version)));
                }
            }
            if (changed) {
                mc.execute(mc::reloadResourcePacks);
            }
        }, "FNViaViewer assets");
        thread.setDaemon(true);
        thread.start();
    }
}
