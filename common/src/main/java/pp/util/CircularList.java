//----------------------------------------
// Programming project code
// UniBw M, 2022-2025
// www.unibw.de/inf2
// (c) Mark Minas (mark.minas@unibw.de)
//----------------------------------------

package pp.util;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Implementation of a doubly-linked circular list.
 *
 * @param <E> the element type.
 */
public class CircularList<E> implements Iterable<CircularList<E>.Entry> {
    /**
     * An entry in the list being linked to predecessor as well as successor and encapsulating an element.
     */
    public class Entry {
        private Entry prev;
        private Entry next;
        private final E elem;

        private Entry(E elem) {
            this.elem = elem;
        }

        /**
         * Returns this entry's predecessor in the list.
         */
        public Entry getPrev() {
            return prev;
        }

        /**
         * Returns this entry's successor in the list.
         */
        public Entry getNext() {
            return next;
        }

        /**
         * Returns this entry's encapsulated element.
         */
        public E getElem() {
            return elem;
        }

        /**
         * Deletes this entry from the list. It is an error to
         * further use this entry after deletion.
         */
        public void delete() {
            if (next == null)
                throw new IllegalStateException("bucket already deleted");
            prev.next = next;
            next.prev = prev;
            size--;
            if (size == 0)
                head = null;
            else if (head == this)
                head = next;
            next = prev = null;
            version++;
        }

        /**
         * Adds a new element to the list that becomes the successor of this entry and
         * the predecessor of the current successor of this entry.
         *
         * @param elem an element
         * @return the entry of the new element.
         */
        public Entry addAfter(E elem) {
            final Entry entry = new Entry(elem);
            if (size == 0)
                throw new IllegalStateException("size is 0 although head is not null");
            entry.next = next;
            entry.prev = this;
            next.prev = entry;
            next = entry;
            version++;
            size++;
            return entry;
        }

        /**
         * Adds a new element to the list that becomes the predecessor of this entry and
         * the successor of the current predecessor of this entry.
         *
         * @param elem an element
         * @return the entry of the new element.
         */
        public Entry addBefore(E elem) {
            final Entry entry = new Entry(elem);
            if (size == 0)
                throw new IllegalStateException("size is 0 although head is not null");
            entry.next = this;
            entry.prev = prev;
            prev.next = entry;
            prev = entry;
            version++;
            size++;
            return entry;
        }
    }

    private int version = 0;
    private int size = 0;
    private Entry head = null;

    /**
     * Creates an empty list.
     */
    public CircularList() {
        // empty
    }

    /**
     * Adds the specified element to this list. If the element is added to the empty list,
     * the list then contains just this element. Otherwise, it becomes the predecessor of
     * the current head of th list.
     *
     * @param elem an element
     * @return the entry of the new element.
     */
    public Entry add(E elem) {
        final Entry entry = new Entry(elem);
        if (head != null)
            return head.addBefore(elem);
        if (size != 0)
            throw new IllegalStateException("size is not 0 although head is null");
        head = entry.prev = entry.next = entry;
        size++;
        version++;
        return entry;
    }

    /**
     * Adds all elements of the specified collection to this list by calling
     * {@linkplain #add(Object)} for each of the elements.
     */
    public void addAll(Iterable<? extends E> elems) {
        elems.forEach(this::add);
    }

    /**
     * Returns the first entry of this list if the list is not empty, and null otherwise.
     */
    public Entry first() {
        return head;
    }

    /**
     * Returns the number of entries of this list.
     */
    public int size() {
        return size;
    }

    /**
     * Returns true if this list is empty, that is, if it contains no entries.
     */
    public boolean isEmpty() {
        return size == 0;
    }

    /**
     * Returns an iterator over the entries of this list, starting
     * at the first entry ({@linkplain #first()} and then following
     * the chain of successors.
     *
     * @return an Iterator.
     */
    @Override
    public Iterator<Entry> iterator() {
        return new Iterator<>() {
            private final int startVersion = version;
            private int remaining = size;
            private Entry cur = head;

            @Override
            public boolean hasNext() {
                return remaining > 0;
            }

            @Override
            public Entry next() {
                if (remaining <= 0)
                    throw new NoSuchElementException();
                if (version != startVersion)
                    throw new ConcurrentModificationException();
                final Entry result = cur;
                cur = cur.next;
                remaining--;
                return result;
            }
        };
    }

    /**
     * Returns a string representation of this list, starting
     * at the first entry ({@linkplain #first()} and then following
     * the chain of successors.
     *
     * @return a string representation of this list
     */
    @Override
    public String toString() {
        final StringBuilder b = new StringBuilder("[");
        final Iterator<Entry> it = iterator();
        while (it.hasNext()) {
            b.append(it.next().elem);
            if (it.hasNext())
                b.append(", ");
        }
        b.append(']');
        return b.toString();
    }

    /**
     * Returns a new list containing the elements of this list.
     * The list contains the elements starting
     * at the first entry ({@linkplain #first()} and then following
     * the chain of successors.
     *
     * @return a list.
     */
    public List<E> toList() {
        final List<E> list = new ArrayList<>(size);
        for (Entry e : this)
            list.add(e.elem);
        return list;
    }
}
