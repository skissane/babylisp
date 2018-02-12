package babylisp;

import babylisp.values.ObjectValue;
import babylisp.values.SymbolValue;
import babylisp.values.Value;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;

public final class Database {
    private Database() {
    }

    public static final SymbolValue CATALOG_NAME = new SymbolValue("catalog/name");

    private static final Database instance = new Database();

    public static Database getInstance() {
        return instance;
    }

    private final Map<SymbolValue, ObjectValue> entries = new HashMap<>();

    public ObjectValue get(@Nonnull SymbolValue symbol) {
        return entries.get(symbol);
    }

    public void set(@Nonnull SymbolValue symbol, @Nonnull ObjectValue value) {
        if (!symbol.equals(value.get(CATALOG_NAME)))
            throw new IllegalArgumentException(value + " should have " + CATALOG_NAME + " of " + symbol);
        entries.put(symbol, Value.immute(value));
    }
}
