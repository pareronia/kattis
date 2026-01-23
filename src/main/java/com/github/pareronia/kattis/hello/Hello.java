package com.github.pareronia.kattis.hello;

import static java.util.Arrays.asList;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.URISyntaxException;
import java.util.List;

/**
 * Hello World!
 * @see <a href="https://open.kattis.com/problems/hello">https://open.kattis.com/problems/hello</a>
 */
public class Hello {

    private final PrintStream out;
    
    public Hello(final PrintStream out) {
        this.out = out;
    }
    
    public void solve() {
        this.out.println("Hello World!");
    }

    public static void main(final String[] args) throws IOException, URISyntaxException {
        final boolean sample = isSample();
        final PrintStream out;
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        long timerStart = 0;
        if (sample) {
            out = new PrintStream(baos, true);
            timerStart = System.nanoTime();
        } else {
            out = System.out;
        }
        
        new Hello(out).solve();
        
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
            final List<String> actual = asList(baos.toString().split("\\r?\\n"));
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
