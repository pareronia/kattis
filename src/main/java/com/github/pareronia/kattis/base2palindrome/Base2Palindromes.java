package com.github.pareronia.kattis.base2palindrome;

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
import java.util.Deque;
import java.util.List;
import java.util.StringTokenizer;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * Base-2 Palindromes
 * @see <a href="https://open.kattis.com/problems/base2palindrome">https://open.kattis.com/problems/base2palindrome</a>
 */
public class Base2Palindromes {

    private final boolean sample;
    private final InputStream in;
    private final PrintStream out;
    
    public Base2Palindromes(
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
        final int m = sc.nextInt();
        final String p = Palindrome.getNthBinaryPalindrome(m);
        final int ans = Integer.parseInt(p, 2);
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
            is = Base2Palindromes.class.getResourceAsStream("sample.in");
            out = new PrintStream(baos, true);
            timerStart = System.nanoTime();
        } else {
            is = System.in;
            out = System.out;
        }
        
        new Base2Palindromes(sample, is, out).solve();
        
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
                    = Paths.get(Base2Palindromes.class.getResource("sample.out").toURI());
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
    
    // TODO Palindrome
    public static final class Palindrome {
        public static boolean isPalindrome(final int number, final int radix) {
            if (radix == 2) {
                return isPalindrome2(number);
            } else if (radix == 10) {
                return isPalindrome10((long) number);
            } else {
                throw new UnsupportedOperationException();
            }
        }
        
        public static boolean isPalindrome(final long number, final int radix) {
            if (radix == 2) {
                return isPalindrome2(number);
            } else if (radix == 10) {
                return isPalindrome10(number);
            } else {
                throw new UnsupportedOperationException();
            }
        }
        
        public static boolean isPalindrome10(final Long number) {
            final String string = number.toString();
            for (int i = 0; i < string.length() / 2; i++) {
                if (string.charAt(i) != string.charAt(string.length() - 1 - i)) {
                    return false;
                }
            }
            return true;
        }

        public static boolean isPalindrome2(final long number) {
            final int lz = Long.numberOfLeadingZeros(number);
            final int bitCount = 64 - lz;
            for (int i = 0; i < bitCount / 2; i++) {
                final long left = number & (1 << (bitCount - 1 - i));
                final long right = number & (1 << i);
                if (Long.bitCount(left) != Long.bitCount(right)) {
                    return false;
                }
            }
            return true;
        }
        
        public static boolean isPalindrome2(final int number) {
            final int lz = Integer.numberOfLeadingZeros(number);
            final int bitCount = 32 - lz;
            for (int i = 0; i < bitCount / 2; i++) {
                final int left = number & (1 << (bitCount - 1 - i));
                final int right = number & (1 << i);
                if (Integer.bitCount(left) != Integer.bitCount(right)) {
                    return false;
                }
            }
            return true;
        }
        
        /**
         * @see <a href = "https://www.geeksforgeeks.org/find-n-th-number-whose-binary-representation-palindrome/">https://www.geeksforgeeks.org/find-n-th-number-whose-binary-representation-palindrome/</a>
         */
        public static String getNthBinaryPalindrome(final int n) {
            if (n == 1) {
                return "1";
            }
            final Deque<String> queue = new ArrayDeque<>();
            queue.add("11");
            int cnt = 1;
            String b = null;
            while (cnt++ < n) {
                b = queue.poll();
                final int len = b.length();
                final String first = b.substring(0, len / 2);
                final String second = b.substring(len / 2, len);
                if (len % 2 == 0) {
                    queue.add(first + "0" + second);
                    queue.add(first + "1" + second);
                } else {
                    final char middle = b.charAt(len / 2);
                    queue.add(first + middle + second);
                }
            }
            return b;
        }
    }
}
