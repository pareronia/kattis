package com.github.pareronia.kattis.dungeon;

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
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Objects;
import java.util.StringTokenizer;

/**
 * Dungeon master
 * @see <a href="https://open.kattis.com/problems/dungeon">https://open.kattis.com/problems/dungeon</a>
 */
public class DungeonMaster {

    private final InputStream in;
    private final PrintStream out;
    
    public DungeonMaster(
            final Boolean sample, final InputStream in, final PrintStream out) {
        this.in = in;
        this.out = out;
    }
    
    private void handleTestCase(final int l, final int r, final int c, final FastScanner sc) {
        final int[][][] d = new int[l][r][c];
        Cube start = null;
        Cube end = null;
        for (int i = 0; i < l; i++) {
            for (int j = 0; j < r; j++) {
                final String row = sc.next();
                for (int k = 0; k < c; k++) {
                    if (row.charAt(k) == '.') {
                        d[i][j][k] = -1;
                    } else if (row.charAt(k) == 'S') {
                        start = new Cube(i, j, k);
                        d[i][j][k] = 0;
                    } else if (row.charAt(k) == 'E') {
                        end = new Cube(i, j, k);
                        d[i][j][k] = -1;
                    } else {
                        d[i][j][k] = Integer.MAX_VALUE;
                    }
                }
            }
        }
        final Deque<Cube> q = new ArrayDeque<>();
        q.add(start);
        while (!q.isEmpty()) {
            final Cube curr = q.poll();
            for (final Cube n : curr.neigbours(l, r, c)) {
                if (d[n.l][n.r][n.c] == -1) {
                    d[n.l][n.r][n.c] = d[curr.l][curr.r][curr.c] + 1;
                    q.add(n);
                }
            }
        }
        final String ans;
        if (d[end.l][end.r][end.c] == -1) {
            ans = "Trapped!";
        } else {
            ans = String.format("Escaped in %d minute(s).", d[end.l][end.r][end.c]);
        }
        this.out.println(ans);
    }
    
    public void solve() {
        try (final FastScanner sc = new FastScanner(this.in)) {
            while (true) {
                final int l = sc.nextInt();
                final int r = sc.nextInt();
                final int c = sc.nextInt();
                if (l== 0 && r == 0 && c == 0) {
                    break;
                }
                handleTestCase(l, r, c, sc);
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
            is = DungeonMaster.class.getResourceAsStream("sample.in");
            out = new PrintStream(baos, true);
            timerStart = System.nanoTime();
        } else {
            is = System.in;
            out = System.out;
        }
        
        new DungeonMaster(sample, is, out).solve();
        
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
                    = Paths.get(DungeonMaster.class.getResource("sample.out").toURI());
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

    private static class Cube {
        private final int l;
        private final int r;
        private final int c;
    
        public Cube(final int l, final int r, final int c) {
            this.l = l;
            this.r = r;
            this.c = c;
        }
        
        public List<Cube> neigbours(final int nl, final int nr, final int nc) {
            final List<Cube> ans = new ArrayList<>();
            final int[][] ds = {
                { -1, 0, 0 }, { 1, 0, 0 },
                { 0, -1, 0 }, { 0, 1, 0 },
                { 0, 0, -1 }, { 0, 0, 1 }
            };
            for (final int[] d : ds) {
                final int ll = l + d[0];
                if (ll < 0 || ll >= nl) {
                    continue;
                }
                final int rr = r + d[1];
                if (rr < 0 || rr >= nr) {
                    continue;
                }
                final int cc = c + d[2];
                if (cc < 0 || cc >= nc) {
                    continue;
                }
                ans.add(new Cube(ll, rr, cc));
            }
            return ans;
        }

        @Override
        public int hashCode() {
            return Objects.hash(c, l, r);
        }

        @Override
        public boolean equals(final Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final Cube other = (Cube) obj;
            return c == other.c && l == other.l && r == other.r;
        }
    }
}
