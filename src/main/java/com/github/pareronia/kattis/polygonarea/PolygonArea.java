package com.github.pareronia.kattis.polygonarea;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;

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
import java.util.List;
import java.util.StringTokenizer;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * Polygon Area
 * @see <a href="https://open.kattis.com/problems/polygonarea">https://open.kattis.com/problems/polygonarea</a>
 * @see <a href="https://www.wikihow.com/Calculate-the-Area-of-a-Polygon#Finding-the-Area-of-Irregular-Polygons">https://www.wikihow.com/Calculate-the-Area-of-a-Polygon#Finding-the-Area-of-Irregular-Polygons</a>
 * @see <a href="https://stackoverflow.com/a/1165943">https://stackoverflow.com/a/1165943</a>
 */
public class PolygonArea {

    private final boolean sample;
    private final InputStream in;
    private final PrintStream out;
    
    public PolygonArea(
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
        final List<String> anss = new ArrayList<>();
        while (true) {
            final int n = sc.nextInt();
            if (n == 0) {
                break;
            }
            final List<Pair<Integer, Integer>> coords = new ArrayList<>();
            for (int j = 0; j < n; j++) {
                final int x = sc.nextInt();
                final int y = sc.nextInt();
                coords.add(Pair.of(x, y));
            }
            log(() -> coords);
            int d = 0;
            for (int j = 0; j < coords.size(); j++) {
                final Pair<Integer, Integer> c1 = coords.get(j);
                final Pair<Integer, Integer> c2 = coords.get((j + 1) % coords.size());
                d += (c2.getOne() - c1.getOne()) * (c2.getTwo() + c1.getTwo());
            }
            final List<Pair<Integer, Integer>> ncoords;
            if (d > 0) {
                ncoords = Stream.iterate(coords.size() - 1, j -> j >= 0, j -> j - 1)
                        .map(coords::get)
                        .collect(toList());
            } else {
                ncoords = new ArrayList<>(coords);
            }
            double l = 0;
            double r = 0;
            for (int j = 0; j < ncoords.size(); j++) {
                final Pair<Integer, Integer> c1 = ncoords.get(j);
                final Pair<Integer, Integer> c2 = ncoords.get((j + 1) % ncoords.size());
                l += c1.getOne() * c2.getTwo();
                r += c1.getTwo() * c2.getOne();
            }
            anss.add(String.format("%s %.1f",
                    d > 0 ? "CW" : "CCW",
                    (l - r) / 2));
        }
        return new Result<>(i, anss);
    }
    
    public void solve() {
        try (final FastScanner sc = new FastScanner(this.in)) {
            final int numberOfTestCases = 1;
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
            is = PolygonArea.class.getResourceAsStream("sample.in");
            out = new PrintStream(baos, true);
            timerStart = System.nanoTime();
        } else {
            is = System.in;
            out = System.out;
        }
        
        new PolygonArea(sample, is, out).solve();
        
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
                    = Paths.get(PolygonArea.class.getResource("sample.out").toURI());
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

        @Override
        public String toString() {
            final StringBuilder builder = new StringBuilder();
            builder.append("Pair [one=").append(one).append(", two=").append(two).append("]");
            return builder.toString();
        }
    }
}
