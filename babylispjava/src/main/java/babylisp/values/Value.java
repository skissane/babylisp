package babylisp.values;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;

public abstract class Value implements Comparable<Value> {
    private final ValueType type;

    protected Value(@Nonnull ValueType type) {
        this.type = Objects.requireNonNull(type);
    }

    public static ValueType type(@Nullable Value value) {
        return value == null ? ValueType.VT_null : value.type;
    }

    public final String toString() {
        return doToString();
    }

    protected abstract String doToString();

    @Override
    public int compareTo(@Nonnull Value o) {
        return compare(this, o);
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        return obj instanceof Value && equals(this, (Value) obj);
    }

    @Override
    public int hashCode() {
        return hashCode(this);
    }

    public static int hashCode(@Nullable Value value) {
        return 31 * (31 + type(value).ordinal()) + (value == null ? 0 : value.doHashCode());
    }

    protected abstract int doHashCode();

    private static boolean equals(@Nullable Value a, @Nullable Value b) {
        return a == null && b == null ||
                a != null && b != null && a.type == b.type && a.doCompare(b) == 0;
    }

    public static int compare(@Nullable Value a, @Nullable Value b) {
        if (a == null && b == null)
            return 0;
        if (a == null)
            return -1;
        if (b == null)
            return 1;
        int tR = a.type.compareTo(b.type);
        if (tR != 0)
            return tR;
        return a.doCompare(b);
    }

    protected abstract int doCompare(@Nonnull Value b);

    protected abstract Value doCopy();

    @SuppressWarnings("unchecked")
    public static <T extends Value> T copy(@Nullable T v) {
        return v == null ? null : (T) v.doCopy();
    }

    public static <T extends Value> T mutable(@Nullable T v) {
        if (v == null || v.immutable() || !(v instanceof ComplexValue))
            return v;
        final ComplexValue cv = (ComplexValue) v;
        if (cv.parent() == null)
            return v;
        return copy(v);
    }

    public abstract boolean immutable();

    protected void doImmute() {
    }

    public static <T extends Value> T immute(@Nullable T value) {
        if (value instanceof ComplexValue) {
            final T copy = mutable(value);
            copy.doImmute();
            return copy;
        }
        return value;
    }

    public static String toString(@Nullable Value v) {
        return v == null ? "*" : v.toString();
    }
}
