package es.furynocturn.viaviewer.via;

import com.google.gson.JsonParser;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

/**
 * Ping de estado minimo para saber el protocolo REAL del servidor. Se manda con protocolo -1:
 * ViaVersion en el servidor solo reescribe el protocolo de la respuesta al del cliente si sabe
 * traducirlo, asi que con -1 no lo toca. Basado en ProtocolVersionDetector de ViaFabricPlus (GPLv3).
 * shortcut: detras de Velocity sale la version maxima del proxy, no la del backend; si da problemas,
 * probar con el protocolo nativo cuando el ping normal ya sale compatible.
 */
public final class ServerProtocolDetector {

    private static final int TIMEOUT_MS = 3_000;

    private ServerProtocolDetector() {
    }

    public static int detect(final InetSocketAddress address) throws IOException {
        try (Socket socket = new Socket()) {
            socket.connect(address, TIMEOUT_MS);
            socket.setSoTimeout(TIMEOUT_MS);
            socket.setTcpNoDelay(true);
            final DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            final DataInputStream in = new DataInputStream(socket.getInputStream());

            final ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            final DataOutputStream handshake = new DataOutputStream(bytes);
            handshake.writeByte(0); // handshake
            writeVarInt(handshake, -1);
            writeString(handshake, address.getHostString());
            handshake.writeShort(address.getPort());
            writeVarInt(handshake, 1); // intent: status
            writeVarInt(out, bytes.size());
            out.write(bytes.toByteArray());
            out.writeByte(1); // status request: longitud 1, id 0
            out.writeByte(0);

            if (readVarInt(in) <= 0 || readVarInt(in) != 0) {
                throw new IOException("Respuesta de estado invalida");
            }
            final int length = readVarInt(in);
            if (length < 0 || length > Short.MAX_VALUE * 4) {
                throw new IOException("Respuesta de estado demasiado larga");
            }
            final byte[] json = new byte[length];
            in.readFully(json);
            return JsonParser.parseString(new String(json, StandardCharsets.UTF_8))
                .getAsJsonObject().getAsJsonObject("version").get("protocol").getAsInt();
        }
    }

    private static int readVarInt(final DataInputStream in) throws IOException {
        int value = 0;
        int position = 0;
        byte b;
        do {
            b = in.readByte();
            value |= (b & 0x7F) << position++ * 7;
            if (position > 5) {
                throw new IOException("VarInt demasiado grande");
            }
        } while ((b & 0x80) != 0);
        return value;
    }

    private static void writeVarInt(final DataOutputStream out, int value) throws IOException {
        while ((value & -128) != 0) {
            out.writeByte(value & 0x7F | 0x80);
            value >>>= 7;
        }
        out.writeByte(value);
    }

    private static void writeString(final DataOutputStream out, final String value) throws IOException {
        final byte[] bytes = value.getBytes(StandardCharsets.UTF_8);
        writeVarInt(out, bytes.length);
        out.write(bytes);
    }
}
