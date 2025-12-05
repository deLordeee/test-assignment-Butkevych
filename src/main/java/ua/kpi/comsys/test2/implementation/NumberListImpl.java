package ua.kpi.comsys.test2.implementation;

import ua.kpi.comsys.test2.NumberList;

import java.io.*;
import java.math.BigInteger;
import java.util.*;

/**
 * Custom implementation of INumberList interface.
 * Represents a number in octal (base-8) system using a circular doubly-linked list.
 *
 * Number: 2
 * C3 = 2 % 3 = 2 -> Кільцевий двонаправлений
 * C5 = 2 % 5 = 2 -> вісімкова
 * Доп: (2+1) mod 5 = 3 -> десяткова
 * C7 = 2 % 7 = 2 ->Множення двох чисел
 *
 * @author Butkevych Yevhenii
 * @version 1.0
 */
public class NumberListImpl implements NumberList {

    /**
     * Node class for circular doubly-linked list
     */
    private static class Node {
        byte data;
        Node next;
        Node prev;

        Node(byte data) {
            this.data = data;
        }
    }

    private Node head;
    private Node tail;
    private int size;

    /**
     * Default constructor. Returns empty <tt>NumberListImpl</tt>
     */
    public NumberListImpl() {
        head = null;
        tail = null;
        size = 0;
    }

    /**
     * Constructs new <tt>NumberListImpl</tt> by <b>decimal</b> number
     * from file, defined in string format.
     *
     * @param file - file where number is stored.
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

        }
    }

    /**
     * Constructs new <tt>NumberListImpl</tt> by <b>decimal</b> number
     * in string notation.
     *
     * @param value - number in string notation.
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
     * Saves the number, stored in the list, into specified file
     * in <b>decimal</b> scale of notation.
     *
     * @param file - file where number has to be stored.
     */
    public void saveList(File file) {
        try (PrintWriter writer = new PrintWriter(file)) {
            writer.print(toDecimalString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns student's record book number, which has 4 decimal digits.
     *
     * @return student's record book number.
     */
    public static int getRecordBookNumber() {
        return 2; // Your record book number
    }

    /**
     * Returns new <tt>NumberListImpl</tt> which represents the same number
     * in decimal (base-10) system.
     *
     * Does not impact the original list.
     *
     * @return <tt>NumberListImpl</tt> in decimal scale.
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
     * Returns new <tt>NumberListImpl</tt> which represents the result of
     * multiplication operation.<p>
     *
     * Does not impact the original list.
     *
     * @param arg - second argument of multiplication operation
     *
     * @return result of multiplication operation.
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
     * Returns string representation of number, stored in the list
     * in <b>decimal</b> scale of notation.
     *
     * @return string representation in <b>decimal</b> scale.
     */
    public String toDecimalString() {
        if (isEmpty()) return "0";


        BigInteger decimal = new BigInteger(toString(), 8);
        return decimal.toString(10);
    }

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

    @Override
    public int hashCode() {
        return Objects.hash(toString());
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
        if (!(o instanceof Byte)) return false;

        byte value = (Byte) o;
        Node current = head;
        for (int i = 0; i < size; i++) {
            if (current.data == value) return true;
            current = current.next;
        }
        return false;
    }

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

    @Override
    public <T> T[] toArray(T[] a) {

        throw new UnsupportedOperationException();
    }

    @Override
    public boolean add(Byte e) {
        add(size, e);
        return true;
    }

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

    @Override
    public boolean containsAll(Collection<?> c) {
        for (Object obj : c) {
            if (!contains(obj)) return false;
        }
        return true;
    }

    @Override
    public boolean addAll(Collection<? extends Byte> c) {
        boolean modified = false;
        for (Byte b : c) {
            add(b);
            modified = true;
        }
        return modified;
    }

    @Override
    public boolean addAll(int index, Collection<? extends Byte> c) {
        checkIndexForAdd(index);

        int i = index;
        for (Byte b : c) {
            add(i++, b);
        }
        return !c.isEmpty();
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        boolean modified = false;
        Iterator<?> it = iterator();
        while (it.hasNext()) {
            if (c.contains(it.next())) {
                it.remove();
                modified = true;
            }
        }
        return modified;
    }

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

    @Override
    public void clear() {
        head = null;
        tail = null;
        size = 0;
    }

    @Override
    public Byte get(int index) {
        checkIndex(index);
        return getNode(index).data;
    }

    @Override
    public Byte set(int index, Byte element) {
        checkIndex(index);
        Node node = getNode(index);
        byte oldValue = node.data;
        node.data = element;
        return oldValue;
    }

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

    @Override
    public Byte remove(int index) {
        checkIndex(index);
        Node node = getNode(index);
        removeNode(node);
        return node.data;
    }

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

    @Override
    public ListIterator<Byte> listIterator() {
        return listIterator(0);
    }

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

    @Override
    public void sortAscending() {
        if (size <= 1) return;

        // Convert to array, sort, then rebuild list
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

    @Override
    public void shiftLeft() {
        if (size > 1) {
            head = head.next;
            tail = tail.next;
        }
    }

    @Override
    public void shiftRight() {
        if (size > 1) {
            head = head.prev;
            tail = tail.prev;
        }
    }



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
