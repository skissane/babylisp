package babylisp.values;

import javax.annotation.Nonnull;

public final class StringValue extends SimpleValue {
    private final String value;

    public StringValue(@Nonnull String value) {
        super(ValueType.VT_string);
        this.value = value;
    }

    public String value() {
        return value;
    }

    @Override
    protected String doToString() {
        return "\"" + value.replace("\\", "\\\\").replace("\"", "\\\"") + "\"";
    }

    @Override
    protected int doHashCode() {
        return value.hashCode();
    }

    @Override
    protected int doCompare(@Nonnull Value b) {
        return value.compareTo(((StringValue) b).value);
    }
}
