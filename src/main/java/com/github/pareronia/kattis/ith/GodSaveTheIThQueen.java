package com.github.pareronia.kattis.ith;

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
import java.util.stream.IntStream;

/**
 * God Save the i-th Queen
 * @see <a href="https://open.kattis.com/problems/ith">https://open.kattis.com/problems/ith</a>
 */
public class GodSaveTheIThQueen {

    private final InputStream in;
    private final PrintStream out;

    public GodSaveTheIThQueen(
            final Boolean sample, final InputStream in, final PrintStream out) {
        this.in = in;
        this.out = out;
    }

    private void handleTestCase(final FastScanner sc) {
        int x = sc.nextInt();
        int y = sc.nextInt();
        int n = sc.nextInt();
        while (!(x == 0 && y == 0 && n == 0)) {
            if (n == 0) {
                this.out.println(x * y);
            } else {
                final int w = x;
                final int h = y;
                final int mx = w - 1;
                final boolean[] r = new boolean[h];
                final boolean[] c = new boolean[w];
                final boolean[] du = new boolean[w + h];
                final boolean[] dd = new boolean[w + h];
                for (int i = 0; i < n; i++) {
                    final int xx = sc.nextInt() - 1;
                    final int yy = sc.nextInt() - 1;
                    r[xx] = true;
                    c[yy] = true;
                    du[xx + yy] = true;
                    dd[(mx - xx) + yy] = true;
                }
                final int ans = IntStream.range(0, w)
                        .filter(xx -> !r[xx])
                        .parallel()
                        .map(xx -> {
                            int cnt = 0;
                            for (int yy = 0; yy < h; yy++) {
                                if (c[yy] || du[xx + yy] || dd[(mx - xx) + yy]) {
                                    continue;
                                }
                                cnt++;
                            }
                            return cnt;
                        })
                        .sum();
                this.out.println(ans);
            }
            x = sc.nextInt();
            y = sc.nextInt();
            n = sc.nextInt();
        }
    }

    public void solve() {
        try (final FastScanner sc = new FastScanner(this.in)) {
            handleTestCase(sc);
        }
    }

    public static void main(final String[] args) throws IOException, URISyntaxException {
        final boolean sample = isSample();
        final InputStream is;
        final PrintStream out;
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        long timerStart = 0;
        if (sample) {
            is = GodSaveTheIThQueen.class.getResourceAsStream("sample.in");
            out = new PrintStream(baos, true);
            timerStart = System.nanoTime();
        } else {
            is = System.in;
            out = System.out;
        }

        new GodSaveTheIThQueen(sample, is, out).solve();

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
                    = Paths.get(GodSaveTheIThQueen.class.getResource("sample.out").toURI());
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
