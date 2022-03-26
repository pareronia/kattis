package com.github.pareronia.kattis.wordcloud;

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
import java.util.List;
import java.util.StringTokenizer;
import java.util.stream.IntStream;

/**
 * Word Cloud
 * @see <a href="https://open.kattis.com/problems/wordcloud">https://open.kattis.com/problems/wordcloud</a>
 */
public class WordCloud {

    private final InputStream in;
    private final PrintStream out;
    
    public WordCloud(
            final Boolean sample, final InputStream in, final PrintStream out) {
        this.in = in;
        this.out = out;
    }
    
    private int ceilDiv(final int x, final int y) {
        return -Math.floorDiv(-x,y);
    }
    
    private void handleTestCase(final int i, final int w, final int n, final FastScanner sc) {
        final String[] s = new String[n];
        final int[] c = new int[n];
        int cmax = Integer.MIN_VALUE;
        for (int j = 0; j < n; j++) {
            s[j] = sc.next();
            c[j] = sc.nextInt();
            cmax = Math.max(c[j], cmax);
        }
        final int[] p = new int[n];
        for (int j = 0; j < n; j++) {
            p[j] = 8 + ceilDiv(40 * (c[j] - 4), (cmax - 4));
        }
        final List<List<Integer>> ll = new ArrayList<>();
        List<Integer> l = new ArrayList<>();
        for (int j = 0; j < n; j++) {
            final int ww = ceilDiv(9 * s[j].length() * p[j], 16);
            if (l.stream().mapToInt(Integer::intValue).sum()
                        + l.size() * 10 + ww > w) {
                ll.add(l);
                l = new ArrayList<>();
            }
            l.add(ww);
        }
        ll.add(l);
        int cnt = 0;
        int tot = 0;
        for (final List<Integer> line : ll) {
            tot += IntStream.range(cnt, cnt + line.size())
                .map(j -> p[j]).max().orElseThrow();
            cnt += line.size();
        }
        final String ans = String.format("CLOUD %d: %d", i, tot);
        this.out.println(ans);
    }
    
    public void solve() {
        try (final FastScanner sc = new FastScanner(this.in)) {
            int i = 1;
            while (true) {
                final int w = sc.nextInt();
                final int n = sc.nextInt();
                if (w == 0 && n == 0) {
                    break;
                }
                handleTestCase(i++, w, n, sc);
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
            is = WordCloud.class.getResourceAsStream("sample.in");
            out = new PrintStream(baos, true);
            timerStart = System.nanoTime();
        } else {
            is = System.in;
            out = System.out;
        }
        
        new WordCloud(sample, is, out).solve();
        
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
                    = Paths.get(WordCloud.class.getResource("sample.out").toURI());
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
