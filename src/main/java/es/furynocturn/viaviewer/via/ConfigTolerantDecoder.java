package es.furynocturn.viaviewer.via;

import com.viaversion.viaversion.api.connection.ProtocolInfo;
import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.protocol.packet.State;
import com.viaversion.viaversion.exception.CancelCodecException;
import com.viaversion.viaversion.platform.ViaDecodeHandler;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import java.util.List;

/**
 * Al cambiar de servidor detras de un proxy (SuperCraft), el proxy manda START_CONFIGURATION y aun
 * reenvia paquetes de la partida. Via pasa a CONFIGURATION al ver START_CONFIGURATION y los traduciria
 * como paquetes de configuracion (UPDATE_TAGS / custom_payload ilegibles -> desconexion).
 * <ol>
 * <li>El protocolo obliga al servidor a esperar nuestro CONFIGURATION_ACKNOWLEDGED antes de mandar
 *     configuracion: lo que llega entre START_CONFIGURATION y ese ACK (servidor ya en CONFIGURATION,
 *     cliente aun en PLAY) son restos de la partida y se tiran sin traducir.</li>
 * <li>Red de seguridad para restos que lleguen justo despues del ACK: si la traduccion falla en
 *     CONFIGURATION, se descarta. Fuera de configuracion un error sigue desconectando.</li>
 * </ol>
 * Idea del punto 2: ViaFabricPlusDecoder (ViaFabricPlus, GPLv3).
 */
final class ConfigTolerantDecoder extends ViaDecodeHandler {

    private static final int MAX_LOGGED = 5;
    private int dropped;
    private int strayInPhase;

    ConfigTolerantDecoder(final UserConnection connection) {
        super(connection);
    }

    @Override
    protected void decode(final ChannelHandlerContext ctx, final ByteBuf buf, final List<Object> out) throws Exception {
        final ProtocolInfo info = this.connection.getProtocolInfo();
        if (info.getServerState() == State.CONFIGURATION && info.getClientState() == State.PLAY) {
            if (this.strayInPhase++ == 0) {
                ViaTranslator.LOGGER.info("Cambio de servidor: descartando paquetes de la partida hasta confirmar la configuracion");
            }
            return;
        }
        this.strayInPhase = 0;
        try {
            super.decode(ctx, buf, out);
        } catch (final Exception e) {
            if (e instanceof CancelCodecException || !this.inConfiguration()) {
                throw e;
            }
            if (++this.dropped <= MAX_LOGGED) {
                ViaTranslator.LOGGER.warn("Paquete descartado en configuracion (cambio de servidor): {}", e.getMessage());
            } else if (this.dropped == MAX_LOGGED + 1) {
                ViaTranslator.LOGGER.warn("Mas paquetes descartados en configuracion en esta conexion; no se registran mas");
            }
        }
    }

    private boolean inConfiguration() {
        final ProtocolInfo info = this.connection.getProtocolInfo();
        return info.getServerState() == State.CONFIGURATION || info.getClientState() == State.CONFIGURATION;
    }
}
