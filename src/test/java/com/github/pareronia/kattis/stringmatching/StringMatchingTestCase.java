package com.github.pareronia.kattis.stringmatching;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.io.InputStream;
import java.util.List;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import com.github.pareronia.kattis.TestBase;

public class StringMatchingTestCase extends TestBase<StringMatching> {

    public StringMatchingTestCase() {
        super(StringMatching.class);
    }

    @Test
    @Timeout(1)
    void test() throws Exception {
        final InputStream in = setUpInput(
                new StringBuilder()
                    .append("11")
                    .append(System.lineSeparator())
                    .append("1111")
                    .append(System.lineSeparator())
                .toString());
        
        assertThat(run(in), is(List.of("0 2")));
    }
    
    @Test
    @Timeout(1)
    void testLargeRandom() throws Exception {
        final String random = RandomStringUtils.random(100_000, "abc".toCharArray());
        final InputStream in = setUpInput(
                new StringBuilder()
                    .append("abcabcabc")
                    .append(System.lineSeparator())
                    .append(random)
                    .append(System.lineSeparator())
                .toString());
        
        run(in);
    }
    
    @Test
    void testBigSetOfLargeRandom() throws Exception {
        final String random = RandomStringUtils.random(5 * 1_024 * 1_024, "abc".toCharArray());
        final StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 1; i++) {
            sb.append("abcabcabc");
            sb.append(System.lineSeparator());
            sb.append(random);
            sb.append(System.lineSeparator());
        }
        final InputStream in = setUpInput(sb.toString());
        
        run(in);
    }

    @Test
    @Timeout(1)
    void testLarge() throws Exception {
        final String random = RandomStringUtils.random(49_995, "xyz".toCharArray());
        final InputStream in = setUpInput(
                new StringBuilder()
                    .append("abcabcabca")
                    .append(System.lineSeparator())
                    .append(random)
                    .append("abcabcabca")
                    .append(random)
                    .append(System.lineSeparator())
                .toString());
        
        assertThat(run(in), is(List.of("49995")));
    }
}
