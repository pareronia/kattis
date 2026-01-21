package com.github.pareronia.kattis.candycontribution;

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
 * Candy Contribution
 * @see <a href="https://open.kattis.com/problems/candycontribution">https://open.kattis.com/problems/candycontribution</a>
 */
public class CandyContribution {

    private final InputStream in;
    private final PrintStream out;

    public CandyContribution(
            final Boolean sample, final InputStream in, final PrintStream out) {
        this.in = in;
        this.out = out;
    }

    private void handleTestCase(final int i, final FastScanner sc) {
        sc.nextInt();
        final int m = sc.nextInt();
        final int s = sc.nextInt();
        final int t = sc.nextInt();
        final long c = sc.nextInt();
        final Map<Integer, Set<Border>> edges = new HashMap<>();
        for (int j = 0; j < m; j++) {
            final int u = sc.nextInt();
            final int v = sc.nextInt();
            final int p = sc.nextInt();
            edges.computeIfAbsent(u, x -> new HashSet<>()).add(new Border(v, p));
            edges.computeIfAbsent(v, x -> new HashSet<>()).add(new Border(u, p));
        }
        final PriorityQueue<State> q = new PriorityQueue<>();
        q.add(new State(s, 0L));
        final Map<Integer, Long> best = new HashMap<>();
        best.put(s, 0L);
        while (!q.isEmpty()) {
            final State state = q.poll();
            if (state.node == t) {
                break;
            }
            final long cost = best.getOrDefault(state.node, Long.MAX_VALUE);
            for (final Border b : edges.getOrDefault(state.node, Set.of())) {
                final long newCost = cost + (long) Math.ceil((c - cost) * b.pct / 100d);
                if (newCost < best.getOrDefault(b.to, Long.MAX_VALUE)) {
                    best.put(b.to, newCost);
                    q.add(new State(b.to, newCost));
                }
            }
        }
        this.out.println(c - best.get(t));
    }

    private record Border(int to, int pct) {}

    private record State(int node, long cost) implements Comparable<State> {

        @Override
        public int compareTo(final State other) {
            return Long.compare(this.cost, other.cost);
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
            is = CandyContribution.class.getResourceAsStream("sample.in");
            out = new PrintStream(baos, true);
            timerStart = System.nanoTime();
        } else {
            is = System.in;
            out = System.out;
        }

        new CandyContribution(sample, is, out).solve();

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
                    = Paths.get(CandyContribution.class.getResource("sample.out").toURI());
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
