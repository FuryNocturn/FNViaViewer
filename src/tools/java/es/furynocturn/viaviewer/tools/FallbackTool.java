package es.furynocturn.viaviewer.tools;

import com.viaversion.viabackwards.ViaBackwardsPlatformImpl;
import com.viaversion.viaversion.ViaManagerImpl;
import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.data.MappingData;
import com.viaversion.viaversion.api.data.Mappings;
import com.viaversion.viaversion.api.platform.ViaPlatformLoader;
import com.viaversion.viaversion.api.protocol.Protocol;
import com.viaversion.viaversion.commands.ViaCommandHandler;
import com.viaversion.viaversion.configuration.AbstractViaConfig;
import com.viaversion.viaversion.libs.gson.JsonArray;
import com.viaversion.viaversion.libs.gson.JsonObject;
import com.viaversion.viaversion.libs.gson.JsonParser;
import com.viaversion.viaversion.platform.NoopInjector;
import com.viaversion.viaversion.platform.UserConnectionViaVersionPlatform;
import es.furynocturn.viaviewer.content.ContentDescriptor;
import es.furynocturn.viaviewer.content.MappingPatcher;
import java.io.File;
import java.io.FileInputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.logging.Logger;

/**
 * Herramienta de desarrollo (gradlew fallbackTool), despues de tools/gen_content.py:
 * 1. Carga ViaVersion + ViaBackwards fuera del juego.
 * 2. Calcula el sustituto nativo de cada estado nuevo con las tablas originales y lo guarda en el
 *    descriptor ("fallback"): de ahi salen colision, luz, dureza y color de mapa en el cliente.
 * 3. Aplica MappingPatcher y comprueba que todo lo nuevo llega al cliente con su ID, que la vuelta
 *    (cliente -> servidor) devuelve el ID de la version nueva y que lo vanilla no cambia.
 */
public final class FallbackTool {

    private static int errors;

