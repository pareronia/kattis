package com.github.pareronia.kattis.chartingprogress;

import static java.util.Arrays.asList;

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
import java.util.Collections;
import java.util.List;

/**
 * Charting Progress
 * @see <a href="https://open.kattis.com/problems/chartingprogress">https://open.kattis.com/problems/chartingprogress</a>
 */
public class ChartingProgress {

    private final InputStream in;
    private final PrintStream out;
    
    public ChartingProgress(
            final Boolean sample, final InputStream in, final PrintStream out) {
        this.in = in;
        this.out = out;
    }
    
    private void handleTestCase(final List<String> input) {
        final int[] l = new int[input.size()];
        for (int j = 0; j < input.size(); j++) {
            l[j] = countStars(input.get(j));
        }
        final int len = input.get(0).length();
        int sum = 0;
        for (int j = 0; j < l.length; j++) {
            final int stars = l[j];
            final char[] s = new char[len];
            for (int k = 0; k < sum; k++) {
                s[k] = '.';
            }
            for (int k = sum; k < sum + stars; k++) {
                s[k] = '*';
            }
            for (int k = sum + stars; k < len; k++) {
                s[k] = '.';
            }
            sum += stars;
            for (int k = len - 1; k >= 0; k--) {
                this.out.print(s[k]);
            }
            this.out.println();
        }
        this.out.println();
    }

    private int countStars(final String s) {
        int ans = 0;
        for (int j = 0; j < s.length(); j++) {
            if (s.charAt(j) == '*') {
                ans++;
            }
        }
        return ans;
    }
    
    public void solve() throws IOException {
        try (final BufferedReader sc = new BufferedReader(new InputStreamReader(this.in))) {
            final List<String> inputs = new ArrayList<>();
            while (true) {
                final String line = sc.readLine();
                if (line == null) {
                    break;
                }
                inputs.add(line);
            }
            toBlocks(inputs).stream()
                    .forEach(block -> handleTestCase(block));
        }
    }
    
    protected List<List<String>> toBlocks(final List<String> inputs) {
        if (inputs.isEmpty()) {
            return Collections.emptyList();
        }
        final List<List<String>> blocks = new ArrayList<>();
        int i = 0;
        final int last = inputs.size() - 1;
        blocks.add(new ArrayList<String>());
        for (int j = 0; j <= last; j++) {
            if (inputs.get(j).isEmpty()) {
                if (j != last) {
                    blocks.add(new ArrayList<String>());
                    i++;
                }
            } else {
                blocks.get(i).add(inputs.get(j));
            }
        }
        return blocks;
    }

    public static void main(final String[] args) throws IOException, URISyntaxException {
        final boolean sample = isSample();
        final InputStream is;
        final PrintStream out;
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        long timerStart = 0;
        if (sample) {
            is = ChartingProgress.class.getResourceAsStream("sample.in");
            out = new PrintStream(baos, true);
            timerStart = System.nanoTime();
        } else {
            is = System.in;
            out = System.out;
        }
        
        new ChartingProgress(sample, is, out).solve();
        
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
                    = Paths.get(ChartingProgress.class.getResource("sample.out").toURI());
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
