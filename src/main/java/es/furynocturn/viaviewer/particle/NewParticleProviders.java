package es.furynocturn.viaviewer.particle;

import es.furynocturn.viaviewer.content.NewParticles;
import es.furynocturn.viaviewer.content.PotentSulfurBlockEntity;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.BaseAshSmokeParticle;
import net.minecraft.client.particle.FallingLeavesParticle;
import net.minecraft.client.particle.NoRenderParticle;
import net.minecraft.client.particle.SingleQuadParticle;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.Vec3;

/**
 * Como se ven las particulas nuevas. Los sprites salen de particles/*.json de la cache (26.3);
 * el movimiento esta reprogramado para parecerse al de la version nueva, con sus mismos valores.
 */
public final class NewParticleProviders {

    private NewParticleProviders() {
    }

    public static void register() {
        final ParticleFactoryRegistry registry = ParticleFactoryRegistry.getInstance();
        // Las hojas de alamo caen igual que las del roble palido
        for (final String color : new String[]{"red", "orange", "yellow"}) {
            registry.register(NewParticles.simple("minecraft:" + color + "_poplar_leaves"), FallingLeavesParticle.PaleOakProvider::new);
        }
        registry.register(NewParticles.simple("minecraft:sulfur_bubbles"), sprites -> (options, level, x, y, z, xa, ya, za, random) ->
            new SulfurBubble(level, x, y, z, xa, za, sprites.get(random)));
        registry.register(NewParticles.simple("minecraft:noxious_gas"), sprites -> (options, level, x, y, z, xa, ya, za, random) ->
            new NoxiousGas(level, x, y, z, xa, ya, za, sprites));
        registry.register(NewParticles.simple("minecraft:noxious_gas_cloud"), (options, level, x, y, z, xa, ya, za, random) ->
            new NoxiousGasCloud(level, x, y, z));
        registry.register(NewParticles.geyser("minecraft:geyser"), (options, level, x, y, z, xa, ya, za, random) ->
            new GeyserEruption(level, x, y, z, xa, ya, za, options.waterBlocks()));
        registry.register(NewParticles.geyser("minecraft:geyser_plume"), sprites -> (options, level, x, y, z, xa, ya, za, random) ->
            new GeyserPlume(level, x + (random.nextFloat() - 0.5F) * 0.2F, y + random.nextFloat(), z + (random.nextFloat() - 0.5F) * 0.2F,
                xa, ya, za, options.waterBlocks(), sprites));
        for (final String id : new String[]{"minecraft:geyser_base", "minecraft:geyser_poof"}) {
            registry.register(NewParticles.geyserBase(id), sprites -> (options, level, x, y, z, xa, ya, za, random) ->
                new GeyserBase(level, x + (random.nextFloat() - 0.5F) * 0.5F, y + (random.nextFloat() - 0.5F) * 0.5F + 0.2F,
                    z + (random.nextFloat() - 0.5F) * 0.5F, xa, ya, za, options.waterBlocks(), options.burstImpulseBase(), sprites));
        }
    }

    /** Burbuja que sube por el agua sobre el azufre y crece; desaparece al salir del agua o al pararse. */
    static final class SulfurBubble extends SingleQuadParticle {
        private final double yStart;
        private final double yEnd;
        private final float sizeStart;
        private double yPrev;

        SulfurBubble(final ClientLevel level, final double x, final double y, final double z, final double xa, final double za,
                     final TextureAtlasSprite sprite) {
            super(level, x, y, z, sprite);
            this.gravity = -0.04F;
            this.friction = 0.85F;
            this.setSize(0.02F, 0.02F);
            this.xd = xa * 0.2 + (this.random.nextFloat() * 2 - 1) * 0.02;
            this.zd = za * 0.2 + (this.random.nextFloat() * 2 - 1) * 0.02;
            this.sizeStart = 0.02F + 0.02F * this.random.nextFloat();
            this.quadSize = this.sizeStart;
            this.lifetime = Integer.MAX_VALUE;
            this.yStart = this.yo;
            this.yEnd = this.yo + 3.0;
            this.yPrev = y;
        }

