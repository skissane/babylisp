package babylisp.values;

import javax.annotation.Nonnull;

public final class IntegerValue extends SimpleValue {
    private final long value;

    public IntegerValue(long value) {
        super(ValueType.VT_integer);
        this.value = value;
    }

    public long value() {
        return value;
    }

    @Override
    protected String doToString() {
        return Long.toString(value);
    }

    @Override
    protected int doHashCode() {
        return Long.hashCode(value);
    }

    @Override
    protected int doCompare(@Nonnull Value b) {
        return Long.compare(value, ((IntegerValue) b).value);
    }
}
