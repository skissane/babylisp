package babylisp.values;

import javax.annotation.Nonnull;
import java.util.Arrays;

public final class BinaryValue extends SimpleValue {
    private final byte[] data;

    public BinaryValue(@Nonnull byte[] data) {
        super(ValueType.VT_binary);
        this.data = data.clone();
    }

    public byte[] value() {
        return data.clone();
    }

    @Override
    protected String doToString() {
        final StringBuilder b = new StringBuilder("#(");
        for (byte v : data)
            b.append(String.format("%02x", v));
        b.append(")");
        return b.toString();
    }

    @Override
    protected int doHashCode() {
        return Arrays.hashCode(data);
    }

    @Override
    protected int doCompare(@Nonnull Value b) {
        final BinaryValue va = this, vb = (BinaryValue) b;
        for (int i = 0; ; i++) {
            final boolean ha = i < va.data.length;
            final boolean hb = i < vb.data.length;
            if (!ha && !hb)
                return 0;
            if (!ha)
                return -1;
            if (!hb)
                return 1;
            final int r = Byte.compare(va.data[i], vb.data[i]);
            if (r != 0)
                return r;
        }
    }
}
