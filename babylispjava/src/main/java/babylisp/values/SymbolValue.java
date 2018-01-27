package babylisp.values;

import javax.annotation.Nonnull;
import java.util.regex.Pattern;

public final class SymbolValue extends SimpleValue {
    private final String name;

    public static final Pattern VALID_SYMBOL =
            Pattern.compile("^([$=][A-Za-z_][A-Za-z0-9_]*|[A-Za-z_][A-za-z0-9_]*(/[A-Za-z_][A-za-z0-9_]*)*)$");

    public SymbolValue(@Nonnull String name) {
        super(ValueType.VT_symbol);
        if (!VALID_SYMBOL.matcher(name).matches())
            throw new IllegalArgumentException("Bad symbol name syntax '" + name + "'");
        this.name = name;
    }

    public String value() {
        return name;
    }

    @Override
    protected String doToString() {
        return name;
    }

    @Override
    protected int doHashCode() {
        return name.hashCode();
    }

    @Override
    protected int doCompare(@Nonnull Value b) {
        return name.compareTo(((SymbolValue) b).name);
    }
}
