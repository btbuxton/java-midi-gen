package net.blabux.midigen.common;

import org.junit.Test;

import java.util.Iterator;

import static org.junit.Assert.*;

public class LimitedIterableTest {

    @Test
    public void testOne() {
        LimitedIterable<Integer> subject = new LimitedIterable<Integer>(3);
        subject.add(1);
        Iterator<Integer> iter = subject.iterator();
        assertTrue(iter.hasNext());
        assertEquals((Integer) 1, iter.next());
        assertFalse(iter.hasNext());
    }

    @Test
    public void testTwo() {
        LimitedIterable<Integer> subject = new LimitedIterable<Integer>(3);
        subject.add(1);
        subject.add(2);
        Iterator<Integer> iter = subject.iterator();
        assertTrue(iter.hasNext());
        assertEquals((Integer) 1, iter.next());
        assertEquals((Integer) 2, iter.next());
        assertFalse(iter.hasNext());
    }

    @Test
    public void testThree() {
        LimitedIterable<Integer> subject = new LimitedIterable<Integer>(3);
        subject.add(1);
        subject.add(2);
        subject.add(3);
        Iterator<Integer> iter = subject.iterator();
        assertTrue(iter.hasNext());
        assertEquals((Integer) 1, iter.next());
        assertEquals((Integer) 2, iter.next());
        assertEquals((Integer) 3, iter.next());
        assertFalse(iter.hasNext());
    }

    @Test
    public void testFour() {
        LimitedIterable<Integer> subject = new LimitedIterable<Integer>(3);
        subject.add(1);
        subject.add(2);
        subject.add(3);
        subject.add(4);
        Iterator<Integer> iter = subject.iterator();
        assertTrue(iter.hasNext());
        assertEquals((Integer) 2, iter.next());
        assertEquals((Integer) 3, iter.next());
        assertEquals((Integer) 4, iter.next());
        assertFalse(iter.hasNext());
    }

    @Test
    public void testFive() {
        LimitedIterable<Integer> subject = new LimitedIterable<Integer>(3);
        subject.add(1);
        subject.add(2);
        subject.add(3);
        subject.add(4);
        subject.add(5);
        Iterator<Integer> iter = subject.iterator();
        assertTrue(iter.hasNext());
        assertEquals((Integer) 3, iter.next());
        assertEquals((Integer) 4, iter.next());
        assertEquals((Integer) 5, iter.next());
        assertFalse(iter.hasNext());
    }

}
