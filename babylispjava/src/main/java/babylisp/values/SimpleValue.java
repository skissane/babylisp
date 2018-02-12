package babylisp.values;

import javax.annotation.Nonnull;

public abstract class SimpleValue extends Value {
    public SimpleValue(@Nonnull ValueType type) {
        super(type);
        if (type.category() != ValueCategory.VC_simple)
            throw new IllegalStateException();
    }

    @Override
    public SimpleValue doCopy() {
        return this;
    }

    @Override
    public boolean immutable() {
        return true;
    }
}
