package my.activity.demo.sleep;

import static my.activity.demo.Helper.log;
import my.activity.demo.Helper;
import my.activity.demo.R;
import my.activity.demo.listmanager.ListManager;
import my.activity.demo.listmanager.SharedList;
import my.activity.demo.listmanager.inmemory.InMemoryList;
import my.activity.demo.period.Period;
import my.activity.demo.period.PeriodActivity;
import my.activity.demo.settings.Settings;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Message;
import android.os.Process;
import android.os.SystemClock;

public class SleepService extends Service {
    private static final String CPU_SLEEP_LIST = "cpu_sleep_list";
    private static final int NOTIFICATION_ID = 10;
    private long lastWakeTime;
    private NotificationManager notificationManager;
    private SharedList<Period> list;
    private Handler serviceHandler;
    private Notification notification;

    @Override
    public synchronized int onStartCommand(Intent intent, int flags, int startId) {
        log("sleep_service", "onStartCommand: " + startId);
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        log("sleep_service", "onLowMemory");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        log("sleep_service", "onDestroy");

        serviceHandler.getLooper().quit();
        notificationManager.cancel(NOTIFICATION_ID);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        log("sleep_service", "onCreate");

        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // create notification
        notification = new Notification(R.drawable.ic_launcher, "Sleep service started.", System.currentTimeMillis());
        notification.defaults |= Notification.DEFAULT_SOUND;
        notification.flags |= Notification.FLAG_ONGOING_EVENT;
        notification.flags |= Notification.FLAG_NO_CLEAR;

        updateNotification("Started sleep service.", "At " + Helper.formatNow() + ".");

        // start foreground service
        startForeground(NOTIFICATION_ID, notification);

        // service thread
        HandlerThread thread = new HandlerThread("ServiceStartArguments", Process.THREAD_PRIORITY_BACKGROUND);
        thread.start();

        // init
        list = ListManager.getOrCreateList(CPU_SLEEP_LIST, new InMemoryList<Period>());
        Settings.init(getApplicationContext());

        serviceHandler = new Handler(thread.getLooper()) {
            @Override
            public void handleMessage(Message msg) {
                long sleepPollingInterval = Settings.getInstance().getSleepPollingInterval();
                long previousMoment = (Long) msg.obj;
                long now = SystemClock.elapsedRealtime();

                if (lastWakeTime == 0) {
                    lastWakeTime = now;
                } else if (now - previousMoment > 2 * sleepPollingInterval) {

                    log("sleep_servce", "wake up, uptime " + SystemClock.uptimeMillis() + ", elapsed " + SystemClock.elapsedRealtime());
                    // updateNotification("Waked up.", "At " + Helper.formatNow() + ".");

                    // adjust elapsed real time to wall clock time
                    long adjust = System.currentTimeMillis() - now;

                    log("sleep_service", "sleep time " + (now - previousMoment));

                    // phone slept between now and previous time
                    // assume it woke up at now, so whole sleep time till now is sleep period
                    Period period = new Period();
                    period.name = "live";
                    period.start = adjust + lastWakeTime;
                    period.end = adjust + previousMoment;
                    list.add(period);

                    period = new Period();
                    period.name = "sleep";
                    period.start = adjust + previousMoment;
                    period.end = adjust + now;
                    list.add(period);

                    // refresh last wake time
                    lastWakeTime = now;
                }
                touch(this, now);
            }

        };
        touch(serviceHandler, SystemClock.uptimeMillis());
    }

    private void updateNotification(String title, String text) {
        Intent activityIntent = new Intent(getApplicationContext(), PeriodActivity.class);
        activityIntent.putExtra(PeriodActivity.LIST_NAME, CPU_SLEEP_LIST);
        activityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent intent = PendingIntent.getActivity(this, NOTIFICATION_ID, activityIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        notification.setLatestEventInfo(getApplicationContext(), title, text, intent);
        notificationManager.notify(NOTIFICATION_ID, notification);
    }

    private static void touch(Handler serviceHandler, long now) {
        serviceHandler.sendMessageDelayed(serviceHandler.obtainMessage(0, now),
            Settings.getInstance().getSleepPollingInterval());
    }
}
