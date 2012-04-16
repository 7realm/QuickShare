package my.activity.demo;

import my.activity.demo.settings.Settings;
import android.app.Application;

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        // init settings with context
        Settings.init(getApplicationContext());
    }

}
