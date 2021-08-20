package com.github.pareronia.kattis.paths;

import static java.util.Arrays.asList;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.List;
import java.util.function.Supplier;

/**
 * Paths
 * @see <a href="https://open.kattis.com/problems/paths">https://open.kattis.com/problems/paths</a>
 */
public class Paths {

    private final boolean sample;
    private final InputStream in;
    private final PrintStream out;
    
    public Paths(
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
    
    private long bfs(final int n, final int k, final List<Integer>[] adj,
            final int[] c, final int start) {
        
        final Deque<int[]> queue = new ArrayDeque<>();
        int[] node = new int[k + 1];
        node[0] = start;
        queue.add(node);
        long cnt = 0;
        while (!queue.isEmpty()) {
            node = queue.poll();
            node[c[node[0]]] = 1;
            int colors = 0;
            for (int j = 1; j < node.length; j++) {
                colors += node[j];
            }
            if (colors > 1) {
                cnt++;
            }
            if (colors == k) {
                continue;
            }
            for (final Integer a : adj[node[0]]) {
                if (node[c[a]] == 0) {
                    final int[] newNode = Arrays.copyOf(node, node.length);
                    newNode[0] = a;
                    queue.add(newNode);
                }
            }
        }
        return cnt;
    }
    
    @SuppressWarnings("unchecked")
    private void handleTestCase(final Integer i, final FastScanner sc) throws IOException {
        final int n = sc.nextInt();
        final int m = sc.nextInt();
        final int k = sc.nextInt();
        final int[] c = new int[n + 1];
        for (int j = 1; j <= n; j++) {
            c[j] = sc.nextInt();
        }
        final List<Integer>[] adj = new ArrayList[n + 1];
        for (int j = 0; j <= n; j++) {
            adj[j] = new ArrayList<>();
        }
        for (int j = 0; j < m; j++) {
           final int v1 = sc.nextInt();
           final int v2 = sc.nextInt();
           adj[v1].add(v2);
           adj[v2].add(v1);
        }
        long ans = 0;
        for (int j = 1; j <= n; j++) {
            if (adj[j].isEmpty()) {
                continue;
            }
            ans += bfs(n, k, adj, c, j);
        }
        this.out.println(ans);
    }
    
    public void solve() throws IOException {
        try (final FastScanner sc = new FastScanner(this.in)) {
            final int numberOfTestCases;
            if (this.sample) {
                numberOfTestCases = sc.nextInt();
            } else {
                numberOfTestCases = 1;
            }
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
            is = Paths.class.getResourceAsStream("sample.in");
            out = new PrintStream(baos, true);
            timerStart = System.nanoTime();
        } else {
            is = System.in;
            out = System.out;
        }
        
        new Paths(sample, is, out).solve();
        
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
                    = java.nio.file.Paths.get(Paths.class.getResource("sample.out").toURI());
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
        final private int BUFFER_SIZE = 1 << 16;
        private final DataInputStream din;
        private final byte[] buffer;
        private int bufferPointer, bytesRead;
 
        public FastScanner(final InputStream in) {
            din = new DataInputStream(in);
            buffer = new byte[BUFFER_SIZE];
            bufferPointer = bytesRead = 0;
        }
 
        public int nextInt() throws IOException {
            int ret = 0;
            byte c = read();
            while (c <= ' ') {
                c = read();
            }
            final boolean neg = (c == '-');
            if (neg) {
                c = read();
            }
            do {
                ret = ret * 10 + c - '0';
            } while ((c = read()) >= '0' && c <= '9');
 
            if (neg) {
                return -ret;
            }
            return ret;
        }
 
        private void fillBuffer() throws IOException {
            bytesRead = din.read(buffer, bufferPointer = 0, BUFFER_SIZE);
            if (bytesRead == -1) {
                buffer[0] = -1;
            }
        }
 
        private byte read() throws IOException {
            if (bufferPointer == bytesRead) {
                fillBuffer();
            }
            return buffer[bufferPointer++];
        }
 
        @Override
        public void close() throws IOException {
            if (din == null) {
                return;
            }
            din.close();
        }
    }
}
