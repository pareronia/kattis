package com.github.pareronia.kattis.dragonsinadungeon;

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
import java.util.Deque;
import java.util.List;
import java.util.StringTokenizer;

/**
 * Dragons in a Dungeon
 * @see <a href="https://open.kattis.com/problems/dragonsinadungeon">https://open.kattis.com/problems/dragonsinadungeon</a>
 */
public class DragonsInADungeon {

	private static final int[][] DIRS = {
			{ 1, 0 }, { -1, 0 }, { 0, 1 }, { 0, -1 }, { 1, 1 }, { -1, 1 }, { 1, -1 }, { -1, -1 }
	};

    private final InputStream in;
    private final PrintStream out;

    public DragonsInADungeon(
            final Boolean sample, final InputStream in, final PrintStream out) {
        this.in = in;
        this.out = out;
    }

    private void handleTestCase(final int i, final FastScanner sc) {
        final int r = sc.nextInt();
        final int c = sc.nextInt();
        int grog = 0;
        int exit = 0;
        final char[][] a = new char[r][c];
        for (int j = 0; j < r; j++) {
            final String s = sc.next();
            int col = s.indexOf('G');
            if (col != -1) {
                grog = j * 100 + col;
            }
            col = s.indexOf('E');
            if (col != -1) {
                exit = j * 100 + col;
            }
            a[j] = s.toCharArray();
        }
        final Deque<Integer> q = new ArrayDeque<>();
        q.add(grog);
        while (!q.isEmpty()) {
            final int cell = q.pollFirst();
            if (cell == exit) {
                this.out.println("YES");
                return;
            }
            final int row = cell / 100;
            final int col = cell % 100;
            a[row][col] = 'X';
            for (final int[] d : DIRS) {
                final int rr = row + d[0];
                final int cc = col + d[1];
                if (rr < 0 || rr >= r || cc < 0 || cc >= c || (a[rr][cc] != '.' && a[rr][cc] != 'E')) {
                    continue;
                }
                q.addLast(rr * 100 + cc);
            }
        }
        this.out.println("NO");
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
            is = DragonsInADungeon.class.getResourceAsStream("sample.in");
            out = new PrintStream(baos, true);
            timerStart = System.nanoTime();
        } else {
            is = System.in;
            out = System.out;
        }

        new DragonsInADungeon(sample, is, out).solve();

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
                    = Paths.get(DragonsInADungeon.class.getResource("sample.out").toURI());
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
