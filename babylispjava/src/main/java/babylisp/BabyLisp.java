package babylisp;

import babylisp.values.SymbolValue;
import babylisp.values.Value;

import javax.annotation.Nonnull;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static babylisp.Utils.handle;
import static java.lang.System.out;

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
        runFile("__init__.baby");
        out.println("BUILTIN NAMES = " + Builtins.enumerate());

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

    private static void evalCode(@Nonnull String text) {
        final Value result = LispEvaluator.readThenEvalCode(text);
        out.println(result);
    }

    private static void loadCode(@Nonnull String text) {
        LispReader.read(text,
                node -> Database.getInstance().set(node.get(ASTUtil.CATALOG_NAME, SymbolValue.class), node));
    }

    private static void runFile(@Nonnull String fileName) {
        loadCode(slurpFile(Paths.get(fileName)));
    }

    private static String slurpFile(@Nonnull Path path) {
        try (BufferedReader rdr = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            return slurp(rdr);
        } catch (IOException e) {
            throw handle(e);
        }
    }

    private static String slurp(@Nonnull BufferedReader rdr) {
        try {
            final StringBuilder b = new StringBuilder();
            final char[] buf = new char[4096];
            while (true) {
                final int r = rdr.read(buf);
                if (r < 0)
                    return b.toString();
                b.append(buf, 0, r);
            }
        } catch (IOException e) {
            throw handle(e);
        }
    }

    private static void showHelp() {
        out.println("Usage:");
        out.println("-help\t\tshow this help");
        out.println("-eval CODE\tevaluate provided code");
        out.println("FILENAME\tevaluate provided file");
        out.println("-repl\t\tlaunch REPL");
        out.println("If no arguments, -repl is default");
    }
}