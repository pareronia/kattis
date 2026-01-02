package com.github.pareronia.kattis.bluetooth;

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
 * Bluetooth
 * @see <a href="https://open.kattis.com/problems/bluetooth">https://open.kattis.com/problems/bluetooth</a>
 */
public class Bluetooth {

    private final InputStream in;
    private final PrintStream out;
    
    public Bluetooth(
            final Boolean sample, final InputStream in, final PrintStream out) {
        this.in = in;
        this.out = out;
    }
    
    private void handleTestCase(final Integer i, final FastScanner sc) {
    	final int n = sc.nextInt();
    	int upperLeft = 8;
    	int lowerLeft = 8;
    	int upperRight = 8;
    	int lowerRight = 8;
    	for (int j = 0; j < n; j++) {
    		final String t = sc.next();
    		final String c = sc.next();
    		final char t1 = t.charAt(0);
    		final char t2 = t.charAt(1);
    		switch (t1) {
				case '+' -> {
					if ("m".equals(c)) {
						upperLeft--;
					} else {
						upperLeft = 0;
						lowerLeft = 0;
					}
				}
				case '-' -> {
					if ("m".equals(c)) {
						lowerLeft--;
					} else {
						upperLeft = 0;
						lowerLeft = 0;
					}
				}
				default -> {
					switch (t2) {
						case '+' -> {
							if ("m".equals(c)) {
								upperRight--;
							} else {
								upperRight = 0;
								lowerRight = 0;
							}
						}
						default -> {
							if ("m".equals(c)) {
								lowerRight--;
							} else {
								upperRight = 0;
								lowerRight = 0;
							}
						}
					}
				}
    		}
		}
        this.out.println(upperLeft >= 1 && lowerLeft >= 1 ? 0 : upperRight >= 1 && lowerRight >= 1 ? 1 : 2);
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
            is = Bluetooth.class.getResourceAsStream("sample.in");
            out = new PrintStream(baos, true);
            timerStart = System.nanoTime();
        } else {
            is = System.in;
            out = System.out;
        }
        
        new Bluetooth(sample, is, out).solve();
        
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
                    = Paths.get(Bluetooth.class.getResource("sample.out").toURI());
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
