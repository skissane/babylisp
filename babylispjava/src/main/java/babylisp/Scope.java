package babylisp;

import babylisp.values.HandleValue;
import babylisp.values.SimpleValue;
import babylisp.values.SymbolValue;
import babylisp.values.Value;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public final class Scope {
    private final Map<SymbolValue, Value> vars = new HashMap<>();

    public Value get(@Nonnull SymbolValue varName) {
        if (!vars.containsKey(varName))
            throw new IllegalArgumentException("Undeclared variable: " + varName);
        return vars.get(varName);
    }

    public void set(@Nonnull SymbolValue varName, @Nullable Value value) {
        if (!varName.value().startsWith("$"))
            throw new IllegalArgumentException("Bad variable name: " + varName);
        if (value != null && !(value instanceof SimpleValue) && !(value instanceof HandleValue))
            throw new IllegalArgumentException("Bad variable value: " + value);
        vars.put(varName, value);
    }
}
