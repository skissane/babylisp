package babylisp.values;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DictValue extends ComplexValue {
    private final SortedMap<SimpleValue, SimpleOrComplex> values = new TreeMap<>();

    public DictValue() {
        super(ValueType.VT_dict);
    }

    @Override
    protected String doToString() {
        return "%{" +
                values.entrySet().stream()
                        .map(e -> "=" + e.getKey() + " " + e.getValue())
                        .collect(Collectors.joining(" ")) + "}";
    }

    @Override
    protected int doHashCode() {
        return Arrays.hashCode(
                values.entrySet().stream()
                        .flatMap(e -> Stream.of(e.getKey(), e.getValue()))
                        .mapToInt(Object::hashCode).toArray()
        );
    }

    @Override
    protected int doCompare(@Nonnull Value b) {
        final DictValue va = this, vb = (DictValue) b;
        final Iterator<Map.Entry<SimpleValue, SimpleOrComplex>> ia = va.values.entrySet().iterator();
        final Iterator<Map.Entry<SimpleValue, SimpleOrComplex>> ib = vb.values.entrySet().iterator();
        while (true) {
            final boolean ha = ia.hasNext();
            final boolean hb = ib.hasNext();
            if (!ha && !hb)
                return 0;
            if (!ha)
                return -1;
            if (!hb)
                return 1;
            final Map.Entry<SimpleValue, SimpleOrComplex> ea = ia.next();
            final Map.Entry<SimpleValue, SimpleOrComplex> eb = ib.next();
            final SimpleValue ka = ea.getKey();
            final SimpleValue kb = eb.getKey();
            final int rk = Value.compare(ka, kb);
            if (rk != 0)
                return rk;
            final SimpleOrComplex la = ea.getValue();
            final SimpleOrComplex lb = eb.getValue();
            final int rl = Value.compare((Value) la, (Value) lb);
            if (rl != 0)
                return rl;
        }
    }
}
