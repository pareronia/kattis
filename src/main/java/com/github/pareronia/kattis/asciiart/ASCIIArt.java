package com.github.pareronia.kattis.asciiart;

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
 * ASCII Art
 * @see <a href="https://open.kattis.com/problems/asciiart">https://open.kattis.com/problems/asciiart</a>
 */
public class ASCIIArt {

    private static final List<String> A = List.of(
            "Problem A is about Ascii Art",
            "   _     __   __  _   _",
            "  / \\   / /  / / | | | |",
            " / _ \\  \\ \\ | |  | | | |",
            "/_/ \\_\\ /_/  \\_\\ |_| |_|"
            );
    private static final List<String> B = List.of(
            "Problem B is about Fortnite",
            "###############",
            "###############",
            "####       /###",
            "####   ########",
            "####       ####",
            "####   ########",
            "####   ########",
            "####   ########",
            "####_~<########",
            "###############"
    );
    private static final List<String> C = List.of(
            "Problem C is about The Legend of Zelda",
            "     /\\",
            "    /  \\",
            "   /____\\",
            "  /\\    /\\",
            " /  \\  /  \\",
            "/____\\/____\\"
    );

    private final InputStream in;
    private final PrintStream out;

    public ASCIIArt(
            final Boolean sample, final InputStream in, final PrintStream out) {
        this.in = in;
        this.out = out;
    }

    private void handleTestCase(final int i, final FastScanner sc) {
        final String q = sc.nextLine();
        final String[] sp = q.strip().split("\\s+");
        if (!"What".equals(sp[0]) || !"is".equals(sp[1])
                || !"problem".equals(sp[2]) || !"about?".equals(sp[4])
                || !("A".equals(sp[3]) || "B".equals(sp[3]) || "C".equals(sp[3]))) {
            this.out.println("I am not sure how to answer that.");
        } else if ("A".equals(sp[3])) {
            A.forEach(this.out::println);
        } else if ("B".equals(sp[3])) {
            B.forEach(this.out::println);
        } else {
            C.forEach(this.out::println);
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
            is = ASCIIArt.class.getResourceAsStream("sample.in");
            out = new PrintStream(baos, true);
            timerStart = System.nanoTime();
        } else {
            is = System.in;
            out = System.out;
        }

        new ASCIIArt(sample, is, out).solve();

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
                    = Paths.get(ASCIIArt.class.getResource("sample.out").toURI());
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
