package com.github.pareronia.kattis.bobby;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;

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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * Bobby's Bet
 * @see <a href="https://open.kattis.com/problems/bobby">https://open.kattis.com/problems/bobby</a>
 */
public class BobbysBet {

    private final boolean sample;
    private final InputStream in;
    private final PrintStream out;
    
    public BobbysBet(
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
    
    private Result<?> handleTestCase(final Integer i, final FastScanner sc) {
        final int n = sc.nextInt();
        final List<String> ans = new ArrayList<>();
        for (int j = 0; j < n; j++) {
           final int r = sc.nextInt();
           final int s = sc.nextInt();
           final int x = sc.nextInt();
           final int y = sc.nextInt();
           final int w = sc.nextInt();
           final double p = (double) (s - r + 1) / s;
           double P = 0d;
           for (int k = x; k <= y; k++) {
               final int nCr = Combinations.rOutOfN(y, k);
               P += nCr * Math.pow(p, k) * Math.pow(1 - p, y - k);
           }
           final double PP = P;
           log(() -> PP);
           ans.add(P * w > 1.0d ? "yes" : "no");
        }
        return new Result<>(i, ans);
    }
    
    public void solve() {
        try (final FastScanner sc = new FastScanner(this.in)) {
            final int numberOfTestCases;
            if (this.sample) {
                numberOfTestCases = sc.nextInt();
            } else {
                numberOfTestCases = 1;
            }
            final List<Result<?>> results
                    = Stream.iterate(1, i -> i <= numberOfTestCases, i -> i + 1)
                            .map(i -> handleTestCase(i, sc))
                            .collect(toList());
            output(results);
        }
    }

    private void output(final List<Result<?>> results) {
        results.forEach(r -> {
            r.getValues().stream().map(Object::toString).forEach(this.out::println);
        });
    }
    
    public static void main(final String[] args) throws IOException, URISyntaxException {
        final boolean sample = isSample();
        final InputStream is;
        final PrintStream out;
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        long timerStart = 0;
        if (sample) {
            is = BobbysBet.class.getResourceAsStream("sample.in");
            out = new PrintStream(baos, true);
            timerStart = System.nanoTime();
        } else {
            is = System.in;
            out = System.out;
        }
        
        new BobbysBet(sample, is, out).solve();
        
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
                    = Paths.get(BobbysBet.class.getResource("sample.out").toURI());
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
    
    private static final class Result<T> {
        @SuppressWarnings("unused")
        private final int number;
        private final List<T> values;
        
        public Result(final int number, final List<T> values) {
            this.number = number;
            this.values = values;
        }

        public List<T> getValues() {
            return values;
        }
    }
    
    public static final class Factorial<N extends Number> {
    	private static final Map<Integer, Integer> INT = new HashMap<>();
    	private static final Map<Integer, Long> LONG = new HashMap<>();
    	
    	static {
    		INT.put(0, 1);
    		INT.put(1, 1);
    		INT.put(2, 2);
    		INT.put(3, 6);
    		INT.put(4, 24);
    		INT.put(5, 120);
    		INT.put(6, 720);
    		INT.put(7, 5_040);
    		INT.put(8, 40_320);
    		INT.put(9, 362_880);
    		INT.put(10, 3_628_800);
    		INT.put(11, 39_916_800);
    		INT.put(12, 479_001_600);
    		LONG.put(13, 6_227_020_800L);
    		LONG.put(14, 87_178_291_200L);
    		LONG.put(15, 1_307_674_368_000L);
    		LONG.put(16, 20_922_789_888_000L);
    		LONG.put(17, 355_687_428_096_000L);
    		LONG.put(18, 6_402_373_705_728_000L);
    		LONG.put(19, 121_645_100_408_832_000L);
    		LONG.put(20, 2_432_902_008_176_640_000L);
    	}
    	
    	@SuppressWarnings("unchecked")
		public static <N extends Number> N fact(final int n) {
    		if (n <= 12) {
    			return (N) INT.get(n);
    		} else if (n <= 20) {
    			return (N) LONG.get(n);
    		} else {
				return (N) bigFact(n);
    		}
        }
    	
    	public static BigInteger bigFact(int n) {
    		if (n <= 20) {
    			return BigInteger.valueOf(fact(n).longValue());
    		}
    		return BigInteger.valueOf(n).multiply(bigFact(n - 1));
    	}
    }
    
    public static final class Combinations {
    	
    	public static int rOutOfN(int n, int r) {
    		return Factorial.<Integer> fact(n) / Factorial.<Integer> fact(r) / Factorial.<Integer> fact(n - r); 
    	}
    }
}
