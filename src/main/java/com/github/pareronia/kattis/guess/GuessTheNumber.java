package com.github.pareronia.kattis.guess;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.URISyntaxException;
import java.util.Scanner;
import java.util.function.Supplier;

/**
 * Guess the Number
 * @see <a href="https://open.kattis.com/problems/guess">https://open.kattis.com/problems/guess</a>
 */
public class GuessTheNumber {

    private final boolean sample;
    private final InputStream in;
    private final PrintStream out;
    
    public GuessTheNumber(
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
    
    private String guess(final int n, final Scanner sc) {
        this.out.println(n);
        this.out.flush();
        return sc.next();
    }
    
    public void solve() {
        try (final Scanner sc = new Scanner(this.in)) {
            int upper = 1_001;
            int lower = 1;
            while (true) {
                final int n = lower + (upper - lower) / 2;
                final String response = guess(n, sc);
                if ("correct".equals(response)) {
                    break;
                } else if ("lower".equals(response)) {
                    upper = n;
                } else {
                    lower = n;
                }
            }
        }
    }

    public static void main(final String[] args) throws IOException, URISyntaxException {
        final boolean sample = isSample();
        final InputStream is = System.in;
        final PrintStream out = System.out;
        
        new GuessTheNumber(sample, is, out).solve();
        
        out.flush();
    }
    
    private static boolean isSample() {
        try {
            return "sample".equals(System.getProperty("kattis"));
        } catch (final SecurityException e) {
            return false;
        }
    }
}
