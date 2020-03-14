package listimplementation;

import java.util.*;

public class ListImpl<T> implements List<T> {
    private static final int DEFAULT_SIZE = 64;
    private static final int MAX_SIZE = 1 << 30;

    private T[] a;
    private int size;


    public ListImpl() {
        this(DEFAULT_SIZE);
    }

    public ListImpl(int cap) {
        a = (T[]) new Object[sizeFor(cap)];
    }

    public ListImpl(Collection<? extends T> c) {
        a = (T[]) new Object[sizeFor(c.size())];
        int i = 0;
        for (T el: c) {
            a[i++] = el;
        }
        size = i;
    }


    private int sizeFor(int cap) {
        int n = -1 >>> Integer.numberOfLeadingZeros(cap - 1);
        return n < 0 ? 1 : (n >= MAX_SIZE ?  MAX_SIZE : n + 1);
    }

    private void resize(int cap) {
        a = Arrays.copyOf(a, sizeFor(cap + 1));
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
        return indexOf(o) != -1;
    }

    @Override
    public Iterator<T> iterator() {
        return new ListImplIterator(0);
    }

    @Override
    public Object[] toArray() {
        return new Object[0];
    }

    @Override
    public <T1> T1[] toArray(T1[] a) {
        if (a == null) throw new NullPointerException();
        if (a.length < size)
            return (T1[])Arrays.copyOf(this.a, size, a.getClass());

        for(int i = 0; i < size; i++) {
            a[i] = (T1)this.a[i];
        }
        if (a.length > size) a[size] = null;
        return a;
    }

    @Override
    public boolean add(T t) {
        if (t == null) throw new NullPointerException();
        if (a.length == size) resize(size);
        a[size++] = t;
        return true;
    }

    @Override
    public boolean remove(Object o) {
        if (o == null) throw new NullPointerException();
        int i = indexOf(o);
        System.arraycopy(a, i + 1, a, i, a.length - 1 - i);
        size--;
        return true;
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        for (Object el: c) {
            if (!contains(el)) return false;
        }
        return true;
    }

    @Override
    public boolean addAll(Collection<? extends T> c) {
        if (size + c.size() > a.length) resize(size + c.size());
        for (T el: c) a[size++] = el;
        return true;
    }

    @Override
    public boolean addAll(int index, Collection<? extends T> c) {
        if (index < 0 || index > size) throw new IndexOutOfBoundsException();
        if (index + c.size() >= a.length) resize(index + c.size());
        for (T el: c) a[index++] = el;
        size = index;
        return true;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        int index;
        for(Object el: c) {
            if ((index = indexOf(el)) != -1) remove(index);
        }
        return true;
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        Iterator<T> iter = iterator();
        while(iter.hasNext()) {
            if (!c.contains(iter.next())) iter.remove();
        }
        return true;
    }

    @Override
    public void clear() {
        size = 0;
    }

    @Override
    public T get(int index) {
        if (index < 0 || index >= size) throw new IndexOutOfBoundsException();
        return a[index];
    }

    @Override
    public T set(int index, T element) {
        if (element == null) throw new NullPointerException();
        if (index < 0 || index >= size) throw new IndexOutOfBoundsException();
        T oldElement = a[index];
        a[index] = element;
        return oldElement;
    }

    @Override
    public void add(int index, T element) {
        if (element == null) throw new NullPointerException();
        if (index < 0 || index > size) throw new IndexOutOfBoundsException();
        T[] srcArr = a;
        if (size == a.length) {
            resize(size);
            a = (T[]) new Object[sizeFor(size + 1)];
        }
        System.arraycopy(srcArr,0, a, 0, index);
        System.arraycopy(srcArr, index, a, index + 1, size - index - 1);
        a[index] = element;
        size++;
    }

    @Override
    public T remove(int index) {
        if (index < 0 || index >= size) throw new IndexOutOfBoundsException();
        T element = a[index];
        System.arraycopy(a, index + 1, a, index, a.length - 1 - index);
        size--;
        return element;
    }

    @Override
    public int indexOf(Object o) {
        if (o == null) throw new NullPointerException();
        for(int i = 0; i < size; i++)
            if (Objects.equals(a[i], o)) return i;
        return -1;
    }

    @Override
    public int lastIndexOf(Object o) {
        if (o == null) throw new NullPointerException();
        for(int i = size -1; i >= 0; i--)
            if (Objects.equals(a[i], o)) return i;
        return -1;
    }

    @Override
    public ListIterator<T> listIterator() {
        return new ListImplIterator(0);
    }

    @Override
    public ListIterator<T> listIterator(int index) {
        return new ListImplIterator(index);
    }

    @Override
    public List<T> subList(int fromIndex, int toIndex) {
        List<T> subList = new ListImpl<>(toIndex - fromIndex);
        for(int i = fromIndex; i < toIndex; i++) {
            subList.add(a[i]);
        }
        return subList;
    }

    public class ListImplIterator implements ListIterator<T> {
        private static final int LAST_INDEX_UNDEFINED = -1;
        private  int lastIndex;
        private int index;

        public ListImplIterator(int index) {
            if (index < 0 || index > size) throw new IndexOutOfBoundsException();
            this.index = index;
            this.lastIndex = LAST_INDEX_UNDEFINED;
        }

        @Override
        public boolean hasNext() {
            return index < size;
        }

        @Override
        public T next() {
            if (!hasNext()) throw new NoSuchElementException();
            lastIndex = index++;
            return a[lastIndex];
        }

        @Override
        public boolean hasPrevious() {
            return index > 0;
        }

        @Override
        public T previous() {
            if (!hasPrevious()) throw new NoSuchElementException();
            lastIndex = --index;
            return a[lastIndex];
        }

        @Override
        public int nextIndex() {
            return index;
        }

        @Override
        public int previousIndex() {
            return index - 1;
        }

        @Override
        public void remove() {
            if (lastIndex == LAST_INDEX_UNDEFINED) throw new IllegalStateException();
            ListImpl.this.remove(lastIndex);
            index--;
            lastIndex = LAST_INDEX_UNDEFINED;
        }

        @Override
        public void set(T t) {
            if (lastIndex == LAST_INDEX_UNDEFINED) throw new IllegalStateException();
            ListImpl.this.set(lastIndex, t);
        }

        @Override
        public void add(T t) {
            ListImpl.this.add(index++, t);
            lastIndex = LAST_INDEX_UNDEFINED;
        }
    }
}
