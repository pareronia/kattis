package com.github.pareronia.kattis.simplearithmetic;

import static java.util.Arrays.asList;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.math.BigInteger;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

/**
 * Simple Arithmetic
 * @see <a href="https://open.kattis.com/problems/simplearithmetic">https://open.kattis.com/problems/simplearithmetic</a>
 */
public class SimpleArithmetic {

    private final InputStream in;
    private final PrintStream out;

    public SimpleArithmetic(
            final Boolean sample, final InputStream in, final PrintStream out) {
        this.in = in;
        this.out = out;
    }

    private void handleTestCase(final int i, final FastScanner sc) {
        final long a = sc.nextInt();
        final long b = sc.nextInt();
        final long c = sc.nextInt();
        final long ab = a * b;
        final String result = divide(BigInteger.valueOf(ab), BigInteger.valueOf(c));
        final StringBuilder ans = new StringBuilder();
        if (result.contains(".")) {
            final String[] split = result.split("\\.");
            if (split[1].length() < 18) {
                ans.repeat(split[1], 18 / split[1].length() + 18 % split[1].length());
            } else {
                ans.append(split[1]);
            }
            ans.delete(18, ans.length());
            ans.insert(0, '.');
            ans.insert(0, split[0]);
        } else {
            ans.repeat('0', 18);
            ans.insert(0, '.');
            ans.insert(0, result);
        }
        this.out.println(ans.toString());
    }

    private String divide(final BigInteger numerator, final BigInteger denominator) {
        String result = numerator.divide(denominator).toString() + ".";
        BigInteger carry = numerator.mod(denominator).multiply(BigInteger.TEN);
        int digitCount = 0;
        final Map<BigInteger, Integer> carries = new HashMap<>();
        while (!carries.containsKey(carry) && digitCount < 18) {
            if (carry.signum() == 0) {
                if (result.endsWith(".")) {
                    result = result.substring(0, result.length() - 1);
                }
                return result;
            }
            carries.put(carry, digitCount++);
            if (carry.compareTo(denominator) < 0) {
                result += "0";
                carry = carry.multiply(BigInteger.TEN);
            } else {
                result += carry.divide(denominator).toString();
                carry = carry.mod(denominator).multiply(BigInteger.TEN);
            }
        }
        return result;
    }

    public void solve() {
        try (final FastScanner sc = new FastScanner(this.in)) {
            final int numberOfTestCases = isSample() ? sc.nextInt() : 1;
            for (int i = 0; i < numberOfTestCases; i++) {
                handleTestCase(i, sc);
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
            is = SimpleArithmetic.class.getResourceAsStream("sample.in");
            out = new PrintStream(baos, true);
            timerStart = System.nanoTime();
        } else {
            is = System.in;
            out = System.out;
        }

        new SimpleArithmetic(sample, is, out).solve();

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
                    = Paths.get(SimpleArithmetic.class.getResource("sample.out").toURI());
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
