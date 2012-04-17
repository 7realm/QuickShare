package my.activity.demo;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.util.Log;

import com.google.code.microlog4android.Level;
import com.google.code.microlog4android.Logger;
import com.google.code.microlog4android.appender.FileAppender;
import com.google.code.microlog4android.format.Formatter;
import com.google.code.microlog4android.repository.DefaultLoggerRepository;

public class Helper {
    public static final String[] STATUSES = {"Out of Service", "Temporarily Unavailable", "Available"};

    private static final DateFormat DATE_LONG = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss.SSS");
    private static final DateFormat DATE_ELAPSED = new SimpleDateFormat("HH:mm:ss");
    private static final Logger LOGGER;
    static {
        DATE_ELAPSED.setTimeZone(TimeZone.getTimeZone("UTC"));

        LOGGER = DefaultLoggerRepository.INSTANCE.getRootLogger();
        FileAppender fileAppender = new FileAppender();
        fileAppender.setFileName(".logs/LOG [" + new SimpleDateFormat("dd-MM-yyyy").format(new Date()) + "].txt");
        fileAppender.setAppend(true);
        fileAppender.setFormatter(new Formatter() {

            @Override
            public void setProperty(String s, String s1) {
                // empty
            }

            @Override
            public String[] getPropertyNames() {
                return new String[0];
            }

            @Override
            public String format(String clientID, String name, long time, Level level, Object message, Throwable t) {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(Helper.formatNow());
                stringBuilder.append(" ");
                stringBuilder.append(message);
                if (t != null) {
                    stringBuilder.append(System.getProperty("line.separator"));
                    stringBuilder.append(t.toString());

                    StringWriter writer = new StringWriter();
                    t.printStackTrace(new PrintWriter(writer, true));
                    stringBuilder.append(writer.getBuffer());
                }
                return stringBuilder.toString();
            }
        });
        LOGGER.addAppender(fileAppender);
    }

    public static Notification createNotification(Context context, CharSequence tickerText, String text) {
        // the next two lines initialize the Notification, using the configurations above
        Notification notification = new Notification(
            R.drawable.ic_launcher,
            tickerText,
            System.currentTimeMillis());

        notification.setLatestEventInfo(context,
            tickerText,
            text,
            PendingIntent.getActivity(context, 0, new Intent(context, ActivityDemoActivity.class), 0));

        // default sound
        notification.defaults |= Notification.DEFAULT_SOUND;

        // We show this for as long as our service is processing a command.
        notification.flags |= Notification.FLAG_ONGOING_EVENT;
        notification.flags |= Notification.FLAG_NO_CLEAR;
        return notification;
    }

    public static void cancelNotification(Context context, int notificationId) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(notificationId);
    }

    public static String get(String[] constants, int index) {
        try {
            return constants[index];
        } catch (IndexOutOfBoundsException e) {
            return String.valueOf(index);
        }
    }

    public static String formatNow() {
        return DATE_LONG.format(new Date());
    }

    public static String format(long milliseconds) {
        return DATE_LONG.format(new Date(milliseconds));
    }

    public static String formatElapsed(long milliseconds) {
        return DATE_ELAPSED.format(new Date(milliseconds));
    }

    public static void backupLogs() {
        File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        File from = new File(path, "service-log.txt");
        File to = new File(path, "service-log" + System.currentTimeMillis() + ".txt");
        from.renameTo(to);
    }

    public static void log(String message) {
        log("default", message);
    }

    public static void log(String tag, String message) {
        log(tag, message, null);
    }

    public static void log(String tag, String message, Throwable causeException) {
        Log.i(tag, message, causeException);

        LOGGER.info("[" + tag + "]: " + message, causeException);
    }

    public static double average(double[] data) {
        double avg = 0;
        for (double element : data) {
            avg += element / data.length;
        }
        return avg;
    }

    public static double deviation(double[] data) {
        if (data.length < 2) {
             return 0;
        }
        double avg = average(data);
        double sum = 0;
        for (double element : data) {
            sum += (element - avg) * (element - avg);
        }

        return Math.sqrt(sum / (data.length - 1));
    }
}
