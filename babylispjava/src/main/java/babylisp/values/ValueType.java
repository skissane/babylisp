package babylisp.values;

import javax.annotation.Nonnull;
import java.util.Objects;

import static babylisp.values.ValueCategory.*;

public enum ValueType {
    VT_null(VC_simple),
    VT_string(VC_simple),
    VT_binary(VC_simple),
    VT_symbol(VC_simple),
    VT_integer(VC_simple),
    VT_handle(VC_handle),
    VT_list(VC_complex),
    VT_dict(VC_complex),
    VT_object(VC_complex);

    private final ValueCategory category;

    ValueType(@Nonnull ValueCategory category) {
        this.category = Objects.requireNonNull(category);
    }

    public ValueCategory category() {
        return category;
    }
}
