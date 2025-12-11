package ua.kpi.comsys.test2.implementation;

import ua.kpi.comsys.test2.NumberList;

import java.io.*;
import java.math.BigInteger;
import java.util.*;

/**
 * Custom implementation of INumberList interface.
 * Represents a number in octal (base-8) system using a circular doubly-linked list.
 * <p>
 * Each node in the list stores a single octal digit (0-7). The list maintains
 * circular references where the tail's next points to head and head's previous
 * points to tail, enabling efficient operations in both directions.
 * </p>
 *
 * <p><strong>Assignment Parameters:</strong></p>
 * <ul>
 * <li>Number: 2</li>
 * <li>Group: IM-34</li>
 * <li>C3 = 2 % 3 = 2 → Кільцевий двонаправлений</li>
 * <li>C5 = 2 % 5 = 2 → вісімкова</li>
 * <li>Доп: (2+1) mod 5 = 3 → десяткова</li>
 * <li>C7 = 2 % 7 = 2 → Множення двох чисел</li>
 * </ul>
 *
 * @author Butkevych Yevhenii
 * @version 1.0
 */
public class NumberListImpl implements NumberList {

    /**
     * Node class for circular doubly-linked list.
     * Each node stores a single octal digit and maintains references
     * to both the next and previous nodes in the circular structure.
     */
    private static class Node {
        /** The octal digit stored in this node (0-7) */
        byte data;

        /** Reference to the next node in the circular list */
        Node next;

        /** Reference to the previous node in the circular list */
        Node prev;

        /**
         * Constructs a new node with the specified data.
         *
         * @param data the octal digit to store in this node
         */
        Node(byte data) {
            this.data = data;
        }
    }

    /** Reference to the first node in the circular list */
    private Node head;

    /** Reference to the last node in the circular list */
    private Node tail;

    /** The number of nodes currently in the list */
    private int size;

    /**
     * Default constructor. Creates an empty NumberListImpl representing zero.
     */
    public NumberListImpl() {
        head = null;
        tail = null;
        size = 0;
    }

