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
        if (value instanceof ObjectValue)
            return evalASTNode((ObjectValue) value);
        throw new UnsupportedOperationException("TODO: " + Value.type(value));
    }

    private static Value evalASTNode(@Nonnull ObjectValue node) {
        final String nodeClass = node.ofClass().value();
        switch (nodeClass) {
            case "ast/invoke":
                return evalASTInvoke(node);
            case "ast/symbolConst":
                return node.getSymbol(new SymbolValue("ast/symbolConst/value"));
            case "ast/stringConst":
                return node.get(new SymbolValue("ast/stringConst/value"), StringValue.class);
            default:
                return node;
        }
    }

    private static Value evalToCall(@Nonnull Value v) {
        if (v instanceof SymbolValue) {
            final Value symVal = Database.getInstance().get((SymbolValue) v);
            if (symVal == null)
                throw new IllegalArgumentException("Not found: " + v);
            return symVal;
        }
        return v;
    }

    private static Value evalASTInvoke(@Nonnull ObjectValue node) {
        final Value what = evalToCall(eval(node.get(new SymbolValue("ast/invoke/what"))));
        if (what instanceof ObjectValue && (SYM_FUNC.equals(((ObjectValue) what).ofClass()))) {
            return evalFunc((ObjectValue) what, node);
        }
        throw new UnsupportedOperationException("Cannot call: " + what);
    }

    private static Value evalList(@Nonnull ListValue value) {
        final ListValue r = new ListValue();
        for (int i = 0; i < value.size(); i++)
            r.add(eval(value.get(i)));
        return r;
    }

    private static Value evalFunc(@Nonnull ObjectValue func, @Nonnull ObjectValue invoke) {
        final ListValue argList = invoke.get(new SymbolValue("ast/invoke/args"), ListValue.class);


        final boolean special = func.getBoolean(FUNC_SPECIAL);
        final boolean builtin = func.getBoolean(FUNC_BUILTIN);
        if (builtin) {
            final SymbolValue name = func.getSymbol(CATALOG_NAME);
            if (name == null)
                throw new IllegalArgumentException("Builtin must have name: " + func);
            final Builtin impl = Builtins.get(name);
            final Value[] args = new Value[argList.size()];
            for (int i = 0; i < argList.size(); i++) {
                final ObjectValue argNode = argList.get(i, ObjectValue.class);
                final SymbolValue argName =
                        argNode.get(new SymbolValue("ast/invokeArg/name"), SymbolValue.class);
                if (argName != null)
                    throw new UnsupportedOperationException("TODO: support named arguments in built-ins");
                final Value argNodeValue = argNode.get(new SymbolValue("ast/invokeArg/value"));
                args[i] = special ? argNodeValue : eval(argNodeValue);
            }
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
