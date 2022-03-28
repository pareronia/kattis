package com.github.pareronia.kattis.ternarianweights;

import static java.util.Arrays.asList;
import static java.util.Comparator.reverseOrder;
import static java.util.stream.Collectors.joining;

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
import java.util.PriorityQueue;
import java.util.StringTokenizer;
import java.util.TreeSet;

/**
 * Ternarian Weights
 * @see <a href="https://open.kattis.com/problems/ternarianweights">https://open.kattis.com/problems/ternarianweights</a>
 */
public class TernarianWeights {

    private final InputStream in;
    private final PrintStream out;
    
    public TernarianWeights(
            final Boolean sample, final InputStream in, final PrintStream out) {
        this.in = in;
        this.out = out;
    }
    
    private void handleTestCase(final Integer i, final FastScanner sc) {
        final TreeSet<Long> p3 = new TreeSet<>();
        p3.add(1L);
        for (int j = 1; j < 30; j++) {
            p3.add(p3.last() * 3);
        }
        final int x = sc.nextInt();
        final State sol = bfs(p3, x);
        final List<String> a = new ArrayList<>();
        a.add(String.format("left pan: %s", sol.l.stream()
                .sorted(reverseOrder())
                .map(Object::toString)
                .collect(joining(" "))).stripTrailing());
        a.add(String.format("right pan: %s", sol.r.stream()
                .sorted(reverseOrder())
                .map(Object::toString)
                .collect(joining(" "))).stripTrailing());
        a.add("");
        final String ans = a.stream().collect(joining(System.lineSeparator()));
        this.out.println(ans);
    }
    
    private List<Long> next(final TreeSet<Long> p3, final long diff) {
        final Long f = p3.floor(diff);
        final Long c = p3.ceiling(diff);
        final List<Long> ans = new ArrayList<>();
        if (f != null) {
            ans.add(f);
        }
        if (c != null) {
            ans.add(c);
        }
        return ans;
    }
    
    private State bfs(final TreeSet<Long> p3, final int x) {
        final State start = new State(p3, new ArrayList<>(), new ArrayList<>(), x);
        final PriorityQueue<State> q = new PriorityQueue<>();
        q.add(start);
        while (!q.isEmpty()) {
            final State curr = q.poll();
            if (curr.diff == 0) {
                return curr;
            }
            for (final Long y : next(curr.p3, curr.diff)) {
                final List<Long> lnewl = new ArrayList<>(curr.l);
                lnewl.add(y);
                final List<Long> lnewr = new ArrayList<>(curr.r);
                final TreeSet<Long> lnewp3 = new TreeSet<>(curr.p3);
                lnewp3.remove(y);
                final State lstate = new State(lnewp3, lnewl, lnewr, x);
                q.add(lstate);
                final List<Long> rnewl = new ArrayList<>(curr.l);
                final List<Long> rnewr = new ArrayList<>(curr.r);
                rnewr.add(y);
                final TreeSet<Long> rnewp3 = new TreeSet<>(curr.p3);
                rnewp3.remove(y);
                final State rstate = new State(rnewp3, rnewl, rnewr, x);
                q.add(rstate);
            }
        }
        throw new IllegalStateException("Unsolvable");
    }
    
    public void solve() {
        try (final FastScanner sc = new FastScanner(this.in)) {
            final int numberOfTestCases = sc.nextInt();
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
            is = TernarianWeights.class.getResourceAsStream("sample.in");
            out = new PrintStream(baos, true);
            timerStart = System.nanoTime();
        } else {
            is = System.in;
            out = System.out;
        }
        
        new TernarianWeights(sample, is, out).solve();
        
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
                    = Paths.get(TernarianWeights.class.getResource("sample.out").toURI());
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
    
    private static class State implements Comparable<State> {
        private final TreeSet<Long> p3;
        private final List<Long> l;
        private final List<Long> r;
        private final int x;
        private final long diff;
        
        public State(final TreeSet<Long> p3, final List<Long> l, final List<Long> r, final int x) {
            this.p3 = p3;
            this.l = l;
            this.r = r;
            this.x = x;
            this.diff = diff();
        }
        
        public long diff() {
            long diff = x;
            for (int i = 0; i < l.size(); i++) {
                diff += l.get(i);
            }
            for (int i = 0; i < r.size(); i++) {
                diff -= r.get(i);
            }
            return Math.abs(diff);
        }

        @Override
        public int compareTo(final State other) {
            return Long.compare(this.diff, other.diff);
        }
    }
}
