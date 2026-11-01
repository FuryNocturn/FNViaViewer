package es.furynocturn.viaviewer.mixin;

import es.furynocturn.viaviewer.access.IConnection;
import es.furynocturn.viaviewer.via.ViaTranslator;
import io.netty.channel.Channel;
import net.minecraft.network.Connection;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** El ChannelInitializer de las conexiones a servidores remotos. */
@Mixin(targets = "net.minecraft.network.Connection$1")
public abstract class MixinConnection_1 {

    @Final
    @Shadow
    Connection val$connection;

    @Inject(method = "initChannel", at = @At("RETURN"))
    private void fnviaviewer$injectVia(Channel channel, CallbackInfo ci) {
        final var target = ((IConnection) this.val$connection).fnviaviewer$getTarget();
        if (target != null) {
            ViaTranslator.inject(channel, target);
        }
    }
}
