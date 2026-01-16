package com.github.pareronia.kattis._3puzzle;

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
 * 3-Puzzle
 * @see <a href="https://open.kattis.com/problems/3puzzle">https://open.kattis.com/problems/3puzzle</a>
 */
public class ThreePuzzle {

	private static final int[][] DIRS = { { 1, 0 }, { -1, 0 }, { 0, 1 }, { 0, -1 } };

    private final InputStream in;
    private final PrintStream out;

    public ThreePuzzle(
            final Boolean sample, final InputStream in, final PrintStream out) {
        this.in = in;
        this.out = out;
    }

    private void handleTestCase(final int i, final FastScanner sc) {
        final char[][] p = new char[2][2];
        p[0] = sc.next().toCharArray();
        p[1] = sc.next().toCharArray();
        this.out.println(bfs(p));
    }

    private int bfs(final char[][] start) {
        final Deque<State> q = new ArrayDeque<>();
        q.addLast(new State(start, 0));
        final Set<Integer> seen = new HashSet<>();
        seen.add(Arrays.deepHashCode(start));
        final int end = Arrays.deepHashCode(new char[][]{{'1', '2'}, {'3', '-'}});
        while (!q.isEmpty()) {
            final State curr = q.pollFirst();
            final char[][] p = curr.puzzle;
            if (Arrays.deepHashCode(p) == end) {
                return curr.steps;
            }
            final Cell blank = curr.getBlank();
            for (final int[] d : DIRS) {
                final int rr = blank.row + d[0];
                final int cc = blank.col + d[1];
                if (rr < 0 || rr > 1 || cc < 0 || cc > 1) {
                    continue;
                }
                final char[][] newp = new char[2][2];
                for (int r = 0; r < 2; r++) {
                    for (int c = 0; c < 2; c++) {
                        if (r == blank.row && c == blank.col) {
                            newp[r][c] = p[rr][cc];
                        } else if (r == rr && c == cc) {
                            newp[r][c] = '-';
                        } else {
                            newp[r][c] = p[r][c];
                        }
                    }
                }
                final int newh = Arrays.deepHashCode(newp);
                if (!seen.contains(newh)) {
                    q.addLast(new State(newp, curr.steps + 1));
                    seen.add(newh);
                }
            }
        }
        throw new IllegalStateException("Unsolvable");
    }

    private record State(char[][] puzzle, int steps) {

        public Cell getBlank() {
            for (int r = 0; r < 2; r++) {
                for (int c = 0; c < 2; c++) {
                    if (this.puzzle[r][c] == '-') {
                        return new Cell(r, c);
                    }
                }
            }
            throw new IllegalStateException("Unsolvable");
        }
    }

    private record Cell(int row, int col) {}

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
            is = ThreePuzzle.class.getResourceAsStream("sample.in");
            out = new PrintStream(baos, true);
            timerStart = System.nanoTime();
        } else {
            is = System.in;
            out = System.out;
        }

        new ThreePuzzle(sample, is, out).solve();

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
                    = Paths.get(ThreePuzzle.class.getResource("sample.out").toURI());
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
