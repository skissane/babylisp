package babylisp.pragmas;

import babylisp.ASTUtil;
import babylisp.LispReader;
import babylisp.PragmaReader;
import babylisp.values.ListValue;
import babylisp.values.ObjectValue;
import babylisp.values.SymbolValue;
import com.google.auto.service.AutoService;

import javax.annotation.Nonnull;

import static babylisp.token.TokenType.*;

@AutoService(PragmaReader.class)
public final class ClassPragmaReader extends PragmaReader {

    private static final SymbolValue CLASS_CLASS = new SymbolValue("class");
    private static final SymbolValue CLASS_EXTENDS = new SymbolValue("class/extends");

    @Override
    public SymbolValue name() {
        return new SymbolValue("class");
    }

    @Override
    public ObjectValue readPragma(@Nonnull LispReader reader) {
        final SymbolValue className = reader.readSymbol();
        reader.tokens().expect(TT_parenBegin);
        final ListValue extending = new ListValue();
        while (reader.tokens().swallow(TT_parenEnd) == null) {
            extending.add(new SymbolValue(reader.tokens().expect(TT_symbol).tokenValue()));
        }
        final ObjectValue o = new ObjectValue(CLASS_CLASS);
        ASTUtil.setName(o, className);
        o.set(CLASS_EXTENDS, extending);
        ASTUtil.readAttrs(reader, o);
        return o;
    }
}
