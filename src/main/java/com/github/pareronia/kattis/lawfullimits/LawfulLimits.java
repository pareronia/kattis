package com.github.pareronia.kattis.lawfullimits;

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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.StringTokenizer;

/**
 * Lawful Limits
 * @see <a href="https://open.kattis.com/problems/lawfullimits">https://open.kattis.com/problems/lawfullimits</a>
 */
public class LawfulLimits {

    private final InputStream in;
    private final PrintStream out;

    public LawfulLimits(
            final Boolean sample, final InputStream in, final PrintStream out) {
        this.in = in;
        this.out = out;
    }

    private void handleTestCase(final int i, final FastScanner sc) {
        final int n = sc.nextInt();
        final int m = sc.nextInt();
        final int t = sc.nextInt();
        final Map<Integer, Set<Road>> a = new HashMap<>();
        for (int j = 0; j < m; j++) {
            final int x = sc.nextInt();
            final int y = sc.nextInt();
            final int l = sc.nextInt();
            final int v = sc.nextInt();
            final int w = sc.nextInt();
            a.computeIfAbsent(x, xx -> new HashSet<>()).add(new Road(y, l, v, w));
            a.computeIfAbsent(y, xx -> new HashSet<>()).add(new Road(x, l, v, w));
        }
        final PriorityQueue<State> q = new PriorityQueue<>();
        q.add(new State(1, 0d));
        final Map<Integer, Double> best = new HashMap<>();
        best.put(1, 0d);
        while (!q.isEmpty()) {
            final State state = q.poll();
            if (state.node == n) {
                break;
            }
            final double cost = best.getOrDefault(state.node, Double.MAX_VALUE);
            for (final Road road : a.getOrDefault(state.node, Set.of())) {
                double len = road.len;
                final double t1 = cost < t ? Math.min(len / road.v1, t - cost) : 0d;
                len = len - t1 * road.v1;
                final double t2 = len > 0 ? len / road.v2 : 0d;
                final double newCost = cost + t1 + t2;
                if (newCost < best.getOrDefault(road.to, Double.MAX_VALUE)) {
                    best.put(road.to, newCost);
                    q.add(new State(road.to, newCost));
                }
            }
        }
        this.out.println(best.get(n));
    }

    private record Road(int to, int len, int v1, int v2) { }

    private record State(int node, double cost) implements Comparable<State> {

        @Override
        public int compareTo(final State other) {
            return Double.compare(this.cost, other.cost);
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
            is = LawfulLimits.class.getResourceAsStream("sample.in");
            out = new PrintStream(baos, true);
            timerStart = System.nanoTime();
        } else {
            is = System.in;
            out = System.out;
        }

        new LawfulLimits(sample, is, out).solve();

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
                    = Paths.get(LawfulLimits.class.getResource("sample.out").toURI());
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