        @Override
        public void tick() {
            super.tick();
            if (!this.removed && (!this.level.getFluidState(BlockPos.containing(this.x, this.y, this.z)).isSourceOfType(Fluids.WATER)
                || this.y >= this.yEnd || this.y <= this.yPrev)) {
                this.remove();
            }
            this.xd += this.wiggle();
            this.zd += this.wiggle();
            this.move(this.xd, 0, this.zd);
            final float progress = (float) ((this.y - this.yStart) / (this.yEnd - this.yStart));
            this.quadSize = this.sizeStart + progress * (0.15F - this.sizeStart);
            this.yPrev = this.y;
        }

        private double wiggle() {
            return this.random.nextFloat() * 0.0015 * (this.random.nextBoolean() ? 1 : -1);
        }

        @Override
        protected Layer getLayer() {
            return Layer.OPAQUE;
        }
    }

    /** Humo amarillento que se queda flotando sobre el agua y se desvanece en la segunda mitad de su vida. */
    static final class NoxiousGas extends BaseAshSmokeParticle {
        private final float fadeStart;

        NoxiousGas(final ClientLevel level, final double x, final double y, final double z, final double xa, final double ya, final double za,
                   final SpriteSet sprites) {
            super(level, x, y, z, 0.1F, 0.1F, 0.1F, xa, ya, za, 3.0F, sprites, 0.3F, 5, -0.02F, true);
            this.rCol = 1.0F;
            this.gCol = 1.0F;
            this.bCol = 1.0F;
            this.lifetime = (int) (6.0 / (this.random.nextFloat() * 0.5 + 0.5) * 3.0F);
            this.fadeStart = this.lifetime / 2.0F;
        }

        @Override
        public void tick() {
            super.tick();
            if (this.age > this.fadeStart) {
                this.setAlpha((this.lifetime - (this.age - this.fadeStart)) / this.lifetime);
            }
        }

        @Override
        public Layer getLayer() {
            return Layer.TRANSLUCENT;
        }
    }

    /** Invisible: durante un segundo suelta gas alrededor del geiser, solo donde el gas llega. */
    static final class NoxiousGasCloud extends NoRenderParticle {
        NoxiousGasCloud(final ClientLevel level, final double x, final double y, final double z) {
            super(level, x, y, z);
            this.lifetime = 20;
        }

        @Override
        public void tick() {
            super.tick();
            if (this.age % 2 != 0) {
                return;
            }
            final BlockPos source = BlockPos.containing(this.x, this.y, this.z);
            final RandomSource random = this.level.getRandom();
            final Vec3 direction = new Vec3(random.nextFloat() - 0.5F, 0, random.nextFloat() - 0.5F).normalize();
            final Vec3 point = Vec3.atCenterOf(source).add(direction.scale(random.nextFloat() * 3.0F)).subtract(0, 0.25, 0);
            if (PotentSulfurBlockEntity.gasReaches(this.level, source, point)) {
                this.level.addAlwaysVisibleParticle(NewParticles.simple("minecraft:noxious_gas"), point.x, point.y, point.z, 0, 0, 0);
            }
        }
    }

    /** Invisible: durante un segundo lanza la columna, la espuma de la base y el estallido. */
    static final class GeyserEruption extends NoRenderParticle {
        private final double xa;
        private final double ya;
        private final double za;
        private final int waterBlocks;
        private final NewParticles.Geyser plume;
        private final NewParticles.GeyserBase base;
        private final NewParticles.GeyserBase poof;

        GeyserEruption(final ClientLevel level, final double x, final double y, final double z, final double xa, final double ya,
                       final double za, final int waterBlocks) {
            super(level, x, y, z);
            this.xa = xa;
            this.ya = ya;
            this.za = za;
            this.waterBlocks = waterBlocks;
            this.lifetime = 20;
            this.plume = new NewParticles.Geyser(NewParticles.geyser("minecraft:geyser_plume"), waterBlocks);
            this.base = new NewParticles.GeyserBase(NewParticles.geyserBase("minecraft:geyser_base"), waterBlocks, 1.5F);
            this.poof = new NewParticles.GeyserBase(NewParticles.geyserBase("minecraft:geyser_poof"), waterBlocks, 2.0F);
        }

