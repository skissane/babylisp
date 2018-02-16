package babylisp.pragmas;

import babylisp.ASTUtil;
import babylisp.LispReader;
import babylisp.PragmaReader;
import babylisp.token.TokenEvent;
import babylisp.token.TokenReader;
import babylisp.values.ListValue;
import babylisp.values.ObjectValue;
import babylisp.values.SymbolValue;
import com.google.auto.service.AutoService;

import javax.annotation.Nonnull;

import static babylisp.token.TokenType.*;

@AutoService(PragmaReader.class)
public final class FuncPragmaReader extends PragmaReader {

    private static final SymbolValue CLASS_FUNC = new SymbolValue("func");
    private static final SymbolValue CALLABLE_ARGS = new SymbolValue("callable/args");
    private static final SymbolValue CALLABLE_OPT_ARGS = new SymbolValue("callable/optArgs");
    private static final SymbolValue CALLABLE_REST = new SymbolValue("callable/rest");
    private static final SymbolValue CALLABLE_BODY = new SymbolValue("callable/body");

    @Override
    public SymbolValue name() {
        return new SymbolValue("func");
    }

    @Override
    public ObjectValue readPragma(@Nonnull LispReader reader) {
        final SymbolValue funcName = reader.readSymbol();
        final TokenReader tokens = reader.tokens();
        tokens.expect(TT_parenBegin);
        final ListValue args = new ListValue();
        final ListValue optArgs = new ListValue();
        final ListValue restArgs = new ListValue();
        boolean gotOpt = false, gotRest = false;
        while (tokens.swallow(TT_parenEnd) == null) {
            if (tokens.swallow(TT_opt) != null) {
                if (gotOpt)
                    throw tokens.syntaxError("duplicate ? in function arguments");
                gotOpt = true;
                continue;
            }
            if (tokens.swallow(TT_rest) != null) {
                if (gotRest)
                    throw tokens.syntaxError("duplicate & in function arguments");
                gotRest = true;
                continue;
            }
            final TokenEvent varToken = tokens.expect(TT_var);
            final SymbolValue varSym = new SymbolValue(varToken.tokenValue());
            if (gotRest && restArgs.size() > 0)
                throw varToken.syntaxError("only one argument allowed in rest position");
            (gotRest ? restArgs : gotOpt ? optArgs : args).add(varSym);
        }
        final ObjectValue o = new ObjectValue(CLASS_FUNC);
        ASTUtil.setName(o, funcName);
        o.set(CALLABLE_ARGS, args);
        if (optArgs.size() > 0)
            o.set(CALLABLE_OPT_ARGS, optArgs);
        if (restArgs.size() > 0)
            o.set(CALLABLE_REST, restArgs.get(0));
        final ListValue body = new ListValue();
        while (tokens.match(TT_bracketEnd) == null) {
            if (ASTUtil.readOneAttr(reader, o))
                continue;
            body.add(reader.read());
        }
        if (body.size() > 0)
            o.set(CALLABLE_BODY, body);
        return o;
    }
}
