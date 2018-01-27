package babylisp.values;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

public class ListValue extends ComplexValue {
    private final List<SimpleOrComplex> values = new ArrayList<>();

    public ListValue() {
        super(ValueType.VT_list);
    }

    @Override
    protected String doToString() {
        return "(" + values.stream().map(Object::toString).collect(Collectors.joining(" ")) + ")";
    }

    @Override
    protected int doHashCode() {
        return Arrays.hashCode(values.stream().mapToInt(Object::hashCode).toArray());
    }

    @Override
    protected int doCompare(@Nonnull Value b) {
        final ListValue va = this, vb = (ListValue) b;
        final Iterator<SimpleOrComplex> ia = va.values.iterator();
        final Iterator<SimpleOrComplex> ib = vb.values.iterator();
        while (true) {
            final boolean ha = ia.hasNext();
            final boolean hb = ib.hasNext();
            if (!ha && !hb)
                return 0;
            if (!ha)
                return -1;
            if (!hb)
                return 1;
            final SimpleOrComplex ea = ia.next();
            final SimpleOrComplex eb = ib.next();
            final int r = Value.compare((Value) ea, (Value) eb);
            if (r != 0)
                return r;
        }
    }
}
