package com.github.pareronia.kattis._10kindsofpeople;

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
 * 10 Kinds of People - TLE
 * @see <a href="https://open.kattis.com/problems/10kindsofpeople">https://open.kattis.com/problems/10kindsofpeople</a>
 */
public class KindsOfPeople {

	private static final int[][] DIRS = { { 1, 0 }, { -1, 0 }, { 0, 1 }, { 0, -1 } };

    private final InputStream in;
    private final PrintStream out;
    
    public KindsOfPeople(
            final Boolean sample, final InputStream in, final PrintStream out) {
        this.in = in;
        this.out = out;
    }
    
    private void handleTestCase(final Integer i, final FastScanner sc) {
    	final int r = sc.nextInt();
    	final int c = sc.nextInt();
    	final int[][] a = new int[r][c];
    	for (int rr = 0; rr < r; rr++) {
    		final String s = sc.next();
    		a[rr] = IntStream.range(0, c).map(cc -> s.charAt(cc) - '0').toArray();
		}
    	int binaryGroup = -100;
    	int decimalGroup = 100;
    	for (int rr = 0; rr < r; rr++) {
    		for (int cc = 0; cc < c; cc++) {
    			final int val = a[rr][cc];
    			if (val == 0) {
    				floodFill(a, rr, cc, val, binaryGroup--);
    			} else if (val == 1) {
    				floodFill(a, rr, cc, val, decimalGroup++);
    			}
			}
		}
    	final int q = sc.nextInt();
    	for (int j = 0; j < q; j++) {
    		final int r1 = sc.nextInt() - 1;
    		final int c1 = sc.nextInt() - 1;
    		final int r2 = sc.nextInt() - 1;
    		final int c2 = sc.nextInt() - 1;
    		final int start = a[r1][c1];
    		final int end = a[r2][c2];
    		if (start != end) {
    			this.out.println("neither");
    		} else {
    			this.out.println(start > 0 ? "decimal" : "binary");
    		}
		}
    }

	private void floodFill(final int[][] a, final int row, final int col, final int val, final int group) {
		a[row][col] = group;
		for (final int[] d : DIRS) {
			final int rr = row + d[0];
			final int cc = col + d[1];
			if (rr < 0 || rr >= a.length || cc < 0 || cc >= a[rr].length) {
				continue;
			}
			if (a[rr][cc] == val) {
				floodFill(a, rr, cc, val, group);
			}
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
            is = KindsOfPeople.class.getResourceAsStream("sample.in");
            out = new PrintStream(baos, true);
            timerStart = System.nanoTime();
        } else {
            is = System.in;
            out = System.out;
        }
        
        new KindsOfPeople(sample, is, out).solve();
        
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
                    = Paths.get(KindsOfPeople.class.getResource("sample.out").toURI());
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
