package com.github.pareronia.kattis.carousel;

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
import java.util.function.Supplier;

/**
 * Carousel Rides
 * @see <a href="https://open.kattis.com/problems/carousel">https://open.kattis.com/problems/carousel</a>
 */
public class CarouselRides {

    private final boolean sample;
    private final InputStream in;
    private final PrintStream out;
    
    public CarouselRides(
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
    
    private double calcPrice(final int[] ab) {
        return ((double) ab[1]) / ab[0];
    }
    
    private void handleTestCase(final Integer n, final Integer m, final FastScanner sc) {
        int[] cheapest = new int[] { 1, Integer.MAX_VALUE };
        double cheapestPrice = calcPrice(cheapest);
        for (int i = 0; i < n; i++) {
            final int ab[] = new int[2];
            ab[0] = sc.nextInt();
            ab[1] = sc.nextInt();
            if (ab[0] > m) {
                continue;
            }
            final double abPrice = calcPrice(ab);
            if (abPrice < cheapestPrice
                    || abPrice == cheapestPrice && ab[0] > cheapest[0]) {
                cheapest = ab;
                cheapestPrice = abPrice;
            }
        }
        final String ans;
        if (cheapest[1] == Integer.MAX_VALUE) {
            ans = "No suitable tickets offered";
        } else {
            ans = String.format("Buy %d tickets for $%d", cheapest[0], cheapest[1]);
        }
        this.out.println(ans);
    }
    
    public void solve() {
        try (final FastScanner sc = new FastScanner(this.in)) {
            while (true) {
                final int n = sc.nextInt();
                final int m = sc.nextInt();
                if (n == 0 && m == 0) {
                    break;
                }
                handleTestCase(n, m, sc);
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
            is = CarouselRides.class.getResourceAsStream("sample.in");
            out = new PrintStream(baos, true);
            timerStart = System.nanoTime();
        } else {
            is = System.in;
            out = System.out;
        }
        
        new CarouselRides(sample, is, out).solve();
        
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
                    = Paths.get(CarouselRides.class.getResource("sample.out").toURI());
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
