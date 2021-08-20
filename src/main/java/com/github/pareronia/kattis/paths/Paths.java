package com.github.pareronia.kattis.paths;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.summingLong;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

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
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * Paths
 * @see <a href="https://open.kattis.com/problems/paths">https://open.kattis.com/problems/paths</a>
 */
public class Paths {

    private final boolean sample;
    private final InputStream in;
    private final PrintStream out;
    
    public Paths(
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
    
    private static final class MutableList {
        public static <T> List<T> of(final T t) {
            return new ArrayList<>(List.of(t));
        }
        
        public static <T> List<T> merge(final List<T> l1, final List<T> l2) {
            l1.addAll(l2);
            return l1;
        }
    }
    
    private long bfs(final Map<Integer, List<Integer>> map, final int[] c,
            final Integer start) {
        
        final Deque<List<Integer>> queue = new ArrayDeque<>();
        queue.add(List.of(start));
        long cnt = 0;
        while (!queue.isEmpty()) {
            final List<Integer> path = queue.poll();
            if (path.size() > 1) {
                cnt++;
            }
            final Set<Integer> colors = path.stream()
                    .map(v -> c[v])
                    .collect(toSet());
            final Integer last = path.get(path.size() - 1);
            final List<Integer> neighbours
                    = map.getOrDefault(last, emptyList());
            for (final Integer n : neighbours) {
                if (!path.contains(n) && !colors.contains(c[n])) {
                    final List<Integer> newPath = new ArrayList<>();
                    newPath.addAll(path);
                    newPath.add(n);
                    queue.add(newPath);
                }
            }
        }
        return cnt;
    }
    
    private Result<?> handleTestCase(final Integer i, final FastScanner sc) {
        final int n = sc.nextInt();
        final int m = sc.nextInt();
        sc.nextInt();
        final int[] c = new int[n + 1];
        for (int j = 1; j <= n; j++) {
            c[j] = sc.nextInt();
        }
        final Map<Integer, List<Integer>> map = new HashMap<>();
        for (int j = 0; j < m; j++) {
           final int v1 = sc.nextInt();
           final int v2 = sc.nextInt();
           map.merge(v1, MutableList.of(v2), MutableList::merge);
           map.merge(v2, MutableList.of(v1), MutableList::merge);
        }
        final Long ans = map.keySet().stream()
                .map(v -> bfs(map, c, v))
                .collect(summingLong(Long::valueOf));
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
            is = Paths.class.getResourceAsStream("sample.in");
            out = new PrintStream(baos, true);
            timerStart = System.nanoTime();
        } else {
            is = System.in;
            out = System.out;
        }
        
        new Paths(sample, is, out).solve();
        
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
                    = java.nio.file.Paths.get(Paths.class.getResource("sample.out").toURI());
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
}
