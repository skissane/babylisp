package babylisp.token;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import java.util.Objects;

public class TokenEvent {
    private final TokenText text;
    private final int start, end;
    private final TokenState tokenState;
    private final TokenType tokenType;
    private final String tokenValue;

    TokenEvent(@Nonnull TokenText text, @Nonnegative int start, @Nonnegative int end,
               @Nonnull TokenState tokenState, @Nonnull TokenType tokenType,
               @Nonnull String tokenValue) {
        this.text = Objects.requireNonNull(text);
        if (end < start)
            throw new IllegalArgumentException();
        this.start = start;
        this.end = end;
        this.tokenState = Objects.requireNonNull(tokenState);
        this.tokenType = Objects.requireNonNull(tokenType);
        this.tokenValue = Objects.requireNonNull(tokenValue);
    }

    public TokenText text() {
        return text;
    }

    public int start() {
        return start;
    }

    public int end() {
        return end;
    }

    public TokenState tokenState() {
        return tokenState;
    }

    public TokenType tokenType() {
        return tokenType;
    }

    public String tokenValue() {
        return tokenValue;
    }
}
