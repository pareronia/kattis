package com.github.pareronia.kattis.knightjump;

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
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * Knight Jump
 * @see <a href="https://open.kattis.com/problems/knightjump">https://open.kattis.com/problems/knightjump</a>
 */
public class KnightJump {

    private static final char KNIGHT = 'K';
    private static final char BLOCKED = '#';
    
    private final boolean sample;
    private final InputStream in;
    private final PrintStream out;
    
    public KnightJump(
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
    
    // TODO knight moves
    public static final class KnightMoves {
        public static List<int[]> get(final int r, final int c) {
            return List.of(
                    new int[] { r+2, c+1 },
                    new int[] { r+2, c-1 },
                    new int[] { r-2, c+1 },
                    new int[] { r-2, c-1 },
                    new int[] { r+1, c+2 },
                    new int[] { r+1, c-2 },
                    new int[] { r-1, c+2 },
                    new int[] { r-1, c-2 }
            );
        }
    }
    
    private int bfs(final char[][] g, final Pair<Integer, Integer> start) {
        int min = Integer.MAX_VALUE;
        final Deque<List<Pair<Integer, Integer>>> queue = new ArrayDeque<>();
        queue.add(List.of(start));
        final Set<Pair<Integer, Integer>> seen = new HashSet<>();
        seen.add(start);
        List<Pair<Integer, Integer>> moves = null;
        while (!queue.isEmpty()) {
            moves = queue.poll();
            final Pair<Integer, Integer> last = moves.get(moves.size() - 1);
            if (last.getOne() == 0 && last.getTwo() == 0) {
                if (moves.size() - 1 < min) {
                    min = moves.size() - 1;
                }
                continue;
            }
            for (final int[] km : KnightMoves.get(last.getOne(), last.getTwo())) {
                final int rr = km[0];
                final int cc = km[1];
                final Pair<Integer, Integer> newMove = Pair.of(rr, cc);
                if (0 <= rr && 0 <= cc
                        && rr < g.length && cc < g[0].length
                        && g[rr][cc] != BLOCKED
                        && !seen.contains(newMove)
                        ) {
                    final ArrayList<Pair<Integer, Integer>> newMoves = new ArrayList<>(moves);
                    newMoves.add(newMove);
                    queue.add(newMoves);
                    seen.add(newMove);
                }
            }
        }
        return min;
    }
    
    private Result<?> handleTestCase(final Integer i, final FastScanner sc) {
        final int n = sc.nextInt();
        final char[][] g = new char[n][n];
        Pair<Integer, Integer> k = null;
        for (int r = 0; r < n; r++) {
            g[r] = sc.next().toCharArray();
            for (int c = 0; c < n; c++) {
                if (g[r][c] == KNIGHT) {
                    k = Pair.of(r, c);
                }
            }
        }
        final int moves = bfs(g, k);
        final int ans = moves == Integer.MAX_VALUE ? -1 : moves;
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
            is = KnightJump.class.getResourceAsStream("sample.in");
            out = new PrintStream(baos, true);
            timerStart = System.nanoTime();
        } else {
            is = System.in;
            out = System.out;
        }
        
        new KnightJump(sample, is, out).solve();
        
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
                    = Paths.get(KnightJump.class.getResource("sample.out").toURI());
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
            final Pair<L, R> other = (Pair<L, R>) obj;
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
