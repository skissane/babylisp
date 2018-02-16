package babylisp.token;

import javax.annotation.Nonnull;
import java.util.regex.Matcher;

import static babylisp.token.TokenType.*;

public class BabyLispTokenLanguage {
    private static final String P_name = "[a-zA-Z_][A-Za-z_0-9]*";
    private static final String P_symbol = "/|/?" + P_name + "(/" + P_name + ")*";
    public static TokenLanguage BABYLISP = new TokenLanguage(
            TokenStateRules.of(TokenState.TS_default,
                    TokenRule.of("[ \t\n]+"),
                    TokenRule.of(P_symbol, TT_symbol),
                    TokenRule.of("[}]", TT_braceEnd),
                    TokenRule.of("%[{]", TT_dictBegin),
                    TokenRule.of("0|-?[1-9][0-9]*", TT_integer),
                    TokenRule.of("=(" + P_symbol + ")", TT_keyword, m -> m.group(1)),
                    TokenRule.of("[?]", TT_opt),
                    TokenRule.of("[&]", TT_rest),
                    TokenRule.of("\\[", TT_objectBegin),
                    TokenRule.of("#\\[", TT_pragmaBegin),
                    TokenRule.of("[$]" + P_name, TT_var),
                    TokenRule.of("[+](" + P_symbol + ")", TT_plusKeyword, m -> m.group(1)),
                    TokenRule.of("[(]", TT_parenBegin),
                    TokenRule.of("[)]", TT_parenEnd),
                    TokenRule.of("[{]", TT_listBegin),
                    TokenRule.of("\\]", TT_bracketEnd),
                    TokenRule.of("\"", TT_dqStrBegin, TokenState.TS_dqString)
            ),
            TokenStateRules.of(TokenState.TS_dqString,
                    TokenRule.ofPopState("\"", TT_dqStrEnd),
                    TokenRule.of("[^\"\\\\]+", TT_dqStrUnescaped),
                    TokenRule.of("\\\\([\\\\\"nrt])", TT_dqStrEscape,
                            BabyLispTokenLanguage::decodeStringEscape),
                    TokenRule.of("\\\\[{]([0-9a-fA-F]{1,6})[}]", TT_dqStrHexEscape,
                            BabyLispTokenLanguage::decodeStringHexEscape)
            )
    );

    private static String decodeStringHexEscape(@Nonnull Matcher m) {
        final int v = Integer.parseInt(m.group(1), 16);
        if (v < 0 || v >= Character.MAX_CODE_POINT)
            throw new IllegalStateException();
        return new String(Character.toChars(v));
    }

    private static String decodeStringEscape(@Nonnull Matcher m) {
        switch (m.group(1)) {
            case "n":
                return "\n";
            case "r":
                return "\r";
            case "t":
                return "\t";
            case "\\":
                return "\\";
            case "\"":
                return "\"";
            default:
                throw new IllegalStateException();
        }
    }
}
