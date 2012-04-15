package my.activity.demo;

import java.util.Random;

import my.activity.demo.listmanager.ListManager;
import my.activity.demo.listmanager.SharedList;
import my.activity.demo.listmanager.inmemory.InMemoryList;
import my.activity.demo.location.CellDetectionActivity;
import my.activity.demo.location.CellScanService;
import my.activity.demo.location.LocationTrackingService;
import my.activity.demo.settings.Settings;
import my.activity.demo.settings.SettingsActivity;
import my.activity.demo.sleep.SleepService;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.NeighboringCellInfo;
import android.telephony.TelephonyManager;
import android.util.Base64;
import android.view.View;
import android.widget.Toast;

public class ActivityDemoActivity extends Activity {
    private PendingIntent operation;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        Settings.init(getApplicationContext());
    }

    @SuppressWarnings("unused")
    public void finishClick(View v) {
        finish();
    }

    @SuppressWarnings("unused")
    public void startLocationClick(View v) {
        startService(new Intent(this, LocationTrackingService.class));
    }

    @SuppressWarnings("unused")
    public void stopLocationClick(View v) {
        stopService(new Intent(this, LocationTrackingService.class));
    }

    @SuppressWarnings("unused")
    public void startSleepClick(View v) {
        startService(new Intent(this, SleepService.class));
    }

    @SuppressWarnings("unused")
    public void stopSleepClick(View v) {
        stopService(new Intent(this, SleepService.class));
    }

    @SuppressWarnings("unused")
    public void settingsClick(View v) {
        startActivity(new Intent(this, SettingsActivity.class));
    }

    @SuppressWarnings("unused")
    public void cellClick(View v) {
        SharedList<NeighboringCellInfo> list = ListManager.getOrCreateList("cell_list", new InMemoryList<NeighboringCellInfo>());
        TelephonyManager manager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        for (NeighboringCellInfo info : manager.getNeighboringCellInfo()) {
            String string = "Cell ID: " + info.getCid() + ", LAC: " + info.getLac() + ", RSSI: " + info.getRssi();
            Helper.log("scan", string);
            list.add(info);
        }
        Helper.log("scan", "------------------scanned--------------------");

        startActivity(new Intent(this, CellDetectionActivity.class));
    }

    @SuppressWarnings("unused")
    public void startScanClick(View v) {
        Intent intent = new Intent(getApplicationContext(), CellScanService.class);
        operation = PendingIntent.getService(this, 0, intent, 0);

        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, 0, 10 * 1000, operation);

        Toast.makeText(this, "Alarm started.", Toast.LENGTH_LONG).show();
    }

    @SuppressWarnings("unused")
    public void stopScanClick(View v) {
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmManager.cancel(operation);

        Toast.makeText(this, "Alarm stopped.", Toast.LENGTH_LONG).show();
    }

    @SuppressWarnings("unused")
    public void tagClick(View v) {
        Random r = new Random(System.currentTimeMillis());
        byte[] buf = new byte[10];
        r.nextBytes(buf);
        String tag = Base64.encodeToString(buf, Base64.DEFAULT);
        Helper.log("cell", "---" + tag);
        Base64.encodeToString(buf, 0);

        Toast.makeText(this, tag, Toast.LENGTH_LONG).show();
    }

}