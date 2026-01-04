package com.github.pareronia.kattis.enduro;

import static java.util.Arrays.asList;
import static java.util.Comparator.comparing;

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
import java.time.Duration;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.StringTokenizer;

/**
 * Enduro
 * @see <a href="https://open.kattis.com/problems/enduro">https://open.kattis.com/problems/enduro</a>
 */
public class Enduro {

    private static final DateTimeFormatter DF = DateTimeFormatter.ofPattern("hh:mm:ss a");
    private static final long DAY = Duration.ofDays(1L).toSeconds();
    private static final long LIMIT = Duration.ofHours(3L).toSeconds();

    private final InputStream in;
    private final PrintStream out;
    
    public Enduro(
            final Boolean sample, final InputStream in, final PrintStream out) {
        this.in = in;
        this.out = out;
    }
    
    private void handleTestCase(final Integer i, final FastScanner sc) {
		final LocalTime start = LocalTime.parse(sc.nextLine(), DF);
    	final int n = sc.nextInt();
    	final Map<String, Stats> map = new HashMap<>();
    	for (int j = 0; j < n; j++) {
    		final String[] sp = sc.nextLine().split(" ", 3);
    		final LocalTime time = LocalTime.parse(sp[0] + " " + sp[1], DF);
    		final long diff = (DAY + Duration.between(start, time).toSeconds()) % DAY;
			if (!map.containsKey(sp[2])) {
				map.put(sp[2], new Stats(1, diff));
			} else {
				final Stats stats = map.get(sp[2]);
				if (stats.total < LIMIT) {
					map.put(sp[2], new Stats(stats.laps + 1, diff));
				}
			}
		}
    	map.entrySet().stream()
    		.filter(e -> e.getValue().total >= LIMIT)
    		.sorted(comparing(Entry::getValue))
    		.map(e -> "%d %s".formatted(e.getValue().laps, e.getKey()))
    		.forEach(this.out::println);
    }

    record Stats(int laps, long total) implements Comparable<Stats> {
    	private static final Comparator<Stats> COMPARATOR
    			= comparing(Stats::laps).reversed().thenComparing(comparing(Stats::total));

		@Override
		public int compareTo(final Stats other) {
			return COMPARATOR.compare(this, other);
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
            is = Enduro.class.getResourceAsStream("sample.in");
            out = new PrintStream(baos, true);
            timerStart = System.nanoTime();
        } else {
            is = System.in;
            out = System.out;
        }
        
        new Enduro(sample, is, out).solve();
        
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
                    = Paths.get(Enduro.class.getResource("sample.out").toURI());
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
