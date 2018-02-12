package babylisp;

import babylisp.values.StringValue;
import babylisp.values.Value;

import java.util.Arrays;
import java.util.stream.Collectors;

@SuppressWarnings("unused")
public class CoreBuiltins {
    public static final Builtin putLine = args -> {
        System.out.println(
                Arrays.stream(args)
                        .map(v -> v instanceof StringValue ? ((StringValue) v).value() : Value.toString(v))
                        .collect(Collectors.joining(" "))
        );
        return null;
    };
}
