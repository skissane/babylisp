package babylisp;

import babylisp.values.ObjectValue;
import babylisp.values.SymbolValue;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableSortedMap;

import javax.annotation.Nonnull;
import java.util.ServiceLoader;

public abstract class PragmaReader {

    private static final Supplier<ImmutableSortedMap<SymbolValue, PragmaReader>> READERS_LOADER =
            Suppliers.memoize(() -> {
                final ImmutableSortedMap.Builder<SymbolValue, PragmaReader> readers = ImmutableSortedMap.naturalOrder();
                ServiceLoader.load(PragmaReader.class).forEach(reader -> readers.put(reader.name(), reader));
                return readers.build();
            });

    public abstract SymbolValue name();

    public abstract ObjectValue readPragma(@Nonnull LispReader reader);

    public static ImmutableSortedMap<SymbolValue, PragmaReader> readers() {
        return READERS_LOADER.get();
    }
}
