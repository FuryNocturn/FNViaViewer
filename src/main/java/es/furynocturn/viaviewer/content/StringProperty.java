package es.furynocturn.viaviewer.content;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Property;

/** Propiedad de bloque con valores de texto, para las que no tienen equivalente vanilla. */
public final class StringProperty extends Property<String> {

    private static Map<String, Property<?>> vanilla;
    private final List<String> values;

    private StringProperty(final String name, final List<String> values) {
        super(name, String.class);
        this.values = List.copyOf(values);
    }

    /**
     * La propiedad vanilla con ese nombre y esos valores en el mismo orden (asi el codigo vanilla que
     * mira p. ej. WATERLOGGED funciona), o una StringProperty si no hay ninguna igual.
     */
    public static Property<?> of(final String name, final List<String> values) {
        final Property<?> match = vanillaProperties().get(name + "=" + values);
        return match != null ? match : new StringProperty(name, values);
    }

    private static synchronized Map<String, Property<?>> vanillaProperties() {
        if (vanilla == null) {
            vanilla = new HashMap<>();
            for (final Field field : BlockStateProperties.class.getFields()) {
                if (Modifier.isStatic(field.getModifiers()) && Property.class.isAssignableFrom(field.getType())) {
                    try {
                        final Property<?> property = (Property<?>) field.get(null);
                        vanilla.putIfAbsent(property.getName() + "=" + names(property), property);
                    } catch (final IllegalAccessException ignored) {
                        // campo publico: no pasa
                    }
                }
            }
        }
        return vanilla;
    }

    private static <T extends Comparable<T>> List<String> names(final Property<T> property) {
        final List<String> names = new ArrayList<>();
        property.getPossibleValues().forEach(value -> names.add(property.getName(value)));
        return names;
    }

    @Override
    public List<String> getPossibleValues() {
        return this.values;
    }

    @Override
    public String getName(final String value) {
        return value;
    }

    @Override
    public Optional<String> getValue(final String name) {
        return this.values.contains(name) ? Optional.of(name) : Optional.empty();
    }

    @Override
    public int getInternalIndex(final String value) {
        return this.values.indexOf(value);
    }

    @Override
    public int generateHashCode() {
        return 31 * super.generateHashCode() + this.values.hashCode();
    }

    @Override
    public boolean equals(final Object other) {
        return other instanceof StringProperty that && super.equals(other) && this.values.equals(that.values);
    }
}
