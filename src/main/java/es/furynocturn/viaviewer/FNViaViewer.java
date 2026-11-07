package es.furynocturn.viaviewer;

import es.furynocturn.viaviewer.assets.AssetCache;
import es.furynocturn.viaviewer.content.NewBlock;
import es.furynocturn.viaviewer.content.NewContent;
import es.furynocturn.viaviewer.via.ViaTranslator;
import java.util.List;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
//#if MC >= 260100
//$$ import net.fabricmc.fabric.api.client.rendering.v1.BlockColorRegistry;
//$$ import net.minecraft.client.color.block.BlockTintSource;
//$$ import net.minecraft.client.renderer.block.BlockAndTintGetter;
//$$ import net.minecraft.core.BlockPos;
//$$ import net.minecraft.world.level.block.state.BlockState;
//#else
import net.fabricmc.fabric.api.client.rendering.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.SignBlock;
//#endif
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.world.level.block.Block;
import es.furynocturn.viaviewer.particle.NewParticleProviders;
import net.minecraft.client.gui.components.toasts.SystemToast;
import net.minecraft.network.chat.Component;

public final class FNViaViewer implements ClientModInitializer {

    public static final String MOD_ID = "fnviaviewer";
    /** Versiones con descriptor en fnviaviewer/content/. shortcut: lista fija, leerla de los recursos al haber mas de una */
    public static final List<String> CONTENT_VERSIONS = List.of("26.3");

    @Override
    public void onInitializeClient() {
        // Capa de render (cutout para hojas, puertas...) y tinte: los del sustituto de cada estado
        for (int b = 0; b < NewContent.blocks().size(); b++) {
            final Block block = NewContent.blocks().get(b);
            final NewBlock.Layout layout = NewContent.layout(b);
            //#if MC >= 260100
            //$$ // Desde 26.1 la capa va en cada cara del modelo (la pone el propio modelo de 26.3)
            //$$ BlockColorRegistry.register(tints(layout), block);
            //#else
            // Carteles y camas: el sustituto se dibuja como entidad (capa solida), pero el modelo de 26.3 tiene huecos (cadenas)
            final boolean modelOnlyInNewVersion = block instanceof SignBlock || block instanceof BedBlock;
            BlockRenderLayerMap.putBlock(block, modelOnlyInNewVersion ? ChunkSectionLayer.CUTOUT
                : ItemBlockRenderTypes.getChunkRenderType(layout.fallbacks()[0]));
            ColorProviderRegistry.BLOCK.register((state, level, pos, tint) ->
                Minecraft.getInstance().getBlockColors().getColor(layout.fallback(state), level, pos, tint), block);
            //#endif
        }

        NewParticleProviders.register();
        es.furynocturn.viaviewer.entity.NewEntityRenderers.registerLayers();

        ViaTranslator.init(FabricLoader.getInstance().getConfigDir().resolve(MOD_ID).resolve("via"));
        ClientLifecycleEvents.CLIENT_STARTED.register(mc -> updateAssets(mc, false));
    }

    //#if MC >= 260100
    //$$ /** Tinte del sustituto de cada estado. shortcut: 2 capas de tinte fijas; ningun sustituto usa mas hoy, subirlo si alguno lo hace */
    //$$ private static List<BlockTintSource> tints(final NewBlock.Layout layout) {
    //$$     final List<BlockTintSource> tints = new java.util.ArrayList<>();
    //$$     for (int i = 0; i < 2; i++) {
    //$$         final int index = i;
    //$$         tints.add(new BlockTintSource() {
    //$$             private BlockTintSource fallback(final BlockState fallback) {
    //$$                 return Minecraft.getInstance().getBlockColors().getTintSource(fallback, index);
    //$$             }
    //$$
    //$$             @Override
    //$$             public int color(final BlockState state) {
    //$$                 final BlockState fallback = layout.fallback(state);
    //$$                 final BlockTintSource source = this.fallback(fallback);
    //$$                 return source == null ? -1 : source.color(fallback);
    //$$             }
    //$$
    //$$             @Override
    //$$             public int colorInWorld(final BlockState state, final BlockAndTintGetter level, final BlockPos pos) {
    //$$                 final BlockState fallback = layout.fallback(state);
    //$$                 final BlockTintSource source = this.fallback(fallback);
    //$$                 return source == null ? -1 : source.colorInWorld(fallback, level, pos);
    //$$             }
    //$$         });
    //$$     }
    //$$     return tints;
    //$$ }
    //#endif

    private static net.minecraft.client.gui.components.toasts.ToastManager toasts(final Minecraft mc) {
        //#if MC >= 260200
        //$$ return mc.gui.toastManager();
        //#else
        return mc.getToastManager();
        //#endif
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
                    final AssetCache cache = new AssetCache(version, net.minecraft.SharedConstants.getCurrentVersion().name());
                    if (redownload) {
                        cache.clear();
                    }
                    changed |= cache.update(language);
                    if (redownload) {
                        mc.execute(() -> SystemToast.add(toasts(mc), SystemToast.SystemToastId.PERIODIC_NOTIFICATION,
                            Component.translatable("fnviaviewer.toast.download_done.title"),
                            Component.translatable("fnviaviewer.toast.download_done.body", version)));
                    }
                } catch (final Exception e) {
                    // Sin assets el mod sigue: los bloques nuevos salen con su sustituto
                    ViaTranslator.LOGGER.warn("No se pudieron bajar los assets de {}", version, e);
                    mc.execute(() -> SystemToast.add(toasts(mc), SystemToast.SystemToastId.PERIODIC_NOTIFICATION,
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
