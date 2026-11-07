package es.furynocturn.viaviewer.particle;

import es.furynocturn.viaviewer.content.NewParticles;
import es.furynocturn.viaviewer.content.PotentSulfurBlockEntity;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.BaseAshSmokeParticle;
//#if MC >= 12109
import net.minecraft.client.particle.BreakingItemParticle;
//#else
//$$ import net.minecraft.client.particle.ParticleRenderType;
//#endif
import net.minecraft.client.particle.FallingLeavesParticle;
import net.minecraft.client.particle.NoRenderParticle;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
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

    /** Como se crea una particula, con el RandomSource que ParticleProvider recibe desde 1.21.9. */
    @FunctionalInterface
    interface Factory<T extends ParticleOptions> {
        Particle create(T options, ClientLevel level, double x, double y, double z, double xa, double ya, double za, RandomSource random);
    }

    private static <T extends ParticleOptions> ParticleProvider<T> provider(final Factory<T> factory) {
        //#if MC >= 12109
        return factory::create;
        //#else
        //$$ return (options, level, x, y, z, xa, ya, za) -> factory.create(options, level, x, y, z, xa, ya, za, level.random);
        //#endif
    }

    /** El primer sprite del conjunto (SpriteSet.first llego en 1.21.9). */
    static TextureAtlasSprite first(final SpriteSet sprites) {
        //#if MC >= 12109
        return sprites.first();
        //#else
        //$$ return sprites.get(0, 1);
        //#endif
    }

    /** Solo las particulas que son nuevas para este cliente: las demas ya las trae de serie. */
    public static void register() {
        final ParticleFactoryRegistry registry = ParticleFactoryRegistry.getInstance();
        // Las hojas de alamo caen igual que las del roble palido
        for (final String color : new String[]{"red", "orange", "yellow"}) {
            if (NewParticles.has("minecraft:" + color + "_poplar_leaves")) {
                registry.register(NewParticles.simple("minecraft:" + color + "_poplar_leaves"), FallingLeavesParticle.PaleOakProvider::new);
            }
        }
        if (NewParticles.has("minecraft:sulfur_bubbles")) {
            registry.register(NewParticles.simple("minecraft:sulfur_bubbles"), sprites -> provider((options, level, x, y, z, xa, ya, za, random) ->
                new SulfurBubble(level, x, y, z, xa, za, sprites.get(random))));
        }
        if (NewParticles.has("minecraft:noxious_gas")) {
            registry.register(NewParticles.simple("minecraft:noxious_gas"), sprites -> provider((options, level, x, y, z, xa, ya, za, random) ->
                new NoxiousGas(level, x, y, z, xa, ya, za, sprites)));
        }
        if (NewParticles.has("minecraft:noxious_gas_cloud")) {
            registry.register(NewParticles.simple("minecraft:noxious_gas_cloud"), provider((options, level, x, y, z, xa, ya, za, random) ->
                new NoxiousGasCloud(level, x, y, z)));
        }
        if (NewParticles.has("minecraft:geyser")) {
            registry.register(NewParticles.geyser("minecraft:geyser"), provider((options, level, x, y, z, xa, ya, za, random) ->
                new GeyserEruption(level, x, y, z, xa, ya, za, options.waterBlocks())));
        }
        if (NewParticles.has("minecraft:geyser_plume")) {
            registry.register(NewParticles.geyser("minecraft:geyser_plume"), sprites -> provider((options, level, x, y, z, xa, ya, za, random) ->
                new GeyserPlume(level, x + (random.nextFloat() - 0.5F) * 0.2F, y + random.nextFloat(), z + (random.nextFloat() - 0.5F) * 0.2F,
                    xa, ya, za, options.waterBlocks(), sprites)));
        }
        for (final String id : new String[]{"minecraft:geyser_base", "minecraft:geyser_poof"}) {
            if (NewParticles.has(id)) {
                registry.register(NewParticles.geyserBase(id), sprites -> provider((options, level, x, y, z, xa, ya, za, random) ->
                    new GeyserBase(level, x + (random.nextFloat() - 0.5F) * 0.5F, y + (random.nextFloat() - 0.5F) * 0.5F + 0.2F,
                        z + (random.nextFloat() - 0.5F) * 0.5F, xa, ya, za, options.waterBlocks(), options.burstImpulseBase(), sprites)));
            }
        }
        if (NewParticles.has("minecraft:sulfur_cube_goo")) {
            registry.register(NewParticles.simple("minecraft:sulfur_cube_goo"), sprites -> provider((options, level, x, y, z, xa, ya, za, random) ->
                new SulfurCubeGoo(level, x, y, z, first(sprites))));
        }
        // Diente de leon dorado sobre una cria: pausar el crecimiento baja, reiniciarlo sube
        if (NewParticles.has("minecraft:pause_mob_growth")) {
            registry.register(NewParticles.simple("minecraft:pause_mob_growth"), sprites -> provider((options, level, x, y, z, xa, ya, za, random) ->
                new MobGrowth(level, x, y, z, xa, ya, za, sprites.get(random), false)));
        }
        if (NewParticles.has("minecraft:reset_mob_growth")) {
            registry.register(NewParticles.simple("minecraft:reset_mob_growth"), sprites -> provider((options, level, x, y, z, xa, ya, za, random) ->
                new MobGrowth(level, x, y, z, xa, ya, za, sprites.get(random), true)));
        }
    }

    /** Pegote del sulfur cube al botar: el mismo trozo que salta del slime, con su sprite. */
    //#if MC >= 12109
    static final class SulfurCubeGoo extends BreakingItemParticle {
        SulfurCubeGoo(final ClientLevel level, final double x, final double y, final double z, final TextureAtlasSprite sprite) {
            super(level, x, y, z, sprite);
        }
    }
    //#else
    //$$ /** Antes de 1.21.9 BreakingItemParticle solo sale de un item: lo mismo con el sprite (un trozo al azar, gravedad 1). */
    //$$ static final class SulfurCubeGoo extends QuadParticle {
    //$$     private final float uo = this.random.nextFloat() * 3.0F;
    //$$     private final float vo = this.random.nextFloat() * 3.0F;
    //$$
    //$$     SulfurCubeGoo(final ClientLevel level, final double x, final double y, final double z, final TextureAtlasSprite sprite) {
    //$$         super(level, x, y, z, 0.0, 0.0, 0.0, sprite);
    //$$         this.gravity = 1.0F;
    //$$         this.quadSize /= 2.0F;
    //$$     }
    //$$
    //$$     @Override
    //$$     protected float getU0() {
    //$$         return this.sprite.getU((this.uo + 1.0F) / 4.0F);
    //$$     }
    //$$
    //$$     @Override
    //$$     protected float getU1() {
    //$$         return this.sprite.getU(this.uo / 4.0F);
    //$$     }
    //$$
    //$$     @Override
    //$$     protected float getV0() {
    //$$         return this.sprite.getV(this.vo / 4.0F);
    //$$     }
    //$$
    //$$     @Override
    //$$     protected float getV1() {
    //$$         return this.sprite.getV((this.vo + 1.0F) / 4.0F);
    //$$     }
    //$$ }
    //#endif

    /** Destello que va recto hacia arriba o hacia abajo, sin gravedad, durante 8 ticks. */
    static final class MobGrowth extends QuadParticle {
        MobGrowth(final ClientLevel level, final double x, final double y, final double z, final double xa, final double ya, final double za,
                  final TextureAtlasSprite sprite, final boolean upwards) {
            super(level, x, y, z, xa, ya, za, sprite);
            this.xd = xa;
            this.yd = ya + (upwards ? 0.03 : -0.03);
            this.zd = za;
            this.gravity = 0.0F;
            this.quadSize *= this.random.nextFloat() * 0.6F + 0.5F;
            this.lifetime = 8;
        }
    }

    /** Burbuja que sube por el agua sobre el azufre y crece; desaparece al salir del agua o al pararse. */
    static final class SulfurBubble extends QuadParticle {
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

        //#if MC >= 12109
        @Override
        public Layer getLayer() {
            return Layer.TRANSLUCENT;
        }
        //#else
        //$$ @Override
        //$$ public ParticleRenderType getRenderType() {
        //$$     return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
        //$$ }
        //#endif
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
    static final class GeyserPlume extends QuadParticle {
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
            super(level, x, y, z, xa, ya, za, first(sprites));
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
    }
}
