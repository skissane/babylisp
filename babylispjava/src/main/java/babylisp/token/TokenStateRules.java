package babylisp.token;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Objects;

public final class TokenStateRules {
    private final TokenState state;
    private final List<TokenRule> rules;

    private TokenStateRules(@Nonnull TokenState state, @Nonnull TokenRule... rules) {
        this.state = Objects.requireNonNull(state);
        this.rules = List.of(rules);
    }

    public static TokenStateRules of(@Nonnull TokenState state, @Nonnull TokenRule... rules) {
        return new TokenStateRules(state, rules);
    }

    public TokenState state() {
        return state;
    }

    public List<TokenRule> rules() {
        return rules;
    }
}
