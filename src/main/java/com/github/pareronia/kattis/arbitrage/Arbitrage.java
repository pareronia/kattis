package com.github.pareronia.kattis.arbitrage;

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
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.function.Consumer;
import java.util.stream.IntStream;

/**
 * Arbitrage? - TLE
 * @see <a href="https://open.kattis.com/problems/arbitrage">https://open.kattis.com/problems/arbitrage</a>
 */
public class Arbitrage {

    private final InputStream in;
    private final PrintStream out;

    public Arbitrage(
            final Boolean sample, final InputStream in, final PrintStream out) {
        this.in = in;
        this.out = out;
    }

    private void handleTestCase(final FastScanner sc) {
        int c;
        while((c = sc.nextInt()) != 0) {
            final List<String> cs = IntStream.range(0, c).mapToObj(j -> sc.next()).toList();
            final int r = sc.nextInt();
            final double[][] a = new double[c][c];
            for (int i = 0; i < r; i++) {
                final int c1 = cs.indexOf(sc.next());
                final int c2 = cs.indexOf(sc.next());
                final String[] sp = sc.next().split(":");
                a[c1][c2] = Double.parseDouble(sp[1]) / Double.parseDouble(sp[0]);
            }
            boolean ans = false;
            for (int i = 0; i < a.length; i++) {
                final Checker checker = new Checker(a);
                dfs(a, List.of(i), checker);
                if (checker.stop) {
                    ans = true;
                    break;
                }
            }
            this.out.println(ans ? "Arbitrage" : "Ok");
        }
    }

    private void dfs(final double[][] a, final List<Integer> idxs, final Checker checker) {
        if (checker.stop) {
            return;
        }
        for (int i = 0; i < a.length; i++) {
            if (a[idxs.getLast()][i] == 0) {
                continue;
            }
            if (idxs.size() > 1 && idxs.getFirst().equals(idxs.getLast())) {
                checker.accept(idxs);
            } else if (idxs.indexOf(i) <= 0) {
                final List<Integer> newIdxs = new ArrayList<>(idxs);
                newIdxs.add(i);
                dfs(a, newIdxs, checker);
            }
        }
    }

    private static final class Checker implements Consumer<List<Integer>> {
        private final double[][] a;
        private boolean stop = false;

        private Checker(final double[][] a) {
            this.a = a;
        }

        @Override
        public void accept(final List<Integer> lst) {
            if (lst.isEmpty() || this.stop) {
                return;
            }
            double ratio = 1d;
            for (int j = 1; j < lst.size(); j++) {
                ratio *= this.a[lst.get(j - 1)][lst.get(j)];
            }
            if (ratio > 1) {
                this.stop = true;
            }
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
            is = Arbitrage.class.getResourceAsStream("sample.in");
            out = new PrintStream(baos, true);
            timerStart = System.nanoTime();
        } else {
            is = System.in;
            out = System.out;
        }

        new Arbitrage(sample, is, out).solve();

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
                    = Paths.get(Arbitrage.class.getResource("sample.out").toURI());
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
