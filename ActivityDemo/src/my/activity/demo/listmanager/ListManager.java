package my.activity.demo.listmanager;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ListManager {
    private static final Map<String, SharedList<?>> LISTS = Collections.synchronizedMap(new HashMap<String, SharedList<?>>());

    @SuppressWarnings({"unchecked", "unused"})
    public static <T> SharedList<T> getList(String name, Class<T> clazz) {
        if (!LISTS.containsKey(name)) {
            return null;
        }
        return (SharedList<T>) LISTS.get(name);
    }

    @SuppressWarnings("unchecked")
    public static <T> SharedList<T> getOrCreateList(String name, SharedList<T> newList) {
        SharedList<T> list = (SharedList<T>) LISTS.get(name);
        if (list == null) {
            LISTS.put(name, newList);
            list = newList;
        }
        return list;
    }

    public static void putList(String name, SharedList<?> list) {
        if (LISTS.containsKey(name)) {
            throw new IllegalArgumentException("List already present for '" + name + "'.");
        }

        LISTS.put(name, list);
    }
}
