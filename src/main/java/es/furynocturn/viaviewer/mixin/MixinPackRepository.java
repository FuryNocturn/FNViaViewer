package es.furynocturn.viaviewer.mixin;

import es.furynocturn.viaviewer.assets.CachePackSource;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;
import net.minecraft.client.resources.ClientPackSource;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.server.packs.repository.RepositorySource;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Anade la cache de assets al repositorio de resource packs del cliente (no a los de datos). */
@Mixin(PackRepository.class)
public abstract class MixinPackRepository {

    @Shadow
    @Final
    @Mutable
    private Set<RepositorySource> sources;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void fnviaviewer$addCachePack(RepositorySource[] repositorySources, CallbackInfo ci) {
        if (Arrays.stream(repositorySources).anyMatch(source -> source instanceof ClientPackSource)) {
            final Set<RepositorySource> all = new LinkedHashSet<>(this.sources);
            all.add(new CachePackSource());
            this.sources = all;
        }
    }
}
