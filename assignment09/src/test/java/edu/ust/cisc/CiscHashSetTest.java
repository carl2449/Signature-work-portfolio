package edu.ust.cisc;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

public class CiscHashSetTest {
    private CiscHashSet<String> hashSet;
    private Field elementData;
    private Field size;
    private Field REMOVED;
    private Method hashFunction;

    @BeforeEach
    public void setUp() throws Exception {
        hashSet = new CiscHashSet<>();
        elementData = CiscHashSet.class.getDeclaredField("elementData");
        elementData.setAccessible(true);
        size = CiscHashSet.class.getDeclaredField("size");
        size.setAccessible(true);
        REMOVED = CiscHashSet.class.getDeclaredField("REMOVED");
        REMOVED.setAccessible(true);
        hashFunction = CiscHashSet.class.getDeclaredMethod("hashFunction", Object.class);
        hashFunction.setAccessible(true);
    }

    @Test
    public void testFields() {
        try {
            assertEquals(4, CiscHashSet.class.getDeclaredFields().length, "CiscHashSet should only have \"elementData\", \"size\", \"REMOVED\", and \"MAX_LOAD_FACTOR\" fields");
        } catch(Exception e) {
            e.printStackTrace();
            fail("CiscHashSet fields are not setup correctly");
        }
    }

    @Test
    public void testConstructor() {
        try {
            assertEquals(11, ((Object[])elementData.get(hashSet)).length, "CiscHashSet constructor is not working correctly (check array length)");
            assertNotNull(REMOVED.get(hashSet), "CiscHashSet constructor is not working correctly (check REMOVED)");
            assertEquals(0, size.get(hashSet), "CiscHashSet constructor is not working correctly (check size)");
        } catch (Exception e) {
            e.printStackTrace();
            fail("CiscHashSet constructor is not working correctly");
        }
    }

    @Test
    public void testIsEmpty() {
        try {
            assertTrue(hashSet.isEmpty(), "isEmpty is not working correctly (not returning true when appropriate)");
            size.set(hashSet, 1);
            assertFalse(hashSet.isEmpty(), "isEmpty is not working correctly (not returning false when appropriate)");
        } catch (Exception e) {
            e.printStackTrace();
            fail("isEmpty is not working correctly");
        }
    }

    @Test
    public void testSize() {
        try {
            assertEquals(0, hashSet.size(), "size method is not working correctly (check with empty tree)");
            size.set(hashSet, 1);
            assertEquals(1, hashSet.size(), "size method is not working correctly (check with non-empty tree)");
        } catch (Exception e) {
            e.printStackTrace();
            fail("size method is not working correctly");
        }
    }

    @Test
    public void testClear() {
        try {
            size.set(hashSet, 3);
            Object[] data = {"one", "two", REMOVED.get(hashSet), "three", null};
            elementData.set(hashSet, data);
            hashSet.clear();
            assertEquals(0, size.get(hashSet), "clear method is not working correctly (check size)");
            for(int i=0; i<data.length; ++i) {
                assertNull(((Object[])elementData.get(hashSet))[i], "clear method is not working correctly (non-null elements should not exist)");
            }
        } catch (Exception e) {
            e.printStackTrace();
            fail("clear method is not working correctly");
        }
    }

    @Test
    public void testContains() {
        try {
            assertFalse(hashSet.contains("2"), "contains method is not working correctly");
            //[2,r,12,n,n,n,n,n,n,1]
            Object[] newData = {"2", REMOVED.get(hashSet), "12", null, null, null, null, null, null, "1"};
            elementData.set(hashSet, newData);
            size.set(hashSet, 3);
            assertTrue(hashSet.contains("2"), "contains method is not working correctly (not returning true when appropriate)");
            assertTrue(hashSet.contains("1"), "contains method is not working correctly (not returning true when appropriate)");
            assertTrue(hashSet.contains("12"), "contains method is not working correctly (not returning true when appropriate)");
            assertFalse(hashSet.contains("13"), "contains method is not working correctly (not returning false when appropriate)");
        } catch (Exception e) {
            e.printStackTrace();
            fail("contains method is not working correctly");
        }
    }

