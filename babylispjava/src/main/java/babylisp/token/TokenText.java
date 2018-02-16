package babylisp.token;

import babylisp.Utils;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TokenText {
    private final String text;
    private final List<Integer> lines;

    TokenText(@Nonnull String text) {
        this.text = stripLastNewLine(Utils.expandTabs(
                text
                        .replace("\r\n", "\n")
                        .replace("\r", "\n"),
                8));
        final List<Integer> lines = new ArrayList<>();
        int pos = 0;
        while (pos >= 0) {
            lines.add(pos > 0 ? pos + 1 : pos);
            pos = text.indexOf('\n', pos + 1);
        }
        this.lines = Collections.unmodifiableList(lines);
    }

    private static String stripLastNewLine(@Nonnull String text) {
        if (text.endsWith("\n"))
            return text.substring(0, text.length() - 1);
        return text;
    }

    int getLineForOffset(int off) {
        final int r = Collections.binarySearch(this.lines, off);
        if (r >= 0)
            return r + 1;
        return -r - 1;
    }

    int getColumnForOffset(int off) {
        int line = getLineForOffset(off);
        int lineStart = lines.get(line - 1);
        return off - lineStart + 1;
    }

    public int length() {
        return text.length();
    }

    public char charAt(int off) {
        return off >= 0 && off < text.length() ? text.charAt(off) : 0xFFFF;
    }

    public String substring(int beginIndex, int endIndex) {
        return text.substring(beginIndex, endIndex);
    }

    public String getTextForLine(int line) {
        final int start = lines.get(line - 1);
        final int end = line == lines.size() ? text.length() : lines.get(line) - 1;
        return substring(start, end);
    }

    public String text() {
        return text;
    }

    @Override
    public String toString() {
        final StringBuilder b = new StringBuilder();
        b.append("TokenText[\n");
        for (int i = 1; i <= lines.size(); i++) {
            final String textForLine = getTextForLine(i);
            b.append(i).append("|").append(textForLine).append(textForLine.endsWith("\n") ? "" : "\n");
        }
        b.append("]");
        return b.toString();
    }

    RuntimeException syntaxError(@Nonnull String why, int pos) {
        final int line = getLineForOffset(pos);
        final int col = getColumnForOffset(pos);
        assert line >= 1 : "line >=1 : line=" + line;
        assert col >= 1 : "col >= 1 : col=" + col + " in " + this;
        String lineText = getTextForLine(line);
        String msg = "(" + line + "," + col + ") syntax error: " + why + "\n" +
                line + ":" + lineText + "\n" +
                String.join("", Collections.nCopies(Integer.toString(line).length(), " ")) +
                ":" + String.join("", Collections.nCopies(col - 1, " ")) + "^";
        return new RuntimeException(msg);
    }
}
