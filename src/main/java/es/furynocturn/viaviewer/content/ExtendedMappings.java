package es.furynocturn.viaviewer.content;

import com.viaversion.viaversion.api.data.BiMappings;
import com.viaversion.viaversion.api.data.Mappings;
import com.viaversion.viaversion.libs.fastutil.ints.Int2IntMap;
import com.viaversion.viaversion.libs.fastutil.ints.Int2IntOpenHashMap;
import java.util.function.BooleanSupplier;

/**
 * Envuelve los Mappings de un paso de ViaBackwards para que el contenido nuevo pase hasta el cliente
 * en vez de cambiarse por un sustituto. Con N cosas nuevas (indice D 0..N-1):
 * <ul>
 * <li>Un ID de 'from' que no existe en 'to' (introduced) sale como out(d).</li>
 * <li>En pasos que no son el primero, los IDs de entrada >= size original vienen de la zona extendida
 *     del paso anterior (origSize + d) y salen tambien como out(d).</li>
 * <li>out(d) = origMapped + d, salvo en el ultimo paso, donde es el ID real del cliente.</li>
 * </ul>
 * size()/mappedSize() crecen en N, que es lo que usa Via para los bits de la paleta global de los chunks.
 * Con 'active' apagado lo nuevo vuelve a su sustituto, pero los tamanos no cambian: el cliente sigue
 * teniendo los IDs extra y los bits de la paleta tienen que cuadrar con su registro.
 */
public final class ExtendedMappings implements BiMappings {

    private final Mappings original;
    private final Int2IntMap introduced;
    private final Int2IntMap introducedInverse = new Int2IntOpenHashMap();
    private final boolean first;
    private final int count;
    private final int[] finalIds;
    private final Int2IntMap finalInverse = new Int2IntOpenHashMap();
    private final int finalSize;
    private final int originalSize;
    private final int originalMappedSize;
    private final BooleanSupplier active;

    /** finalIds/finalSize solo en el ultimo paso (null/-1 en los demas). */
    public ExtendedMappings(final Mappings original, final Int2IntMap introduced, final boolean first, final int count,
                            final int[] finalIds, final int finalSize, final BooleanSupplier active) {
        this.original = original;
        this.introduced = introduced;
        this.first = first;
        this.count = count;
        this.finalIds = finalIds;
        this.finalSize = finalSize;
        this.active = active;
        this.originalSize = original.size();
        this.originalMappedSize = original.mappedSize();
        this.introducedInverse.defaultReturnValue(-1);
        this.finalInverse.defaultReturnValue(-1);
        introduced.forEach((from, d) -> this.introducedInverse.put((int) d, (int) from));
        if (finalIds != null) {
            for (int d = 0; d < finalIds.length; d++) {
                this.finalInverse.put(finalIds[d], d);
            }
        }
    }

    public int out(final int d) {
        return this.finalIds != null ? this.finalIds[d] : this.originalMappedSize + d;
    }

    /** ID de entrada de este paso para el indice d, o -1 si aun no es nuevo aqui. */
    public int source(final int d) {
        final int from = this.introducedInverse.get(d);
        if (from != -1) {
            return from;
        }
        return this.first ? -1 : this.originalSize + d;
    }

    private int indexOfMapped(final int mappedId) {
        if (this.finalIds != null) {
            return this.finalInverse.get(mappedId);
        }
        final int d = mappedId - this.originalMappedSize;
        return d >= 0 && d < this.count ? d : -1;
    }

    @Override
    public int getNewId(final int id) {
        if (!this.first && id >= this.originalSize) {
            final int d = id - this.originalSize;
            return d < this.count ? this.out(d) : -1;
        }
        final int d = this.introduced.getOrDefault(id, -1);
        return d != -1 && this.active.getAsBoolean() ? this.out(d) : this.original.getNewId(id);
    }

    @Override
    public void setNewId(final int id, final int mappedId) {
        this.original.setNewId(id, mappedId);
    }

    @Override
    public int size() {
        return this.originalSize + (this.first ? 0 : this.count);
    }

    @Override
    public int mappedSize() {
        return this.finalIds != null ? this.finalSize : this.originalMappedSize + this.count;
    }

    @Override
    public boolean isIdentity() {
        return false;
    }

    @Override
    public BiMappings inverse() {
        final Mappings originalInverse = this.original.inverse();
        final ExtendedMappings self = this;
        return new BiMappings() {
            @Override
            public int getNewId(final int mappedId) {
                final int d = self.indexOfMapped(mappedId);
                return d != -1 ? self.source(d) : originalInverse.getNewId(mappedId);
            }

            @Override
            public void setNewId(final int id, final int mappedId) {
                originalInverse.setNewId(id, mappedId);
            }

            @Override
            public int size() {
                return self.mappedSize();
            }

            @Override
            public int mappedSize() {
                return self.size();
            }

            @Override
            public boolean isIdentity() {
                return false;
            }

            @Override
            public BiMappings inverse() {
                return self;
            }
        };
    }
}
