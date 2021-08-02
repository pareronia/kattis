package com.github.pareronia.kattis.calculatingdartscores;

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
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * Calculating Dart Scores
 * @see <a href="https://open.kattis.com/problems/calculatingdartscores">https://open.kattis.com/problems/calculatingdartscores</a>
 */
public class CalculatingDartScores {

    private final boolean sample;
    private final InputStream in;
    private final PrintStream out;
    
    public CalculatingDartScores(
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
    
    private void score(final List<String> ans, final int n) {
        if (n == 0) {
            return;
        }
        if (n == 143) {
            ans.add("triple 20");
            ans.add("triple 17");
            ans.add("double 16");
            return;
        }
        if (n == 145) {
            ans.add("triple 20");
            ans.add("triple 15");
            ans.add("double 20");
            return;
        }
        if (n == 149) {
            ans.add("triple 20");
            ans.add("triple 19");
            ans.add("double 16");
            return;
        }
        if (n == 151) {
            ans.add("triple 20");
            ans.add("triple 17");
            ans.add("double 20");
            return;
        }
        if (n == 157) {
            ans.add("triple 20");
            ans.add("triple 19");
            ans.add("double 20");
            return;
        }
        if (n <= 20) {
            ans.add(String.format("single %d", n));
            return;
        } else {
            if (ans.size() == 1 && n % 5 == 0 && n / 5 <= 20) {
                ans.add(String.format("triple %d", n / 5));
                ans.add(String.format("double %d", n / 5));
                return;
            } else if (ans.size() == 1 && n % 4 == 0 && n / 4 <= 20) {
                ans.add(String.format("double %d", n / 4));
                ans.add(String.format("double %d", n / 4));
                return;
            } else if (ans.size() == 2 && n % 3 == 0 && n / 3 <= 20) {
                ans.add(String.format("triple %d", n / 3));
                return;
            } else if (ans.size() == 2 && n % 2 == 0 && n / 2 <= 20) {
                ans.add(String.format("double %d", n / 2));
                return;
            } else if (n >= 60) {
                ans.add("triple 20");
                score(ans, n - 60);
            } else if (n >= 40) {
                ans.add("double 20");
                score(ans, n - 40);
            } else if (n >= 20) {
                ans.add("single 20");
                score(ans, n - 20);
            }
        }
    }
    
    private Result<?> handleTestCase(final Integer i, final FastScanner sc) {
        final int n = sc.nextInt();
        final List<String> ans = calc(n);
        return new Result<>(i, ans);
    }

    private List<String> calc(final int n) {
        List<String> ans = new ArrayList<>();
        score(ans, n);
        if (ans.size() > 3) {
            ans = List.of("impossible");
        }
        return ans;
    }
    
    private void verify(final List<String> ans, final int n) {
        if (ans.size() == 1 && "impossible".equals(ans.get(0))) {
            assert Set.of(161, 163, 164, 166, 167, 169, 170, 172, 173, 175, 176, 178, 179).contains(n);
            return;
        }
        int sum = 0;
        for (final String string : ans) {
            final String[] s = string.split(" ");
            final Integer d = Integer.valueOf(s[1]);
            assert 1 <= d && d <= 20;
            if ("single".equals(s[0])) {
                sum += d;
            } else if ("double".equals(s[0])) {
                sum += 2 * d;
            } else if ("triple".equals(s[0])) {
                sum += 3 * d;
            } else {
                assert false;
            }
        }
        assert sum == n;
    }
    
    public void solve() {
        try (final FastScanner sc = new FastScanner(this.in)) {
            final int numberOfTestCases;
            if (this.sample) {
                for (int j = 0; j < 180; j++) {
                    final List<String> ans = calc(j);
                    verify(ans, j);
                }
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
            is = CalculatingDartScores.class.getResourceAsStream("sample.in");
            out = new PrintStream(baos, true);
            timerStart = System.nanoTime();
        } else {
            is = System.in;
            out = System.out;
        }
        
        new CalculatingDartScores(sample, is, out).solve();
        
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
            final List<String> actual = asList(baos.toString().split("\\r?\\n"));
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
