package com.github.pareronia.kattis.rust;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.BufferedWriter;
import java.io.StringWriter;
import java.util.Random;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import com.github.pareronia.kattis.TestBase;

@Timeout(1)
class RustTest extends TestBase<Rust> {

    protected RustTest() {
        super(Rust.class);
    }

    @Test
    void test() throws Exception {
        final StringWriter sw = new StringWriter();
        try (final BufferedWriter writer = new BufferedWriter(sw)) {
            writer.write("5 3");
            writer.newLine();
            writer.write(".....");
            writer.newLine();
            writer.write(".5.7.");
            writer.newLine();
            writer.write("...#.");
            writer.newLine();
            writer.write(".9...");
            writer.newLine();
            writer.write("2....");
            writer.newLine();
        }
        assertThat(run(setUpInput(sw.toString()))).containsExactly("5");
    }

    @Test
    void testLarge() throws Exception {
        final StringWriter sw = new StringWriter();
        try (final BufferedWriter writer = new BufferedWriter(sw)) {
            writer.write("1000 3");
            writer.newLine();
            writer.write(new StringBuilder().repeat('.', 1000).toString());
            writer.newLine();
            final Random rand = new Random(System.nanoTime());
            for (int i = 0; i < 998; i++) {
                final StringBuilder s = new StringBuilder();
                s.append('.');
                for (int j = 0; j < 998; j++) {
                    final int r = rand.nextInt(11);
                    if (r == 9) {
                        s.append('#');
                    } else if (r == 10) {
                        s.append('.');
                    } else {
                        s.append('1' + r);
                    }
                }
                s.append('.');
                writer.write(s.toString());
                writer.newLine();
            }
            writer.write(new StringBuilder().repeat('.', 1000).toString());
            writer.newLine();
        }
        assertThat(run(setUpInput(sw.toString()))).isNotEmpty();
    }
}
