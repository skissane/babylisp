package babylisp.token;

public enum TokenType {
    TT_symbol,
    TT_integer,
    TT_listBegin,
    TT_objectBegin,
    TT_pragmaBegin,
    TT_bracketEnd,
    TT_dictBegin,
    TT_braceEnd,
    TT_var,
    TT_keyword,
    TT_plusKeyword,
    TT_parenBegin,
    TT_parenEnd,
    TT_opt,
    TT_rest,
    TT_dqStrBegin,
    TT_dqStrEnd,
    TT_dqStrUnescaped,
    TT_dqStrEscape,
    TT_dqStrHexEscape

}
