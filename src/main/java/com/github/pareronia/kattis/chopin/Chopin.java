package com.github.pareronia.kattis.chopin;

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
import java.util.function.Supplier;

/**
 * Chopin
 * @see <a href="https://open.kattis.com/problems/chopin">https://open.kattis.com/problems/chopin</a>
 */
public class Chopin {

    private static final char[] NOTES
            = new char[] { 'A', 'B', 'C', 'D', 'E', 'F', 'G' };
    
    private final boolean sample;
    private final InputStream in;
    private final PrintStream out;
    
    public Chopin(
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
        final String note = sc.next();
        final String tonality = sc.next();
        String ans;
        if (note.length() == 1) {
            ans = "UNIQUE";
        } else if (note.charAt(1) == '#') {
            final int n = Arrays.binarySearch(NOTES, note.charAt(0));
            final char alt = NOTES[(NOTES.length + n + 1) % NOTES.length];
            ans = String.format("%sb ", alt) + tonality;
        } else if (note.charAt(1) == 'b') {
            final int n = Arrays.binarySearch(NOTES, note.charAt(0));
            final char alt = NOTES[(NOTES.length + n - 1) % NOTES.length];
            ans = String.format("%s# ", alt) + tonality;
        } else {
            throw new IllegalStateException("Unsolvable");
        }
        return new Result<>(i, List.of(ans));
    }
    
    public void solve() {
        try (final FastScanner sc = new FastScanner(this.in)) {
            final List<Result<?>> results = new ArrayList<>();
            try {
                int i = 1;
                while (true) {
                    results.add(handleTestCase(i++, sc));
                }
            } catch (final NullPointerException e) {
                // nop
            }
            output(results);
        }
    }

    private void output(final List<Result<?>> results) {
        results.forEach(r -> this.out.println(String.format("Case %d: %s",
                r.number,
                r.values.stream().map(Object::toString).collect(joining(" ")))));
    }
    
    public static void main(final String[] args) throws IOException, URISyntaxException {
        final boolean sample = isSample();
        final InputStream is;
        final PrintStream out;
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        long timerStart = 0;
        if (sample) {
            is = Chopin.class.getResourceAsStream("sample.in");
            out = new PrintStream(baos, true);
            timerStart = System.nanoTime();
        } else {
            is = System.in;
            out = System.out;
        }
        
        new Chopin(sample, is, out).solve();
        
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
                    = Paths.get(Chopin.class.getResource("sample.out").toURI());
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
        private final int number;
        private final List<T> values;
        
        public Result(final int number, final List<T> values) {
            this.number = number;
            this.values = values;
        }
    }
}
