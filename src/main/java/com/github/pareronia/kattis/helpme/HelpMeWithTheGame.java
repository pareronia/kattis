package com.github.pareronia.kattis.helpme;

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
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

/**
 * Help Me With The Game
 * @see <a href="https://open.kattis.com/problems/helpme">https://open.kattis.com/problems/helpme</a>
 */
public class HelpMeWithTheGame {

    private final InputStream in;
    private final PrintStream out;

    public HelpMeWithTheGame(
            final Boolean sample, final InputStream in, final PrintStream out) {
        this.in = in;
        this.out = out;
    }

    private void handleTestCase(final int i, final FastScanner sc) {
        final int h = 17;
        final int w = 33;
        final char[][] a = new char[h][w];
        for (int j = 0; j < h; j++) {
            a[j] = sc.next().toCharArray();
        }
        final Map<Character, Set<Position>> pos = new HashMap<>();
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                final char ch = a[2 * r + 1][4 * c + 2];
                if (ch == '.' || ch == ':') {
                    continue;
                }
                if (Character.isUpperCase(ch)) {
                    pos.computeIfAbsent(ch, x -> new HashSet<>()).add(new Position(8 - r, c, true));
                } else {
                    pos.computeIfAbsent(Character.toUpperCase(ch), x -> new HashSet<>()).add(new Position(8 - r, c, false));
                }
            }
        }
        this.out.println("White: " + notations(pos, true).stream().collect(joining(",")));
        this.out.println("Black: " + notations(pos, false).stream().collect(joining(",")));
    }

    private List<String> notations(final Map<Character, Set<Position>> positions, final boolean white) {
        final List<String> notations = new ArrayList<>();
        for (final Character ch : List.of('K', 'Q', 'R', 'B', 'N', 'P')) {
            positions.getOrDefault(ch, Set.of()).stream()
                    .filter(p -> p.white == white)
                    .sorted(white ? Position.WHITE : Position.BLACK)
                    .map(p -> (ch == 'P' ? "" : String.valueOf(Character.toUpperCase(ch))) + p)
                    .forEach(notations::add);
        }
        return notations;
    }

    private record Position(int row, int col, boolean white) {
        private static final Comparator<Position> WHITE
                = Comparator.comparing(Position::row).thenComparing(Position::col);
        private static final Comparator<Position> BLACK
                = Comparator.comparing(Position::row).reversed().thenComparing(Position::col);

        @Override
        public String toString() {
            return String.valueOf((char) ('a' + this.col)) + this.row;
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
            is = HelpMeWithTheGame.class.getResourceAsStream("sample.in");
            out = new PrintStream(baos, true);
            timerStart = System.nanoTime();
        } else {
            is = System.in;
            out = System.out;
        }

        new HelpMeWithTheGame(sample, is, out).solve();

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
                    = Paths.get(HelpMeWithTheGame.class.getResource("sample.out").toURI());
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
