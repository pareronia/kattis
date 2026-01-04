package com.github.pareronia.kattis.conversationlog;

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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.StringTokenizer;

/**
 * Conversation Log
 * @see <a href="https://open.kattis.com/problems/conversationlog">https://open.kattis.com/problems/conversationlog</a>
 */
public class ConversationLog {

    private final InputStream in;
    private final PrintStream out;
    
    public ConversationLog(
            final Boolean sample, final InputStream in, final PrintStream out) {
        this.in = in;
        this.out = out;
    }
    
    private void handleTestCase(final Integer i, final FastScanner sc) {
    	final int m = sc.nextInt();
    	final Map<String, List<String>> counter = new HashMap<>();
    	final Set<String> users = new HashSet<>();
    	for (int j = 0; j < m; j++) {
    		final String msg = sc.nextLine();
    		final String[] split = msg.split(" ");
    		users.add(split[0]);
			Arrays.stream(split).skip(1).forEach(
					sp -> counter.computeIfAbsent(sp, x -> new ArrayList<>()).add(split[0]));
		}
    	final List<String> ans = counter.entrySet().stream()
    		.filter(e -> Set.copyOf(e.getValue()).equals(users))
    		.sorted((e1, e2) -> {
    			final int cmp = -Integer.compare(e1.getValue().size(), e2.getValue().size());
    			if (cmp == 0) {
    				return e1.getKey().compareTo(e2.getKey());
    			}
    			return cmp;
    		})
    		.map(Entry::getKey)
    		.toList();
    	if (ans.isEmpty()) {
    		this.out.println("ALL CLEAR");
    	} else {
    		ans.forEach(this.out::println);
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
            is = ConversationLog.class.getResourceAsStream("sample.in");
            out = new PrintStream(baos, true);
            timerStart = System.nanoTime();
        } else {
            is = System.in;
            out = System.out;
        }
        
        new ConversationLog(sample, is, out).solve();
        
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
                    = Paths.get(ConversationLog.class.getResource("sample.out").toURI());
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
