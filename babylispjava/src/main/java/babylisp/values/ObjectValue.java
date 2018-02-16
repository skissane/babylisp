package babylisp.values;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class ObjectValue extends ComplexValue {
    private final SymbolValue CLASS_SYM = new SymbolValue("ofClass");

    private final SortedMap<SymbolValue, Value> values = new TreeMap<>();

    public ObjectValue(@Nonnull String ofClass) {
        this(new SymbolValue(ofClass));
    }

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
        final Iterator<Map.Entry<SymbolValue, Value>> ia = va.values.entrySet().iterator();
        final Iterator<Map.Entry<SymbolValue, Value>> ib = vb.values.entrySet().iterator();
        while (true) {
            final boolean ha = ia.hasNext();
            final boolean hb = ib.hasNext();
            if (!ha && !hb)
                return 0;
            if (!ha)
                return -1;
            if (!hb)
                return 1;
            final Map.Entry<SymbolValue, Value> ea = ia.next();
            final Map.Entry<SymbolValue, Value> eb = ib.next();
            final SymbolValue ka = ea.getKey();
            final SymbolValue kb = eb.getKey();
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

    public Value set(@Nonnull String attr, @Nullable Value value) {
        return set(new SymbolValue(attr), value);
    }

    public Value set(@Nonnull SymbolValue attr, @Nullable Value value) {
        ensureMutable();
        if (CLASS_SYM.equals(attr)) {
            if (ofClass().equals(value))
                return value;
            throw new IllegalStateException("Cannot change class of object");
        }
        return orphan(values.put(attr, adopt(value)));
    }

    public Value get(@Nonnull SymbolValue attr) {
        return values.get(attr);
    }

    public <T extends Value> T get(@Nonnull SymbolValue attr, @Nonnull Class<T> cls) {
        return cls.cast(get(attr));
    }

    @Override
    protected ObjectValue doCopy() {
        final ObjectValue copy = new ObjectValue(ofClass());
        values.forEach(copy::set);
        return copy;
    }

    @Override
    public int size() {
        return values.size();
    }

    public String getString(@Nonnull SymbolValue attr) {
        final StringValue stringValue = (StringValue) get(attr);
        return stringValue == null ? null : stringValue.value();
    }

    public boolean getBoolean(@Nonnull SymbolValue attr) {
        final SymbolValue value = (SymbolValue) get(attr);
        if (value == null)
            return false;
        if (new SymbolValue("false").equals(value))
            return false;
        if (new SymbolValue("true").equals(value))
            return true;
        throw new IllegalArgumentException("Value not boolean: " + value);
    }

    public SymbolValue getSymbol(@Nonnull SymbolValue attr) {
        return (SymbolValue) get(attr);
    }
}
