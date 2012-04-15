package my.activity.demo.location;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import my.activity.demo.Helper;
import android.app.IntentService;
import android.content.Intent;
import android.telephony.NeighboringCellInfo;
import android.telephony.TelephonyManager;

public class CellScanService extends IntentService {
    private static class ScanDataItem {
        private int cellId;
        private int lac;
        private int rssi;
    }

    private static class ScanData {
        private final List<ScanDataItem> scanItems = new ArrayList<ScanDataItem>();

        private void add(int cellId, int lac, int rssi) {
            ScanDataItem item = new ScanDataItem();
            item.cellId = cellId;
            item.lac = lac;
            item.rssi = rssi;

            scanItems.add(item);
        }
    }

    private static TelephonyManager telephonyManager;

    private final static List<ScanData> data = new ArrayList<ScanData>();

    public CellScanService() {
        super("CellScanService");

        // do not re deliver intent if service dies
        setIntentRedelivery(false);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        if (telephonyManager == null) {
            telephonyManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        }
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Helper.log("cell", "---Scan start at " + Helper.formatNow());

        ScanData scanData = new ScanData();
        List<NeighboringCellInfo> neighboringCellInfo = telephonyManager.getNeighboringCellInfo();
        for (NeighboringCellInfo info : neighboringCellInfo) {
            scanData.add(info.getCid(), info.getLac(), info.getRssi());
            String string = "Cell ID: " + info.getCid() + ", LAC: " + info.getLac() + ", RSSI: " + info.getRssi();
            Helper.log("cell", string);
        }
        data.add(scanData);

        Map<Integer, Integer> map = new TreeMap<Integer, Integer>();
        ListIterator<ScanData> i = data.listIterator(data.size() - 1);
        int count = 20;
        while (i.hasPrevious() && count > 0) {
            for (ScanDataItem item : i.previous().scanItems) {
                if (map.containsKey(item.cellId)) {
                    map.put(item.cellId, map.get(item.cellId) + 1);
                } else {
                    map.put(item.cellId, 1);
                }
            }

            count--;
        }

        Helper.log("cell", "---Processed data, size " + data.size());
        for (Entry<Integer, Integer> entry : map.entrySet()) {
            Helper.log("cell", entry.getKey() + ": " + entry.getValue());
        }
    }
}
