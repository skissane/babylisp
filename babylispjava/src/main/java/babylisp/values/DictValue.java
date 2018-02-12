package babylisp.values;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DictValue extends ComplexValue {
    private final SortedMap<SimpleValue, Value> values = new TreeMap<>();

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
        final Iterator<Map.Entry<SimpleValue, Value>> ia = va.values.entrySet().iterator();
        final Iterator<Map.Entry<SimpleValue, Value>> ib = vb.values.entrySet().iterator();
        while (true) {
            final boolean ha = ia.hasNext();
            final boolean hb = ib.hasNext();
            if (!ha && !hb)
                return 0;
            if (!ha)
                return -1;
            if (!hb)
                return 1;
            final Map.Entry<SimpleValue, Value> ea = ia.next();
            final Map.Entry<SimpleValue, Value> eb = ib.next();
            final SimpleValue ka = ea.getKey();
            final SimpleValue kb = eb.getKey();
            final int rk = Value.compare(ka, kb);
            if (rk != 0)
                return rk;
            final Value la = ea.getValue();
            final Value lb = eb.getValue();
            final int rl = Value.compare(la, lb);
            if (rl != 0)
                return rl;
        }
    }

    @Override
    protected DictValue doCopy() {
        final DictValue copy = new DictValue();
        values.forEach(copy::set);
        return copy;
    }

    public Value set(@Nonnull SimpleValue key, @Nullable Value value) {
        ensureMutable();
        return orphan(values.put(key, adopt(value)));
    }

    @Override
    public int size() {
        return values.size();
    }
}
