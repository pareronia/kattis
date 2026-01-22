package com.github.pareronia.kattis.chess;

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
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

/**
 * Chess
 * @see <a href="https://open.kattis.com/problems/chess">https://open.kattis.com/problems/chess</a>
 */
public class Chess {

	private static final int[][] DIRS = { { 1, 1 }, { -1, 1 }, { 1, -1 }, { -1, -1 } };

    private final InputStream in;
    private final PrintStream out;

    public Chess(
            final Boolean sample, final InputStream in, final PrintStream out) {
        this.in = in;
        this.out = out;
    }

    private void handleTestCase(final int i, final FastScanner sc) {
        final int c1 = sc.next().charAt(0) - 'A';
        final int r1 = 8 - sc.nextInt();
        final int c2 = sc.next().charAt(0) - 'A';
        final int r2 = 8 - sc.nextInt();
        if (((r1 + c1) & 1) != ((r2 + c2) & 1)) {
            this.out.println("Impossible");
        } else if (r1 == r2 && c1 == c2) {
            this.out.println("%d %s".formatted(0, new Cell(r1, c1)));
        } else {
            final Cell start = new Cell(r1, c1);
            final Cell end = new Cell(r2, c2);
            final Deque<List<Cell>> q = new ArrayDeque<>();
            q.add(List.of(start));
            final Set<List<Cell>> seen = new HashSet<>();
            seen.add(List.of(start));
            while (!q.isEmpty()) {
                final List<Cell> curr = q.pollFirst();
                if (curr.getLast().equals(end)) {
                    this.out.print(curr.size() - 1);
                    this.out.print(" ");
                    this.out.println(curr.stream().map(Cell::toString).collect(joining(" ")));
                    break;
                }
                final Cell cell = curr.getLast();
                for (final int[] d : DIRS) {
                    for (int cnt = 1; ; cnt++) {
                        final int rr = cell.row + cnt * d[0];
                        final int cc = cell.col + cnt * d[1];
                        if (rr < 0 || rr > 7 || cc < 0 || cc > 7) {
                            break;
                        }
                        final List<Cell> newPath = new ArrayList<>(curr);
                        newPath.add(new Cell(rr, cc));
                        if (!seen.contains(newPath)) {
                            q.addLast(newPath);
                        }
                    }
                }
            }
        }
    }

    private record Cell(int row, int col) {

        @Override
        public String toString() {
            return "%s %d".formatted((char) ('A' + col), 8 - row);
        }
    }

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
            is = Chess.class.getResourceAsStream("sample.in");
            out = new PrintStream(baos, true);
            timerStart = System.nanoTime();
        } else {
            is = System.in;
            out = System.out;
        }

        new Chess(sample, is, out).solve();

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
                    = Paths.get(Chess.class.getResource("sample.out").toURI());
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
