package com.github.pareronia.kattis.fendofftitan;

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
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.StringTokenizer;

/**
 * Fend Off Titan
 * @see <a href="https://open.kattis.com/problems/fendofftitan">https://open.kattis.com/problems/fendofftitan</a>
 */
public class FendOffTitan {

    private final InputStream in;
    private final PrintStream out;
    
    public FendOffTitan(
            final Boolean sample, final InputStream in, final PrintStream out) {
        this.in = in;
        this.out = out;
    }
    
    private void handleTestCase(final Integer i, final FastScanner sc) {
    	@SuppressWarnings("unused")
		final int n = sc.nextInt();
    	final int m = sc.nextInt();
    	final int x = sc.nextInt();
    	final int y = sc.nextInt();
    	final Map<Integer, Set<Road>> roads = new HashMap<>();
    	for (int j = 0; j < m; j++) {
			final int a = sc.nextInt();
			final int b = sc.nextInt();
			final int w = sc.nextInt();
			final int c = sc.nextInt();
			roads.computeIfAbsent(a, k -> new HashSet<>()).add(new Road(b, w, c));
			roads.computeIfAbsent(b, k -> new HashSet<>()).add(new Road(a, w, c));
		}
        final PriorityQueue<State> q = new PriorityQueue<>();
    	q.add(new State(x, new Cost(0, 0, 0)));
        final Map<Integer, Cost> best = new HashMap<>();
        best.put(x, new Cost(0, 0, 0));
    	while (!q.isEmpty()) {
    		final State state = q.poll();
    		if (state.v == y) {
    			break;
    		}
            final Cost cost = best.getOrDefault(state.v, Cost.MAX);
    		for (final Road road : roads.getOrDefault(state.v, Set.of())) {
				final Cost newCost = new Cost(
						cost.titans + (road.foe == 2 ? 1 : 0),
						cost.shamans + (road.foe == 1 ? 1 : 0),
						cost.distance + road.length);
    			if (newCost.compareTo(best.getOrDefault(road.to, Cost.MAX)) < 0) {
    				best.put(road.to, newCost);
    				q.add(new State(road.to, newCost));
    			}
			}
    	}
    	if (best.containsKey(y)) {
    		final Cost cost = best.get(y);
    		this.out.println("%d %d %d".formatted(cost.distance, cost.shamans, cost.titans));
    	} else {
    		this.out.println("IMPOSSIBLE");
    	}
    }

    record Road(int to, int length, int foe) {}

    record Cost(int titans, int shamans, long distance) implements Comparable<Cost> {

    	private static final Cost MAX
    			= new Cost(Integer.MAX_VALUE, Integer.MAX_VALUE, Long.MAX_VALUE);
		private static final Comparator<Cost> COMPARATOR = Comparator.comparing(Cost::titans)
				.thenComparing(Cost::shamans).thenComparing(Cost::distance);

		@Override
		public int compareTo(final Cost other) {
			return COMPARATOR.compare(this, other);
		}
	}

    record State(int v, Cost cost) implements Comparable<State> {

		@Override
		public int compareTo(final State other) {
			return this.cost.compareTo(other.cost);
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
            is = FendOffTitan.class.getResourceAsStream("sample.in");
            out = new PrintStream(baos, true);
            timerStart = System.nanoTime();
        } else {
            is = System.in;
            out = System.out;
        }
        
        new FendOffTitan(sample, is, out).solve();
        
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
                    = Paths.get(FendOffTitan.class.getResource("sample.out").toURI());
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
