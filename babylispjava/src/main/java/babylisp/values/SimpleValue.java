package babylisp.values;

import javax.annotation.Nonnull;

public abstract class SimpleValue extends Value implements SimpleOrHandle, SimpleOrComplex {
    public SimpleValue(@Nonnull ValueType type) {
        super(type);
        if (type.category() != ValueCategory.VC_simple)
            throw new IllegalStateException();
    }
}
