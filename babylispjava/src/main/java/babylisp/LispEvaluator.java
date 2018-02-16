package babylisp;

import babylisp.values.*;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static babylisp.Database.CATALOG_NAME;

public class LispEvaluator {
    public static final SymbolValue SYM_FUNC = new SymbolValue("func");
    public static final SymbolValue FUNC_BUILTIN = new SymbolValue("func/builtin");
    public static final SymbolValue FUNC_SPECIAL = new SymbolValue("func/special");

    public static Value eval(@Nullable Value value) {
        if (value == null || value instanceof SimpleValue)
            return value;
        if (value instanceof ListValue)
            return evalList((ListValue) value);
        throw new UnsupportedOperationException("TODO: " + Value.type(value));
    }

    private static Value evalList(@Nonnull ListValue value) {
        if (value.size() == 0)
            return value;
        Value toCall = value.get(0);
        if (toCall instanceof SymbolValue) {
            final Value symVal = Database.getInstance().get((SymbolValue) toCall);
            if (symVal == null)
                throw new IllegalArgumentException("Not found: " + toCall);
            toCall = symVal;
        }
        if (toCall instanceof ObjectValue) {
            final ObjectValue o = (ObjectValue) toCall;
            if (o.ofClass().equals(SYM_FUNC))
                return evalFunc(o, value);
        }
        throw new UnsupportedOperationException("Cannot call: " + toCall);
    }

    private static Value evalFunc(@Nonnull ObjectValue func, @Nonnull ListValue argv) {
        final boolean special = func.getBoolean(FUNC_SPECIAL);
        final boolean builtin = func.getBoolean(FUNC_BUILTIN);
        if (builtin) {
            final SymbolValue name = func.getSymbol(CATALOG_NAME);
            if (name == null)
                throw new IllegalArgumentException("Builtin must have name: " + func);
            final Builtin impl = Builtins.get(name);
            final Value[] args = new Value[argv.size() - 1];
            for (int i = 1; i < argv.size(); i++)
                args[i - 1] = special ? argv.get(i) : eval(argv.get(i));
            return impl.exec(args);
        }
        throw new UnsupportedOperationException("TODO:" + func);
    }

    static Value readThenEvalCode(@Nonnull String text) {
        final Value[] result = new Value[1];
        LispReader.read(text, code -> result[0] = eval(code));
        return result[0];
    }
}
