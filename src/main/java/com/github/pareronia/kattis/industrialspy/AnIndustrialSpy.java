package com.github.pareronia.kattis.industrialspy;

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
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.function.Supplier;
import java.util.stream.Stream;
import java.util.stream.Stream.Builder;

/**
 * An Industrial Spy
 * @see <a href="https://open.kattis.com/problems/industrialspy">https://open.kattis.com/problems/industrialspy</a>
 */
public class AnIndustrialSpy {

    private final boolean sample;
    private final InputStream in;
    private final PrintStream out;
    
    public AnIndustrialSpy(
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
    
    private void permutate(String str, final int l, final int r,
            final Stream.Builder<String> perms) {
        
        if (l == r) {
            perms.add(str);
        } else {
            for (int i = l; i <= r; i++) {
                str = swap(str,l,i);
                permutate(str, l+1, r, perms);
                str = swap(str,l,i);
            }
        }
    }
    
    private String swap(final String a, final int i, final int j) {
        char temp;
        final char[] charArray = a.toCharArray();
        temp = charArray[i] ;
        charArray[i] = charArray[j];
        charArray[j] = temp;
        return String.valueOf(charArray);
    }
    
    private void powerSet(final String s, final Stream.Builder<String> builder) {
        final int size = (int) Math.pow(2, s.length());
        for (int i = 0; i < size; i++) {
            final StringBuilder sb = new StringBuilder();
            for (int j = 0; j < s.length(); j++) {
                if ((i & 1 << j) > 0) {
                    sb.append(s.charAt(j));
                }
            }
            final String ans = sb.toString();
            if (ans != null && !ans.isEmpty()) {
                builder.add(ans);
            }
        }
    }
    
    private boolean isPrime(final Integer number) {
        final int start = (int) Math.floor(Math.sqrt(number));
        for (int i = start; i >= 2; i--) {
            if (number % i == 0) {
                return false;
            }
        }
        return true;
    }
    
    private Result<?> handleTestCase(final Integer i, final FastScanner sc) {
        final String s = sc.next();
        final Set<Integer> seen = new HashSet<>();
        final Builder<String> powerSetBuilder = Stream.builder();
        powerSet(s, powerSetBuilder);
        final long ans = powerSetBuilder.build()
                .flatMap(ss -> {
                    final Builder<String> builder = Stream.builder();
                    permutate(ss, 0, ss.length() - 1, builder);
                    return builder.build();
                })
                .map(Integer::valueOf)
                .filter(p -> p > 1)
                .filter(p -> !seen.contains(p))
                .filter(p -> {
                    seen.add(p);
                    return isPrime(p);
                })
                .count();
        return new Result<>(i, List.of(ans));
    }
    
    public void solve() {
        try (final FastScanner sc = new FastScanner(this.in)) {
            final int numberOfTestCases = sc.nextInt();
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
            is = AnIndustrialSpy.class.getResourceAsStream("sample.in");
            out = new PrintStream(baos, true);
            timerStart = System.nanoTime();
        } else {
            is = System.in;
            out = System.out;
        }
        
        new AnIndustrialSpy(sample, is, out).solve();
        
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
                    = Paths.get(AnIndustrialSpy.class.getResource("sample.out").toURI());
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
