package com.github.pareronia.kattis.skotleikur;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.StringWriter;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import com.github.pareronia.kattis.TestBase;

@Timeout(1)
class SkotleikurTest extends TestBase<Skotleikur> {

    protected SkotleikurTest() {
        super(Skotleikur.class);
    }

    @Test
    void test() throws Exception {
        assertThat(run(setUpInput(setUp(150, 100, 50)))).containsExactly("2", "0 3", "1 1");
    }

    @Test
    void testLarge() throws Exception {
        assertThat(run(setUpInput(setUp(1000000, 100, 10)))).isNotEmpty();
    }

    private String setUp(final int k, final int a, final int b) throws IOException {
        final StringWriter sw = new StringWriter();
        try (final BufferedWriter writer = new BufferedWriter(sw)) {
            writer.write(String.valueOf(k));
            writer.newLine();
            writer.write(String.valueOf(a));
            writer.newLine();
            writer.write(String.valueOf(b));
            writer.newLine();
        }
        return sw.toString();
    }
}
