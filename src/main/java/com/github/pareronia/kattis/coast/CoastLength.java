package com.github.pareronia.kattis.coast;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.joining;
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
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * Coast Length
 * @see <a href="https://open.kattis.com/problems/coast">https://open.kattis.com/problems/coast</a>
 */
public class CoastLength {

    private static final int WATER = 0;
    private static final int LAND = 1;
    private static final int SEA = 2;
    private static final Set<Pair<Integer, Integer>> NSEW = Set.of(
            Pair.of(-1, 0), Pair.of(1, 0), Pair.of(0, -1), Pair.of(0, 1)
    );
    
    private final boolean sample;
    private final InputStream in;
    private final PrintStream out;
    
    public CoastLength(
            final Boolean sample, final InputStream in, final PrintStream out) {
        this.sample = sample;
        this.in = in;
        this.out = out;
    }
    
    private void log(final Supplier<Object> supplier) {
        if (!sample) {
            return;
        }
        System.out.println(supplier.get());
    }
    
    private void printGrid(final int[][] grid) {
        Arrays.stream(grid).forEach(r ->
                log(() -> Arrays.stream(r)
                        .mapToObj(Integer::valueOf)
                        .map(String::valueOf)
                        .collect(joining(""))
                        .replace('1', '\u2592')
                        .replace('2', '~')
                        .replace('0', ' ')));
    }
    
    private void checkSea(final int[][] g) {
        for (int r = 0; r < g.length; r++) {
            for (int c = 0; c < g[0].length; c++) {
                if (g[r][c] != WATER) {
                    continue;
                }
                if (r == 0 || c == 0 || r == g.length - 1 || c == g[0].length - 1) {
                    g[r][c] = SEA;
                    continue;
                }
                final Deque<List<Pair<Integer, Integer>>> queue = new ArrayDeque<>();
                final Pair<Integer, Integer> sq = Pair.of(r, c);
                queue.add(List.of(sq));
                final Set<Pair<Integer, Integer>> seen = new HashSet<>();
                seen.add(sq);
                while (!queue.isEmpty()) {
                    final List<Pair<Integer, Integer>> pp = queue.poll();
                    final Pair<Integer, Integer> last = pp.get(pp.size() - 1);
                    final Integer lastr = last.getOne();
                    final Integer lastc = last.getTwo();
                    if (lastr == 0 || lastc == 0
                            || lastr == g.length - 1 || lastc == g[0].length - 1
                            || g[lastr][lastc] == SEA) {
                        for (int k = 0; k < pp.size() - 1; k++) {
                            g[pp.get(k).getOne()][pp.get(k).getTwo()] = SEA;
                        }
                        continue;
                    }
                    for (final Pair<Integer, Integer> rc : NSEW) {
                        final int rr = lastr + rc.getOne();
                        final int cc = lastc + rc.getTwo();
                        if (rr < 0 || rr >= g.length
                                || cc < 0 || cc >= g[0].length) {
                            continue;
                        }
                        if (g[rr][cc] != LAND) {
                            final Pair<Integer, Integer> newsq = Pair.of(rr, cc);
                            if (!seen.contains(newsq)) {
                                final List<Pair<Integer, Integer>> newpp = new ArrayList<>(pp);
                                seen.add(newsq);
                                newpp.add(newsq);
                                queue.add(newpp);
                            }
                        }
                    }
                }
            }
        }
    }
    
    private Result<?> handleTestCase(final Integer i, final FastScanner sc) {
        final int n = sc.nextInt();
        final int m = sc.nextInt();
        final int[][] g = new int[n][m];
        for (int r = 0; r < n; r++) {
            final String row = sc.next();
            for (int c = 0; c < m; c++) {
                g[r][c] = row.charAt(c) == '0' ? WATER : LAND;
            }
        }
        checkSea(g);
        log(() -> i);
        printGrid(g);
        int ans = 0;
        for (int r = 0; r < n; r++) {
            for (int c = 0; c < m; c++) {
                if (g[r][c] != 1) {
                    continue;
                }
                if (r == 0) {
                    ans++;
                }
                if (r == n - 1) {
                    ans++;
                }
                if (c == 0) {
                    ans++;
                }
                if (c == m - 1) {
                    ans++;
                }
                for (final Pair<Integer, Integer> rc : NSEW) {
                    final int rr = r + rc.getOne();
                    final int cc = c + rc.getTwo();
                    if (rr < 0 || rr >= n || cc < 0 || cc >= m) {
                        continue;
                    }
                    if (g[rr][cc] == SEA) {
                        ans++;
                    }
                }
            }
        }
        return new Result<>(i, List.of(ans));
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
            is = CoastLength.class.getResourceAsStream("sample.in");
            out = new PrintStream(baos, true);
            timerStart = System.nanoTime();
        } else {
            is = System.in;
            out = System.out;
        }
        
        new CoastLength(sample, is, out).solve();
        
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
                    = Paths.get(CoastLength.class.getResource("sample.out").toURI());
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

        @Override
        public int hashCode() {
            return Objects.hash(one, two);
        }

        @Override
        @SuppressWarnings("unchecked")
        public boolean equals(final Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final Pair<Integer, Integer> other = (Pair<Integer, Integer>) obj;
            return Objects.equals(one, other.one) && Objects.equals(two, other.two);
        }

        @Override
        public String toString() {
            final StringBuilder builder = new StringBuilder();
            builder.append("Pair [one=").append(one).append(", two=").append(two).append("]");
            return builder.toString();
        }

        public L getOne() {
            return one;
        }
        
        public R getTwo() {
            return two;
        }
    }
}
