package babylisp;

import babylisp.values.Value;
import jline.console.ConsoleReader;

import java.io.IOException;

/**
 * Read-Eval-Print-Loop.
 */
public class Repl {
    public static void run() {
        try (ConsoleReader r = new ConsoleReader()) {
            while (true) {
                final String line = r.readLine("BabyLisp> ");
                if (line == null)
                    return;
                if (line.trim().isEmpty())
                    continue;
                final Value value = LispReader.read(line);
                final Value result = LispEvaluator.eval(value);
                System.out.println(result);
            }
        } catch (IOException e) {
            throw Utils.handle(e);
        }
    }
}
