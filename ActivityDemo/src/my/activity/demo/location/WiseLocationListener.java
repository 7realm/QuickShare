package my.activity.demo.location;

import static my.activity.demo.Helper.STATUSES;
import static my.activity.demo.Helper.get;
import static my.activity.demo.Helper.log;
import my.activity.demo.Helper;
import my.activity.demo.settings.Settings;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;

public class WiseLocationListener implements LocationListener {
    private static final boolean NOTIFICATIONS_ENABLED = false;
    private Location lastKnownLocation;
    private boolean isEnabled;
    private boolean isUnavailable;
    private long lastAvailableTime;

    private final Object lock = new Object();
    private final Context context;
    private final MessageLoopHandler serviceHandler;

    public WiseLocationListener(Context context, MessageLoopHandler serviceHandler) {
        this.context = context;
        this.serviceHandler = serviceHandler;
        isUnavailable = false;

        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        String providerName = Settings.getInstance().getProviderName();
        lastKnownLocation = locationManager.getLastKnownLocation(providerName);
        isEnabled = locationManager.isProviderEnabled(providerName);

        if (lastKnownLocation != null) {
            log("location", "Initialized at last known location.");
            serviceHandler.sendMessage(serviceHandler.obtainMessage(0, MessageLoopHandler.INIT, 0));
        }

        locationManager.requestLocationUpdates(providerName, 1000, 5, this);

        log("location", "Updates requested for [" + providerName + "], enabled: " + isEnabled + ", last location: " + lastKnownLocation);
    }

    @Override
    public void onLocationChanged(Location location) {
        synchronized (lock) {
            if (lastKnownLocation == null) {
                lastKnownLocation = location;

                log("location", "Initialized at location change.");
                serviceHandler.sendMessage(serviceHandler.obtainMessage(0, MessageLoopHandler.INIT, 0));
                return;
            }

            if (!isEnabled) {
                log("location", "update, but not enabled: " + location);
                return;
            }

            float distance = location.distanceTo(lastKnownLocation);

            log("location", "update, distance " + distance);
            WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            WifiInfo info = wifiManager.getConnectionInfo();
            log("location", "WifiInfo: " + info);
            showNotification("Location updated.", "New location: " + location.toString());

            serviceHandler.sendEmptyMessage(0);

            // check if location change can be accepted with location accuracy
            if (location.getAccuracy() < distance) {

                // check if moved for specified distance
                if (distance > 100) {
                    log("location", "moved: " + location);
                    showNotification("You have moved.", "Distance to previous location: " + distance);

                    // calculate move time from last available moment
                    int moveTime = !isUnavailable ? 0 : (int) (System.currentTimeMillis() - lastAvailableTime);

                    // generate move event
                    serviceHandler.sendMessage(serviceHandler.obtainMessage(0, MessageLoopHandler.MOVED, moveTime));
                }

                // update unavailable state
                lastKnownLocation = location;
                isUnavailable = false;
            }
        }
    }

    @Override
    public void onProviderDisabled(String provider) {
        log("location", "disabled: " + provider);
        showNotification("Provider disabled.", "Provider " + provider + " disabled.");

        synchronized (lock) {
            if (isEnabled) {
                isEnabled = false;

                // generate disable event
                serviceHandler.sendMessage(serviceHandler.obtainMessage(0, MessageLoopHandler.DISABLED, 0));
            }
        }
    }

    @Override
    public void onProviderEnabled(String provider) {
        log("location", "enabled: " + provider);
        showNotification("Provider enabled.", "Provider " + provider + " enabled.");

        synchronized (lock) {
            if (!isEnabled) {
                isEnabled = true;

                // generate enable event
                serviceHandler.sendMessage(serviceHandler.obtainMessage(0, MessageLoopHandler.ENABLED, 0));
            }
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        String statusName = get(STATUSES, status);
        log("location", "status changed: " + statusName);
        showNotification("Status changed.", "Status changed to: " + statusName);

        synchronized (lock) {
            switch (status) {
            case LocationProvider.TEMPORARILY_UNAVAILABLE:
                if (!isUnavailable) {
                    log("location", "Made unavailable at " + Helper.formatNow());
                    // we should handle only first unavailable moment
                    isUnavailable = true;
                    lastAvailableTime = System.currentTimeMillis();
                }
                break;
            }
        }
    }

    public void showNotification(CharSequence tickerText, String text) {
        if (NOTIFICATIONS_ENABLED) {
            System.out.println(context);
            throw new UnsupportedOperationException("Do not show notification " + tickerText + " text: " + text);
        }
    }
}
