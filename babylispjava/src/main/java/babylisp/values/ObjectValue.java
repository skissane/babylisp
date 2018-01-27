package babylisp.values;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class ObjectValue extends ComplexValue {
    private final SymbolValue CLASS_SYM = new SymbolValue("ofClass");

    private final SortedMap<SymbolValue, SimpleOrComplex> values = new TreeMap<>();

    public ObjectValue(@Nonnull SymbolValue ofClass) {
        super(ValueType.VT_object);
        values.put(CLASS_SYM, ofClass);
    }

    public SymbolValue ofClass() {
        return (SymbolValue) values.get(CLASS_SYM);
    }

    @Override
    protected String doToString() {
        return "[" + ofClass() + " " +
                values.entrySet().stream()
                        .filter(e -> !CLASS_SYM.equals(e.getKey()))
                        .map(e -> "=" + e.getKey() + " " + e.getValue())
                        .collect(Collectors.joining(" ")) + "]";
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
        final ObjectValue va = this, vb = (ObjectValue) b;
        final Iterator<Map.Entry<SymbolValue, SimpleOrComplex>> ia = va.values.entrySet().iterator();
        final Iterator<Map.Entry<SymbolValue, SimpleOrComplex>> ib = vb.values.entrySet().iterator();
        while (true) {
            final boolean ha = ia.hasNext();
            final boolean hb = ib.hasNext();
            if (!ha && !hb)
                return 0;
            if (!ha)
                return -1;
            if (!hb)
                return 1;
            final Map.Entry<SymbolValue, SimpleOrComplex> ea = ia.next();
            final Map.Entry<SymbolValue, SimpleOrComplex> eb = ib.next();
            final SymbolValue ka = ea.getKey();
            final SymbolValue kb = eb.getKey();
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
