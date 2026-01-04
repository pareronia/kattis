package com.github.pareronia.kattis.aldursrodun;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.joining;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.math.BigInteger;
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
 * Aldursröðun
 * @see <a href="https://open.kattis.com/problems/aldursrodun">https://open.kattis.com/problems/aldursrodun</a>
 */
public class Aldursrodun {

    private final InputStream in;
    private final PrintStream out;
    
    public Aldursrodun(
            final Boolean sample, final InputStream in, final PrintStream out) {
        this.in = in;
        this.out = out;
    }
    
    private void handleTestCase(final Integer i, final FastScanner sc) {
    	final int n = sc.nextInt();
    	final int[] a = sc.nextIntArray(n);
    	final List<Integer> ans = new ArrayList<>();
    	for (int j = 0; j < n; j++) {
    		final int curr = j;
    		final int[] b = IntStream.range(0, n).filter(k -> k != curr).map(k -> a[k]).toArray();
    		permutations(b, b.length, new Consumer<int[]>() {
    			private boolean found = false;

				@Override
				public void accept(final int[] p) {
					if (found) {
						return;
					}
					final List<Integer> pp = new ArrayList<>();
					pp.add(a[curr]);
					for (final int e : p) {
						pp.add(e);
					}
					if (IntStream.range(1, n).allMatch(k ->
								BigInteger.valueOf(pp.get(k)).gcd(
										BigInteger.valueOf(pp.get(k - 1))).intValue() > 1)) {
						ans.addAll(pp);
						found = true;
					}
				}
			});
    		if (!ans.isEmpty()) {
    			break;
    		}
		}
    	if (ans.isEmpty()) {
    		this.out.println("Neibb");
    	} else {
    		this.out.println(ans.stream().map(t -> String.valueOf(t) + " ").collect(joining()));
    	}
    }

    private void permutations(final int[] a, final int n, final Consumer<int[]> consumer) {
		if (n == 1) {
			consumer.accept(a);
			return;
		}
		for (int i = 0; i < n - 1; i++) {
			permutations(a, n - 1, consumer);
			final int j = n % 2 == 0 ? i : 0;
			a[n - 1] ^= a[j];
			a[j] ^= a[n - 1];
			a[n - 1] ^= a[j];
		}
		permutations(a, n - 1, consumer);
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
            is = Aldursrodun.class.getResourceAsStream("sample.in");
            out = new PrintStream(baos, true);
            timerStart = System.nanoTime();
        } else {
            is = System.in;
            out = System.out;
        }
        
        new Aldursrodun(sample, is, out).solve();
        
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
                    = Paths.get(Aldursrodun.class.getResource("sample.out").toURI());
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
        
        public int[] nextIntArray(final int n) {
            final int[] a = new int[n];
            for (int i = 0; i < n; i++) {
                a[i] = nextInt();
            }
            return a;
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
