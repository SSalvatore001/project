//----------------------------------------
// Programming project code
// UniBw M, 2022-2025
// www.unibw.de/inf2
// (c) Mark Minas (mark.minas@unibw.de)
//----------------------------------------

package pp.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CircularListTest {
    private CircularList<Integer> list;

    @BeforeEach
    public void setUp() {
        list = new CircularList<>();
        list.addAll(List.of(1, 2, 3, 4));
    }

    @Test
    public void testSize() {
        assertEquals(4, list.size());
        assertEquals(0, new CircularList<>().size());
    }

    @Test
    public void testToString() {
        assertEquals("[1, 2, 3, 4]", list.toString());
        assertEquals("[]", new CircularList<>().toString());
    }

    @Test
    public void testToList() {
        assertEquals(List.of(1, 2, 3, 4), list.toList());
        assertEquals(Collections.emptyList(), new CircularList<>().toList());
    }

    @Test
    public void testAdd() {
        list.add(5);
        assertEquals(5, list.first().getPrev().getElem().intValue());
        assertEquals(5, list.size());
        assertEquals("[1, 2, 3, 4, 5]", list.toString());
        assertEquals(List.of(1, 2, 3, 4, 5), list.toList());
    }

    @Test
    public void testAddAfter() {
        list.first().getNext().addAfter(6);
        assertEquals(6, list.first().getNext().getNext().getElem().intValue());
        assertEquals(5, list.size());
        assertEquals("[1, 2, 6, 3, 4]", list.toString());
        assertEquals(List.of(1, 2, 6, 3, 4), list.toList());
    }

    @Test
    public void testAddBefore() {
        list.first().getNext().addBefore(7);
        assertEquals(7, list.first().getNext().getElem().intValue());
        assertEquals(5, list.size());
        assertEquals("[1, 7, 2, 3, 4]", list.toString());
        assertEquals(List.of(1, 7, 2, 3, 4), list.toList());
    }

    @Test
    public void testDelete() {
        list.first().getNext().delete();
        assertEquals(3, list.size());
        assertEquals("[1, 3, 4]", list.toString());
        assertEquals(List.of(1, 3, 4), list.toList());
        list.first().delete();
        assertEquals(2, list.size());
        assertEquals("[3, 4]", list.toString());
        assertEquals(List.of(3, 4), list.toList());
        list.first().getNext().delete();
        assertEquals(1, list.size());
        assertEquals("[3]", list.toString());
        assertEquals(List.of(3), list.toList());
        list.first().delete();
        assertEquals(0, list.size());
        assertEquals("[]", list.toString());
        assertEquals(Collections.emptyList(), list.toList());
    }
}