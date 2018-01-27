package babylisp.values;

import javax.annotation.Nonnull;
import java.util.List;

public abstract class ComplexValue extends Value implements SimpleOrComplex {
    protected ComplexValue(@Nonnull ValueType type) {
        super(type);
    }

    private ComplexValue parent;
    private List<HandleValue> direct;
    private List<HandleValue> root;
}
