package com.github.pareronia.kattis.stringmatching;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * String Matching
 * @see <a href="https://open.kattis.com/problems/stringmatching">https://open.kattis.com/problems/stringmatching</a>
 */
public class StringMatching {

    private final boolean sample;
    private final InputStream in;
    private final PrintStream out;
    
    public StringMatching(
            final Boolean sample, final InputStream in, final PrintStream out) {
        this.sample = sample;
        this.in = in;
        this.out = out;
    }
    
    @SuppressWarnings("unused")
    private void log(final Supplier<Object> supplier) {
        if (!sample) {
            return;
        }
        System.out.println(supplier.get());
    }
    
    private static final class Output {
        private final PrintStream out;
        private int cnt = 0;
        
        private Output(final PrintStream out) {
            this.out = out;
        }

        public void print(final int a) {
            if (cnt > 0) {
                this.out.print(" ");
            }
            this.out.print(a);
            cnt++;
        }
        
        public void println() {
            this.out.println();
            cnt = 0;
        }
    }
    
    @SuppressWarnings("unused")
    private void handleTestCase(final Integer i, final Pair<String, String> ss) {
        final List<Integer> ans = KnuthMorrisPratt.found(ss.getOne(), ss.getTwo());
        final Output output = new Output(this.out);
        ans.forEach(a -> {
            output.print(a);
        });
        output.println();
    }
    
    private void handleTestCase2(final Integer i, final Pair<String, String> ss) {
        final Pattern pattern = Pattern.compile(Pattern.quote(ss.getOne()));
        final Matcher m = pattern.matcher(ss.getTwo());
        final Output output = new Output(this.out);
        while (m.find()) {
            output.print(m.start());
        }
        output.println();
    }
    
    public void solve() throws IOException {
        try (final BufferedReader sc = new BufferedReader(new InputStreamReader(this.in))) {
            int i = 1;
            while (true) {
                final String s = sc.readLine();
                if (s == null) {
                    break;
                }
                handleTestCase2(i++, Pair.of(s, sc.readLine()));
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
            is = StringMatching.class.getResourceAsStream("sample.in");
            out = new PrintStream(baos, true);
            timerStart = System.nanoTime();
        } else {
            is = System.in;
            out = System.out;
        }
        
        new StringMatching(sample, is, out).solve();
        
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
                    = Paths.get(StringMatching.class.getResource("sample.out").toURI());
            final List<String> expected = Files.readAllLines(path);
            final List<String> actual = asList(baos.toString()
                    .split("\\r?\\n")).stream()
                    .map(String::stripTrailing).collect(toList());
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
    
    private static final class Pair<L, R> {
        private final L one;
        private final R two;

        private Pair(final L one, final R two) {
            this.one = one;
            this.two = two;
        }

        public static <L, R> Pair<L, R> of(final L one, final R two) {
            return new Pair<>(one, two);
        }

        public L getOne() {
            return one;
        }
        
        public R getTwo() {
            return two;
        }
    }
    
    public static final class KnuthMorrisPratt {
        
        public static List<Integer> found(final String w, final String t) {
            final List<Integer> idxs = new ArrayList<>();
            int i = 0;
            int j = 0;
            final int[] lps = LPS.get(w);
            while (i < t.length()) {
                while (j >= 0 && t.charAt(i) != w.charAt(j)) {
                    j = lps[j];
                }
                i++;
                j++;
                if (j == w.length()) {
                    idxs.add(i - j);
                    j = lps[j - 1];
                }
            }
            return idxs;
        }
        
        public static final class LPS {
            public static int[] get(final String w) {
                final int[] lps = new int[w.length() + 1];
                int i = 0;
                int j = -1;
                lps[0] = -1;
                
                while (i < w.length()) {
                    while (j >= 0 && w.charAt(i) != w.charAt(j)) {
                        j = lps[j];
                    }
                    i++;
                    j++;
                    lps[i] = j;
                }
                return lps;
            }
        }
    }
}
