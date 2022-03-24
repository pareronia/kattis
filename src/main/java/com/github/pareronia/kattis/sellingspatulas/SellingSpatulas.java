package com.github.pareronia.kattis.sellingspatulas;

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
import java.util.Arrays;
import java.util.List;
import java.util.StringTokenizer;
import java.util.function.Supplier;

/**
 * Selling Spatulas
 * @see <a href="https://open.kattis.com/problems/sellingspatulas">https://open.kattis.com/problems/sellingspatulas</a>
 */
public class SellingSpatulas {

    private final boolean sample;
    private final InputStream in;
    private final PrintStream out;
    
    public SellingSpatulas(
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
    
    public static final class Kadane {
        public static Pair<Integer, Integer> get(final double[] a) {
            double sum = 0;
            double max = Double.MIN_VALUE;
            int start = 0;
            int end = 0;
            int s = 0;
            for (int j = 0; j < a.length; j++) {
                sum = sum + a[j];
                if (max < sum) {
                    max = sum;
                    start = s;
                    end = j;
                }
                if (sum < 0) {
                    sum = 0;
                    s = j + 1;
                }
            }
            return Pair.of(start, end);
        }
    }
    
    private void handleTestCase(final Integer n, final FastScanner sc) {
        final double[] a = new double[1440 + 1440];
        Arrays.fill(a, -0.08d);
        for (int j = 0; j < n; j++) {
            final int m = sc.nextInt();
            final double val = sc.nextDouble();
            a[m] += val;
            a[m + 1440] += val;
        }
        final Pair<Integer, Integer> pair = Kadane.get(a);
        double total = 0;
        for (int j = pair.getOne(); j <= pair.getTwo(); j++) {
            total += a[j];
        }
        if (total <= 0) {
            this.out.println("no profit");
        } else {
            this.out.println(String.format("%.2f %d %d",
                    total, pair.getOne() % 1440, pair.getTwo() % 1440));
        }
    }

    public void solve() {
        try (final FastScanner sc = new FastScanner(this.in)) {
            while (true) {
                final int n = sc.nextInt();
                if (n == 0) {
                    break;
                }
                handleTestCase(n, sc);
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
            is = SellingSpatulas.class.getResourceAsStream("sample.in");
            out = new PrintStream(baos, true);
            timerStart = System.nanoTime();
        } else {
            is = System.in;
            out = System.out;
        }
        
        new SellingSpatulas(sample, is, out).solve();
        
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
                    = Paths.get(SellingSpatulas.class.getResource("sample.out").toURI());
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
        
        public double nextDouble() {
            return Double.parseDouble(next());
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

    private static final class Pair<L, R> {
        private final L one;
        private final R two;

        private Pair(final L one, final R two) {
            this.one = one;
            this.two = two;
        }

        public static <L, R> Pair<L, R> of(final L one, final R two) {
            return new Pair<>(one, two);
        }

        public L getOne() {
            return one;
        }
        
        public R getTwo() {
            return two;
        }
    }
}
