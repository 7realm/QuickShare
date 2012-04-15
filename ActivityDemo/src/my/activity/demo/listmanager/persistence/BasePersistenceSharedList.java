package my.activity.demo.listmanager.persistence;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import my.activity.demo.listmanager.BaseSharedList;

public abstract class BasePersistenceSharedList<T> extends BaseSharedList<T> {
    private static final String KEY_SIZE = "size";
    private static final String CHARSET = "utf-8";
    protected final ObjectConvertor<T> convertor;

    public BasePersistenceSharedList(ObjectConvertor<T> convertor) {
        this.convertor = convertor;
    }

    protected abstract String hashString(String input);

    protected abstract String fromPersistenceString(String key);

    protected abstract int fromPersistenceInt(String key);

    protected abstract boolean toPersistence(String key, String value);

    protected abstract boolean toPersistence(String key, int value);

    @Override
    public int size() {
        return fromPersistenceInt(hashString(KEY_SIZE));
    }

    @Override
    public T get(int index) {
        try {
            String value = fromPersistenceString(hashInteger(index));
            DataInputStream input = new DataInputStream(new ByteArrayInputStream(value.getBytes(CHARSET)));
            try {
                return convertor.fromStream(input);
            } finally {
                input.close();
            }
        } catch (IOException e) {
            throw new PersistenceSharedListException("Failed to get item at index '" + index + "'.", e);
        }
    }

    @Override
    protected boolean doAdd(T item) {
        try {
            int size = size();
            ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
            DataOutputStream output = new DataOutputStream(byteArray);
            try {
                convertor.toStream(item, output);
                return toPersistence(hashInteger(size), byteArray.toString(CHARSET)) &&
                    toPersistence(hashString(KEY_SIZE), size + 1);
            } finally {
                output.close();
            }
        } catch (IOException e) {
            throw new PersistenceSharedListException("Failed to add item '" + item + "'.", e);
        }
    }

    @Override
    protected boolean doRemove(int index) {
        return toPersistence(hashInteger(index), null) &&
            toPersistence(hashString(KEY_SIZE), size() - 1);
    }

    private String hashInteger(int i) {
        return hashString(String.valueOf(i));
    }
}
