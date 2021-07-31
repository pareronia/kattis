package com.github.pareronia.kattis.cardtrick2;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.github.pareronia.kattis.TestBase;

public class CardTrickTestCase extends TestBase<CardTrick> {

    public CardTrickTestCase() {
        super(CardTrick.class);
    }

    @Test
    void test1() throws Exception {
        assertThat(doTest(1), is(List.of("1")));
        check("1", 1);
    }
    
    @Test
    void test2() throws Exception {
        assertThat(doTest(2), is(List.of("2 1")));
        check("2 1", 2);
    }
    
    @Test
    void test3() throws Exception {
        assertThat(doTest(3), is(List.of("3 1 2")));
        check("3 1 2", 3);
    }
    
    @Test
    void test4() throws Exception {
        assertThat(doTest(4), is(List.of("2 1 4 3")));
        check("2 1 4 3", 4);
    }
    
    @Test
    void test5() throws Exception {
        assertThat(doTest(5), is(List.of("3 1 4 5 2")));
        check("3 1 4 5 2", 5);
    }

    @Test
    void test6() throws Exception {
        assertThat(doTest(6), is(List.of("4 1 6 3 2 5")));
        check("4 1 6 3 2 5", 6);
    }

    @Test
    void test7() throws Exception {
        assertThat(doTest(7), is(List.of("5 1 3 4 2 6 7")));
        check("5 1 3 4 2 6 7", 7);
    }
    
    @Test
    void test8() throws Exception {
        assertThat(doTest(8), is(List.of("3 1 7 5 2 6 8 4")));
        check("3 1 7 5 2 6 8 4", 8);
    }

    @Test
    void test9() throws Exception {
        assertThat(doTest(9), is(List.of("7 1 8 6 2 9 4 5 3")));
        check("7 1 8 6 2 9 4 5 3", 9);
    }
    
    @Test
    void test10() throws Exception {
        assertThat(doTest(10), is(List.of("9 1 8 5 2 4 7 6 3 10")));
        check("9 1 8 5 2 4 7 6 3 10", 10);
    }
    
    @Test
    void test11() throws Exception {
        assertThat(doTest(11), is(List.of("5 1 6 4 2 10 11 7 3 8 9")));
        check("5 1 6 4 2 10 11 7 3 8 9", 11);
    }

    @Test
    void test12() throws Exception {
        assertThat(doTest(12), is(List.of("7 1 4 9 2 11 10 8 3 6 5 12")));
        check("7 1 4 9 2 11 10 8 3 6 5 12", 12);
    }
    
    @Test
    void test13() throws Exception {
        assertThat(doTest(13), is(List.of("4 1 13 11 2 10 6 7 3 5 12 9 8")));
        check("4 1 13 11 2 10 6 7 3 5 12 9 8", 13);
    }
    
    private List<String> doTest(final int n) throws Exception {
        return run(setUpInput("1" + System.lineSeparator() + String.valueOf(n)));
    }
    
    private void check(final String seq, final int cnt) {
        final String[] cards = seq.split(" ");
        final Deque<Integer> d = new ArrayDeque<>();
        for (int i = 0; i < cards.length; i++) {
            final Integer card = Integer.valueOf(cards[i]);
            d.add(card);
        }
        for (int i = 1; i <= cards.length; i++) {
            for (int k = i; k > 0; k--) {
               d.addLast(d.pollFirst());
            }
            final Integer t = d.pollFirst();
            assertThat(t, equalTo(i));
        }
    }
}
