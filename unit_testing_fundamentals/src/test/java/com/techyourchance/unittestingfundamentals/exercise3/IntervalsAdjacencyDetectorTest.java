package com.techyourchance.unittestingfundamentals.exercise3;

import com.techyourchance.unittestingfundamentals.example3.Interval;

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class IntervalsAdjacencyDetectorTest {
    IntervalsAdjacencyDetector SUT;

    @Before
    public void setUp() throws Exception {
        SUT = new IntervalsAdjacencyDetector();
    }

    @Test
    public void intervalAdjacentDetector_interval1BeforeInterval2_returnFalse() {
        Interval interval1 = new Interval(-5,-1);
        Interval interval2 = new Interval(1,5);
        boolean result = SUT.isAdjacent(interval1,interval2);
        assertThat(result,is(false));
    }

    @Test
    public void intervalAdjacentDetector_interval1AfterInterval2_returnFalse() {
        Interval interval1 = new Interval(1,5);
        Interval interval2 = new Interval(-5,-1);
        boolean result = SUT.isAdjacent(interval1,interval2);
        assertThat(result,is(false));
    }

    @Test
    public void intervalAdjacentDetector_interval1BeforeAdjacentInterval2_returnTrue() {
        Interval interval1 = new Interval(1,5);
        Interval interval2 = new Interval(-5,1);
        boolean result = SUT.isAdjacent(interval1,interval2);
        assertThat(result,is(true));
    }

    @Test
    public void intervalAdjacentDetector_interval1AfterAdjacentInterval2_returnTrue() {
        Interval interval1 = new Interval(1,5);
        Interval interval2 = new Interval(-5,1);
        boolean result = SUT.isAdjacent(interval1,interval2);
        assertThat(result,is(true));
    }

    @Test
    public void intervalAdjacentDetector_interval1EqualsInterval2_returnTrue() {
        Interval interval1 = new Interval(1,5);
        Interval interval2 = new Interval(1,5);
        boolean result = SUT.isAdjacent(interval1,interval2);
        assertThat(result,is(false));
    }
}