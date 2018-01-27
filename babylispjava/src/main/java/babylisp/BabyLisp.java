package babylisp;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class BabyLisp {
    private enum OptionType {
        PRINT_HELP,
        RUN_FILE,
        EVAL_CODE,
        REPL
    }

    private static class Option {
        final OptionType type;
        final String arg;

        public Option(OptionType type, String arg) {
            this.type = Objects.requireNonNull(type);
            this.arg = arg;
        }

        public void run() {
            switch (type) {
                case PRINT_HELP:
                    showHelp();
                    return;
                case RUN_FILE:
                    runFile(arg);
                    return;
                case EVAL_CODE:
                    evalCode(arg);
                    return;
                case REPL:
                    Repl.run();
                    return;
                default:
                    throw new UnsupportedOperationException("option: " + type);
            }
        }
    }

    public static void main(String[] args) {
        final List<Option> opts = new ArrayList<>();
        if (args.length == 0)
            opts.add(new Option(OptionType.REPL, null));
        for (int i = 0; i < args.length; i++) {
            if ("-help".equals(args[i])) {
                opts.add(new Option(OptionType.PRINT_HELP, null));
            } else if ("-eval".equals(args[i])) {
                opts.add(new Option(OptionType.EVAL_CODE, args[++i]));
            } else if ("-repl".equals(args[i])) {
                opts.add(new Option(OptionType.REPL, null));
            } else if (args[i].startsWith("-")) {
                throw new IllegalArgumentException("Unsupported argument '" + args[i] + "'");
            } else
                opts.add(new Option(OptionType.RUN_FILE, args[i]));
        }
        opts.forEach(Option::run);
    }

    private static void evalCode(String arg) {

    }

    private static void runFile(String fileName) {

    }

    private static void showHelp() {

    }
}