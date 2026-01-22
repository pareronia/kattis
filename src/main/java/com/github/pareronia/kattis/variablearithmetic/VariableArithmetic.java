package com.github.pareronia.kattis.variablearithmetic;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.joining;

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
 * Variable Arithmetic
 * @see <a href="https://open.kattis.com/problems/variablearithmetic">https://open.kattis.com/problems/variablearithmetic</a>
 */
public class VariableArithmetic {

    private final InputStream in;
    private final PrintStream out;

    public VariableArithmetic(
            final Boolean sample, final InputStream in, final PrintStream out) {
        this.in = in;
        this.out = out;
    }

    private void handleTestCase(final int i, final FastScanner sc) {
        final StringBuilder ans = new StringBuilder();
        final Map<String, Integer> map = new HashMap<>();
        String line;
        while (!"0".equals(line = sc.nextLine())) {
            if (line.contains("=")) {
                final String[] sp = line.split(" = ");
                map.put(sp[0], Integer.parseInt(sp[1]));
            } else {
                int tot = 0;
                final List<String> ss = new ArrayList<>();
                final String[] sp = line.split(" \\+ ");
                for (final String s : sp) {
                    if (Character.isDigit(s.charAt(0))) {
                        tot += Integer.parseInt(s);
                    } else if (map.containsKey(s)) {
                        tot += map.get(s);
                    } else {
                        ss.add(s);
                    }
                }
                if (ss.isEmpty()) {
                    ans.append(tot);
                } else {
                    if (tot > 0) {
                        ss.add(0, String.valueOf(tot));
                    }
                    ans.append(ss.stream().collect(joining(" + ")));
                }
                ans.append(System.lineSeparator());
            }
        }
        this.out.print(ans);
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
            is = VariableArithmetic.class.getResourceAsStream("sample.in");
            out = new PrintStream(baos, true);
            timerStart = System.nanoTime();
        } else {
            is = System.in;
            out = System.out;
        }

        new VariableArithmetic(sample, is, out).solve();

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
                    = Paths.get(VariableArithmetic.class.getResource("sample.out").toURI());
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
