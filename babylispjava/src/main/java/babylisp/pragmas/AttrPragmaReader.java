package babylisp.pragmas;

import babylisp.ASTUtil;
import babylisp.LispReader;
import babylisp.PragmaReader;
import babylisp.values.ObjectValue;
import babylisp.values.SymbolValue;
import com.google.auto.service.AutoService;

import javax.annotation.Nonnull;

@AutoService(PragmaReader.class)
public final class AttrPragmaReader extends PragmaReader {

    private static final SymbolValue CLASS_ATTR = new SymbolValue("attr");

    @Override
    public SymbolValue name() {
        return new SymbolValue("attr");
    }

    @Override
    public ObjectValue readPragma(@Nonnull LispReader reader) {
        final SymbolValue attrName = reader.readSymbol();
        final ObjectValue o = new ObjectValue(CLASS_ATTR);
        ASTUtil.setName(o, attrName);
        ASTUtil.readAttrs(reader, o);
        return o;
    }
}
