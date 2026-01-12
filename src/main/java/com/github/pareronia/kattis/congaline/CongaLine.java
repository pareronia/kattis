package com.github.pareronia.kattis.congaline;

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
 * Conga Line
 * @see <a href="https://open.kattis.com/problems/congaline">https://open.kattis.com/problems/congaline</a>
 */
public class CongaLine {

    private static final int PREV = 0;
    private static final int NEXT = 1;

    private final InputStream in;
    private final PrintStream out;

    public CongaLine(
            final Boolean sample, final InputStream in, final PrintStream out) {
        this.in = in;
        this.out = out;
    }

    private void handleTestCase(final int i, final FastScanner sc) {
        final int n = sc.nextInt();
        sc.nextInt();
        final String[] names = new String[2 * n];
        final int[][] line = new int[2 * n][2];
        for (int j = 0; j < 2 * n; j++) {
            names[j] = sc.next();
            line[j] = new int[] { j - 1, j + 1};
        }
        line[0][PREV] = line.length - 1;
        line[line.length - 1][NEXT] = 0;
        int mic = 0;
        int head = 0;
        int tail = (2 * n) - 1;
        final StringBuilder shouts = new StringBuilder();
        final char[] ins = sc.nextLine().toCharArray();
        for (final char ch : ins) {
            switch (ch) {
                case 'F' -> mic = line[mic][PREV];
                case 'B' -> mic = line[mic][NEXT];
                case 'R' -> {
                    final int p = mic;
                    mic = mic == tail ? head : line[mic][NEXT];
                    final int[] np = line[p];
                    if (p == head) {
                        head = np[NEXT];
                    }
                    if (p != tail) {
                        line[np[PREV]][NEXT] = np[NEXT];
                        line[np[NEXT]][PREV] = np[PREV];
                        np[NEXT] = line[tail][NEXT];
                        line[line[tail][NEXT]][PREV] = p;
                        np[PREV] = tail;
                        line[tail][NEXT] = p;
                        tail = p;
                    }
                }
                case 'C' -> {
                    final int p1 = mic;
                    mic = mic == tail ? head : line[mic][NEXT];
                    final int p2 = (p1 & 1) == 0 ? p1 + 1 : p1 - 1;
                    if (p2 == tail) {
                        tail = p1;
                    }
                    final int[] np1 = line[p1];
                    if (p1 == head) {
                        head = np1[NEXT];
                    }
                    if (p1 != line[p2][NEXT]) {
                        line[np1[PREV]][NEXT] = np1[NEXT];
                        line[np1[NEXT]][PREV] = np1[PREV];
                        np1[NEXT] = line[p2][NEXT];
                        line[line[p2][NEXT]][PREV] = p1;
                        np1[PREV] = p2;
                        line[p2][NEXT] = p1;
                    }
                }
                case 'P' -> {
                    final int p = (mic & 1) == 0 ? mic + 1 : mic - 1;
                    shouts.append(names[p]).append(System.lineSeparator());
                }
            }
        }
        this.out.print(shouts.toString());
        this.out.println();
        final StringBuilder lineOut = new StringBuilder();
        int p = head;
        for (int j = 0; j < 2 * n; j++) {
            lineOut.append(names[p]).append(System.lineSeparator());
            p = line[p][NEXT];
        }
        this.out.print(lineOut.toString());
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
            is = CongaLine.class.getResourceAsStream("sample.in");
            out = new PrintStream(baos, true);
            timerStart = System.nanoTime();
        } else {
            is = System.in;
            out = System.out;
        }

        new CongaLine(sample, is, out).solve();

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
                    = Paths.get(CongaLine.class.getResource("sample.out").toURI());
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
