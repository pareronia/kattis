package com.github.pareronia.kattis.evenup;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.joining;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * Even Up Solitaire
 * @see <a href="https://open.kattis.com/problems/evenup">https://open.kattis.com/problems/evenup</a>
 */
public class EvenUpSolitaire {

    private final boolean sample;
    private final InputStream in;
    private final PrintStream out;
    
    public EvenUpSolitaire(
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
    
    private static class DoublyLinkedList {
        
        private Node head;
        private Node tail;
        private int size;

        public DoublyLinkedList() {
            this.head = null;
            this.tail = null;
            this.size = 0;
        }
        
        public boolean isEmpty() {
            return (this.head == null);
        }
        
        public void addTail(final int value) {
            final Node node = new Node(value);
            node.next = null;
            if (isEmpty()) {
                this.tail = node;
                this.head = node;
            } else {
                this.tail.next = node;
                node.prev = this.tail;
                this.tail = node;
            }
            this.size++;
        }
        
        public Node removeHead() {
            final Node temp = this.head;
            this.head = this.head.next;
            if (this.head == null) {
                this.tail = null;
            } else {
                this.head.prev = null;
            }
            this.size--;
            return temp;
        }
        
        public Node removeTail() {
            final Node temp = this.tail;
            this.tail = this.tail.prev;
            if (this.tail == null) {
                this.head = null;
            } else {
                this.tail.next = null;
            }
            this.size--;
            return temp;
        }
        
        public void remove(final Node node) {
            if (node == this.tail) {
                removeTail();
            } else if (node == this.head) {
                removeHead();
            } else {
                node.prev.next = node.next;
                node.next.prev = node.prev;
                this.size--;
            }
        }
        
        @Override
        public String toString() {
            return Stream.iterate(this.head, curr -> curr != null, curr -> curr.next)
                    .map(Node::toString)
                    .collect(joining(" "));
        }

        public Node head() {
            return head;
        }

        public int size() {
            return this.size;
        }

        public static class Node {
            public int value;
            public Node next;
            public Node prev;
            
            public Node(final int value) {
                this.value = value;
            }

            @Override
            public String toString() {
                return String.valueOf(value);
            }
            
            public boolean hasNext() {
                return this.next != null;
            }
            
            public boolean hasPrev() {
                return this.prev != null;
            }

            public int value() {
                return value;
            }

            public Node next() {
                return next;
            }

            public Node prev() {
                return prev;
            }
        }
    }
    
    private void handleTestCase(final FastScanner sc) throws IOException {
        final int n = sc.nextInt();
        final DoublyLinkedList dll = new DoublyLinkedList();
        for (int j = 0; j < n; j++) {
            dll.addTail(sc.nextInt());
        }
        DoublyLinkedList.Node curr = dll.head();
        while (curr != null && curr.hasNext()) {
            if ((curr.value() + curr.next().value()) % 2 == 0) {
                if (curr.hasPrev()) {
                    curr = curr.prev();
                    dll.remove(curr.next());
                    dll.remove(curr.next());
                } else {
                    dll.remove(curr.next());
                    dll.remove(curr);
                    curr = dll.head();
                }
            } else {
                curr = curr.next();
            }
        }
        this.out.println(dll.size());
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
                handleTestCase(sc);
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
            is = EvenUpSolitaire.class.getResourceAsStream("sample.in");
            out = new PrintStream(baos, true);
            timerStart = System.nanoTime();
        } else {
            is = System.in;
            out = System.out;
        }
        
        new EvenUpSolitaire(sample, is, out).solve();
        
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
                    = Paths.get(EvenUpSolitaire.class.getResource("sample.out").toURI());
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
