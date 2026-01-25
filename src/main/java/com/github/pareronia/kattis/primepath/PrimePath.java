package com.github.pareronia.kattis.primepath;

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
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

/**
 * Prime Path
 * @see <a href="https://open.kattis.com/problems/primepath">https://open.kattis.com/problems/primepath</a>
 */
public class PrimePath {

    private final InputStream in;
    private final PrintStream out;

    public PrimePath(
            final Boolean sample, final InputStream in, final PrintStream out) {
        this.in = in;
        this.out = out;
    }

    private void handleTestCase(final int i, final FastScanner sc) {
        final char[] a = sc.next().toCharArray();
        final char[] b = sc.next().toCharArray();
        final int end = Arrays.hashCode(b);
        final Deque<State> q = new ArrayDeque<>();
        q.add(new State(a, 0));
        final Set<Integer> seen = new HashSet<>();
        seen.add(Arrays.hashCode(a));
        String ans = "Impossible";
        while (!q.isEmpty()) {
            final State curr = q.pollFirst();
            final int h = Arrays.hashCode(curr.num);
            if (h == end) {
                ans = String.valueOf(curr.steps);
                break;
            }
            for (int j = 0; j < 4; j++) {
                for (int k = (j == 0 ? 1 : 0); k < 10; k++) {
                    final char[] nxt = Arrays.copyOf(curr.num, 4);
                    final char ch = (char) ('0' + k);
                    if (ch == nxt[j]) {
                        continue;
                    }
                    nxt[j] = ch;
                    if (!isPrime((nxt[0] - '0') * 1000 + (nxt[1] - '0') * 100 + (nxt[2] - '0') * 10 + (nxt[3] - '0'))) {
                        continue;
                    }
                    final int nxth = Arrays.hashCode(nxt);
                    if (!seen.contains(nxth)) {
                        q.add(new State(nxt, curr.steps + 1));
                        seen.add(nxth);
                    }
                }
            }
        }
        this.out.println(ans);
    }

    private boolean isPrime(final int num) {
        final int start = (int) Math.floor(Math.sqrt(num));
        for (int i = start; i >= 2; i--) {
            if (num % i == 0) {
                return false;
            }
        }
        return true;
    }

    private record State(char[] num, int steps) {}

    public void solve() {
        try (final FastScanner sc = new FastScanner(this.in)) {
            final int numberOfTestCases = sc.nextInt();
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
            is = PrimePath.class.getResourceAsStream("sample.in");
            out = new PrintStream(baos, true);
            timerStart = System.nanoTime();
        } else {
            is = System.in;
            out = System.out;
        }

        new PrimePath(sample, is, out).solve();

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
                    = Paths.get(PrimePath.class.getResource("sample.out").toURI());
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
