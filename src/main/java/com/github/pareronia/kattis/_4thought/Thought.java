package com.github.pareronia.kattis._4thought;

import static java.util.Arrays.asList;

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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.function.Supplier;

/**
 * 4 thought
 * @see <a href="https://open.kattis.com/problems/4thought">https://open.kattis.com/problems/4thought</a>
 */
public class Thought {

    private final boolean sample;
    private final InputStream in;
    private final PrintStream out;
    
    public Thought(
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
    
    private static final char[] OPS = new char[] { '/', '-', '+', '*' };
    
    private int eval(final int lhs, final char op, final int rhs) {
        if (op == '+') {
            return lhs + rhs;
        } else if (op == '-') {
            return lhs - rhs;
        } else if (op == '*') {
            return lhs * rhs;
        } else {
            return lhs / rhs;
        }
    }
    
    private void handleTestCase(final Map<Integer, String> ans, final FastScanner sc) {
        final int n = sc.nextInt();
        if (ans.containsKey(n)) {
            this.out.println(ans.get(n));
        } else {
            this.out.println("no solution");
        }
    }
    
    public void solve() {
        final Map<Integer, String> ans = new HashMap<>();
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                for (int k = 0; k < 4; k++) {
                    final List<Integer> nums = new ArrayList<>(List.of(4, 4, 4, 4));
                    final List<Character> ops = new ArrayList<>(List.of(OPS[i], OPS[j], OPS[k]));
                    for (int m = 0; m < ops.size(); m++) {
                        if (ops.get(m) == '*' || ops.get(m) == '/') {
                            final int res = eval(nums.get(m), ops.get(m), nums.get(m + 1));
                            nums.set(m, res);
                            nums.remove(m + 1);
                            ops.remove(m);
                            m--;
                        }
                    }
                    for (int m = 0; m < ops.size(); m++) {
                        final int res = eval(nums.get(m), ops.get(m), nums.get(m + 1));
                        nums.set(m, res);
                        nums.remove(m + 1);
                        ops.remove(m);
                        m--;
                    }
                    final StringBuilder sb = new StringBuilder();
                    sb.append("4 ").append(OPS[i]).append(" 4 ")
                            .append(OPS[j]).append(" 4 ").append(OPS[k])
                            .append(" 4 = ").append(nums.get(0));
                    ans.put(nums.get(0), sb.toString());
                }
            }
        }
        try (final FastScanner sc = new FastScanner(this.in)) {
            final int numberOfTestCases = sc.nextInt();
            for (int i = 0; i < numberOfTestCases; i++) {
                handleTestCase(ans, sc);
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
            is = Thought.class.getResourceAsStream("sample.in");
            out = new PrintStream(baos, true);
            timerStart = System.nanoTime();
        } else {
            is = System.in;
            out = System.out;
        }
        
        new Thought(sample, is, out).solve();
        
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
                    = Paths.get(Thought.class.getResource("sample.out").toURI());
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
}
