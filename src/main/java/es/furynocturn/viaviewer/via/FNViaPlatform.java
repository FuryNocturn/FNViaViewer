package es.furynocturn.viaviewer.via;

import com.viaversion.viaversion.configuration.AbstractViaConfig;
import com.viaversion.viaversion.platform.UserConnectionViaVersionPlatform;
import java.io.File;
import java.util.logging.Logger;
import net.fabricmc.loader.api.FabricLoader;

final class FNViaPlatform extends UserConnectionViaVersionPlatform {

    FNViaPlatform(final File dataFolder) {
        super(dataFolder);
    }

    @Override
    public Logger createLogger(final String name) {
        return Logger.getLogger(name);
    }

    @Override
    public String getPlatformName() {
        return "FNViaViewer";
    }

    @Override
    public String getPlatformVersion() {
        return FabricLoader.getInstance().getModContainer("fnviaviewer")
            .map(mod -> mod.getMetadata().getVersion().getFriendlyString()).orElse("?");
    }

    @Override
    protected AbstractViaConfig createConfig() {
        return new AbstractViaConfig(new File(getDataFolder(), "viaversion.yml"), getLogger()) {
            @Override
            public boolean isCheckForUpdates() {
                return false;
            }
        };
    }
}
