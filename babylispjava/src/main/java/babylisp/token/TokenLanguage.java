package babylisp.token;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.function.Function.identity;

public final class TokenLanguage {
    private final Map<TokenState, TokenStateRules> stateRules;

    TokenLanguage(@Nonnull TokenStateRules... stateRules) {
        this.stateRules = Collections.unmodifiableMap(Arrays.stream(stateRules)
                .collect(Collectors.toMap(TokenStateRules::state, identity())));
    }

    public Map<TokenState, TokenStateRules> stateRules() {
        return stateRules;
    }
}
