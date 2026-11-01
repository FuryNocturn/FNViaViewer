package es.furynocturn.viaviewer.mixin;

import es.furynocturn.viaviewer.access.IConnection;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import com.viaversion.viaversion.platform.ViaChannelInitializer;
import io.netty.channel.Channel;
import net.minecraft.network.Connection;
import net.minecraft.network.HandlerNames;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Connection.class)
public abstract class MixinConnection implements IConnection {

    @Shadow
    public Channel channel;

    @Unique
    private ProtocolVersion fnviaviewer$target;

    @Inject(method = "setupCompression", at = @At("RETURN"))
    private void fnviaviewer$reorderCompression(int threshold, boolean validate, CallbackInfo ci) {
        // Via tiene que ir entre la compresion y los codecs de Minecraft
        if (this.fnviaviewer$target != null) {
            ViaChannelInitializer.reorderPipeline(this.channel.pipeline(), HandlerNames.COMPRESS, HandlerNames.DECOMPRESS);
        }
    }

    @Override
    public ProtocolVersion fnviaviewer$getTarget() {
        return this.fnviaviewer$target;
    }

    @Override
    public void fnviaviewer$setTarget(final ProtocolVersion target) {
        this.fnviaviewer$target = target;
    }
}
