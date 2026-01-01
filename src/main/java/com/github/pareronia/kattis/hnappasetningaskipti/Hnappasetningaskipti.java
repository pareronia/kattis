package com.github.pareronia.kattis.hnappasetningaskipti;

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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringTokenizer;

/**
 * Hnappasetningaskipti
 * @see <a href="https://open.kattis.com/problems/hnappasetningaskipti">https://open.kattis.com/problems/hnappasetningaskipti</a>
 */
public class Hnappasetningaskipti {

    private static final List<String> LAYOUTS = List.of("qwerty", "dvorak", "bjarki");
    private static final String TABLE = """
    		~ ~ 0
    		1 1 2
    		2 2 4
    		3 3 8
    		4 4 6
    		5 5 1
    		6 6 3
    		7 7 5
    		8 8 7
    		9 9 9
    		0 0 =
    		- [ -
    		= ] /
    		q ' b
    		w , j
    		e . a
    		r p r
    		t y k
    		y f i
    		u g g
    		i c u
    		o r s
    		p l t
    		[ / .
    		] = ,
    		a a l
    		s o o
    		d e e
    		f u m
    		g i p
    		h d d
    		j h c
    		k t n
    		l n v
    		; s q
    		' - ;
    		z ; [
    		x q ]
    		c j y
    		v k z
    		b x h
    		n b w
    		m m f
    		, w x
    		. v '
    		/ z ~
    		""";

    private final InputStream in;
    private final PrintStream out;
    @SuppressWarnings("rawtypes")
	private final List[] table = {new ArrayList<Character>(), new ArrayList<Character>(), new ArrayList<Character>()};
    
    @SuppressWarnings("unchecked")
	public Hnappasetningaskipti(
            final Boolean sample, final InputStream in, final PrintStream out) {
        this.in = in;
        this.out = out;
        Arrays.stream(TABLE.split("\\r?\\n")).forEach(sp -> {
        	final String[] chars = sp.split(" ");
        	for (int i = 0; i < 3; i++) {
				table[i].add(Character.valueOf(chars[i].charAt(0)));
			}
        });
    }
    
    private void handleTestCase(final Integer i, final FastScanner sc) {
    	final String s1 = sc.nextLine();
    	final String[] sp = s1.split(" on ");
    	final int from = LAYOUTS.indexOf(sp[0]);
    	final int to = LAYOUTS.indexOf(sp[1]);
    	final String s2 = sc.nextLine();
    	final StringBuilder ans = new StringBuilder();
    	for (int j = 0; j < s2.length(); j++) {
			final char ch = s2.charAt(j);
			if (ch == ' ') {
				ans.append(' ');
			} else {
				final int idx = this.table[from].indexOf(ch);
				ans.append(this.table[to].get(idx));
			}
		}
        this.out.println(ans.toString());
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
            is = Hnappasetningaskipti.class.getResourceAsStream("sample.in");
            out = new PrintStream(baos, true);
            timerStart = System.nanoTime();
        } else {
            is = System.in;
            out = System.out;
        }
        
        new Hnappasetningaskipti(sample, is, out).solve();
        
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
                    = Paths.get(Hnappasetningaskipti.class.getResource("sample.out").toURI());
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
