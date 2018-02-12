package babylisp.values;

import javax.annotation.Nonnull;
import java.util.Objects;

public final class HandleValue extends Value {
    public HandleValue(@Nonnull ComplexValue to) {
        super(ValueType.VT_handle);
        this.to = Objects.requireNonNull(to);
    }

    private final ComplexValue to;
    private ComplexValue root;

    public ComplexValue to() {
        return to;
    }

    @Override
    protected String doToString() {
        return to.toString();
    }

    @Override
    protected int doHashCode() {
        return to.hashCode();
    }

    @Override
    protected int doCompare(@Nonnull Value b) {
        return to.compareTo(((HandleValue) b).to);
    }

    @Override
    public boolean immutable() {
        return to.immutable();
    }

    @Override
    public Value doCopy() {
        return new HandleValue(Value.copy(to));
    }
}