    @Test
    public void testAdd() {
        try {
            //add without rehash
            hashSet.add("2");   //hashes to 6
            Object[] expectedData = {null,null,null,null,null,null,"2",null,null,null,null};
            assertArrayEquals((Object[])elementData.get(hashSet), expectedData, "add method is not working correctly (tested without collision, wrapping, removals, duplicate key, or rehash)");
            assertEquals(1, size.get(hashSet), "add method is not working correctly (check size)");

            hashSet.add("13");  //hashes to 8
            Object[] expectedData2 = {null,null,null,null,null,null,"2",null,"13",null,null};
            assertArrayEquals((Object[])elementData.get(hashSet), expectedData2, "add method is not working correctly (tested with collision but without wrapping, removals, duplicate key, or rehash)");
            assertEquals(2, size.get(hashSet), "add method is not working correctly (check size)");

            hashSet.add("1");	//hashes to 5
            Object[] expectedData3 = {null,null,null,null,null,"1","2",null,"13",null,null};
            assertArrayEquals((Object[])elementData.get(hashSet), expectedData3, "add method is not working correctly (tested without collision, wrapping, removals, duplicate key, or rehash)");
            assertEquals(3, size.get(hashSet), "add method is not working correctly (check size)");

            hashSet.add("12");	//hashes to 7
            Object[] expectedData4 = {null,null,null,null,null,"1","2","12","13",null,null};
            assertArrayEquals((Object[])elementData.get(hashSet), expectedData4, "add method is not working correctly (tested with collision and wrapping but without removals, duplicate key, or rehash)");
            assertEquals(4, size.get(hashSet), "add method is not working correctly (check size)");

            Object[] setData = {null,null,null,null,null,"1","2",REMOVED.get(hashSet),"13",null,null};
            elementData.set(hashSet, setData);
            size.set(hashSet, 3);
            hashSet.add("12");    //hashes to 7
            Object[] expectedData5 = {null,null,null,null,null,"1","2",REMOVED.get(hashSet),"13","12",null};
            assertArrayEquals((Object[])elementData.get(hashSet), expectedData5, "add method is not working correctly (tested with collision, wrapping, and removals but without duplicate key or rehash)");
            assertEquals(4, size.get(hashSet), "add method is not working correctly (check size)");

            hashSet.add("22");	//hashes to 5
            Object[] expectedData6 = {null,null,null,null,null,"1","2",REMOVED.get(hashSet),"13","12","22"};
            assertArrayEquals((Object[])elementData.get(hashSet), expectedData6, "add method is not working correctly (tested with collision but without wrapping, removals, duplicate key, or rehash)");
            assertEquals(5, size.get(hashSet), "add method is not working correctly (check size)");

            hashSet.add("3");	//hashes to 7
            Object[] expectedData7 = {"3",null,null,null,null,"1","2",REMOVED.get(hashSet),"13","12","22"};
            assertArrayEquals((Object[])elementData.get(hashSet), expectedData7, "add method is not working correctly (tested with collision but without wrapping, removals, duplicate key, or rehash)");
            assertEquals(6, size.get(hashSet), "add method is not working correctly (check size)");

            hashSet.add("4");	//hashes to 8
            Object[] expectedData8 = {"3","4",null,null,null,"1","2",REMOVED.get(hashSet),"13","12","22"};
            assertArrayEquals((Object[])elementData.get(hashSet), expectedData8, "add method is not working correctly (tested with collision but without wrapping, removals, duplicate key, or rehash)");
            assertEquals(7, size.get(hashSet), "add method is not working correctly (check size)");

            hashSet.add("5");	//hashes to 9
            Object[] expectedData9 = {"3","4","5",null,null,"1","2",REMOVED.get(hashSet),"13","12","22"};
            assertArrayEquals((Object[])elementData.get(hashSet), expectedData9, "add method is not working correctly (tested with collision but without wrapping, removals, duplicate key, or rehash)");
            assertEquals(8, size.get(hashSet), "add method is not working correctly (check size)");

            hashSet.add("6");	//hashes to 10
            Object[] expectedData10 = {"3","4","5","6",null,"1","2",REMOVED.get(hashSet),"13","12","22"};
            assertArrayEquals((Object[])elementData.get(hashSet), expectedData10, "add method is not working correctly (tested with collision and duplicate key, but without wrapping, removals, or rehash)");
            assertEquals(9, size.get(hashSet), "add method is not working correctly (check size)");

            //put duplicate Object - no rehash
            hashSet.add("2");   //hashes to 6
            assertArrayEquals((Object[])elementData.get(hashSet), expectedData10, "add method is not working correctly (tested with collision and duplicate key, but without wrapping, removals, or rehash)");
            assertEquals(9, size.get(hashSet), "add method is not working correctly (check size)");

            //put with rehash and removal
            hashSet.add("7");	//hashes to 4
            Object[] expectedData11 = {null, null, null, "1", "2", "3", "4", "5", "6", "13", "12", "7", null, "22", null, null, null, null, null, null, null, null, null};
            assertArrayEquals((Object[])elementData.get(hashSet), expectedData11, "add method (and/or rehash) is not working correctly (tested with collision, removals, and rehash but without wrapping)");
            assertEquals(10, size.get(hashSet), "add method (and/or rehash) is not working correctly (check size)");
        } catch (Exception e) {
            e.printStackTrace();
            fail("add method (and/or rehash) is not working correctly");
        }
    }

