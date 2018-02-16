package babylisp;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.stream.Collectors;

public class Utils {
    private Utils() {
    }

    public static RuntimeException handle(@Nonnull Throwable e) {
        if (e instanceof Error)
            throw (Error) e;
        return e instanceof RuntimeException ? (RuntimeException) e : new RuntimeException(e);
    }

    @SuppressWarnings("SameParameterValue")
    private static String repeatString(@Nonnull String s, int count) {
        return String.join("", Collections.nCopies(count, s));
    }

    public static String expandTabs(@Nonnull String s, int tabSize) {
        final int[] col = new int[1];
        return s.chars().mapToObj(c -> {
            switch (c) {
                case '\t':
                    int expandBy = tabSize - (col[0] % tabSize);
                    col[0] += expandBy;
                    return repeatString(" ", expandBy);
                case '\n':
                    col[0] = 0;
                    break;
                default:
                    col[0]++;
            }
            return String.valueOf((char) c);
        }).collect(Collectors.joining());
    }
}
