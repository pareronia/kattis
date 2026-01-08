package com.github.pareronia.kattis.namegeneration;

import static java.util.Arrays.asList;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.URISyntaxException;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.StringTokenizer;

/**
 * Name Generation
 * @see <a href="https://open.kattis.com/problems/namegeneration">https://open.kattis.com/problems/namegeneration</a>
 */
public class NameGeneration {

    private final InputStream in;
    private final PrintStream out;

    public NameGeneration(
            final Boolean sample, final InputStream in, final PrintStream out) {
        this.in = in;
        this.out = out;
    }

    private void handleTestCase(final int i, final FastScanner sc) {
        final int n = sc.nextInt();
        final Deque<String> q = new ArrayDeque<>();
        q.add("aa");
        int cnt = 0;
        while (true) {
            final String s = q.pollFirst();
            if (s.length() >= 3) {
                this.out.println(s);
                cnt++;
            }
            if (cnt == n) {
                break;
            }
            int cntv = 0;
            int cntc = 0;
            for (int j = 1; j <= 2; j++) {
                final char ch = s.charAt(s.length() - j);
                if (ch == 'a' || ch == 'e' || ch== 'i' || ch == 'o' || ch == 'u') {
                    cntv++;
                } else {
                    cntc++;
                }
            }
            for (int j = 0; j < 26; j++) {
                final char ch = (char) ('a' + j);
                if (ch == 'a' || ch == 'e' || ch== 'i' || ch == 'o' || ch == 'u') {
                    if (cntv == 2) {
                        continue;
                    }
                } else {
                    if (cntc == 2) {
                        continue;
                    }
                }
                q.addLast(s + ch);
            }
        }
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
            is = NameGeneration.class.getResourceAsStream("sample.in");
            out = new PrintStream(baos, true);
            timerStart = System.nanoTime();
        } else {
            is = System.in;
            out = System.out;
        }

        new NameGeneration(sample, is, out).solve();

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
