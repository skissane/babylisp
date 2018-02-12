package babylisp;

import babylisp.values.Value;

public interface Builtin {
    Value exec(Value[] args);
}
