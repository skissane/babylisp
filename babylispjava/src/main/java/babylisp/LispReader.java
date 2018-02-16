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

    public ObjectValue read() {
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
                    return readSymbolAST();
                case TT_integer:
                    return readIntegerAST();
                case TT_objectBegin:
                    return readObject();
                case TT_pragmaBegin:
                    return readPragma();
                case TT_parenBegin:
                    return readInvoc();
                case TT_var:
                    return readVar();
            }
        throw reader.syntaxError("illegal syntax, got " + reader.got());
    }

    private ObjectValue readVar() {
        return varNode(new SymbolValue(reader.expect(TT_var).tokenValue()));
    }

    private ObjectValue readInvoc() {
        reader.expect(TT_parenBegin);
        final ObjectValue o = new ObjectValue("ast/invoke");
        o.set("ast/invoke/what", read());
        final ListValue args = new ListValue();
        o.set("ast/invoke/args", args);
        while (true) {
            reader.expectNotEOF();
            if (reader.match(TT_parenEnd) != null)
                break;
            else if (reader.match(TT_plusKeyword) != null) {
                final TokenEvent plusKeyword = reader.swallow(TT_plusKeyword);
                args.add(invocNamedArg(new SymbolValue("$" + plusKeyword.tokenValue()), constTrue()));
            } else if (reader.match(TT_keyword) != null) {
                final TokenEvent keyword = reader.swallow(TT_keyword);
                args.add(invocNamedArg(new SymbolValue("$" + keyword.tokenValue()), read()));
            } else
                args.add(invocPosArg(read()));
        }
        reader.expect(TT_parenEnd);
        return o;
    }

    private ObjectValue invocNamedArg(@Nonnull SymbolValue argName, @Nonnull ObjectValue value) {
        final ObjectValue o = new ObjectValue("ast/invokeArg");
        o.set("ast/invokeArg/name", argName);
        o.set("ast/invokeArg/value", value);
        return o;
    }

    private ObjectValue invocPosArg(@Nonnull ObjectValue value) {
        final ObjectValue o = new ObjectValue("ast/invokeArg");
        o.set("ast/invokeArg/value", value);
        return o;
    }

    private ObjectValue constTrue() {
        return constValue("symbol", SymbolValue.TRUE);
    }

    private ObjectValue readPragma() {
        reader.expect(TT_pragmaBegin);
        final TokenEvent eName = reader.expect(TT_symbol);
        final SymbolValue name = new SymbolValue(eName.tokenValue());
        final PragmaReader pragmaReader = PragmaReader.readers().get(name);
        if (pragmaReader == null)
            throw reader.syntaxError("unrecognised pragma #[" + name + "]");
        final ObjectValue pragma = pragmaReader.readPragma(this);
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

    public SymbolValue readSymbol() {
        return new SymbolValue(reader.expect(TT_symbol).tokenValue());
    }

    private ObjectValue readSymbolAST() {
        return constValue("symbol", readSymbol());
    }

    private ObjectValue readIntegerAST() {
        return constValue("integer", readInteger());
    }

    private Value readInteger() {
        return new IntegerValue(Long.parseLong(reader.expect(TT_integer).tokenValue()));
    }

    private static ObjectValue constValue(@Nonnull String type, @Nonnull Value value) {
        final ObjectValue r = new ObjectValue("ast/" + type + "Const");
        r.set("ast/" + type + "Const/value", value);
        return r;
    }

    private static ObjectValue varNode(@Nonnull SymbolValue varName) {
        final ObjectValue r = new ObjectValue("ast/var");
        r.set("ast/var/name", varName);
        return r;
    }

    private ObjectValue readString() {
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
        return constValue("string", new StringValue(b.toString()));
    }

    private ObjectValue readList() {
        final ObjectValue r = new ObjectValue("ast/list");
        final ListValue entries = new ListValue();
        r.set("ast/list/entries", entries);
        reader.expect(TT_listBegin);
        while (true) {
            reader.expectNotEOF();
            if (reader.swallow(TT_braceEnd) != null)
                return r;
            entries.add(read());
        }
    }

    private ObjectValue readDict() {
        final ObjectValue r = new ObjectValue("ast/dict");
        final ListValue entries = new ListValue();
        r.set("ast/dict/entries", entries);
        reader.expect(TT_dictBegin);
        while (true) {
            reader.expectNotEOF();
            if (reader.swallow(TT_braceEnd) != null)
                return r;
            final Value key = read();
            final Value value = read();
            final ObjectValue e = new ObjectValue("ast/dictEntry");
            e.set(new SymbolValue("ast/dictEntry/key"), key);
            e.set(new SymbolValue("ast/dictEntry/value"), value);
            entries.add(e);
        }
    }

    public TokenReader tokens() {
        return reader;
    }
}
