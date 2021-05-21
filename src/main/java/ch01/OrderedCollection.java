package ch01;

@SuppressWarnings("rawtypes")
public class OrderedCollection<T> {

    private Comparable[] datas = new Comparable[16];
    private int size;

    public boolean isEmpty() {
        return datas == null || size == 0;
    }

    public void makeEmpty() {
        datas = new Comparable[16];
        size = 0;
    }

    public <T extends Comparable> void insert(T data) {
        if (size >= datas.length) {
            long newSize = size * 2L;
            if (newSize > Integer.MAX_VALUE) {
                newSize = Integer.MAX_VALUE;
            }
            Comparable[] newDatas = new Comparable[(int) newSize];
            System.arraycopy(datas, 0, newDatas, 0, size);
            datas = newDatas;
        }
        datas[size++] = data;
    }

    public boolean remove(T data) {
        int index = -1;
        if (data == null) {
            for (int i = 0; i < size; i++) {
                if (datas[i] == null) {
                    index = i;
                    break;
                }
            }
        } else {
            for (int i = 0; i < size; i++) {
                if (data.equals(datas[i])) {
                    index = i;
                    break;
                }
            }
        }
        if (index > -1) {
            if (index < size - 1) {
                Comparable[] newData = new Comparable[datas.length - 1];
                // copy [0, index-1]
                if (index > 0) {
                    System.arraycopy(datas, 0, newData, 0, index);
                }
                // copy [index+1, (size-1)-index]
                System.arraycopy(datas, index + 1, newData, index, (size - 1) - index);
                datas = newData;
            } else {
                datas[index] = null;
            }
            size--;
            return true;
        }
        return false;
    }

    public T findMin() {
        Comparable element = null;
        for (Comparable data : datas) {
            if (element == null) {
                element = data;
                continue;
            }
            // < 0: A < B
            if (data != null && data.compareTo(element) < 0) {
                element = data;
            }
        }
        return (T) element;
    }

    public T findMax() {
        Comparable element = null;
        for (Comparable data : datas) {
            if (element == null) {
                element = data;
                continue;
            }
            // > 0: A > B
            if (data != null && data.compareTo(element) > 0) {
                element = data;
            }
        }
        return (T) element;
    }
}
