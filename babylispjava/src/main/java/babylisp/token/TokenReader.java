package babylisp.token;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;

public class TokenReader {
    private final TokenLanguage language;
    private final TokenText text;
    private TokenState state;
    private int pos = 0;
    private final List<TokenState> stateStack = new ArrayList<>();
    private final List<TokenEvent> lookahead = new ArrayList<TokenEvent>();

    public TokenReader(@Nonnull TokenLanguage language, @Nonnull String text) {
        this.language = Objects.requireNonNull(language);
        this.text = new TokenText(text);
        this.state = TokenState.values()[0];
    }

    public static List<TokenEvent> readAll(@Nonnull TokenLanguage language, @Nonnull String text) {
        final TokenReader r = new TokenReader(language, text);
        final List<TokenEvent> events = new ArrayList<>();
        while (true) {
            final TokenEvent e = r.read();
            if (e == null)
                return events;
            events.add(e);
        }
    }

    public TokenEvent peek(@Nonnegative int off) {
        while (lookahead.size() <= off) {
            final TokenEvent e = read0();
            if (e == null)
                return null;
            lookahead.add(e);
        }
        return lookahead.get(off);
    }

    public TokenEvent match(@Nonnull TokenType type) {
        final TokenEvent e = peek(0);
        return e != null && e.tokenType() == type ? e : null;
    }

    public TokenEvent swallow(@Nonnull TokenType type) {
        final TokenEvent e = match(type);
        if (e != null)
            read();
        return e;
    }

    public TokenEvent expect(@Nonnull TokenType type) {
        final TokenEvent e = swallow(type);
        if (e == null) {
            throw syntaxError("expected " + type + ", got " + got());
        }
        return e;
    }

    public String got() {
        final TokenEvent peek = peek(0);
        return peek == null ? "<eof>" : peek.tokenType().name();
    }

    public TokenEvent read() {
        if (!lookahead.isEmpty())
            return lookahead.remove(0);
        return read0();
    }

    private TokenEvent read0() {
        final TokenStateRules rules = language.stateRules().get(state);
        OUTER:
        while (true) {
            if (peek() == 0xFFFF)
                return null;
            for (TokenRule rule : rules.rules()) {
                final Matcher m = rule.pattern().matcher(text.text());
                m.region(pos, text.length());
                if (m.lookingAt()) {
                    final int matchLength = m.group(0).length();
                    final String value = rule.extractValue() != null ? rule.extractValue().apply(m) :
                            m.group(0);
                    final TokenType tokenType = rule.token();
                    final TokenEvent e = tokenType != null ? new TokenEvent(text, pos, pos + matchLength,
                            state, tokenType, value) : null;
                    advance(matchLength);
                    final TokenState gotoState = rule.gotoState();
                    if (gotoState != null) {
                        stateStack.add(state);
                        state = gotoState;
                    }
                    if (rule.popState()) {
                        if (stateStack.isEmpty())
                            throw syntaxError("state stack underflow in rule " + rule);
                        state = stateStack.remove(stateStack.size() - 1);
                    }
                    if (e != null)
                        return e;
                    continue OUTER;
                }
            }
            throw syntaxError("no token rule matched in state " + state);
        }
    }

    private char peek() {
        return text.charAt(pos);
    }

    private void advance(int off) {
        if (off <= 0)
            throw new IllegalArgumentException();
        final String passed = text.substring(pos, Math.min(pos + off, text.length()));
        pos += passed.length();
    }

    public RuntimeException syntaxError(@Nonnull String why) {
        final int pos = lookahead.isEmpty() ? this.pos : lookahead.get(0).end();
        final int line = text.getLineForOffset(pos);
        final int col = text.getColumnForOffset(pos);
        assert line >= 1 : "line >=1 : line=" + line;
        assert col >= 1 : "col >= 1 : col=" + col + " in " + text;
        String lineText = text.getTextForLine(line);
        String msg = "(" + line + "," + col + ") syntax error: " + why + "\n" +
                line + ":" + lineText + "\n" +
                String.join("", Collections.nCopies(Integer.toString(line).length(), " ")) +
                ":" + String.join("", Collections.nCopies(col - 1, " ")) + "^";
        return new RuntimeException(msg);
    }

    public boolean matchEOF() {
        return peek(0) == null;
    }

    public void expectEOF() {
        if (!matchEOF())
            throw syntaxError("expected EOF, got " + got());
    }

    public void expectNotEOF() {
        if (matchEOF())
            throw syntaxError("unexpected EOF");
    }
}
