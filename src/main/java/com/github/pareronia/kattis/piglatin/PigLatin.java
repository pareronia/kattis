package com.github.pareronia.kattis.piglatin;

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
import java.util.Set;

/**
 * Pig Latin
 * @see <a href="https://open.kattis.com/problems/piglatin">https://open.kattis.com/problems/piglatin</a>
 */
public class PigLatin {

    private final InputStream in;
    private final PrintStream out;
    
    public PigLatin(
            final Boolean sample, final InputStream in, final PrintStream out) {
        this.in = in;
        this.out = out;
    }
    
    private void handleTestCase(final FastScanner sc) {
    	while (true) {
    		final String s = sc.nextLine();
    		if (s == null) {
    			break;
    		}
    		final StringBuilder ans = new StringBuilder();
    		for (final String w : s.split(" ")) {
    			if (Set.of('a', 'e', 'i', 'o', 'u', 'y').contains(w.charAt(0))) {
    				ans.append(w + "yay").append(" ");
    			} else {
    				final String[] sp = w.splitWithDelimiters("(a|e|i|o|u|y)", 2);
    				ans.append(sp[1]).append(sp[2]).append(sp[0]).append("ay").append(" ");
    			}
    		}
    		ans.deleteCharAt(ans.length() - 1);
    		this.out.println(ans.toString());
    	}
    }
    
    public void solve() {
        try (final FastScanner sc = new FastScanner(this.in)) {
        	handleTestCase(sc);
        }
    }

    public static void main(final String[] args) throws IOException, URISyntaxException {
        final boolean sample = isSample();
        final InputStream is;
        final PrintStream out;
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        long timerStart = 0;
        if (sample) {
            is = PigLatin.class.getResourceAsStream("sample.in");
            out = new PrintStream(baos, true);
            timerStart = System.nanoTime();
        } else {
            is = System.in;
            out = System.out;
        }
        
        new PigLatin(sample, is, out).solve();
        
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
                    = Paths.get(PigLatin.class.getResource("sample.out").toURI());
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
        
        public FastScanner(final InputStream in) {
            this.br = new BufferedReader(new InputStreamReader(in));
        }
        
        public String nextLine() {
			try {
				 return br.readLine();
			} catch (final IOException e) {
				throw new RuntimeException(e);
			}
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
