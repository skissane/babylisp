package babylisp;

import babylisp.token.TokenEvent;
import babylisp.token.TokenReader;
import babylisp.values.ObjectValue;
import babylisp.values.StringValue;
import babylisp.values.SymbolValue;

import javax.annotation.Nonnull;

import static babylisp.token.TokenType.*;

public final class ASTUtil {

    private static final SymbolValue DBOBJECT_NAME = new SymbolValue("dbObject/name");
    private static final SymbolValue DBOBJECT_PARENT = new SymbolValue("dbObject/parent");

    private ASTUtil() {
    }

    public static void setName(@Nonnull ObjectValue astNode, @Nonnull SymbolValue name) {
        astNode.set(DBOBJECT_PARENT, name.parent());
        astNode.set(DBOBJECT_NAME, new StringValue(name.ownName()));
    }

    public static void readAttrs(@Nonnull LispReader reader, @Nonnull ObjectValue astNode) {
        final TokenReader tokens = reader.tokens();
        while (tokens.match(TT_bracketEnd) == null) {
            if (!readOneAttr(reader, astNode))
                throw tokens.syntaxError("expected attribute-value-pair");
        }
    }

    public static boolean readOneAttr(@Nonnull LispReader reader, @Nonnull ObjectValue astNode) {
        final TokenReader tokens = reader.tokens();
        tokens.expectNotEOF();
        final TokenEvent plusKeyword = tokens.swallow(TT_plusKeyword);
        if (plusKeyword != null) {
            astNode.set(new SymbolValue(plusKeyword.tokenValue()), SymbolValue.TRUE);
            return true;
        } else {
            final TokenEvent keyword = tokens.swallow(TT_keyword);
            if (keyword != null) {
                astNode.set(new SymbolValue(keyword.tokenValue()), reader.read());
                return true;
            }
        }
        return false;
    }
}
