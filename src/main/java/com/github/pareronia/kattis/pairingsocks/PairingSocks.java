package com.github.pareronia.kattis.pairingsocks;

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
import java.util.function.Supplier;

/**
 * Pairing Socks
 * @see <a href="https://open.kattis.com/problems/pairingsocks">https://open.kattis.com/problems/pairingsocks</a>
 */
public class PairingSocks {

    private final boolean sample;
    private final InputStream in;
    private final PrintStream out;
    
    public PairingSocks(
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
    
    public class DoublyLinkedList {
        
        private Node head;
        private Node tail;

        public DoublyLinkedList() {
            this.head = null;
            this.tail = null;
        }
        
        public boolean isEmpty() {
            return (this.head == null);
        }
        
        public void addHead(final int value) {
            final Node node = new Node(value);
            node.prev = null;
            if (isEmpty()) {
                this.head = node;
                this.tail = node;
            } else {
                this.head.prev = node;
                node.next = this.head;
                this.head = node;
            }
        }
        
        public void addTail(final int value) {
            final Node node = new Node(value);
            node.next = null;
            if (isEmpty()) {
                this.tail = node;
                this.head = tail;
            } else {
                this.tail.next = node;
                node.prev = this.tail;
                this.tail = node;
            }
        }
        
        public Node removeHead() {
            final Node temp = this.head;
            this.head = this.head.next;
            if (this.head == null) {
                this.tail = null;
            } else {
                this.head.prev = null;
            }
            return temp;
        }
        
        public Node head() {
            return head;
        }

        public class Node {
            public int value;
            public Node next;
            public Node prev;
            
            public Node(final int value) {
                this.value = value;
            }

            public int value() {
                return value;
            }
        }
    }
    
    private void handleTestCase(final Integer i, final FastScanner sc) {
        final int n = sc.nextInt();
        final DoublyLinkedList s1 = new DoublyLinkedList();
        for (int j = 0; j < 2 * n; j++) {
            s1.addTail(sc.nextInt());
        }
        final DoublyLinkedList s2 = new DoublyLinkedList();
        int ans = 0;
        while (!s1.isEmpty()) {
            ans++;
            if (s2.isEmpty()) {
                s2.addHead(s1.removeHead().value());
                continue;
            }
            if (s1.head().value() == s2.head().value()) {
                s1.removeHead();
                s2.removeHead();
                continue;
            }
            s2.addHead(s1.removeHead().value());
        }
        if (s2.isEmpty()) {
            this.out.println(ans);
        } else {
            this.out.println("impossible");
        }
    }
    
    public void solve() {
        try (final FastScanner sc = new FastScanner(this.in)) {
            final int numberOfTestCases;
            if (this.sample) {
                numberOfTestCases = sc.nextInt();
            } else {
                numberOfTestCases = 1;
            }
            for (int i = 0; i < numberOfTestCases; i ++) {
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
            is = PairingSocks.class.getResourceAsStream("sample.in");
            out = new PrintStream(baos, true);
            timerStart = System.nanoTime();
        } else {
            is = System.in;
            out = System.out;
        }
        
        new PairingSocks(sample, is, out).solve();
        
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
                    = Paths.get(PairingSocks.class.getResource("sample.out").toURI());
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
        public int[] nextIntArray(final int n) {
            final int[] a = new int[n];
            for (int i = 0; i < n; i++) {
                a[i] = nextInt();
            }
            return a;
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
}
