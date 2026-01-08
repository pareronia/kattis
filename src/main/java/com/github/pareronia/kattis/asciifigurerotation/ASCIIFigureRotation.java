package com.github.pareronia.kattis.asciifigurerotation;

import static java.util.Arrays.asList;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.StringTokenizer;

/**
 * ASCII Figure Rotation
 * @see <a href="https://open.kattis.com/problems/asciifigurerotation">https://open.kattis.com/problems/asciifigurerotation</a>
 */
public class ASCIIFigureRotation {

    private final InputStream in;
    private final PrintStream out;

    public ASCIIFigureRotation(
            final Boolean sample, final InputStream in, final PrintStream out) {
        this.in = in;
        this.out = out;
    }

    private void handleTestCase(final int i, final FastScanner sc) {
        int n = sc.nextInt();
        while (true) {
            final char[][] a = new char[n][100];
            final String e = new StringBuilder().repeat(' ', 100).toString();
            for (int j = 0; j < n; j++) {
                a[j] = e.toCharArray();
            }
            int maxlen = 0;
            for (int j = 0; j < n; j++) {
                final char[] s = sc.nextLine().toCharArray();
                maxlen = Math.max(maxlen, s.length);
                System.arraycopy(s, 0, a[j], 0, s.length);
            }
            final String[] ans = new String[maxlen];
            for (int c = 0; c < maxlen; c++) {
                final StringBuilder sb = new StringBuilder();
                for (int r = n - 1; r >= 0; r--) {
                    final char ch = a[r][c];
                    switch (ch) {
                        case '-' -> sb.append('|');
                        case '|' -> sb.append('-');
                        default -> sb.append(ch);
                    }
                }
                ans[c] = sb.toString().stripTrailing();
            }
            for (final String line : ans) {
                this.out.println(line);
            }
            n = sc.nextInt();
            if (n > 0) {
                this.out.println();
            } else {
                break;
            }
        }
    }

    public void solve() {
        try (final FastScanner sc = new FastScanner(this.in)) {
            final int numberOfTestCases = isSample() ? sc.nextInt() : 1;
            for (int i = 0; i < numberOfTestCases; i++) {
                handleTestCase(i, sc);
            }
        }
    }

    public static void main(final String[] args) throws IOException, URISyntaxException {
        final boolean sample = isSample();
        final InputStream is;
        final PrintStream out;
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        long timerStart = 0;
        if (sample) {
            is = ASCIIFigureRotation.class.getResourceAsStream("sample.in");
            out = new PrintStream(baos, true);
            timerStart = System.nanoTime();
        } else {
            is = System.in;
            out = System.out;
        }

        new ASCIIFigureRotation(sample, is, out).solve();

        out.flush();
        if (sample) {
            final long timeSpent = (System.nanoTime() - timerStart) / 1_000;
            final double time;
            final String unit;
            if (timeSpent < 1_000) {
                time = timeSpent;
                unit = "µs";
            } else if (timeSpent < 1_000_000) {
                time = timeSpent / 1_000.0;
                unit = "ms";
            } else {
                time = timeSpent / 1_000_000.0;
                unit = "s";
            }
            final Path path
                    = Paths.get(ASCIIFigureRotation.class.getResource("sample.out").toURI());
            final List<String> expected = Files.readAllLines(path);
            final List<String> actual = asList(baos.toString().split("\\r?\\n"));
            if (!expected.equals(actual)) {
                throw new AssertionError(String.format(
                        "Expected %s, got %s", expected, actual));
            }
            actual.forEach(System.out::println);
            System.out.println(String.format("took: %.3f %s", time, unit));
        }
    }

    private static boolean isSample() {
        try {
            return "sample".equals(System.getProperty("kattis"));
        } catch (final SecurityException e) {
            return false;
        }
    }

    private static final class FastScanner implements Closeable {
        private final BufferedReader br;
        private StringTokenizer st;

        public FastScanner(final InputStream in) {
            this.br = new BufferedReader(new InputStreamReader(in));
            st = new StringTokenizer("");
        }

        public String nextLine() {
            try {
                return br.readLine();
            } catch (final IOException e) {
                throw new RuntimeException(e);
            }
        }

        public String next() {
            while (!st.hasMoreTokens()) {
                try {
                    st = new StringTokenizer(br.readLine());
                } catch (final IOException e) {
                    throw new RuntimeException(e);
                }
            }
            return st.nextToken();
        }

        public int nextInt() {
            return Integer.parseInt(next());
        }

        @Override
        public void close() {
            try {
                this.br.close();
            } catch (final IOException e) {
                // ignore
            }
        }
    }
}
