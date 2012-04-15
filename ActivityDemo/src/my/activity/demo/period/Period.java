package my.activity.demo.period;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import my.activity.demo.listmanager.persistence.ObjectConvertor;

public class Period implements ObjectConvertor<Period> {
    public String name;
    public long start;
    public long end;

    @Override
    public void toStream(Period object, DataOutputStream outputStream) throws IOException {
        outputStream.writeUTF(name);
        outputStream.writeLong(start);
        outputStream.writeLong(end);
    }

    @Override
    public Period fromStream(DataInputStream inputStream) throws IOException {
        Period period = new Period();
        period.name = inputStream.readUTF();
        period.start = inputStream.readLong();
        period.end = inputStream.readLong();
        return period;
    }
}