    public static void main(final String[] args) throws Exception {
        final Path descriptorPath = Path.of(args[0]);
        final File dataFolder = new File(args[1]);
        dataFolder.mkdirs();

        ViaManagerImpl.initAndLoad(new UserConnectionViaVersionPlatform(dataFolder) {
            @Override
            public Logger createLogger(final String name) {
                return Logger.getLogger(name);
            }

            @Override
            public String getPlatformName() {
                return "FNViaViewer-tools";
            }

            @Override
            public String getPlatformVersion() {
                return "dev";
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
        }, new NoopInjector(), new ViaCommandHandler(false), new ViaPlatformLoader() {
            @Override
            public void load() {
            }

            @Override
            public void unload() {
            }
        }, ViaBackwardsPlatformImpl::new);

        ContentDescriptor descriptor;
        try (FileInputStream in = new FileInputStream(descriptorPath.toFile())) {
            descriptor = ContentDescriptor.load(in);
        }
        final List<MappingData> chain = new ArrayList<>();
        for (final ContentDescriptor.Step step : descriptor.steps()) {
            final Protocol<?, ?, ?, ?> protocol = MappingPatcher.protocol(step);
            final CompletableFuture<Void> future = Via.getManager().getProtocolManager().getMappingLoaderFuture(protocol.getClass());
            if (future != null) {
                future.join();
            }
            chain.add(protocol.getMappingData());
        }

        // Los tamanos encadenan: lo que sale de un paso es lo que entra en el siguiente
        for (int i = 0; i + 1 < chain.size(); i++) {
            check(chain.get(i).getBlockStateMappings().mappedSize() == chain.get(i + 1).getBlockStateMappings().size(), "tamanos de estados entre pasos " + i);
            check(chain.get(i).getItemMappings().mappedSize() == chain.get(i + 1).getItemMappings().size(), "tamanos de items entre pasos " + i);
        }

        final Tables before = Tables.read(chain);
        final int states = descriptor.stateCount();
        final int nativeStates = size(chain, MappingData::getBlockStateMappings);
        final int nativeBlocks = size(chain, MappingData::getBlockMappings);
        final int nativeItems = size(chain, MappingData::getItemMappings);
        final int nativeSounds = size(chain, MappingData::getSoundMappings);

        // 1. Sustituto original de cada estado nuevo
        final int[] fallback = new int[states];
        for (final ContentDescriptor.BlockDef block : descriptor.blocks()) {
            for (int i = 0; i < block.states(); i++) {
                fallback[block.firstIndex() + i] = run(chain, MappingData::getBlockStateMappings, block.firstState() + i);
                check(fallback[block.firstIndex() + i] >= 0, "sin sustituto: " + block.id() + " estado " + i);
            }
        }

        final int[] itemFallback = descriptor.items().stream().mapToInt(e -> run(chain, MappingData::getItemMappings, e.newestId())).toArray();
        final int[] soundFallback = descriptor.sounds().stream().mapToInt(e -> run(chain, MappingData::getSoundMappings, e.newestId())).toArray();
        final java.util.concurrent.atomic.AtomicBoolean on = new java.util.concurrent.atomic.AtomicBoolean(true);

        // 2. Parchear como en el cliente (IDs nuevos justo detras de los vanilla)
        final MappingData last = chain.getLast();
        MappingPatcher.patch(descriptor, new MappingPatcher.Client(
            MappingPatcher.sequential(nativeStates, states),
            MappingPatcher.sequential(nativeBlocks, descriptor.blocks().size()),
            MappingPatcher.sequential(nativeItems, descriptor.items().size()),
            MappingPatcher.sequential(nativeSounds, descriptor.sounds().size())), new MappingPatcher.Toggles(on::get, on::get, on::get));

        // 3. Comprobar ida, vuelta y que lo vanilla no cambia
        for (final ContentDescriptor.BlockDef block : descriptor.blocks()) {
            for (int i = 0; i < block.states(); i++) {
                final int d = block.firstIndex() + i;
                check(run(chain, MappingData::getBlockStateMappings, block.firstState() + i) == nativeStates + d, "estado " + block.id() + " " + i);
            }
            final int d = descriptor.blocks().indexOf(block);
            check(run(chain, MappingData::getBlockMappings, block.newestId()) == nativeBlocks + d, "bloque " + block.id());
        }
        checkEntries(chain, nativeItems, descriptor.items(), MappingData::getItemMappings, "item");
        checkEntries(chain, nativeSounds, descriptor.sounds(), MappingData::getSoundMappings, "sonido");
        check(last.getBlockStateMappings().mappedSize() == nativeStates + states, "mappedSize final de estados");
        for (int d = 0; d < descriptor.items().size(); d++) {
            final String name = last.getFullItemMappings().mappedIdentifier(nativeItems + d);
            check(descriptor.items().get(d).id().equals(name), "nombre del item " + descriptor.items().get(d).id() + " = " + name);
        }
        final java.util.Set<Integer> newStates = new java.util.HashSet<>();
        descriptor.blocks().forEach(b -> { for (int i = 0; i < b.states(); i++) newStates.add(b.firstState() + i); });
        int vanilla = 0;
        for (int id = 0; id < before.stateResults.length; id++) {
            if (!newStates.contains(id)) {
                check(run(chain, MappingData::getBlockStateMappings, id) == before.stateResults[id], "estado vanilla " + id);
                vanilla++;
            }
        }
        System.out.println("Estados vanilla sin cambios: " + vanilla);

        // Interruptores apagados: lo nuevo vuelve a su sustituto y los tamanos siguen ampliados
        on.set(false);
        for (final ContentDescriptor.BlockDef block : descriptor.blocks()) {
            for (int i = 0; i < block.states(); i++) {
                check(run(chain, MappingData::getBlockStateMappings, block.firstState() + i) == fallback[block.firstIndex() + i], "apagado: estado " + block.id());
            }
        }
        for (int d = 0; d < itemFallback.length; d++) {
            check(run(chain, MappingData::getItemMappings, descriptor.items().get(d).newestId()) == itemFallback[d], "apagado: item " + d);
        }
        for (int d = 0; d < soundFallback.length; d++) {
            check(run(chain, MappingData::getSoundMappings, descriptor.sounds().get(d).newestId()) == soundFallback[d], "apagado: sonido " + d);
        }
        check(last.getBlockStateMappings().mappedSize() == nativeStates + states, "apagado: mappedSize final de estados");
        on.set(true);

        // 4. Guardar el sustituto en el descriptor
        final JsonObject json = JsonParser.parseString(Files.readString(descriptorPath)).getAsJsonObject();
        final JsonArray array = new JsonArray();
        for (final int id : fallback) {
            array.add(id);
        }
        json.add("fallback", array);
        Files.writeString(descriptorPath, json.toString(), StandardCharsets.UTF_8);

        System.out.println(errors == 0 ? "OK: " + states + " estados, sustitutos guardados" : "FALLO: " + errors + " errores");
        System.exit(errors == 0 ? 0 : 1);
    }

    private static void checkEntries(final List<MappingData> chain, final int nativeSize, final List<ContentDescriptor.Entry> entries,
                                     final Function<MappingData, Mappings> table, final String kind) {
        for (int d = 0; d < entries.size(); d++) {
            final ContentDescriptor.Entry entry = entries.get(d);
            check(run(chain, table, entry.newestId()) == nativeSize + d, kind + " " + entry.id());
            // vuelta: del cliente al servidor
            int id = nativeSize + d;
            for (int i = chain.size() - 1; i >= 0 && id >= 0; i--) {
                id = table.apply(chain.get(i)).inverse().getNewId(id);
            }
            check(id == entry.newestId(), kind + " vuelta " + entry.id() + " = " + id);
        }
    }

    private static int run(final List<MappingData> chain, final Function<MappingData, Mappings> table, int id) {
        for (final MappingData data : chain) {
            final Mappings mappings = table.apply(data);
            if (mappings != null && id >= 0) {
                id = mappings.getNewId(id);
            }
        }
        return id;
    }

    private static void check(final boolean ok, final String what) {
        if (!ok) {
            errors++;
            if (errors <= 30) {
                System.out.println("ERROR: " + what);
            }
        }
    }

    private static int size(final List<MappingData> chain, final Function<MappingData, Mappings> table) {
        return table.apply(chain.getLast()).mappedSize();
    }

    /** Resultado original de la cadena para todos los estados de la version nueva. */
    private record Tables(int[] stateResults) {
        static Tables read(final List<MappingData> chain) {
            final int[] results = new int[chain.getFirst().getBlockStateMappings().size()];
            for (int id = 0; id < results.length; id++) {
                results[id] = run(chain, MappingData::getBlockStateMappings, id);
            }
            return new Tables(results);
        }
    }
}
