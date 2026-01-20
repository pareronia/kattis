package com.github.pareronia.kattis.nineknights;

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
import java.util.List;
import java.util.StringTokenizer;

/**
 * Nine Knights
 * @see <a href="https://open.kattis.com/problems/nineknights">https://open.kattis.com/problems/nineknights</a>
 */
public class NineKnights {

	private static final int[][] KNIGHT_MOVES = {
			{ 1, 2 }, { 1, -2 }, { -1, 2 }, { -1, -2}, { 2, 1 }, { 2, -1 }, { -2, 1 }, { -2, -1 }
	};

    private final InputStream in;
    private final PrintStream out;

    public NineKnights(
            final Boolean sample, final InputStream in, final PrintStream out) {
        this.in = in;
        this.out = out;
    }

    private void handleTestCase(final int i, final FastScanner sc) {
        final char[][] b = new char[5][5];
        for (int j = 0; j < 5; j++) {
            b[j] = sc.next().toCharArray();
        }
        boolean ans = true;
        int cnt = 0;
        for (int r = 0; r < 5; r++) {
            for (int c = 0; c < 5; c++) {
               if (b[r][c] == '.') {
                   continue;
               }
               cnt++;
               if (!ans) {
                   continue;
               }
               for (final int[] d : KNIGHT_MOVES) {
                   final int rr = r + d[0];
                   final int cc = c + d[1];
                   if (rr < 0 || rr >= 5 || cc < 0 || cc >= 5) {
                       continue;
                   }
                   if (b[rr][cc] == 'k') {
                       ans = false;
                       break;
                   }
               }
            }
        }
        this.out.println(ans && cnt == 9 ? "valid" : "invalid");
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
            is = NineKnights.class.getResourceAsStream("sample.in");
            out = new PrintStream(baos, true);
            timerStart = System.nanoTime();
        } else {
            is = System.in;
            out = System.out;
        }

        new NineKnights(sample, is, out).solve();

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
                    = Paths.get(NineKnights.class.getResource("sample.out").toURI());
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
