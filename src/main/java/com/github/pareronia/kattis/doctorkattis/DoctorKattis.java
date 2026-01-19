package com.github.pareronia.kattis.doctorkattis;

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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.StringTokenizer;

/**
 * Doctor Kattis - TLE
 * @see <a href="https://open.kattis.com/problems/doctorkattis">https://open.kattis.com/problems/doctorkattis</a>
 */
public class DoctorKattis {

    private final InputStream in;
    private final PrintStream out;

    public DoctorKattis(
            final Boolean sample, final InputStream in, final PrintStream out) {
        this.in = in;
        this.out = out;
    }

    private void handleTestCase(final int i, final FastScanner sc) {
        final int n = sc.nextInt();
        final Map<String, Integer> level = new HashMap<>();
        final Map<String, Integer> order = new HashMap<>();
        final PriorityQueue<String> q
            = new PriorityQueue<>((c1, c2) -> {
                final int cmp = -Integer.compare(level.get(c1), level.get(c2));
                if (cmp == 0) {
                    return Integer.compare(order.get(c1), order.get(c2));
                }
                return cmp;
            });
        final StringBuilder ans = new StringBuilder();
        int k = 0;
        for (int j = 0; j < n; j++) {
            switch (sc.nextInt()) {
                case 0 -> {
                    final String name = sc.next();
                    level.put(name, sc.nextInt());
                    order.put(name, k++);
                    q.add(name);
                }
                case 1 -> {
                    final String name = sc.next();
                    q.remove(name);
                    level.put(name, level.get(name) + sc.nextInt());
                    q.add(name);
                }
                case 2 -> {
                    final String name = sc.next();
                    q.remove(name);
                    level.remove(name);
                    order.remove(name);
                }
                case 3 -> {
                    if (q.isEmpty()) {
                        ans.append("The clinic is empty");
                    } else {
                        ans.append(q.peek());
                    }
                    ans.append(System.lineSeparator());
                }
            }
        }
        this.out.print(ans.toString());
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
            is = DoctorKattis.class.getResourceAsStream("sample.in");
            out = new PrintStream(baos, true);
            timerStart = System.nanoTime();
        } else {
            is = System.in;
            out = System.out;
        }

        new DoctorKattis(sample, is, out).solve();

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
                    = Paths.get(DoctorKattis.class.getResource("sample.out").toURI());
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
