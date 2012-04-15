package my.activity.demo.listmanager;

public interface SharedList<T> extends Iterable<T> {
    void clear();

    int size();

    T get(int index);

    boolean add(T item);

    boolean remove(int index);

    void registerUpdateListener(SharedListUpdateListener listener);

    void unregisterUpdateListener(SharedListUpdateListener listener);
}
