package my.activity.demo.location;

import my.activity.demo.Helper;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

public class WifiBroadcastReceiver extends BroadcastReceiver {
    private static final String[] WIFI_STATES = {
        "WIFI_STATE_DISABLING", "WIFI_STATE_DISABLED",
        "WIFI_STATE_ENABLING", "WIFI_STATE_ENABLED",
        "WIFI_STATE_UNKNOWN"};

    @Override
    public void onReceive(Context context, Intent intent) {
        Helper.log("receiver", "Intent: " + intent);
//        Helper.log("receiver", "Received WIFI state: " +
//            Helper.get(WIFI_STATES, intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, -1)));
//        Helper.log("receiver", "Previous WIFI state: " +
//            Helper.get(WIFI_STATES, intent.getIntExtra(WifiManager.EXTRA_PREVIOUS_WIFI_STATE, -1)));
        Bundle extras = intent.getExtras();
        for (String extra: extras.keySet()) {
            Helper.log("receiver", extra + ": " + extras.get(extra));
        }

//        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
//        WifiInfo info = wifiManager.getConnectionInfo();
//        if (info != null) {
//            Helper.log("receiver", "Wifi info: " + info.toString());
//            Helper.log("receiver", "Enabled: " + wifiManager.isWifiEnabled() + ", state: " + Helper.get(WIFI_STATES, wifiManager.getWifiState()));
//            Helper.log("receiver", "Supplicant state: " + WifiInfo.getDetailedStateOf(info.getSupplicantState()).name());
//
//        }
    }
}
