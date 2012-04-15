package my.activity.demo.location;

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
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.os.SystemClock;

public final class MessageLoopHandler extends Handler {
    public static final int MOVED = 1;
    public static final int DISABLED = 2;
    public static final int ENABLED = 3;
    public static final int BECOME_STABLE = 4;
    public static final int ALREADY_STABLE = 5;
    public static final int INIT = 6;

    public static final int NOTIFICATION_ID = 44;

    private static final String LOCATION_LIST_NAME = "location_list";
    private int stateId = 1;

    private boolean isStable;
    private boolean isDisabled;
    private long lastStateChangeTime;

    private final Context context;
    private final NotificationManager notificationManager;
    private final Notification notification;
    private final SharedList<Period> list;

    public MessageLoopHandler(Context context) {
        super(createLooper());
        this.context = context;

        // create list
        list = ListManager.getOrCreateList(LOCATION_LIST_NAME, new InMemoryList<Period>());

        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // create notification
        notification = new Notification(R.drawable.ic_launcher, "Location tracking service started.", System.currentTimeMillis());
        notification.flags |= Notification.FLAG_ONGOING_EVENT;
        notification.flags |= Notification.FLAG_NO_CLEAR;

        updateNotification("Started.", "Started at " + Helper.formatNow() + ".");
    }

    public Notification getNotification() {
        return notification;
    }

    @Override
    public synchronized void handleMessage(Message msg) {
        if (msg.what != 0 && msg.what < stateId) {
            log("handler", "Message received from past: " + msg.arg1);
            return;
        }

        // skip empty messages
        if (msg.arg1 == 0) {
            return;
        }

        long stableBecomeTimeout = Settings.getInstance().getStableBecomeTimeout();
        long stablePeriodTimeout = Settings.getInstance().getStablePeriodTimeout();

        // remove messages that relates to current state
        removeMessages(stateId);

        // advance to next state
        stateId++;

        switch (msg.arg1) {
        case MOVED:
            log("handler", "Move event received, unavailable gap: " + msg.arg2);

            doDeparture(msg.arg2);

            doTryStable(stableBecomeTimeout);
            break;
        case DISABLED:
            log("handler", "Disabled event received: stable [" + isStable + "], disabled [" + isDisabled + "].");

            doDeparture(msg.arg2);

            isDisabled = true;
            break;
        case ENABLED:
            log("handler", "Enabled event received: stable [" + isStable + "], disabled [" + isDisabled + "].");

            if (isDisabled) {
                isDisabled = false;

                doTryStable(stableBecomeTimeout);
            }
            break;
        case BECOME_STABLE:
            log("handler", "Become stable event received: stable [" + isStable + "], disabled [" + isDisabled + "] at uptime "
                + SystemClock.uptimeMillis() + ", elapsed time " + SystemClock.elapsedRealtime());

            if (!isStable) {
                isStable = true;

                long stableStateStartTime = System.currentTimeMillis() - stableBecomeTimeout;
                long moveTime = stableStateStartTime - lastStateChangeTime;

                log("handler", "Started stable period at " + Helper.format(stableStateStartTime) + " move time " + Helper.formatElapsed(moveTime));
                updateNotification("Arrived.", "Moving time: " + Helper.formatElapsed(moveTime));

                doStablePeriod(stablePeriodTimeout);

                // set start of stable period
                lastStateChangeTime = stableStateStartTime;

                startPeriod(true);
            }

            break;
        case ALREADY_STABLE:
            log("handler", "Already stable event received at uptime " + SystemClock.uptimeMillis() + ", elapsed time "
                + SystemClock.elapsedRealtime());

            long stayTime = System.currentTimeMillis() - lastStateChangeTime;
            updateNotification("Staying.", "Staying time: " + Helper.formatElapsed(stayTime));

            doStablePeriod(stablePeriodTimeout);
            break;
        case INIT:
            log("handler", "Init event received.");

            log("handler", "Started stable period at " + Helper.formatNow() + ".");
            updateNotification("Initialize.", "Started at " + Helper.formatNow() + ".");

            lastStateChangeTime = System.currentTimeMillis();

            isStable = true;
            doStablePeriod(stablePeriodTimeout);

            startPeriod(true);
            break;
        default:
            log("handler", "Unknown message type: " + msg.arg1);
            break;
        }
    }

    @Override
    public boolean sendMessageAtTime(Message msg, long uptimeMillis) {
        boolean sent = super.sendMessageAtTime(msg, uptimeMillis);
        if (!sent) {
            log("handler", "Message is not sent. Message arg1: " + msg.arg1);
        }
        return sent;
    }

    public void cancelNotification() {
        notificationManager.cancel(NOTIFICATION_ID);
    }

    /**
     * Sends a message that will check in configured time if state is still stable. If state will not change, it will be
     * considered stable.
     */
    private void doTryStable(long stableBecomeTimeout) {
        sendMessageDelayed(obtainMessage(stateId, BECOME_STABLE, 0), stableBecomeTimeout);
        log("handler", "Sending message become stable in " + stableBecomeTimeout + " at uptime " + SystemClock.uptimeMillis() + ", elapsed time "
            + SystemClock.elapsedRealtime());
    }

    /**
     * Send a message that will be fired during stable period.
     */
    private void doStablePeriod(long stablePeriodTimeout) {
        sendMessageDelayed(obtainMessage(stateId, ALREADY_STABLE, 0), stablePeriodTimeout);
        log("handler", "Sending message period stable in " + stablePeriodTimeout + " at uptime " + SystemClock.uptimeMillis() + ", elapsed time "
            + SystemClock.elapsedRealtime());
    }

    /**
     * Makes departure event immediately, because for example, object is moved.
     */
    private void doDeparture(int unavailableGap) {
        if (isStable) {
            isStable = false;

            // generate notification that stable state ended
            long stayTime = System.currentTimeMillis() - lastStateChangeTime - unavailableGap;
            log("handler", "Departure, stay time: " + stayTime / 1000 + " seconds.");
            updateNotification("Departure.", "Staying time: " + Helper.formatElapsed(stayTime));

            lastStateChangeTime = System.currentTimeMillis() - unavailableGap;

            startPeriod(false);
        }
    }

    private void startPeriod(boolean isStable) {
        // end previous period
        int size = list.size();
        if (size > 0) {
            Period lastPeriod = list.get(size - 1);
            lastPeriod.end = lastStateChangeTime;

            list.remove(size - 1);
            list.add(lastPeriod);
        }

        Period period = new Period();
        period.name = isStable ? "stable" : "moving";
        period.start = lastStateChangeTime;
        period.end = 0;
        list.add(period);

        log("handler", "Period started, last change time " + Helper.format(lastStateChangeTime) + ", stable " + isStable);
    }

    private void updateNotification(String title, String text) {
        Intent activityIntent = new Intent(context, PeriodActivity.class);
        activityIntent.putExtra(PeriodActivity.LIST_NAME, LOCATION_LIST_NAME);
        activityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent intent = PendingIntent.getActivity(context, NOTIFICATION_ID, activityIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        notification.setLatestEventInfo(context, title, text, intent);
        notificationManager.notify(NOTIFICATION_ID, notification);
    }

    private static Looper createLooper() {
        HandlerThread thread = new HandlerThread("[location handler thread]", Process.THREAD_PRIORITY_BACKGROUND);
        thread.start();

        return thread.getLooper();
    }
}