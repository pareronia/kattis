package com.github.pareronia.kattis.hardware;

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

/**
 * Hardware
 * @see <a href="https://open.kattis.com/problems/hardware">https://open.kattis.com/problems/hardware</a>
 */
public class Hardware {

    private final InputStream in;
    private final PrintStream out;

    public Hardware(
            final Boolean sample, final InputStream in, final PrintStream out) {
        this.in = in;
        this.out = out;
    }

    private void handleTestCase(final FastScanner sc) {
        int n = sc.nextInt();
        final StringBuilder ans = new StringBuilder();
        while (n-- > 0) {
            ans.append(sc.nextLine()).append(System.lineSeparator());
            String line = sc.nextLine();
            ans.append(line).append(System.lineSeparator());
            int cnt = Integer.parseInt(line.split(" ")[0]);
            final List<Integer> nums = new ArrayList<>();
            while (cnt > 0) {
                line = sc.nextLine();
                if (line.startsWith("+")) {
                    final String[] sp = line.split(" ");
                    final int start = Integer.parseInt(sp[1]);
                    final int end = Integer.parseInt(sp[2]);
                    final int step = Integer.parseInt(sp[3]);
                    for (int j = start; j <= end; j += step) {
                        nums.add(j);
                        cnt--;
                    }
                } else {
                    nums.add(Integer.parseInt(line));
                    cnt--;
                }
            }
            final Map<Integer, Integer> map = new HashMap<>();
            for (final int num : nums) {
                int x = num;
                while (x > 0) {
                    map.merge(x % 10, 1, Integer::sum);
                    x /= 10;
                }
            }
            int tot = 0;
            for (int j = 0; j <= 9; j++) {
                final int a = map.getOrDefault(j, 0);
                ans.append("Make %d digit %d".formatted(a, j)).append(System.lineSeparator());
                tot += a;
            }
            ans.append("In total %d digit%s".formatted(tot, tot == 1 ? "" : "s"));
            ans.append(System.lineSeparator());
        }
        this.out.print(ans.toString());
    }

    public void solve() {
        try (final FastScanner sc = new FastScanner(this.in)) {
            handleTestCase(sc);
        }
    }

    public static void main(final String[] args) throws IOException, URISyntaxException {
        final boolean sample = isSample();
        final InputStream is;
        final PrintStream out;
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        long timerStart = 0;
        if (sample) {
            is = Hardware.class.getResourceAsStream("sample.in");
            out = new PrintStream(baos, true);
            timerStart = System.nanoTime();
        } else {
            is = System.in;
            out = System.out;
        }

        new Hardware(sample, is, out).solve();

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
                    = Paths.get(Hardware.class.getResource("sample.out").toURI());
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

        public String nextLine() {
            try {
                return br.readLine();
            } catch (final IOException e) {
                throw new RuntimeException(e);
            }
        }

        public String next() {
            while (!st.hasMoreTokens()) {
                st = new StringTokenizer(nextLine());
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