    @Test
    public void testRemove() {
        try {
            hashSet.remove("2");
            //[2,13,12,n,n,n,n,n,n,1]
            Object[] newData = {"2", "13", "12", null, null, null, null, null, null, "1"};
            elementData.set(hashSet, newData);
            size.set(hashSet, 4);

            hashSet.remove("2");
            newData[0] = REMOVED.get(hashSet);
            assertArrayEquals(newData, (Object[])elementData.get(hashSet), "remove method is not working correctly (check array contents)");
            assertEquals(3, size.get(hashSet), "remove method is not working correctly (check size)");

            hashSet.remove("13");
            newData[1] = REMOVED.get(hashSet);
            assertArrayEquals(newData, (Object[])elementData.get(hashSet), "remove method is not working correctly (check array contents)");
            assertEquals(2, size.get(hashSet), "remove method is not working correctly (check size)");

            hashSet.remove("1");
            newData[9] = REMOVED.get(hashSet);
            assertArrayEquals(newData, (Object[])elementData.get(hashSet), "remove method is not working correctly (check array contents)");
            assertEquals(1, size.get(hashSet), "remove method is not working correctly (check size)");

            hashSet.remove("12");
            newData[2] = REMOVED.get(hashSet);
            assertArrayEquals(newData, (Object[])elementData.get(hashSet), "remove method is not working correctly (check array contents)");
            assertEquals(0, size.get(hashSet), "remove method is not working correctly (check size)");
        } catch (Exception e) {
            e.printStackTrace();
            fail("remove method is not working correctly");
        }
    }

    @Test
    public void testIterator() {
        try {
            Iterator<String> itr = hashSet.iterator();
            assertFalse(itr.hasNext(), "iterator is not working correctly (check hasNext with empty set)");

            Object[] data = {"2", null, null, null, null, null, null, null, null, null};
            size.set(hashSet, 1);
            elementData.set(hashSet, data);
            itr = hashSet.iterator();
            assertTrue(itr.hasNext(), "iterator is not working correctly (check hasNext with set of size 1)");
            assertEquals("2", itr.next(), "iterator is not working correctly (check next with set of size 1)");
            assertFalse(itr.hasNext(), "iterator is not working correctly (check hasNext when iterator has returned 'last' element)");

            Object[] data2 = {"2", REMOVED.get(hashSet), "12", null, null, null, null, null, null, "1"};
            size.set(hashSet, 3);
            elementData.set(hashSet, data2);
            itr = hashSet.iterator();
            assertTrue(itr.hasNext(), "iterator is not working correctly (check hasNext with non-empty set)");
            assertEquals("2", itr.next(), "iterator is not working correctly (check next with non-empty set)");
            assertTrue(itr.hasNext(), "iterator is not working correctly (check hasNext with non-empty set)");
            assertEquals("12", itr.next(), "iterator is not working correctly (check next with non-empty set)");
            assertTrue(itr.hasNext(), "iterator is not working correctly (check hasNext with non-empty set)");
            assertEquals("1", itr.next(), "iterator is not working correctly (check next with non-empty set)");
            assertFalse(itr.hasNext(), "iterator is not working correctly (check hasNext when iterator has returned 'last' element)");
        } catch (Exception e) {
            e.printStackTrace();
            fail("iterator is not working correctly");
        }
    }

    @Test
    public void testToArray() {
        try {
            Object[] data = {};
            assertArrayEquals(data, hashSet.toArray(), "toArray method is not working correctly (check when empty)");

            Object[] data2 = {"2", null, null, null, null, null, null, null, null, null};
            Object[] data3 = {"2"};
            size.set(hashSet, 1);
            elementData.set(hashSet, data2);
            assertArrayEquals(data3, hashSet.toArray(), "toArray method is not working correctly (check with single element)");

            Object[] data4 = {"2", REMOVED.get(hashSet), "12", null, null, null, null, null, null, "1"};
            Object[] data5 = {"2", "12", "1"};
            elementData.set(hashSet, data4);
            size.set(hashSet, 3);
            assertArrayEquals(data5, hashSet.toArray(), "toArray method is not working correctly (check with multiple elements)");
        } catch (Exception e) {
            e.printStackTrace();
            fail("toArray method is not working correctly");
        }
    }

    @Test
    public void testToString() {
        try {
            assertEquals("[]", hashSet.toString(), "toString method is not working correctly (check when empty)");

            Object[] data = {"2"};
            size.set(hashSet, 1);
            elementData.set(hashSet, data);
            assertEquals("[2]", hashSet.toString(), "toString method is not working correctly (check with single element)");

            Object[] newData = {"2", REMOVED.get(hashSet), "12", null, null, null, null, null, null, "1"};
            elementData.set(hashSet, newData);
            size.set(hashSet, 3);
            assertEquals("[2, 12, 1]", hashSet.toString(), "toString method is not working correctly (check with multiple elements)");
        } catch (Exception e) {
            e.printStackTrace();
            fail("toString method is not working correctly");
        }
    }

    @Test
    public void testHashFunction() {
        try {
            Random r = new Random();
            int capacity = 10;
            int randVal;
            for(int c=1; c<5; ++c) {
                Object[] data = new Object[capacity];
                elementData.set(hashSet, data);
                for(int i=0; i<100; ++i) {
                    randVal = r.nextInt();
                    assertEquals(h(randVal, capacity), hashFunction.invoke(hashSet, randVal), "hashFunction method is not working correctly (not returning correct value)");
                }
                capacity = capacity * 2;
            }
        } catch (Exception e) {
            fail("hashFunction method is not working correctly");
        }
    }

    private int h(Object k, int l) {
    return Math.abs(k.hashCode()) % l;
}
}
