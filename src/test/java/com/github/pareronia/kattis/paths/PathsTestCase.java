package com.github.pareronia.kattis.paths;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.util.List;
import java.util.Random;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import com.github.pareronia.kattis.TestBase;

@Timeout(3)
class PathsTestCase extends TestBase<Paths> {

    protected PathsTestCase() {
        super(Paths.class);
    }
    
    @Test
    void test() throws Exception {
        final int n = 4;
        final int m = 3;
        final int k = 3;
        final StringWriter sw = new StringWriter();
        try (final BufferedWriter writer = new BufferedWriter(sw)) {
            writer.write(n + " " + m + " " + k);
            writer.newLine();
            writer.write("1 2 1 3");
            writer.newLine();
            writer.write("1 2");
            writer.newLine();
            writer.write("2 3");
            writer.newLine();
            writer.write("4 2");
            writer.newLine();
        }
        assertThat(run(setUpInput(sw.toString())), is(List.of("10")));
    }
    
    @Test
    void test100k100k5() throws Exception {
        run(setUpInput(setUp(100_000, 100_000, 5)));
    }

    @Test
    void test300k300k3() throws Exception {
        run(setUpInput(setUp(300_000, 300_000, 3)));
    }

    @Test
    void test300k300k4() throws Exception {
        run(setUpInput(setUp(300_000, 300_000, 4)));
    }

    @Test
    void test300k300k5() throws Exception {
        run(setUpInput(setUp(300_000, 300_000, 5)));
    }
    
    @Test
    void test100k300k3() throws Exception {
        run(setUpInput(setUp(100_000, 300_000, 3)));
    }

    @Test
    void test100k300k4() throws Exception {
        run(setUpInput(setUp(100_000, 300_000, 4)));
    }

    @Test
    void test100k300k5() throws Exception {
        run(setUpInput(setUp(100_000, 300_000, 5)));
    }

    private String setUp(final int n, final int m, final int k) throws IOException {
        final Random rand = new Random(System.nanoTime());
        final StringWriter sw = new StringWriter();
        try (final BufferedWriter writer = new BufferedWriter(sw)) {
            writer.write(n + " " + m + " " + k);
            writer.newLine();
            for (int j = 0; j < n; j++) {
                if (j > 0) {
                    writer.write(" ");
                }
                writer.write(String.valueOf(rand.nextInt(k) + 1));
            }
            writer.newLine();
            for (int j = 0; j < m; j++) {
                final String v1 = String.valueOf(rand.nextInt(n) + 1);
                String v2;
                do {
                    v2 = String.valueOf(rand.nextInt(n) + 1);
                } while (v1 == v2);
                writer.write(v1 + " " + v2);
                writer.newLine();
            }
        }
        return sw.toString();
    }
}
