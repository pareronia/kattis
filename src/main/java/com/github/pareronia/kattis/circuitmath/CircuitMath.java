package com.github.pareronia.kattis.circuitmath;

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
 * Circuit Math
 * @see <a href="https://open.kattis.com/problems/circuitmath">https://open.kattis.com/problems/circuitmath</a>
 */
public class CircuitMath {

    private final InputStream in;
    private final PrintStream out;
    
    public CircuitMath(
            final Boolean sample, final InputStream in, final PrintStream out) {
        this.in = in;
        this.out = out;
    }
    
    private void handleTestCase(final int i, final FastScanner sc) {
    	final int n = sc.nextInt();
    	final boolean[] a = new boolean[n];
    	for (int j = 0; j < n; j++) {
    		a[j] = "T".equals(sc.next());
		}
    	final Deque<Term> stack = new ArrayDeque<>();
    	for (final String s : sc.nextLine().split(" ")) {
    		final char ch = s.charAt(0);
    		switch (ch) {
    			case '*' -> stack.addFirst(new Operation(OpCode.AND, stack.pollFirst(), stack.pollFirst()));
    			case '+' -> stack.addFirst(new Operation(OpCode.OR, stack.pollFirst(), stack.pollFirst()));
    			case '-' -> stack.addFirst(new Operation(OpCode.NOT, stack.pollFirst()));
    			default -> stack.addFirst(new Set(a[ch - 'A']));
    		}
    	}
    	assert stack.size() == 1;
        this.out.println(stack.peekFirst().eval() ? "T" : "F");
    }

    enum OpCode {
    	AND, OR, NOT
    }

    private interface Term {
    	boolean eval();
    }

    private record Set(boolean value) implements Term {

		@Override
		public boolean eval() {
			return this.value;
		}
    }

    private record Operation(OpCode op, Term... operands) implements Term {

		@Override
		public boolean eval() {
			return switch(this.op) {
				case AND -> operands[0].eval() && operands[1].eval();
				case OR -> operands[0].eval() || operands[1].eval();
				case NOT -> !operands[0].eval();
			};
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
            is = CircuitMath.class.getResourceAsStream("sample.in");
            out = new PrintStream(baos, true);
            timerStart = System.nanoTime();
        } else {
            is = System.in;
            out = System.out;
        }
        
        new CircuitMath(sample, is, out).solve();
        
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
                    = Paths.get(CircuitMath.class.getResource("sample.out").toURI());
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
