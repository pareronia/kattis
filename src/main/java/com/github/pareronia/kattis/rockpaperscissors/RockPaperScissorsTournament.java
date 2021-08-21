package com.github.pareronia.kattis.rockpaperscissors;

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
 * Rock-Paper-Scissors Tournament
 * @see <a href="https://open.kattis.com/problems/rockpaperscissors">https://open.kattis.com/problems/rockpaperscissors</a>
 */
public class RockPaperScissorsTournament {

    private final boolean sample;
    private final InputStream in;
    private final PrintStream out;
    
    public RockPaperScissorsTournament(
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
    
    private void handleTestCase(final Integer n, final FastScanner sc) {
        final int k = sc.nextInt();
        final int wins[] = new int[n + 1];
        final int draws[] = new int[n + 1];
        for (int j = 0; j < k * n * (n - 1) / 2; j++) {
            final int p1 = sc.nextInt();
            final String t1 = sc.next();
            final int p2 = sc.nextInt();
            final String t2 = sc.next();
            if (t1.equals(t2)) {
                draws[p1]++;
                draws[p2]++;
            } else if ("paper".equals(t1)) {
                if ("rock".equals(t2)) {
                    wins[p1]++;
                } else {
                    wins[p2]++;
                }
            } else if ("paper".equals(t2)) {
                if ("rock".equals(t1)) {
                    wins[p2]++;
                } else {
                    wins[p1]++;
                }
            } else if ("scissors".equals(t1)) {
                if ("paper".equals(t2)) {
                    wins[p1]++;
                } else {
                    wins[p2]++;
                }
            } else if ("scissors".equals(t2)) {
                if ("paper".equals(t1)) {
                    wins[p2]++;
                } else {
                    wins[p1]++;
                }
            }
        }
        for (int j = 1; j <= n; j++) {
            final int decided = k * (n - 1) - draws[j];
            if (decided == 0) {
                this.out.println("-");
            } else {
                this.out.println(String.format("%.3f", ((double) wins[j]) / decided));
            }
        }
        this.out.println();
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
            is = RockPaperScissorsTournament.class.getResourceAsStream("sample.in");
            out = new PrintStream(baos, true);
            timerStart = System.nanoTime();
        } else {
            is = System.in;
            out = System.out;
        }
        
        new RockPaperScissorsTournament(sample, is, out).solve();
        
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
                    = Paths.get(RockPaperScissorsTournament.class.getResource("sample.out").toURI());
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
