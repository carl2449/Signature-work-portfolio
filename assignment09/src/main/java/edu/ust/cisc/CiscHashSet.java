package edu.ust.cisc;

import java.util.ArrayList;
import java.util.Iterator;

public class CiscHashSet<E> implements CiscCollection<E> {

    private static final double MAX_LOAD_FACTOR = 0.75;
    private Object[] elementData;
    private int size;
    private static final Object REMOVED = new Object();

    public CiscHashSet() {
        elementData = new Object[11];
        size = 0;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public boolean contains(Object o) {
        int bucket = hashFunction(o);
        Object current = elementData[bucket];

        while (current == REMOVED || (current != null && !current.equals(o))) {
            bucket = (bucket + 1) % elementData.length;
            current = elementData[bucket];
        }
        return current != null;
    }

    @Override
    public Iterator<E> iterator() {
        return new CiscHashSetIterator();
    }

    @Override
    public Object[] toArray() {
        ArrayList<Object> list = new ArrayList<>();
        Object[] toReturn;
        for (int i = 0; i < elementData.length; i++) {
            if (elementData[i] != null && elementData[i] != REMOVED) {
                list.add(elementData[i]);
            }
        }
        toReturn = list.toArray();
        return toReturn;
    }

    @Override
    public void clear() {
        for (int i = 0; i < elementData.length; i++) {
            if (elementData[i] != null) {
                elementData[i] = null;
            }
        }
        size = 0;
    }

    public void add(E value) {
        int bucket = hashFunction(value);
        Object current = elementData[bucket];
        double ratio;

        while (current == REMOVED || current != null) {
            if (current.equals(value)) {
                return;
            }

            bucket = (bucket + 1) % elementData.length;
            current = elementData[bucket];
        }

        /*while (current == REMOVED || (current != null && !current.equals(value))) {
            bucket = (bucket + 1) % elementData.length;
            current = elementData[bucket];
        }*/

        ratio = ((double) size) / elementData.length;
        if ((ratio) >= MAX_LOAD_FACTOR) {
            rehash();
            add(value);
            return;
        }
        elementData[bucket] = value;
        size = size + 1;

    }

    public void remove(E value) {
        int bucket = hashFunction(value);
        Object current = elementData[bucket];

        while (current == REMOVED || (current != null && !current.equals(value))) {
            bucket = (bucket + 1) % elementData.length;
            current = elementData[bucket];
        }

        if (current != null) {
            elementData[bucket] = REMOVED;
            size = size - 1;
        }
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        sb.append("[");
        if (!isEmpty()) {
            for (int i = 0; i < elementData.length; i++) {
                if (elementData[i] != null & elementData[i] != REMOVED) {
                    if (first != true) {
                        sb.append(", ");
                    }
                    sb.append(elementData[i]);
                    first = false;
                }
            }
        }
        sb.append("]");
        return sb.toString();
    }

    private int hashFunction(Object value) {
        return Math.abs(value.hashCode()) % elementData.length;
    }

    private void rehash() {
        int newCapacity = (elementData.length * 2) + 1;
        Object[] oldArray = elementData;
        int theSize = this.size;
        elementData = new Object[newCapacity];

        for (int i = 0; i < oldArray.length; i++) {
            if (oldArray[i] != null && oldArray[i] != REMOVED) {
                add((E) oldArray[i]);
            }
        }
        this.size = theSize;
    }

    private class CiscHashSetIterator implements Iterator<E> {

        private int nextIndex;

        public CiscHashSetIterator() {
            nextIndex = getNext(0);
        }

        @Override
        public boolean hasNext() {
            return nextIndex != -1;
        }

        @Override
        public E next() {
            E toReturn = (E) elementData[nextIndex];
            nextIndex = getNext(nextIndex+1);
            return toReturn;
        }

        private int getNext(int index) {
            for (int i = index; i < elementData.length; i++) {
                if (elementData[i] != null && elementData[i] != REMOVED) {
                    return i;
                }
            }
            return -1;
        }
    }
}