package my.activity.demo.listmanager.persistence.file;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import my.activity.demo.Helper;
import my.activity.demo.listmanager.persistence.BasePersistenceSharedList;
import my.activity.demo.listmanager.persistence.ObjectConvertor;
import android.content.Context;

public class FileList<T> extends BasePersistenceSharedList<T> {
    private final Properties properties = new Properties();
    private final String fileName;
    private final Context context;;

    public FileList(ObjectConvertor<T> convertor, String fileName, Context context) {
        super(convertor);
        this.fileName = fileName;
        this.context = context;

        try {
            FileInputStream input = context.openFileInput(fileName);
            try {
                properties.clear();
                properties.load(input);
            } finally {
                input.close();
            }
        } catch (IOException e) {
            Helper.log("file-list", "I/O exception occurred while creating file-based shared list.", e);
        }
    }

    @Override
    protected String hashString(String input) {
        return input;
    }

    @Override
    protected String fromPersistenceString(String key) {
        return properties.getProperty(key);
    }

    @Override
    protected int fromPersistenceInt(String key) {
        try {
            return Integer.parseInt(properties.getProperty(key));
        } catch (NumberFormatException e) {
            Helper.log("file-list", "I/O exception occurred while parsing value for '" + key + "'.", e);
            return 0;
        }
    }

    @Override
    protected boolean toPersistence(String key, String value) {
        properties.put(key, value);

        try {
            FileOutputStream output = context.openFileOutput(fileName, Context.MODE_WORLD_READABLE);
            try {
                properties.store(output, "Last updated " + Helper.formatNow());
                return true;
            } finally {
                output.close();
            }
        } catch (IOException e) {
            Helper.log("file-list", "Failed to write data to file.", e);
            return false;
        }
    }

    @Override
    protected boolean toPersistence(String key, int value) {
        return toPersistence(key, String.valueOf(value));
    }

}
