package babylisp;

import babylisp.values.*;

import javax.annotation.Nonnull;
import java.util.Collections;

public class LispReader {
    private final String text;
    private int pos = 0;
    private int line = 1, col = 1;

    public LispReader(@Nonnull String text) {
        this.text = text.replace("\r\n", "\n").replace("\r", "\n");
    }

    public char peek(int off) {
        final int w = pos + off;
        return w < 0 || w >= text.length() ? (char) 0xFFFF : text.charAt(w);
    }

    public boolean match(@Nonnull String str) {
        for (int i = 0; i < str.length(); i++) {
            if (peek(i) != str.charAt(i))
                return false;
        }
        return true;
    }

    public void advance(int off) {
        if (off <= 0)
            throw new IllegalArgumentException();
        final String passed = text.substring(pos, Math.min(pos + off, text.length()));
        final long lines = passed.chars().filter(c -> c == '\n').count();
        line += lines;
        if (lines > 0) {
            col += passed.length() - passed.lastIndexOf('\n');
        } else
            col += passed.length();
        pos += passed.length();
    }

    public boolean swallow(@Nonnull String str) {
        if (!match(str))
            return false;
        advance(str.length());
        return true;
    }

    public void expect(@Nonnull String str) {
        if (swallow(str))
            return;
        throw syntaxError("expected " + escapeString(str));
    }

    private String escapeString(@Nonnull String str) {
        return "\"" + str.replace("\\", "\\\\").replace("\"", "\\\"") + "\"";
    }

    public RuntimeException syntaxError(@Nonnull String why) {
        String lineText = text.split("\n")[line - 1];
        String msg = "(" + line + "," + col + ") syntax error: + " + why + "\n" +
                line + ":" + lineText + "\n" +
                String.join("", Collections.nCopies(Integer.toString(line).length(), "")) +
                ":" + Collections.nCopies(col - 1, " ") + "^";
        return new RuntimeException(msg);
    }

    public boolean matchEOF(int off) {
        return peek(off) == 0xFFFF;
    }

    public void skipWSP() {
        while (Character.isWhitespace(peek(0)))
            advance(1);
    }

    public static Value read(@Nonnull String text) {
        final LispReader rdr = new LispReader(text);
        rdr.skipWSP();
        final Value v = rdr.read();
        rdr.skipWSP();
        rdr.expectEOF();
        return v;
    }

    private void expectEOF() {
        if (!matchEOF(0))
            throw syntaxError("expected EOF");
    }

    private Value read() {
        if (match("%{"))
            return readDict();
        if (match("("))
            return readList();
        if (match("\""))
            return readString();
        if (matchSymbolStart())
            return readSymbol();
        if (matchDigit() || match("-"))
            return readInteger();
        if (match("["))
            return readObject();
        throw syntaxError("illegal syntax");

    }

    private ObjectValue readObject() {
        expect("[");
        final SymbolValue ofClass = readSymbol();
        final ObjectValue o = new ObjectValue(ofClass);
        skipWSP();
        while (!match("]")) {
            expectNotEOF();
            expect("=");
            final SymbolValue attr = readSymbol();
            skipWSP();
            final Value value = read();
            o.set(attr, value);
        }
        return o;
    }

    private SymbolValue readSymbol() {
        if (!matchSymbolStart())
            throw syntaxError("invalid symbol");
        final StringBuilder b = new StringBuilder();
        while (matchSymbolStart() || matchDigit()) {
            b.append(peek(0));
            advance(1);
        }
        return new SymbolValue(b.toString());
    }

    private Value readInteger() {
        final boolean negated = swallow("-");
        final StringBuilder b = new StringBuilder();
        while (matchDigit()) {
            b.append(peek(0));
            advance(1);
        }
        if (b.length() == 0)
            throw syntaxError("invalid integer syntax");
        final long r = Long.parseLong(b.toString());
        return new IntegerValue(negated ? -r : r);
    }

    private boolean matchDigit() {
        final char c = peek(0);
        return c >= '0' && c <= '9';
    }

    private StringValue readString() {
        final StringBuilder b = new StringBuilder();
        expect("\"");
        while (!swallow("\"")) {
            expectNotEOF();
            if (swallow("\\")) {
                expectNotEOF();
                char ch = peek(0);
                switch (ch) {
                    case 'n':
                        b.append("\n");
                        break;
                    case 'r':
                        b.append("\r");
                        break;
                    case 't':
                        b.append("\t");
                        break;
                    case '\\':
                        b.append("\\");
                        break;
                    case '\"':
                        b.append("\"");
                        break;
                    default:
                        throw syntaxError("unrecognised escape in quoted string");
                }
            } else {
                b.append(peek(0));
            }
            advance(1);
        }
        return new StringValue(b.toString());
    }

    private void expectNotEOF() {
        if (peek(0) == 0xFFFF)
            throw syntaxError("unexpected EOF");
    }

    private boolean matchSymbolStart() {
        char ch = peek(0);
        return ch >= 'A' && ch <= 'Z' ||
                ch >= 'a' && ch <= 'z' ||
                ch == '_' || ch == '$';
    }

    private ListValue readList() {
        final ListValue r = new ListValue();
        expect("(");
        while (true) {
            skipWSP();
            if (swallow(")"))
                return r;
            r.add(read());
        }
    }

    private DictValue readDict() {
        final DictValue r = new DictValue();
        expect("%{");
        while (true) {
            skipWSP();
            if (swallow("}"))
                return r;
            final Value key = read();
            skipWSP();
            final Value value = read();
            r.set((SimpleValue) key, value);
        }
    }
}
