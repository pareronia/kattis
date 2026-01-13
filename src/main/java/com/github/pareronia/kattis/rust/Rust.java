package com.github.pareronia.kattis.rust;

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
 * Rust - TLE
 * @see <a href="https://open.kattis.com/problems/rust">https://open.kattis.com/problems/rust</a>
 */
public class Rust {

    private final InputStream in;
    private final PrintStream out;

    public Rust(
            final Boolean sample, final InputStream in, final PrintStream out) {
        this.in = in;
        this.out = out;
    }

    private void handleTestCase(final int i, final FastScanner sc) {
        final int n = sc.nextInt();
        final int k = sc.nextInt();
        final char[][] a = new char[n][n];
        for (int j = 0; j < n; j++) {
            a[j] = sc.next().toCharArray();
        }
        int ans = 0;
        for (int r = 0; r <= n - k; r++) {
            for (int c = 0; c <= n - k; c++) {
                if (!allDots(a, r, c, k) || !allDots(a, r + k - 1, c, k)) {
                    continue;
                }
                boolean ok = true;
                for (int j = 1; j < k - 1; j++) {
                    if (a[r + j][c] != '.' || a[r + j][c + k - 1] != '.') {
                        ok = false;
                        break;
                    }
                }
                if (!ok) {
                    continue;
                }
                int val = 0;
                for (int j = 1; j < k - 1; j++) {
                    val += value(a, r + j, c, k);
                }
                ans = Math.max(ans, val);
            }
        }
        this.out.println(ans);
    }

    private boolean allDots(final char[][] a, final int row, final int col, final int len) {
        for (int j = 0; j < len; j++) {
            if (a[row][col + j] != '.') {
                return false;
            }
        }
        return true;
    }

    private int value(final char[][] a, final int row, final int col, final int len) {
        int ans = 0;
        for (int j = 1; j < len - 1; j++) {
            final char ch = a[row][col + j];
            if (ch != '.' && ch != '#') {
                ans += ch - '0';
            }
        }
        return ans;
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
            is = Rust.class.getResourceAsStream("sample.in");
            out = new PrintStream(baos, true);
            timerStart = System.nanoTime();
        } else {
            is = System.in;
            out = System.out;
        }

        new Rust(sample, is, out).solve();

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
                    = Paths.get(Rust.class.getResource("sample.out").toURI());
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
