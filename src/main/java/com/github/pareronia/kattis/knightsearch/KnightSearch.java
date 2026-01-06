package com.github.pareronia.kattis.knightsearch;

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
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.StringTokenizer;
import java.util.function.Supplier;
import java.util.stream.IntStream;

/**
 * Knight Search
 * @see <a href="https://open.kattis.com/problems/knightsearch">https://open.kattis.com/problems/knightsearch</a>
 */
public class KnightSearch {

	private static final int[][] KNIGHT_MOVES = {
			{ 1, 2 }, { 1, -2 }, { -1, 2 }, { -1, -2}, { 2, 1 }, { 2, -1 }, { -2, 1 }, { -2, -1 }
	};

    private final boolean sample;
    private final InputStream in;
    private final PrintStream out;
    
    public KnightSearch(
            final Boolean sample, final InputStream in, final PrintStream out) {
        this.sample = sample;
        this.in = in;
        this.out = out;
    }
    
    @SuppressWarnings("unused")
    private void log(final Supplier<Object> supplier) {
        if (!sample) {
            return;
        }
        System.out.println(supplier.get());
    }
    
    private void handleTestCase(final int i, final FastScanner sc) {
    	final int n = sc.nextInt();
    	final char[] s = sc.next().toCharArray();
    	final Deque<State> q = new ArrayDeque<>();
    	IntStream.range(0, n * n).filter(j -> s[j] == 'I').forEach(j -> q.addLast(new State(j, "I")));
    	while (!q.isEmpty()) {
    		final State curr = q.pollFirst();
    		if ("ICPCASIASG".equals(curr.string)) {
    			this.out.println("YES");
    			return;
    		}
    		final int r = curr.pos / n;
    		final int c = curr.pos % n;
    		for (final int[] d : KNIGHT_MOVES) {
    			final int rr = r + d[0];
    			final int cc = c + d[1];
    			if (rr < 0 || rr >= n || cc < 0 || cc >= n) {
    				continue;
    			}
    			final int nxtPos = rr * n + cc;
    			final String nxtString = curr.string + s[nxtPos];
    			if (!"ICPCASIASG".startsWith(nxtString)) {
    				continue;
    			}
				q.addLast(new State(nxtPos, nxtString));
    		}
    	}
        this.out.println("NO");
    }

    private record State(int pos, String string) {}
    
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
            is = KnightSearch.class.getResourceAsStream("sample.in");
            out = new PrintStream(baos, true);
            timerStart = System.nanoTime();
        } else {
            is = System.in;
            out = System.out;
        }
        
        new KnightSearch(sample, is, out).solve();
        
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
                    = Paths.get(KnightSearch.class.getResource("sample.out").toURI());
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
        
        @SuppressWarnings("unused")
        public int[] nextIntArray(final int n) {
            final int[] a = new int[n];
            for (int i = 0; i < n; i++) {
                a[i] = nextInt();
            }
            return a;
        }
        
        @SuppressWarnings("unused")
        public long nextLong() {
            return Long.parseLong(next());
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
