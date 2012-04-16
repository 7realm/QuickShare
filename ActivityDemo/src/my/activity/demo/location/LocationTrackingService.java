package my.activity.demo.location;

import static my.activity.demo.Helper.log;

import java.lang.Thread.UncaughtExceptionHandler;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.IBinder;

public class LocationTrackingService extends Service {
    private MessageLoopHandler serviceHandler;

    private LocationListener listener;

    @Override
    public synchronized int onStartCommand(Intent intent, int flags, int startId) {
        log("service", "onStartCommand: " + startId);
        if (listener != null) {
            unregisterLocationListener();
        }
        listener = new WiseLocationListener(getApplicationContext(), serviceHandler);
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        log("service", "onLowMemory");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        log("service", "onCreate");

        // init service handler
        serviceHandler = new MessageLoopHandler(this);

        // start foreground service
        startForeground(MessageLoopHandler.NOTIFICATION_ID, serviceHandler.getNotification());

        // register exception listener
        Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread thread, Throwable e) {
                log("service", "Exception in " + thread.getName() + ".", e);
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        log("service", "onDestroy");

        // stop message loop
        serviceHandler.getLooper().quit();

        unregisterLocationListener();

        // clear notification
        serviceHandler.cancelNotification();
    }

    private void unregisterLocationListener() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationManager.removeUpdates(listener);
    }
}
