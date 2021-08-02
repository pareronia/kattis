package com.github.pareronia.kattis.basicprogramming2;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.groupingBy;
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
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * Basic Programming 2
 * @see <a href="https://open.kattis.com/problems/basicprogramming2">https://open.kattis.com/problems/basicprogramming2</a>
 */
public class BasicProgramming2 {

    private final boolean sample;
    private final InputStream in;
    private final PrintStream out;
    
    public BasicProgramming2(
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
        final int t = sc.nextInt();
        final int[] a = sc.nextIntArray(n);
        String ans = "";
        if (t == 1) {
            ans = summants(a, 7777);
        } else if (t == 2) {
            ans = duplicate(a);
        } else if (t == 3) {
            ans = largerThanHalfArraySize(a);
        } else if (t == 4) {
            ans = median(n, a);
        } else if (t == 5) {
            ans = between99And1000(a);
        }
        return new Result<>(i, List.of(ans));
    }

    private String between99And1000(final int[] a) {
        String ans;
        Arrays.sort(a);
        ans = Arrays.stream(a)
                .filter(x -> x >= 100)
                .filter(x -> x <= 999)
                .boxed()
                .map(String::valueOf)
                .collect(joining(" "));
        return ans;
    }

    private String median(final int n, final int[] a) {
        String ans;
        Arrays.sort(a);
        if (n % 2 == 1) {
            ans = String.valueOf(a[n / 2]);
        } else {
            ans = String.valueOf(a[n / 2 - 1]) + " "+ String.valueOf(a[n / 2]);
        }
        return ans;
    }

    private String largerThanHalfArraySize(final int[] a) {
        String ans;
        final Map<Integer, Long> map = Arrays.stream(a)
                .boxed().collect(groupingBy(x -> x, counting()));
        ans = map.entrySet().stream()
                .filter(e -> e.getValue() > a.length / 2)
                .map(Entry::getKey)
                .findFirst()
                .map(String::valueOf)
                .orElse("-1");
        return ans;
    }
    
    private String summants(final int[] a, final int target) {
        final Set<Integer> seen = new HashSet<>();
        for (int j = 0; j < a.length; j++) {
            seen.add(a[j]);
            final int n2 = target - a[j];
            if (seen.contains(n2)) {
                return "Yes";
            }
        }
        return "No";
    }

    private String duplicate(final int[] a) {
        String ans;
        final Map<Integer, Long> map = Arrays.stream(a)
                .boxed().collect(groupingBy(x -> x, counting()));
        ans = map.values().stream().anyMatch(x -> x > 1)
                ? "Contains duplicate" : "Unique";
        return ans;
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
            is = BasicProgramming2.class.getResourceAsStream("sample.in");
            out = new PrintStream(baos, true);
            timerStart = System.nanoTime();
        } else {
            is = System.in;
            out = System.out;
        }
        
        new BasicProgramming2(sample, is, out).solve();
        
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
                    = Paths.get(BasicProgramming2.class.getResource("sample.out").toURI());
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
}
