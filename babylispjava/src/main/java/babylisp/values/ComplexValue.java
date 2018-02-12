package babylisp.values;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public abstract class ComplexValue extends Value {
    protected ComplexValue(@Nonnull ValueType type) {
        super(type);
    }

    private boolean immutable = false;
    private ComplexValue parent;
    private List<HandleValue> direct;
    private List<HandleValue> root;

    public ComplexValue parent() {
        return parent;
    }

    @SuppressWarnings("unchecked")
    <T extends Value> T adopt(@Nullable T v) {
        if (v == null || (v instanceof SimpleValue))
            return v;
        if (v instanceof ComplexValue) {
            final ComplexValue cv = (ComplexValue) v;
            final ComplexValue copy = Value.mutable(cv);
            if (copy.parent != null)
                throw new IllegalStateException();
            copy.parent = this;
            return (T) copy;
        }
        throw new IllegalStateException("not valid in complex: " + Value.type(v));
    }

    <T extends Value> T orphan(@Nullable T v) {
        if (v instanceof ComplexValue) {
            final ComplexValue cv = (ComplexValue) v;
            cv.parent = null;
        }
        return v;
    }

    @Override
    public boolean immutable() {
        if (parent != null)
            return parent.immutable();
        return immutable;
    }

    public abstract int size();

    @Override
    protected void doImmute() {
        if (parent != null)
            throw new IllegalStateException();
        immutable = true;
    }

    protected void ensureMutable() {
        if (immutable())
            throw new UnsupportedOperationException("Attempt to modify mutable value");
    }
}
