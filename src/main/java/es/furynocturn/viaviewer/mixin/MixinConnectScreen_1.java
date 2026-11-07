package es.furynocturn.viaviewer.mixin;

import es.furynocturn.viaviewer.access.IConnection;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import es.furynocturn.viaviewer.via.ServerProtocolDetector;
import es.furynocturn.viaviewer.via.ViaTranslator;
import io.netty.channel.ChannelFuture;
import java.io.IOException;
import java.net.InetSocketAddress;
import net.minecraft.network.Connection;
//#if MC >= 12111
import net.minecraft.server.network.EventLoopGroupHolder;
//#endif
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

/** Hilo de conexion de ConnectScreen: decide si esta conexion pasa por Via. */
@Mixin(targets = "net.minecraft.client.gui.screens.ConnectScreen$1")
public abstract class MixinConnectScreen_1 {

    //#if MC >= 12111
    @WrapOperation(method = "run", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/Connection;connect(Ljava/net/InetSocketAddress;Lnet/minecraft/server/network/EventLoopGroupHolder;Lnet/minecraft/network/Connection;)Lio/netty/channel/ChannelFuture;"))
    private ChannelFuture fnviaviewer$chooseVersion(InetSocketAddress address, EventLoopGroupHolder group, Connection connection, Operation<ChannelFuture> original) {
        this.fnviaviewer$chooseVersion(address, connection);
        return original.call(address, group, connection);
    }
    //#else
    //$$ @WrapOperation(method = "run", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/Connection;connect(Ljava/net/InetSocketAddress;ZLnet/minecraft/network/Connection;)Lio/netty/channel/ChannelFuture;"))
    //$$ private ChannelFuture fnviaviewer$chooseVersion(InetSocketAddress address, boolean nativeTransport, Connection connection, Operation<ChannelFuture> original) {
    //$$     this.fnviaviewer$chooseVersion(address, connection);
    //$$     return original.call(address, nativeTransport, connection);
    //$$ }
    //#endif

    @Unique
    private void fnviaviewer$chooseVersion(final InetSocketAddress address, final Connection connection) {
        ((IConnection) connection).fnviaviewer$setTarget(ViaTranslator.targetFor(this.fnviaviewer$serverProtocol(address)));
    }

    @Unique
    private int fnviaviewer$serverProtocol(final InetSocketAddress address) {
        // No vale el ping de la lista: con Via en el servidor dice que es de nuestra version
        try {
            return ServerProtocolDetector.detect(address);
        } catch (final IOException | RuntimeException e) {
            // Sin respuesta valida: se entra en vanilla y Minecraft muestra su propio error
            ViaTranslator.LOGGER.warn("No se pudo detectar la version de {}: {}", address, e.toString());
            return ViaTranslator.NATIVE_VERSION.getOriginalVersion();
        }
    }
}
