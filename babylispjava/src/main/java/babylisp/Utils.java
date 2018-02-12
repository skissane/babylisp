package babylisp;

import javax.annotation.Nonnull;

public class Utils {
    private Utils() {
    }

    public static RuntimeException handle(@Nonnull Throwable e) {
        if (e instanceof Error)
            throw (Error) e;
        return e instanceof RuntimeException ? (RuntimeException) e : new RuntimeException(e);
    }
}
