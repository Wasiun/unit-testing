package com.techyourchance.unittestingfundamentals.exercise2;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class StringDuplicatorTest {
    StringDuplicator SUT;

    @Before
    public void setUp() throws Exception {
        SUT=new StringDuplicator();
    }

    @Test
    public void stringDuplicator_empty_returnsEmpty() {
        String output = SUT.duplicate("");
        assertThat(output,is(""));
    }

    @Test
    public void stringDuplicator_char_returnsChars() {
        String output = SUT.duplicate("s");
        assertThat(output,is("ss"));
    }

    @Test
    public void stringDuplicator_word_returnsDoubleWords() {
        String output = SUT.duplicate("sex");
        assertThat(output,is("sexsex"));
    }
}