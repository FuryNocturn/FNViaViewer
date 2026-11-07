package es.furynocturn.viaviewer.particle;

import net.minecraft.client.multiplayer.ClientLevel;
//#if MC >= 12109
import net.minecraft.client.particle.SingleQuadParticle;
//#else
//$$ import net.minecraft.client.particle.ParticleRenderType;
//$$ import net.minecraft.client.particle.TextureSheetParticle;
//#endif
import net.minecraft.client.renderer.texture.TextureAtlasSprite;

/**
 * Particula de un sprite, opaca. En 1.21.9 TextureSheetParticle paso a ser SingleQuadParticle (con el
 * sprite en el constructor y la capa en getLayer): esta clase deja una sola forma para las dos.
 */
abstract class QuadParticle extends
    //#if MC >= 12109
    SingleQuadParticle
    //#else
    //$$ TextureSheetParticle
    //#endif
{

    QuadParticle(final ClientLevel level, final double x, final double y, final double z, final TextureAtlasSprite sprite) {
        //#if MC >= 12109
        super(level, x, y, z, sprite);
        //#else
        //$$ super(level, x, y, z);
        //$$ this.setSprite(sprite);
        //#endif
    }

    QuadParticle(final ClientLevel level, final double x, final double y, final double z, final double xa, final double ya, final double za,
                 final TextureAtlasSprite sprite) {
        //#if MC >= 12109
        super(level, x, y, z, xa, ya, za, sprite);
        //#else
        //$$ super(level, x, y, z, xa, ya, za);
        //$$ this.setSprite(sprite);
        //#endif
    }

    //#if MC >= 12109
    @Override
    protected Layer getLayer() {
        return Layer.OPAQUE;
    }
    //#else
    //$$ @Override
    //$$ public ParticleRenderType getRenderType() {
    //$$     return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
    //$$ }
    //#endif
}
