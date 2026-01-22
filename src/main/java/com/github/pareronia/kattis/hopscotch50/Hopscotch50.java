package com.github.pareronia.kattis.hopscotch50;

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
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

/**
 * Hopscotch 50 - TLE
 * @see <a href="https://open.kattis.com/problems/hopscotch50">https://open.kattis.com/problems/hopscotch50</a>
 */
public class Hopscotch50 {

    private final InputStream in;
    private final PrintStream out;

    public Hopscotch50(
            final Boolean sample, final InputStream in, final PrintStream out) {
        this.in = in;
        this.out = out;
    }

    private void handleTestCase(final int i, final FastScanner sc) {
        final int n = sc.nextInt();
        final int k = sc.nextInt();
        final Map<Integer, Set<Integer>> map = new HashMap<>();
        for (int r = 0; r < n; r++) {
            for (int c = 0; c < n; c++) {
                final int v = sc.nextInt();
                map.computeIfAbsent(v, x -> new HashSet<>()).add(r * n + c);
            }
        }
        final int[] memo = new int[n * n];
        Arrays.fill(memo, -1);
        final int ans = map.getOrDefault(1, Set.of()).stream()
                .mapToInt(cell -> dfs(map, memo, n, k, cell, 1))
                .filter(x -> x <= 2 * k * n)
                .min().orElse(-1);
        this.out.println(ans);
    }

    private int dfs(final Map<Integer, Set<Integer>> map, final int[] memo,
            final int n, final int k, final int start, final int val)
    {
        if (memo[start] != -1) {
            return memo[start];
        }
        int ans = Integer.MAX_VALUE - 2 * k * n;
        if (map.getOrDefault(k, Set.of()).contains(start)) {
            ans = 0;
        } else {
            final int r = start / n;
            final int c = start % n;
            for (final int nxt : map.getOrDefault(val + 1, Set.of())) {
                final int nr = nxt / n;
                final int nc = nxt % n;
                final int mc = Math.abs(r - nr) + Math.abs(c - nc);
                ans = Math.min(ans, mc + dfs(map, memo, n, k, nxt, val + 1));
            }
        }
        memo[start] = ans;
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
            is = Hopscotch50.class.getResourceAsStream("sample.in");
            out = new PrintStream(baos, true);
            timerStart = System.nanoTime();
        } else {
            is = System.in;
            out = System.out;
        }

        new Hopscotch50(sample, is, out).solve();

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
                    = Paths.get(Hopscotch50.class.getResource("sample.out").toURI());
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
