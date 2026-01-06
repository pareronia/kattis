package com.github.pareronia.kattis.shortestpath1;

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
 * Single source shortest path, non-negative weights
 * @see <a href="https://open.kattis.com/problems/shortestpath1">https://open.kattis.com/problems/shortestpath1</a>
 */
public class SingleSourceShortestPathNonNegativeWeights {

    private final InputStream in;
    private final PrintStream out;
    
    public SingleSourceShortestPathNonNegativeWeights(
            final Boolean sample, final InputStream in, final PrintStream out) {
        this.in = in;
        this.out = out;
    }
    
    private void handleTestCase(final int i, final FastScanner sc) {
    	while (true) {
    		final int n = sc.nextInt();
    		final int m = sc.nextInt();
    		final int q = sc.nextInt();
    		final int s = sc.nextInt();
    		if (n == 0 && m == 0 && q == 0 && s == 0) {
    			break;
    		}
    		final Map<Integer, Set<Edge>> edges = new HashMap<>();
    		for (int j = 0; j < m; j++) {
				final int u = sc.nextInt();
				final int v = sc.nextInt();
				final int w = sc.nextInt();
				edges.computeIfAbsent(u, x -> new HashSet<>()).add(new Edge(v, w));
			}
			final PriorityQueue<State> queue = new PriorityQueue<>();
			queue.add(new State(s, 0));
			final Map<Integer, Integer> best = new HashMap<>();
			best.put(s, 0);
			while (!queue.isEmpty()) {
				final State state = queue.poll();
				final int cost = best.getOrDefault(state.node, Integer.MAX_VALUE);
				for (final Edge edge : edges.getOrDefault(state.node, Set.of())) {
					final int newCost = cost + edge.weight;
					if (newCost < best.getOrDefault(edge.to, Integer.MAX_VALUE)) {
						best.put(edge.to, newCost);
						queue.add(new State(edge.to, newCost));
					}
				}
			}
			for (int j = 0; j < q; j++) {
				final int e = sc.nextInt();
				if (best.containsKey(e)) {
					this.out.println(best.get(e));
				} else {
					this.out.println("Impossible");
				}
			}
    	}
    }

    private record Edge(int to, int weight) {}

    private record State(int node, int cost) implements Comparable<State> {

		@Override
		public int compareTo(final State other) {
			return Integer.compare(this.cost, other.cost);
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
            is = SingleSourceShortestPathNonNegativeWeights.class.getResourceAsStream("sample.in");
            out = new PrintStream(baos, true);
            timerStart = System.nanoTime();
        } else {
            is = System.in;
            out = System.out;
        }
        
        new SingleSourceShortestPathNonNegativeWeights(sample, is, out).solve();
        
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
                    = Paths.get(SingleSourceShortestPathNonNegativeWeights.class.getResource("sample.out").toURI());
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
