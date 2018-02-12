package babylisp;

import babylisp.values.ListValue;
import babylisp.values.ObjectValue;
import babylisp.values.SymbolValue;

import javax.annotation.Nonnull;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static babylisp.Utils.handle;

public class Builtins {
    private static final Map<SymbolValue, Builtin> builtins = new HashMap<>();
    public static final SymbolValue CLASS_FUNC = new SymbolValue("func");
    public static final SymbolValue ATTR_BUILTIN = new SymbolValue("func/builtin");
    public static final SymbolValue CATALOG_NAME = new SymbolValue("catalog/name");

    static void process(@Nonnull Class<?> cls) {
        Arrays.stream(cls.getFields())
                .filter(f -> (f.getModifiers() & Modifier.STATIC) != 0)
                .filter(f -> (f.getModifiers() & Modifier.PUBLIC) != 0)
                .filter(f -> Builtin.class.equals(f.getType()))
                .forEach(f -> {
                    final String name = f.getName();
                    final SymbolValue sym = new SymbolValue(name);
                    final Builtin impl = getImpl(f);
                    builtins.put(sym, impl);
                    final ObjectValue defn = new ObjectValue(CLASS_FUNC);
                    defn.set(CATALOG_NAME, sym);
                    defn.set(ATTR_BUILTIN, SymbolValue.TRUE);
                    Database.getInstance().set(sym, defn);
                });
    }

    private static Builtin getImpl(@Nonnull Field f) {
        try {
            return (Builtin) f.get(null);
        } catch (IllegalAccessException e) {
            throw handle(e);
        }
    }

    static {
        process(CoreBuiltins.class);
    }

    public static Builtin get(@Nonnull SymbolValue name) {
        final Builtin builtin = builtins.get(name);
        if (builtin != null)
            return builtin;
        throw new IllegalArgumentException("No such builtin: " + name);
    }

    public static ListValue enumerate() {
        final ListValue r = new ListValue();
        builtins.keySet().stream().sorted().forEach(r::add);
        return r;
    }
}
