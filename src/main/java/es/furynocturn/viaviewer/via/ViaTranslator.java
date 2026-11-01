package es.furynocturn.viaviewer.via;

import com.viaversion.viabackwards.ViaBackwardsPlatformImpl;
import com.viaversion.viaversion.ViaManagerImpl;
import com.viaversion.viaversion.api.Via;
import es.furynocturn.viaviewer.config.ViewerConfig;
import es.furynocturn.viaviewer.content.ContentDescriptor;
import es.furynocturn.viaviewer.content.MappingPatcher;
import es.furynocturn.viaviewer.content.NewContent;
import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import com.viaversion.viaversion.commands.ViaCommandHandler;
import com.viaversion.viaversion.platform.NoopInjector;
import com.viaversion.viaversion.platform.ViaChannelInitializer;
import com.viaversion.viaversion.platform.ViaDecodeHandler;
import com.viaversion.viaversion.platform.ViaEncodeHandler;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.flow.FlowControlHandler;
import io.netty.util.AttributeKey;
import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;
import net.minecraft.SharedConstants;
import net.minecraft.network.Connection;
import net.minecraft.network.HandlerNames;
import net.minecraft.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Mete ViaVersion + ViaBackwards en el cliente: el cliente 1.21.11 habla con el servidor
 * como si fuera de la version del servidor y Via traduce los paquetes aqui mismo.
 * Basado en el ProtocolTranslator de ViaFabricPlus (GPLv3).
 */
public final class ViaTranslator {

    public static final Logger LOGGER = LoggerFactory.getLogger("FNViaViewer");
    public static final ProtocolVersion NATIVE_VERSION = ProtocolVersion.v1_21_11;
    /** Version del servidor a la que traduce Via en este canal. */
    public static final AttributeKey<ProtocolVersion> TARGET_VERSION = AttributeKey.newInstance("fnviaviewer-target");

    private static volatile boolean loaded;

    private ViaTranslator() {
    }

    public static void init(final Path dataFolder) {
        if (SharedConstants.getProtocolVersion() != NATIVE_VERSION.getOriginalVersion()) {
            throw new IllegalStateException("FNViaViewer solo funciona en " + NATIVE_VERSION);
        }
        CompletableFuture.runAsync(() -> {
            ViaManagerImpl.initAndLoad(
                new FNViaPlatform(dataFolder.toFile()),
                new NoopInjector(),
                new ViaCommandHandler(false),
                new FNPlatformLoader(),
                ViaBackwardsPlatformImpl::new
            );
            // Antes de la primera conexion: que el contenido nuevo pase hasta el cliente
            final ContentDescriptor descriptor = NewContent.descriptor();
            for (final ContentDescriptor.Step step : descriptor.steps()) {
                final var future = Via.getManager().getProtocolManager().getMappingLoaderFuture(MappingPatcher.protocol(step).getClass());
                if (future != null) {
                    future.join();
                }
            }
            try {
                MappingPatcher.patch(descriptor, NewContent.clientIds(), ViewerConfig.get().toggles());
            } catch (final ReflectiveOperationException e) {
                throw new IllegalStateException(e);
            }
            loaded = true;
            LOGGER.info("ViaVersion cargado: servidores hasta {}, {} estados nuevos de {}",
                ProtocolVersion.getProtocols().getLast(), descriptor.stateCount(), descriptor.version());
        }, Util.backgroundExecutor()).exceptionally(e -> {
            LOGGER.error("No se pudo cargar ViaVersion; el mod queda desactivado", e);
            return null;
        });
    }

    /** Version a la que traducir para un servidor con ese protocolo, o null si se entra en vanilla. */
    public static ProtocolVersion targetFor(final int serverProtocol) {
        if (!loaded || !ViewerConfig.get().enabled || serverProtocol == NATIVE_VERSION.getOriginalVersion()
            || !ProtocolVersion.isRegistered(serverProtocol)) {
            return null;
        }
        final ProtocolVersion version = ProtocolVersion.getProtocol(serverProtocol);
        return version.newerThan(NATIVE_VERSION) ? version : null;
    }

    public static void inject(final Channel channel, final ProtocolVersion target) {
        channel.attr(TARGET_VERSION).set(target);
        final UserConnection user = ViaChannelInitializer.createUserConnection(channel, true);

        final ChannelPipeline pipeline = channel.pipeline();
        pipeline.addBefore(HandlerNames.INBOUND_CONFIG, ViaDecodeHandler.NAME, new ConfigTolerantDecoder(user));
        pipeline.addBefore(HandlerNames.ENCODER, ViaEncodeHandler.NAME, new ViaEncodeHandler(user));
        // Via puede sacar varios paquetes de uno: que no se lean de golpe con autoRead apagado
        pipeline.addAfter(ViaDecodeHandler.NAME, "via-flow-control", new FlowControlHandler() {
            @Override
            public void read(final ChannelHandlerContext ctx) throws Exception {
                if (ctx.channel().config().isAutoRead()) {
                    super.read(ctx);
                }
            }
        });
        LOGGER.info("Traduciendo {} -> {}", NATIVE_VERSION, target);
    }
}
