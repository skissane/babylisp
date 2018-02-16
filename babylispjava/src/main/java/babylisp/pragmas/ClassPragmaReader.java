package babylisp.pragmas;

import babylisp.PragmaReader;
import babylisp.token.TokenReader;
import babylisp.values.ListValue;
import babylisp.values.ObjectValue;
import babylisp.values.SymbolValue;
import babylisp.values.Value;
import com.google.auto.service.AutoService;

import javax.annotation.Nonnull;

import static babylisp.token.TokenType.*;

@AutoService(PragmaReader.class)
public final class ClassPragmaReader extends PragmaReader {

    public static final SymbolValue CLASS_CLASS = new SymbolValue("class");
    public static final SymbolValue CLASS_EXTENDS = new SymbolValue("class/extends");

    @Override
    public SymbolValue name() {
        return new SymbolValue("class");
    }

    @Override
    public Value readPragma(@Nonnull TokenReader reader) {
        final SymbolValue className = new SymbolValue(reader.expect(TT_symbol).tokenValue());
        reader.expect(TT_parenBegin);
        final ListValue extending = new ListValue();
        while (reader.swallow(TT_parenEnd) == null) {
            extending.add(new SymbolValue(reader.expect(TT_symbol).tokenValue()));
        }
        final ObjectValue o  = new ObjectValue(CLASS_CLASS);
        o.set(CLASS_EXTENDS, extending);
        if (reader.match(TT_bracketEnd)!=null)
            return o;
        throw reader.syntaxError("TODO");
    }
}
