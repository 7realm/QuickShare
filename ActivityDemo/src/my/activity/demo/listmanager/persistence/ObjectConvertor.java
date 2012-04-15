package my.activity.demo.listmanager.persistence;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public interface ObjectConvertor<T> {
    void toStream(T object, DataOutputStream outputStream) throws IOException;

    T fromStream(DataInputStream inputStream) throws IOException;
}
