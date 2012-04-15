package my.activity.demo.listmanager.persistence.sharedpreferences;

import my.activity.demo.listmanager.persistence.BasePersistenceSharedList;
import my.activity.demo.listmanager.persistence.ObjectConvertor;
import android.content.Context;
import android.content.SharedPreferences.Editor;

public class SharedPreferencesList<T> extends BasePersistenceSharedList<T> {
    private final String prefix;
    private final Context context;
    private final String preferencesName;

    public SharedPreferencesList(Context context, String preferencesName, ObjectConvertor<T> convertor) {
        this(context, preferencesName, "list", convertor);
    }

    public SharedPreferencesList(Context context, String preferencesName, String prefix, ObjectConvertor<T> convertor) {
        super(convertor);
        this.context = context;
        this.preferencesName = preferencesName;
        this.prefix = prefix;
    }

    @Override
    protected String hashString(String input) {
        return prefix + "_" + input;
    }

    @Override
    protected String fromPersistenceString(String key) {
        return context.getSharedPreferences(preferencesName, Context.MODE_WORLD_READABLE).getString(key, null);
    }

    @Override
    protected int fromPersistenceInt(String key) {
        return context.getSharedPreferences(preferencesName, Context.MODE_WORLD_READABLE).getInt(key, 0);
    }

    @Override
    protected boolean toPersistence(String key, String value) {
        Editor edit = context.getSharedPreferences(preferencesName, Context.MODE_WORLD_READABLE).edit();
        if (value == null) {
            return edit.remove(key).commit();
        }

        return edit.putString(key, value).commit();
    }

    @Override
    protected boolean toPersistence(String key, int value) {
        Editor edit = context.getSharedPreferences(preferencesName, Context.MODE_WORLD_READABLE).edit();
        return edit.putInt(key, value).commit();
    }
}