    /**
     * Constructs a new NumberListImpl by reading a decimal number from a file.
     * The file should contain a single line with a decimal number in string format.
     * The number is automatically converted to octal representation internally.
     *
     * @param file the file containing a decimal number
     * @throws IllegalArgumentException if the file contains invalid number format
     */
    public NumberListImpl(File file) {
        this();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line = reader.readLine();
            if (line != null && line.matches("\\d+")) {
                BigInteger decimal = new BigInteger(line);
                String octalStr = decimal.toString(8);
                for (char c : octalStr.toCharArray()) {
                    add(Byte.parseByte(String.valueOf(c)));
                }
            }
        } catch (IOException e) {
            // Silent failure - list remains empty
        }
    }

    /**
     * Constructs a new NumberListImpl from a decimal number in string notation.
     * The input string is converted from decimal to octal representation internally.
     *
     * <p><strong>Example:</strong></p>
     * <pre>
     * NumberListImpl num = new NumberListImpl("64");
     * // Internal representation: "100" in octal (1*64 + 0*8 + 0*1 = 64)
     * </pre>
     *
     * @param value the decimal number as a string; must contain only digits
     * @throws NumberFormatException if the string contains non-digit characters
     */
    public NumberListImpl(String value) {
        this();
        if (value != null && value.matches("\\d+")) {
            BigInteger decimal = new BigInteger(value);
            String octalStr = decimal.toString(8);
            for (char c : octalStr.toCharArray()) {
                add(Byte.parseByte(String.valueOf(c)));
            }
        }
    }

    /**
     * Saves the number stored in this list to the specified file in decimal notation.
     * The octal number is converted to decimal before writing.
     *
     * @param file the file where the decimal number will be saved
     * @throws IOException if an I/O error occurs while writing to the file
     */
    public void saveList(File file) {
        try (PrintWriter writer = new PrintWriter(file)) {
            writer.print(toDecimalString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns the student's record book number.
     * This method is required by the assignment specification.
     *
     * @return the student's record book number (4 decimal digits)
     */
    public static int getRecordBookNumber() {
        return 2;
    }

    /**
     * Converts this octal number to its decimal (base-10) representation.
     * Creates a new NumberListImpl where each digit represents a decimal digit
     * instead of an octal digit. The original list remains unchanged.
     *
     * <p><strong>Example:</strong></p>
     * <pre>
     * NumberListImpl octal = new NumberListImpl("8"); // stores as "10" in octal
     * NumberListImpl decimal = octal.changeScale();   // returns "8" in decimal
     * </pre>
     *
     * @return a new NumberListImpl containing the decimal representation; never null
     */
    public NumberListImpl changeScale() {
        String octalString = this.toString();
        BigInteger decimalValue;

        if (octalString.isEmpty()) {
            decimalValue = BigInteger.ZERO;
        } else {
            decimalValue = new BigInteger(octalString, 8);
        }

        NumberListImpl result = new NumberListImpl();
        if (decimalValue.equals(BigInteger.ZERO)) {
            result.add((byte) 0);
            return result;
        }

        String decimalStr = decimalValue.toString(10);
        for (char c : decimalStr.toCharArray()) {
            result.add(Byte.parseByte(String.valueOf(c)));
        }

        return result;
    }

    /**
     * Performs multiplication of this number with another number.
     * Both numbers are treated as octal, converted to decimal for multiplication,
     * and the result is returned as a new octal NumberListImpl.
     * The original lists remain unchanged.
     *
     * <p><strong>Example:</strong></p>
     * <pre>
     * NumberListImpl a = new NumberListImpl("8");  // 10 in octal
     * NumberListImpl b = new NumberListImpl("8");  // 10 in octal
     * NumberListImpl result = a.additionalOperation(b);
     * // result is 100 in octal (8 * 8 = 64 decimal = 100 octal)
     * </pre>
     *
     * @param arg the second argument of the multiplication operation; must not be null
     * @return a new NumberListImpl representing the product in octal; never null
     * @throws NullPointerException if arg is null
     * @throws ClassCastException if arg is not an instance of NumberListImpl
     */
    public NumberListImpl additionalOperation(NumberList arg) {
        BigInteger thisDecimal = new BigInteger(this.toDecimalString());
        BigInteger argDecimal = new BigInteger(((NumberListImpl)arg).toDecimalString());
        BigInteger resultDecimal = thisDecimal.multiply(argDecimal);

        String resultOctal = resultDecimal.toString(8);
        NumberListImpl result = new NumberListImpl();

        for (char c : resultOctal.toCharArray()) {
            result.add(Byte.parseByte(String.valueOf(c)));
        }

        return result;
    }

    /**
     * Converts the octal number stored in this list to its decimal string representation.
     *
     * @return the decimal string representation of this number; never null
     */
    public String toDecimalString() {
        if (isEmpty()) return "0";
        BigInteger decimal = new BigInteger(toString(), 8);
        return decimal.toString(10);
    }

    /**
     * Returns the string representation of this number in octal notation.
     * Each digit in the returned string represents an octal digit.
     *
     * @return the octal string representation; "0" if the list is empty
     */
    @Override
    public String toString() {
        if (isEmpty()) return "0";

        StringBuilder sb = new StringBuilder();
        Node current = head;
        do {
            sb.append(current.data);
            current = current.next;
        } while (current != head);

        return sb.toString();
    }

    /**
     * Compares this NumberListImpl with another object for equality.
     * Two NumberListImpl objects are equal if they have the same size
     * and contain the same digits in the same order.
     *
     * @param o the object to compare with
     * @return true if the objects are equal, false otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof NumberListImpl)) return false;

        NumberListImpl that = (NumberListImpl) o;
        if (size != that.size) return false;

        Node thisCurrent = head;
        Node thatCurrent = that.head;

        for (int i = 0; i < size; i++) {
            if (thisCurrent.data != thatCurrent.data) return false;
            thisCurrent = thisCurrent.next;
            thatCurrent = thatCurrent.next;
        }

        return true;
    }

    /**
     * Returns the hash code value for this list.
     * The hash code is computed based on the string representation of the number.
     *
     * @return the hash code value for this list
     */
    @Override
    public int hashCode() {
        return Objects.hash(toString());
    }

    /**
     * Returns the number of elements in this list.
     *
     * @return the number of octal digits in this list
     */
    @Override
    public int size() {
        return size;
    }

    /**
     * Returns true if this list contains no elements.
     *
     * @return true if this list is empty, false otherwise
     */
    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    /**
     * Returns true if this list contains the specified element.
     *
     * @param o the element whose presence is to be tested
     * @return true if this list contains the specified element
     */
    @Override
    public boolean contains(Object o) {
        if (!(o instanceof Byte)) return false;

        byte value = (Byte) o;
        Node current = head;

        for (int i = 0; i < size; i++) {
            if (current.data == value) return true;
            current = current.next;
        }

        return false;
    }

    /**
     * Returns an iterator over the elements in this list in proper sequence.
     * The iterator traverses from head to tail.
     *
     * @return an iterator over the elements in this list
     */
    @Override
    public Iterator<Byte> iterator() {
        return new Iterator<Byte>() {
            private Node current = head;
            private int index = 0;

            @Override
            public boolean hasNext() {
                return index < size;
            }

            @Override
            public Byte next() {
                if (!hasNext()) throw new NoSuchElementException();
                byte data = current.data;
                current = current.next;
                index++;
                return data;
            }
        };
    }

    /**
     * Returns an array containing all elements in this list in proper sequence.
     *
     * @return an array containing all elements in this list
     */
    @Override
    public Object[] toArray() {
        Object[] array = new Object[size];
        Node current = head;

        for (int i = 0; i < size; i++) {
            array[i] = current.data;
            current = current.next;
        }

        return array;
    }

    /**
     * This operation is not supported.
     *
     * @param a the array (not used)
     * @return never returns normally
     * @throws UnsupportedOperationException always
     */
    @Override
    public <T> T[] toArray(T[] a) {
        throw new UnsupportedOperationException();
    }

    /**
     * Appends the specified element to the end of this list.
     *
     * @param e the element to be appended
     * @return true (as specified by Collection.add)
     */
    @Override
    public boolean add(Byte e) {
        add(size, e);
        return true;
    }

    /**
     * Removes the first occurrence of the specified element from this list.
     * If the element is not present, the list remains unchanged.
     *
     * @param o the element to be removed
     * @return true if this list contained the specified element
     */
    @Override
    public boolean remove(Object o) {
        if (!(o instanceof Byte)) return false;

        byte value = (Byte) o;
        Node current = head;

        for (int i = 0; i < size; i++) {
            if (current.data == value) {
                removeNode(current);
                return true;
            }
            current = current.next;
        }

        return false;
    }

    /**
     * Returns true if this list contains all elements in the specified collection.
     *
     * @param c the collection to be checked for containment
     * @return true if this list contains all elements in the collection
     * @throws NullPointerException if the specified collection is null
     */
    @Override
    public boolean containsAll(Collection<?> c) {
        for (Object obj : c) {
            if (!contains(obj)) return false;
        }
        return true;
    }

    /**
     * Appends all elements in the specified collection to the end of this list.
     *
     * @param c the collection containing elements to be added
     * @return true if this list changed as a result of the call
     * @throws NullPointerException if the specified collection is null
     */
    @Override
    public boolean addAll(Collection<? extends Byte> c) {
        boolean modified = false;
        for (Byte b : c) {
            add(b);
            modified = true;
        }
        return modified;
    }

    /**
     * Inserts all elements in the specified collection into this list at the specified position.
     *
     * @param index the index at which to insert the first element
     * @param c the collection containing elements to be added
     * @return true if this list changed as a result of the call
     * @throws IndexOutOfBoundsException if the index is out of range
     * @throws NullPointerException if the specified collection is null
     */
    @Override
    public boolean addAll(int index, Collection<? extends Byte> c) {
        checkIndexForAdd(index);
        int i = index;
        for (Byte b : c) {
            add(i++, b);
        }
        return !c.isEmpty();
    }

    /**
     * Removes from this list all elements that are contained in the specified collection.
     *
     * @param c the collection containing elements to be removed
     * @return true if this list changed as a result of the call
     * @throws NullPointerException if the specified collection is null
     */
    @Override
    public boolean removeAll(Collection<?> c) {
        boolean modified = false;
        Iterator<Byte> it = iterator();

        while (it.hasNext()) {
            if (c.contains(it.next())) {
                it.remove();
                modified = true;
            }
        }

        return modified;
    }

    /**
     * Retains only the elements in this list that are contained in the specified collection.
     *
     * @param c the collection containing elements to be retained
     * @return true if this list changed as a result of the call
     * @throws NullPointerException if the specified collection is null
     */
    @Override
    public boolean retainAll(Collection<?> c) {
        boolean modified = false;
        Iterator<Byte> it = iterator();

        while (it.hasNext()) {
            if (!c.contains(it.next())) {
                it.remove();
                modified = true;
            }
        }

        return modified;
    }

    /**
     * Removes all elements from this list. The list will be empty after this call.
     */
    @Override
    public void clear() {
        head = null;
        tail = null;
        size = 0;
    }

    /**
     * Returns the element at the specified position in this list.
     *
     * @param index the index of the element to return
     * @return the element at the specified position
     * @throws IndexOutOfBoundsException if the index is out of range
     */
    @Override
    public Byte get(int index) {
        checkIndex(index);
        return getNode(index).data;
    }

    /**
     * Replaces the element at the specified position with the specified element.
     *
     * @param index the index of the element to replace
     * @param element the element to be stored at the specified position
     * @return the element previously at the specified position
     * @throws IndexOutOfBoundsException if the index is out of range
     */
    @Override
    public Byte set(int index, Byte element) {
        checkIndex(index);
        Node node = getNode(index);
        byte oldValue = node.data;
        node.data = element;
        return oldValue;
    }

    /**
     * Inserts the specified element at the specified position in this list.
     * Shifts the element currently at that position (if any) and any subsequent
     * elements to the right.
     *
     * @param index the index at which the specified element is to be inserted
     * @param element the element to be inserted
     * @throws IndexOutOfBoundsException if the index is out of range
     */
    @Override
    public void add(int index, Byte element) {
        checkIndexForAdd(index);
        Node newNode = new Node(element);

        if (isEmpty()) {
            head = newNode;
            tail = newNode;
            head.next = head;
            head.prev = head;
        } else if (index == 0) {
            newNode.next = head;
            newNode.prev = tail;
            head.prev = newNode;
            tail.next = newNode;
            head = newNode;
        } else if (index == size) {
            newNode.next = head;
            newNode.prev = tail;
            tail.next = newNode;
            head.prev = newNode;
            tail = newNode;
        } else {
            Node current = getNode(index);
            Node prev = current.prev;
            prev.next = newNode;
            newNode.prev = prev;
            newNode.next = current;
            current.prev = newNode;
        }

        size++;
    }

    /**
     * Removes the element at the specified position in this list.
     * Shifts any subsequent elements to the left.
     *
     * @param index the index of the element to be removed
     * @return the element that was removed from the list
     * @throws IndexOutOfBoundsException if the index is out of range
     */
    @Override
    public Byte remove(int index) {
        checkIndex(index);
        Node node = getNode(index);
        removeNode(node);
        return node.data;
    }

    /**
     * Returns the index of the first occurrence of the specified element,
     * or -1 if this list does not contain the element.
     *
     * @param o the element to search for
     * @return the index of the first occurrence, or -1 if not found
     */
    @Override
    public int indexOf(Object o) {
        if (!(o instanceof Byte)) return -1;

        byte value = (Byte) o;
        Node current = head;

        for (int i = 0; i < size; i++) {
            if (current.data == value) return i;
            current = current.next;
        }

        return -1;
    }

    /**
     * Returns the index of the last occurrence of the specified element,
     * or -1 if this list does not contain the element.
     *
     * @param o the element to search for
     * @return the index of the last occurrence, or -1 if not found
     */
    @Override
    public int lastIndexOf(Object o) {
        if (!(o instanceof Byte)) return -1;

        byte value = (Byte) o;
        Node current = tail;

        for (int i = size - 1; i >= 0; i--) {
            if (current.data == value) return i;
            current = current.prev;
        }

        return -1;
    }

    /**
     * Returns a list iterator over the elements in this list (in proper sequence).
     *
     * @return a list iterator over the elements in this list
     */
    @Override
    public ListIterator<Byte> listIterator() {
        return listIterator(0);
    }

    /**
     * Returns a list iterator over the elements in this list (in proper sequence),
     * starting at the specified position in the list.
     *
     * @param index the index of the first element to be returned
     * @return a list iterator over the elements in this list
     * @throws IndexOutOfBoundsException if the index is out of range
     */
    @Override
    public ListIterator<Byte> listIterator(int index) {
        checkIndexForAdd(index);

        return new ListIterator<Byte>() {
            private Node nextNode = (index == size) ? head : getNode(index);
            private Node lastReturned = null;
            private int nextIndex = index;
            private int expectedModCount = 0;

            @Override
            public boolean hasNext() {
                return nextIndex < size;
            }

            @Override
            public Byte next() {
                if (!hasNext()) throw new NoSuchElementException();
                lastReturned = nextNode;
                nextNode = nextNode.next;
                nextIndex++;
                return lastReturned.data;
            }

            @Override
            public boolean hasPrevious() {
                return nextIndex > 0;
            }

            @Override
            public Byte previous() {
                if (!hasPrevious()) throw new NoSuchElementException();
                nextNode = nextNode.prev;
                nextIndex--;
                lastReturned = nextNode;
                return lastReturned.data;
            }

            @Override
            public int nextIndex() {
                return nextIndex;
            }

            @Override
            public int previousIndex() {
                return nextIndex - 1;
            }

            @Override
            public void remove() {
                if (lastReturned == null) throw new IllegalStateException();
                Node toRemove = lastReturned;

                if (toRemove == nextNode) {
                    nextNode = nextNode.next;
                } else {
                    nextIndex--;
                }

                removeNode(toRemove);
                lastReturned = null;
                expectedModCount++;
            }

            @Override
            public void set(Byte b) {
                if (lastReturned == null) throw new IllegalStateException();
                lastReturned.data = b;
            }

            @Override
            public void add(Byte b) {
                lastReturned = null;
                Node newNode = new Node(b);

                if (isEmpty()) {
                    head = newNode;
                    tail = newNode;
                    head.next = head;
                    head.prev = head;
                    nextNode = head;
                } else if (nextIndex == 0) {
                    newNode.next = head;
                    newNode.prev = tail;
                    head.prev = newNode;
                    tail.next = newNode;
                    head = newNode;
                    nextNode = head.next;
                } else if (nextIndex == size) {
                    newNode.next = head;
                    newNode.prev = tail;
                    tail.next = newNode;
                    head.prev = newNode;
                    tail = newNode;
                    nextNode = head;
                } else {
                    newNode.next = nextNode;
                    newNode.prev = nextNode.prev;
                    nextNode.prev.next = newNode;
                    nextNode.prev = newNode;
                    nextNode = newNode.next;
                }

                size++;
                nextIndex++;
                expectedModCount++;
            }
        };
    }

    /**
     * Returns a view of the portion of this list between the specified
     * fromIndex (inclusive) and toIndex (exclusive).
     *
     * @param fromIndex the low endpoint (inclusive) of the subList
     * @param toIndex the high endpoint (exclusive) of the subList
     * @return a view of the specified range within this list
     * @throws IndexOutOfBoundsException if the indices are out of range
     */
    @Override
    public List<Byte> subList(int fromIndex, int toIndex) {
        if (fromIndex < 0 || toIndex > size || fromIndex > toIndex) {
            throw new IndexOutOfBoundsException();
        }

        NumberListImpl subList = new NumberListImpl();
        Node current = getNode(fromIndex);

        for (int i = fromIndex; i < toIndex; i++) {
            subList.add(current.data);
            current = current.next;
        }

        return subList;
    }

    /**
     * Swaps the elements at the specified positions in this list.
     *
     * @param index1 the index of the first element
     * @param index2 the index of the second element
     * @return true if the swap was successful, false if indices are invalid
     */
    @Override
    public boolean swap(int index1, int index2) {
        if (index1 < 0 || index1 >= size || index2 < 0 || index2 >= size) {
            return false;
        }

        if (index1 == index2) return true;

        Node node1 = getNode(index1);
        Node node2 = getNode(index2);

        byte temp = node1.data;
        node1.data = node2.data;
        node2.data = temp;

        return true;
    }

    /**
     * Sorts the elements in this list in ascending order.
     * Uses the natural ordering of byte values.
     */
    @Override
    public void sortAscending() {
        if (size <= 1) return;

        byte[] array = new byte[size];
        Node current = head;

        for (int i = 0; i < size; i++) {
            array[i] = current.data;
            current = current.next;
        }

        Arrays.sort(array);

        current = head;
        for (int i = 0; i < size; i++) {
            current.data = array[i];
            current = current.next;
        }
    }

    /**
     * Sorts the elements in this list in descending order.
     * Uses the natural ordering of byte values in reverse.
     */
    @Override
    public void sortDescending() {
        if (size <= 1) return;

        byte[] array = new byte[size];
        Node current = head;

        for (int i = 0; i < size; i++) {
            array[i] = current.data;
            current = current.next;
        }

        Arrays.sort(array);

        for (int i = 0; i < size / 2; i++) {
            byte temp = array[i];
            array[i] = array[size - 1 - i];
            array[size - 1 - i] = temp;
        }

        current = head;
        for (int i = 0; i < size; i++) {
            current.data = array[i];
            current = current.next;
        }
    }

    /**
     * Shifts all elements in the list one position to the left in a circular manner.
     * The head element becomes the new tail, and the second element becomes the new head.
     * <p>
     * For example, if the list is [1, 2, 3, 4], after shiftLeft it becomes [2, 3, 4, 1].
     * </p>
     */
    @Override
    public void shiftLeft() {
        if (size > 1) {
            head = head.next;
            tail = tail.next;
        }
    }

    /**
     * Shifts all elements in the list one position to the right in a circular manner.
     * The tail element becomes the new head, and the head becomes the second element.
     * <p>
     * For example, if the list is [1, 2, 3, 4], after shiftRight it becomes [4, 1, 2, 3].
     * </p>
     */
    @Override
    public void shiftRight() {
        if (size > 1) {
            head = head.prev;
            tail = tail.prev;
        }
    }

    /**
     * Returns the node at the specified index.
     * Uses bidirectional traversal optimization: traverses from head if index
     * is in the first half, from tail if in the second half.
     *
     * @param index the index of the node to retrieve
     * @return the node at the specified index
     * @throws IndexOutOfBoundsException if the index is out of range
     */
    private Node getNode(int index) {
        checkIndex(index);

        if (index < size / 2) {
            Node current = head;
            for (int i = 0; i < index; i++) {
                current = current.next;
            }
            return current;
        } else {
            Node current = tail;
            for (int i = size - 1; i > index; i--) {
                current = current.prev;
            }
            return current;
        }
    }

    /**
     * Removes the specified node from the list.
     * Updates the circular references and head/tail pointers as needed.
     *
     * @param node the node to remove
     */
    private void removeNode(Node node) {
        if (size == 1) {
            head = null;
            tail = null;
        } else {
            node.prev.next = node.next;
            node.next.prev = node.prev;

            if (node == head) {
                head = node.next;
            }
            if (node == tail) {
                tail = node.prev;
            }
        }
        size--;
    }

    /**
     * Checks if the specified index is valid for element access operations.
     *
     * @param index the index to check
     * @throws IndexOutOfBoundsException if the index is out of range (index < 0 || index >= size)
     */
    private void checkIndex(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        }
    }

    private void checkIndexForAdd(int index) {
        if (index < 0 || index > size) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        }
    }
}
