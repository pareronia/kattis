package com.github.pareronia.kattis.knightwalk;

import static java.util.Arrays.asList;
import static java.util.Comparator.comparing;
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
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

/**
 * Knight Walk
 * @see <a href="https://open.kattis.com/problems/knightwalk">https://open.kattis.com/problems/knightwalk</a>
 */
public class KnightWalk {

	private static final int[][] KNIGHT_MOVES = {
			{ 1, 2 }, { 1, -2 }, { -1, 2 }, { -1, -2}, { 2, 1 }, { 2, -1 }, { -2, 1 }, { -2, -1 }
	};

    private final InputStream in;
    private final PrintStream out;

    public KnightWalk(
            final Boolean sample, final InputStream in, final PrintStream out) {
        this.in = in;
        this.out = out;
    }

    private void handleTestCase(final int i, final FastScanner sc) {
        bfs(Cell.fromString(sc.next()), Cell.fromString(sc.next())).stream()
                .sorted(comparing(path -> path.stream().map(Cell::toString).collect(joining())))
                .map(path -> path.stream().map(Cell::toString).collect(joining(" -> ")))
                .forEach(this.out::println);
    }

    private List<List<Cell>> bfs(final Cell start, final Cell end) {
        final List<List<Cell>> ans = new ArrayList<>();
        final Deque<List<Cell>> q = new ArrayDeque<>();
        q.add(List.of(start));
        final Set<List<Cell>> seen = new HashSet<>();
        seen.add(List.of(start));
        int best = Integer.MAX_VALUE;
        while (!q.isEmpty()) {
            final List<Cell> path = q.pollFirst();
            final Cell cell = path.getLast();
            if (cell.equals(end)) {
                if (path.size() < best) {
                    best = path.size();
                    ans.clear();
                    ans.add(path);
                } else if (path.size() == best) {
                    ans.add(path);
                }
                continue;
            }
            if (path.size() >= best) {
                continue;
            }
            for (final int[] d : KNIGHT_MOVES) {
                final int rr = cell.row + d[0];
                final int cc = cell.col + d[1];
                if (rr < 0 || rr > 7 || cc < 0 || cc > 7) {
                    continue;
                }
                final List<Cell> newPath = new ArrayList<>(path);
                newPath.add(new Cell(rr, cc));
                if (!seen.contains(newPath)) {
                    q.addLast(newPath);
                    seen.add(newPath);
                }
            }
        }
        return ans;
    }

    private record Cell(int row, int col) {

        public static Cell fromString(final String string) {
            final int col = string.charAt(0) - 'a';
            final int row = '8' - string.charAt(1);
            return new Cell(row, col);
        }

        @Override
        public String toString() {
            return String.valueOf((char) ('a' + this.col)) + String.valueOf((char) ('8' - this.row));
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
            is = KnightWalk.class.getResourceAsStream("sample.in");
            out = new PrintStream(baos, true);
            timerStart = System.nanoTime();
        } else {
            is = System.in;
            out = System.out;
        }

        new KnightWalk(sample, is, out).solve();

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
                    = Paths.get(KnightWalk.class.getResource("sample.out").toURI());
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
