package babylisp.values;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

public class ListValue extends ComplexValue {
    private final List<Value> values = new ArrayList<>();

    public ListValue() {
        super(ValueType.VT_list);
    }

    @Override
    protected String doToString() {
        return "{" + values.stream().map(Object::toString).collect(Collectors.joining(" ")) + "}";
    }

    @Override
    protected int doHashCode() {
        return Arrays.hashCode(values.stream().mapToInt(Object::hashCode).toArray());
    }

    @Override
    protected int doCompare(@Nonnull Value b) {
        final ListValue va = this, vb = (ListValue) b;
        final Iterator<Value> ia = va.values.iterator();
        final Iterator<Value> ib = vb.values.iterator();
        while (true) {
            final boolean ha = ia.hasNext();
            final boolean hb = ib.hasNext();
            if (!ha && !hb)
                return 0;
            if (!ha)
                return -1;
            if (!hb)
                return 1;
            final Value ea = ia.next();
            final Value eb = ib.next();
            final int r = Value.compare(ea, eb);
            if (r != 0)
                return r;
        }
    }

    public void add(@Nullable Value v) {
        ensureMutable();
        values.add(this.adopt(v));
    }

    @Override
    protected ListValue doCopy() {
        final ListValue copy = new ListValue();
        values.forEach(copy::add);
        return copy;
    }

    @Override
    public int size() {
        return values.size();
    }

    public Value get(int index) {
        return values.get(index);
    }
}
