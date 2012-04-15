package my.activity.demo.settings;

import my.activity.demo.location.LocationTrackingService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.LocationManager;

public class Settings {
    private static final long STABLE_BECOME_TIMEOUT = 5 * 60 * 1000;
    private static final long STABLE_PERIOD_TIMEOUT = 20 * 60 * 1000;
    private static final long SLEEP_POLLING_INTERVAL = 1 * 1000;
    private static final String PROVIDER_NAME = LocationManager.NETWORK_PROVIDER;

    private static Settings instance;

    private final Context context;

    public static Settings getInstance() {
        if (instance == null) {
            throw new IllegalStateException("Settings are not initialized.");
        }
        return instance;
    }

    public static void init(Context context) {
        if (instance == null) {
            instance = new Settings(context);
        }
    }

    private Settings(Context context) {
        this.context = context;
    }

    public synchronized long getSleepPollingInterval() {
        return getGeneralPreferences().getLong("sleep.polling.interval", SLEEP_POLLING_INTERVAL);
    }

    public synchronized void setSleepPollingInterval(long sleepPollingInterval) {
        getGeneralPreferences().edit().putLong("sleep.polling.interval", sleepPollingInterval).commit();
    }

    public synchronized long getStableBecomeTimeout() {
        return getGeneralPreferences().getLong("stable.become.timeout", STABLE_BECOME_TIMEOUT);
    }

    public synchronized void setStableBecomeTimeout(long stableBecomeTimeout) {
        getGeneralPreferences().edit().putLong("stable.become.timeout", stableBecomeTimeout).commit();
    }

    public synchronized long getStablePeriodTimeout() {
        return getGeneralPreferences().getLong("stable.period.timeout", STABLE_PERIOD_TIMEOUT);
    }

    public synchronized void setStablePeriodTimeout(long stablePeriodTimeout) {
        getGeneralPreferences().edit().putLong("stable.period.timeout", stablePeriodTimeout).commit();
    }

    public synchronized String getProviderName() {
        return getGeneralPreferences().getString("provider.name", PROVIDER_NAME);
    }

    public synchronized void setProviderName(String providerName) {
        if (!getProviderName().equals(providerName)) {
            getGeneralPreferences().edit().putString("provider.name", providerName).commit();

            // start service, that will cause reinitialization to location listener
            context.startService(new Intent(context, LocationTrackingService.class));
        }
    }

    private SharedPreferences getGeneralPreferences() {
        return context.getSharedPreferences("settings", Context.MODE_WORLD_READABLE);
    }
}
