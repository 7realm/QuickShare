package my.activity.demo.listmanager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public abstract class BaseSharedList<T> implements SharedList<T> {
    private final List<SharedListUpdateListener> listeners =
        Collections.synchronizedList(new ArrayList<SharedListUpdateListener>());
    private final Object listenerLock = new Object();

    @Override
    public Iterator<T> iterator() {
        return new Iterator<T>() {
            private int pos = -1;

            @Override
            public boolean hasNext() {
                return pos + 1 < size();
            }

            @Override
            public T next() {
                return get(++pos);
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException("Not implemented.");
            }
        };
    }

    @Override
    public void clear() {
        boolean changed = false;
        while (size() > 0) {
            doRemove(0);
        }

        synchronized (listenerLock) {
            invokeListener(changed);
        }
    }

    @Override
    public boolean add(T item) {
        boolean result = doAdd(item);
        synchronized (listenerLock) {
            invokeListener(result);
        }
        return result;
    }

    @Override
    public boolean remove(int index) {
        boolean result = doRemove(index);
        synchronized (listenerLock) {
            invokeListener(result);
        }
        return result;
    }

    @Override
    public void registerUpdateListener(SharedListUpdateListener listener) {
        synchronized (listenerLock) {
            if (!listeners.contains(listener)) {
                listeners.add(listener);
            }
        }
    }

    @Override
    public void unregisterUpdateListener(SharedListUpdateListener listener) {
        synchronized (listenerLock) {
            listeners.remove(listener);
        }
    }

    private void invokeListener(boolean condition) {
        if (condition) {
            for (SharedListUpdateListener listener : listeners) {
                listener.onUpdate();
            }
        }
    }

    protected abstract boolean doAdd(T item);

    protected abstract boolean doRemove(int index);
}
