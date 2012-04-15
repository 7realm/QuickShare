package my.activity.demo.listmanager.inmemory;

import java.util.ArrayList;
import java.util.List;

import my.activity.demo.listmanager.BaseSharedList;

public class InMemoryList<T> extends BaseSharedList<T> {
    private final List<T> internalList = new ArrayList<T>();

    @Override
    public int size() {
        return internalList.size();
    }

    @Override
    public T get(int index) {
        return internalList.get(index);
    }

    @Override
    protected boolean doAdd(T item) {
        return internalList.add(item);
    }

    @Override
    protected boolean doRemove(int index) {
        return internalList.remove(index) != null;
    }
}
