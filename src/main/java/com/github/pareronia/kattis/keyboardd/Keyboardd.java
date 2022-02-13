package com.github.pareronia.kattis.keyboardd;

import static java.util.Arrays.asList;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

/**
 * Keyboardd
 * @see <a href="https://open.kattis.com/problems/keyboardd">https://open.kattis.com/problems/keyboardd</a>
 */
public class Keyboardd {

    private final boolean sample;
    private final InputStream in;
    private final PrintStream out;
    
    public Keyboardd(
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
    
    private void handleTestCase(final Integer i, final BufferedReader br) throws IOException {
        final String s = br.readLine();
        String t = br.readLine();
        for (final char ch : s.toCharArray()) {
            t = t.replaceFirst(String.valueOf(ch), "");
        }
        final Set<Character> ans = new HashSet<>();
        for (final char ch : t.toCharArray()) {
            ans.add(ch);
        }
        for (final char ch : ans) {
            this.out.print(ch);
        }
        this.out.println();
    }
    
    public void solve() throws IOException {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(in))) {
            final int numberOfTestCases;
            if (this.sample) {
                numberOfTestCases = Integer.valueOf(br.readLine());
            } else {
                numberOfTestCases = 1;
            }
            for (int i = 0; i < numberOfTestCases; i++) {
                handleTestCase(i, br);
            }
        }
    }

    public static void main(final String[] args) throws Exception {
        final boolean sample = isSample();
        final InputStream is;
        final PrintStream out;
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        long timerStart = 0;
        if (sample) {
            is = Keyboardd.class.getResourceAsStream("sample.in");
            out = new PrintStream(baos, true);
            timerStart = System.nanoTime();
        } else {
            is = System.in;
            out = System.out;
        }
        
        new Keyboardd(sample, is, out).solve();
        
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
                    = Paths.get(Keyboardd.class.getResource("sample.out").toURI());
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
}
