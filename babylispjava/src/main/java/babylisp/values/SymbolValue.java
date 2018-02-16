package babylisp.values;

import com.google.common.collect.ImmutableList;

import javax.annotation.Nonnull;
import java.util.regex.Pattern;

public final class SymbolValue extends SimpleValue {

    private final String name;

    public static final Pattern VALID_SYMBOL =
            Pattern.compile("^([$=][A-Za-z_][A-Za-z0-9_]*|[A-Za-z_][A-za-z0-9_]*(/[A-Za-z_][A-za-z0-9_]*)*|/)$");

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

    public static final SymbolValue TRUE = new SymbolValue("true");
    public static final SymbolValue FALSE = new SymbolValue("false");
    public static final SymbolValue ROOT = new SymbolValue("/");

    public ImmutableList<String> segments() {
        return ImmutableList.copyOf(name.split("/"));
    }

    public SymbolValue parent() {
        if (ROOT.equals(this))
            return null;
        final ImmutableList<String> segments = segments();
        if (segments.size() <= 1)
            return ROOT;
        return new SymbolValue(String.join("/", segments.subList(0, segments.size() - 1)));
    }

    public String ownName() {
        if (ROOT.equals(this))
            return name;
        final ImmutableList<String> segments = segments();
        if (segments.size() <= 1)
            return name;
        return segments.get(segments.size() - 1);
    }
}