        @Override
        public void tick() {
            super.tick();
            if (this.age % 2 == 0) {
                this.spawn(this.base, 2);
            }
            this.spawn(this.plume, this.waterBlocks + 2);
            if (this.age % 10 == 0) {
                this.spawn(this.poof, 20);
            }
        }

        private void spawn(final net.minecraft.core.particles.ParticleOptions options, final int count) {
            for (int i = 0; i < count; i++) {
                this.level.addParticle(options, this.x, this.y, this.z, this.xa, this.ya, this.za);
            }
        }
    }

    /** Espuma que sale disparada de la base del chorro; mas fuerte cuanta mas agua hay encima. */
    static final class GeyserBase extends BaseAshSmokeParticle {
        GeyserBase(final ClientLevel level, final double x, final double y, final double z, final double xa, final double ya, final double za,
                   final int waterBlocks, final float burstImpulseBase, final SpriteSet sprites) {
            super(level, x, y, z, burstImpulseBase + 0.25F * waterBlocks, burstImpulseBase + 0.25F * waterBlocks,
                burstImpulseBase + 0.25F * waterBlocks, xa, ya, za, 3.0F + 0.125F * waterBlocks, sprites, 0.0F, 0, 0.0F, true);
            this.friction = 0.725F;
            this.rCol = 1.0F;
            this.gCol = 1.0F;
            this.bCol = 1.0F;
            this.yd = Math.abs(this.yd);
            this.lifetime = (int) (25.0F * (0.8F + 0.2F * level.getRandom().nextFloat()));
        }
    }

    /** Parte de la columna de agua: sale disparada, frena al acercarse a su altura y se abre en abanico. */
    static final class GeyserPlume extends SingleQuadParticle {
        private final SpriteSet sprites;
        private final double startY;
        private final double maxY;
        private final float propulsion;
        private final float sprayX;
        private final float sprayZ;
        private final float minSize;
        private final float maxSize;
        private boolean done;

        GeyserPlume(final ClientLevel level, final double x, final double y, final double z, final double xa, final double ya, final double za,
                    final int waterBlocks, final SpriteSet sprites) {
            super(level, x, y, z, xa, ya, za, sprites.first());
            final int height = 5 * Math.max(1, waterBlocks);
            this.hasPhysics = true;
            this.speedUpWhenYMotionIsBlocked = true;
            this.lifetime = height * 5;
            this.yd = 0;
            this.startY = y;
            this.maxY = y + height - 1.0;
            this.sprayX = (level.getRandom().nextFloat() - 0.5F) * 0.2F;
            this.sprayZ = (level.getRandom().nextFloat() - 0.5F) * 0.2F;
            this.friction = 1.0F;
            this.propulsion = (waterBlocks == 1 ? 1.5F : 1.0F) * height * 1.45F;
            this.gravity = -this.propulsion;
            final float base = this.quadSize * 0.75F;
            this.minSize = base * (2.0F + height / 8.0F);
            this.maxSize = base * (3.0F + height / 8.0F);
            this.quadSize = this.minSize;
            this.sprites = sprites;
            this.setSpriteFromAge(sprites);
        }

        @Override
        public void tick() {
            super.tick();
            if (!this.done && (this.yd < 0 || this.y > this.maxY || this.y == this.yo)) {
                // Arriba del todo: unos pocos ticks mas y fuera
                this.lifetime = Math.min(this.lifetime, this.age + 5);
                this.friction = 0.0F;
                this.done = true;
            }
            final double progress = Mth.clamp((this.y - this.startY) / (this.maxY - this.startY), 0.0, 1.0);
            this.gravity = this.propulsion * (float) Math.pow(progress, 3.0) * 0.12F;
            this.xd = progress * this.sprayX;
            this.zd = progress * this.sprayZ;
            this.setSpriteFromAge(this.sprites);
            this.quadSize = this.minSize + (float) (progress * (this.maxSize - this.minSize));
        }

        @Override
        protected Layer getLayer() {
            return Layer.OPAQUE;
        }
    }
}
