package com.github.pareronia.kattis.ternarianweights;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.joining;

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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringTokenizer;
import java.util.stream.IntStream;

/**
 * Ternarian Weights
 * @see <a href="https://open.kattis.com/problems/ternarianweights">https://open.kattis.com/problems/ternarianweights</a>
 */
public class TernarianWeights {

    private final InputStream in;
    private final PrintStream out;
    
    public TernarianWeights(
            final Boolean sample, final InputStream in, final PrintStream out) {
        this.in = in;
        this.out = out;
    }
    
    private void handleTestCase(final Integer i, final FastScanner sc) {
        final int x = sc.nextInt();
        final int[] x3 = toBase3(x);
        final int[] l = new int[x3.length];
        for (int j = x3.length - 1; j > 1; j--) {
            if (x3[j] == 2) {
                x3[j] = 0;
                x3[j - 1]++;
                l[x3.length - 1 - j] = 1;
            }
            if (x3[j] == 3) {
                x3[j] = 0;
                x3[j - 1]++;
            }
        }
        final String ls = IntStream.range(0, l.length)
                .map(j -> l.length - 1 - j)
                .filter(j -> l[j] == 1)
                .mapToLong(p -> pow3(p)).boxed()
                .map(Object::toString)
                .collect(joining(" "));
        final String rs = IntStream.range(0, x3.length)
                .filter(j -> x3[j] == 1)
                .mapToLong(p -> pow3(x3.length - 1 - p)).boxed()
                .map(Object::toString)
                .collect(joining(" "));
        final List<String> a = new ArrayList<>();
        a.add(("left pan: " + ls).stripTrailing());
        a.add(("right pan: " + rs).stripTrailing());
        a.add("");
        final String ans = a.stream().collect(joining(System.lineSeparator()));
        this.out.println(ans);
    }
    
    private int[] toBase3(long n) {
        final int[] a = new int[25];
        Arrays.fill(a, 0);
        int pos = 24;
        while (n > 0) {
           a[pos--] = (int) (n % 3);
           n /= 3;
        }
        return a;
    }
    
    private long pow3(final int n) {
        long ans = 1;
        for (int i = 0; i < n; i++) {
            ans *= 3;
        }
        return ans;
    }
    
    public void solve() {
        try (final FastScanner sc = new FastScanner(this.in)) {
            final int numberOfTestCases = sc.nextInt();
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
            is = TernarianWeights.class.getResourceAsStream("sample.in");
            out = new PrintStream(baos, true);
            timerStart = System.nanoTime();
        } else {
            is = System.in;
            out = System.out;
        }
        
        new TernarianWeights(sample, is, out).solve();
        
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
                    = Paths.get(TernarianWeights.class.getResource("sample.out").toURI());
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
