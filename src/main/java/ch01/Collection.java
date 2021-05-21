package ch01;

public class Collection<T> {

    private Object[] datas = new Object[16];
    private int size;

    public boolean isEmpty() {
        return datas == null || size == 0;
    }

    public void makeEmpty() {
        datas = new Object[16];
        size = 0;
    }

    public void insert(T data) {
        if (size >= datas.length) {
            long newSize = size * 2L;
            if (newSize > Integer.MAX_VALUE) {
                newSize = Integer.MAX_VALUE;
            }
            Object[] newDatas = new Object[(int) newSize];
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
                Object[] newData = new Object[datas.length - 1];
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

    public boolean isPresent(T data) {
        if (data == null) {
            for (int i = 0; i < size; i++) {
                if (datas[i] == null) {
                    return true;
                }
            }
        } else {
            for (int i = 0; i < size; i++) {
                if (data.equals(datas[i])) {
                    return true;
                }
            }
        }
        return false;
    }
}
