package com.github.pareronia.kattis.raidteams;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

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
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * Raid Teams
 * @see <a href="https://open.kattis.com/problems/raidteams">https://open.kattis.com/problems/raidteams</a>
 */
public class RaidTeams {

    private final boolean sample;
    private final InputStream in;
    private final PrintStream out;
    
    public RaidTeams(
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
    
    private Comparator<Player> bySkillAndPlayerName(final int skill) {
        return (p1, p2) -> {
            final int sk1 = skill == 1 ? p1.skill1 : (skill == 2 ? p1.skill2 : p1.skill3);
            final int sk2 = skill == 1 ? p2.skill1 : (skill == 2 ? p2.skill2 : p2.skill3);
            if (sk1 == sk2) {
                return p1.name.compareTo(p2.name);
            }
            if (sk1 < sk2) {
                return 1;
            } else {
                return -1;
            }
        };
    }
    
    private static final class Player {
        private final String name;
        private final int skill1;
        private final int skill2;
        private final int skill3;
    
        public Player(final String name,
                        final int skill1, final int skill2, final int skill3) {
            this.name = name;
            this.skill1 = skill1;
            this.skill2 = skill2;
            this.skill3 = skill3;
        }
    }
    
    private Result<?> handleTestCase(final Integer i, final FastScanner sc) {
        final int n = sc.nextInt();
        final Player[] sk1 = new Player[n];
        final Player[] sk2 = new Player[n];
        final Player[] sk3 = new Player[n];
        for (int j = 0; j < n; j++) {
            final Player p = new Player(sc.next(), sc.nextInt(),
                                        sc.nextInt(), sc.nextInt());
            sk1[j] = p;
            sk2[j] = p;
            sk3[j] = p;
        }
        Arrays.sort(sk1, bySkillAndPlayerName(1));
        Arrays.sort(sk2, bySkillAndPlayerName(2));
        Arrays.sort(sk3, bySkillAndPlayerName(3));
        final Set<String> seen = new HashSet<>();
        int idx1 = 0, idx2 = 0, idx3 = 0;
        final List<String> anss = new ArrayList<>();
        int cnt = 0;
        while (cnt < n / 3) {
            String p1 = null, p2 = null, p3 = null;
            do {
                p1 = sk1[idx1++].name;
            } while (idx1 < n && seen.contains(p1));
            seen.add(p1);
            do {
                p2 = sk2[idx2++].name;
            } while (idx2 < n && seen.contains(p2));
            seen.add(p2);
            do {
                p3 = sk3[idx3++].name;
            } while (idx3 < n && seen.contains(p3));
            seen.add(p3);
            anss.add(List.of(p1, p2, p3).stream()
                    .sorted()
                    .collect(joining(" ")));
            cnt++;
        }
        return new Result<>(i, anss);
    }
    
    public void solve() {
        try (final FastScanner sc = new FastScanner(this.in)) {
            final int numberOfTestCases;
            if (this.sample) {
                numberOfTestCases = sc.nextInt();
            } else {
                numberOfTestCases = 1;
            }
            final List<Result<?>> results
                    = Stream.iterate(1, i -> i <= numberOfTestCases, i -> i + 1)
                            .map(i -> handleTestCase(i, sc))
                            .collect(toList());
            output(results);
        }
    }

    private void output(final List<Result<?>> results) {
        results.forEach(r -> {
            r.getValues().stream().map(Object::toString).forEach(this.out::println);
        });
    }
    
    public static void main(final String[] args) throws IOException, URISyntaxException {
        final boolean sample = isSample();
        final InputStream is;
        final PrintStream out;
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        long timerStart = 0;
        if (sample) {
            is = RaidTeams.class.getResourceAsStream("sample.in");
            out = new PrintStream(baos, true);
            timerStart = System.nanoTime();
        } else {
            is = System.in;
            out = System.out;
        }
        
        new RaidTeams(sample, is, out).solve();
        
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
                    = Paths.get(RaidTeams.class.getResource("sample.out").toURI());
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
        
        @SuppressWarnings("unused")
        public long nextLong() {
            return Long.parseLong(next());
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
    
    private static final class Result<T> {
        @SuppressWarnings("unused")
        private final int number;
        private final List<T> values;
        
        public Result(final int number, final List<T> values) {
            this.number = number;
            this.values = values;
        }

        public List<T> getValues() {
            return values;
        }
    }
}
