package com.github.pareronia.kattis._99problems2;

import static java.util.stream.Collectors.joining;
import static org.assertj.core.api.Assertions.assertThat;

import java.io.BufferedWriter;
import java.io.StringWriter;
import java.util.Random;
import java.util.stream.IntStream;

import org.junit.jupiter.api.Test;

import com.github.pareronia.kattis.TestBase;

class NinetyNineProblemsTest extends TestBase<NinetyNineProblems> {

    protected NinetyNineProblemsTest() {
        super(NinetyNineProblems.class);
    }

    @Test
    void test() throws Exception {
        final StringWriter sw = new StringWriter();
        try (final BufferedWriter writer = new BufferedWriter(sw)) {
            writer.write("3 4");
            writer.newLine();
            writer.write("10 10 11");
            writer.newLine();
            writer.write("1 10");
            writer.newLine();
            writer.write("1 10");
            writer.newLine();
            writer.write("1 9");
            writer.newLine();
            writer.write("1 5");
            writer.newLine();
        }
        assertThat(run(setUpInput(sw.toString()))).containsExactly("11", "-1", "10", "10");
    }

    @Test
    void testLarge() throws Exception {
        final Random rand = new Random(System.nanoTime());
        final StringWriter sw = new StringWriter();
        try (final BufferedWriter writer = new BufferedWriter(sw)) {
            writer.write("500000 100000");
            writer.newLine();
            final String d = IntStream.generate(() -> 1 + rand.nextInt(1_000_000_000))
                    .limit(500_000)
                    .mapToObj(String::valueOf)
                    .collect(joining(" "));
            writer.write(d);
            writer.newLine();
            for (int i = 0; i < 100_000; i++) {
                writer.write(String.valueOf(1 + rand.nextInt(2)));
                writer.write(" ");
                writer.write(String.valueOf(1 + rand.nextInt(1_000_000_000)));
                writer.newLine();
            }
        }
        assertThat(run(setUpInput(sw.toString()))).hasSize(100_000);
    }
}
