package babylisp;

import babylisp.token.BabyLispTokenLanguage;
import babylisp.token.TokenEvent;
import babylisp.token.TokenReader;
import babylisp.values.*;

import javax.annotation.Nonnull;
import java.util.function.Consumer;

import static babylisp.token.TokenType.*;

public class LispReader {
    private final TokenReader reader;

    private LispReader(@Nonnull String text) {
        this.reader = new TokenReader(BabyLispTokenLanguage.BABYLISP, text);
    }

    private String escapeString(@Nonnull String str) {
        return "\"" + str.replace("\\", "\\\\").replace("\"", "\\\"") + "\"";
    }

    public static void read(@Nonnull String text, @Nonnull Consumer<Value> executor) {
        final LispReader rdr = new LispReader(text);
        while (!rdr.reader.matchEOF())
            executor.accept(rdr.read());
        rdr.reader.expectEOF();
    }

    private Value read() {
        final TokenEvent e = reader.peek(0);
        if (e != null)
            switch (e.tokenType()) {
                case TT_dictBegin:
                    return readDict();
                case TT_listBegin:
                    return readList();
                case TT_dqStrBegin:
                    return readString();
                case TT_symbol:
                    return readSymbol();
                case TT_integer:
                    return readInteger();
                case TT_objectBegin:
                    return readObject();
                case TT_pragmaBegin:
                    return readPragma();
            }
        throw reader.syntaxError("illegal syntax, got " + reader.got());
    }

    private Value readPragma() {
        reader.expect(TT_pragmaBegin);
        final TokenEvent eName = reader.expect(TT_symbol);
        final SymbolValue name = new SymbolValue(eName.tokenValue());
        final PragmaReader pragmaReader = PragmaReader.readers().get(name);
        if (pragmaReader == null)
            throw reader.syntaxError("unrecognised pragma #[" + name + "]");
        final Value pragma = pragmaReader.readPragma(reader);
        reader.expect(TT_bracketEnd);
        return pragma;
    }

    private ObjectValue readObject() {
        reader.expect(TT_objectBegin);
        final SymbolValue ofClass = readSymbol();
        final ObjectValue o = new ObjectValue(ofClass);
        while (reader.swallow(TT_bracketEnd) == null) {
            reader.expectNotEOF();
            final TokenEvent plusKeyword = reader.swallow(TT_plusKeyword);
            if (plusKeyword != null) {
                o.set(new SymbolValue(plusKeyword.tokenValue()), SymbolValue.TRUE);
            } else {
                final TokenEvent keyword = reader.expect(TT_keyword);
                o.set(new SymbolValue(keyword.tokenValue()), read());
            }
        }
        return o;
    }

    private SymbolValue readSymbol() {
        return new SymbolValue(reader.expect(TT_symbol).tokenValue());
    }

    private Value readInteger() {
        return new IntegerValue(Long.parseLong(reader.expect(TT_integer).tokenValue()));
    }


    private StringValue readString() {
        final StringBuilder b = new StringBuilder();
        reader.expect(TT_dqStrBegin);
        while (reader.swallow(TT_dqStrEnd) == null) {
            final TokenEvent e = reader.read();
            if (e == null)
                throw reader.syntaxError("unexpected EOF");
            switch (e.tokenType()) {
                case TT_dqStrEscape:
                case TT_dqStrHexEscape:
                case TT_dqStrUnescaped: {
                    b.append(e.tokenValue());
                    break;
                }
                default:
                    throw reader.syntaxError("unexpected " + e.tokenType());
            }
        }
        return new StringValue(b.toString());
    }

    private ListValue readList() {
        final ListValue r = new ListValue();
        reader.expect(TT_listBegin);
        while (true) {
            reader.expectNotEOF();
            if (reader.swallow(TT_braceEnd) != null)
                return r;
            r.add(read());
        }
    }

    private DictValue readDict() {
        final DictValue r = new DictValue();
        reader.expect(TT_dictBegin);
        while (true) {
            reader.expectNotEOF();
            if (reader.swallow(TT_braceEnd) != null)
                return r;
            final Value key = read();
            final Value value = read();
            r.set((SimpleValue) key, value);
        }
    }
}
