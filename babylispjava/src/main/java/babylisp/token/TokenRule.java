package babylisp.token;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class TokenRule {
    private final Pattern pattern;
    private final TokenType token;
    private final TokenState gotoState;
    private final boolean popState;
    private final Function<Matcher, String> extractValue;

    private TokenRule(@Nonnull String pattern,
                      @Nullable TokenType token,
                      @Nullable TokenState gotoState, boolean popState,
                      @Nullable Function<Matcher, String> extractValue) {
        this.pattern = Pattern.compile(pattern);
        this.token = token;
        this.gotoState = gotoState;
        this.popState = popState;
        this.extractValue = extractValue;
    }

    public static TokenRule of(@Nonnull String pattern,
                               @Nonnull TokenType token,
                               @Nullable TokenState gotoState,
                               @Nullable Function<Matcher, String> extractValue) {
        return new TokenRule(pattern, Objects.requireNonNull(token), gotoState, false, extractValue);
    }

    public static TokenRule of(@Nonnull String pattern,
                               @Nonnull TokenType token,
                               @Nonnull Function<Matcher, String> extractValue) {
        return new TokenRule(pattern, Objects.requireNonNull(token), null, false, extractValue);
    }

    public static TokenRule ofPopState(@Nonnull String pattern,
                                       @Nonnull TokenType token,
                                       @Nullable Function<Matcher, String> extractValue) {
        return new TokenRule(pattern, Objects.requireNonNull(token), null, true, extractValue);
    }

    public static TokenRule ofPopState(@Nonnull String pattern,
                                       @Nonnull TokenType token) {
        return new TokenRule(pattern, Objects.requireNonNull(token), null, true, null);
    }

    public static TokenRule of(@Nonnull String pattern,
                               @Nullable TokenState gotoState) {
        return new TokenRule(pattern, null, gotoState, false, null);
    }

    public static TokenRule ofPopState(@Nonnull String pattern) {
        return new TokenRule(pattern, null, null, true, null);
    }

    public static TokenRule of(@Nonnull String pattern, @Nonnull TokenType token) {
        return new TokenRule(pattern, token, null, false, null);
    }

    public static TokenRule of(@Nonnull String pattern) {
        return new TokenRule(pattern, null, null, false, null);
    }

    public static TokenRule of(@Nonnull String pattern,
                               @Nonnull TokenType token,
                               @Nullable TokenState gotoState) {
        return new TokenRule(pattern, Objects.requireNonNull(token), gotoState, false, null);
    }

    public Pattern pattern() {
        return pattern;
    }

    public TokenType token() {
        return token;
    }

    public TokenState gotoState() {
        return gotoState;
    }

    public boolean popState() {
        return popState;
    }

    public Function<Matcher, String> extractValue() {
        return extractValue;
    }

    @Override
    public String toString() {
        return "TokenRule(" +
                "pattern=" + pattern +
                ":token=" + token +
                ":gotoState=" + gotoState +
                ":popState=" + popState +
                ":extractValue=" + extractValue +
                ")";
    }
}
