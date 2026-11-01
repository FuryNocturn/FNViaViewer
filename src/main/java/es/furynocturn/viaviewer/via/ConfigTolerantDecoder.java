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
 * Al cambiar de servidor detras de un proxy, el cliente pasa a CONFIGURATION mientras el proxy aun
 * reenvia paquetes de la partida. Vanilla los ignora; Via los intenta traducir como de configuracion
 * y desconecta (UPDATE_TAGS ilegible en SuperCraft). Aqui se descartan solo en ese estado; fuera de
 * el un error de traduccion sigue desconectando para no esconder fallos reales.
 * Idea de ViaFabricPlusDecoder (ViaFabricPlus, GPLv3).
 */
final class ConfigTolerantDecoder extends ViaDecodeHandler {

    private static final int MAX_LOGGED = 5;
    private int dropped;

    ConfigTolerantDecoder(final UserConnection connection) {
        super(connection);
    }

    @Override
    protected void decode(final ChannelHandlerContext ctx, final ByteBuf buf, final List<Object> out) throws Exception {
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
