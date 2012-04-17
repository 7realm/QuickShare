package my.activity.demo.location;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.TreeMap;

import my.activity.demo.Helper;
import my.activity.demo.listmanager.ListManager;
import my.activity.demo.listmanager.SharedList;
import my.activity.demo.listmanager.persistence.file.FileList;
import my.activity.demo.period.Period;
import android.app.IntentService;
import android.content.Intent;
import android.telephony.NeighboringCellInfo;
import android.telephony.TelephonyManager;

public class CellScanService extends IntentService {
    public static final String LIST_CELL_SCAN = "cell_scan";

    private static class ScanDataItem {
        private int cellId;
        private int lac;
        private int rssi;
    }

    private static class ScanData {
        private final List<ScanDataItem> scanItems = new ArrayList<ScanDataItem>();
        private final Date scanTime;

        private ScanData(Date scanTime) {
            this.scanTime = scanTime;
        }

        private void add(int cellId, int lac, int rssi) {
            ScanDataItem item = new ScanDataItem();
            item.cellId = cellId;
            item.lac = lac;
            item.rssi = rssi;

            scanItems.add(item);
        }
    }

    private static class CellCalculatedInfo {
        private final List<Integer> rssis = new ArrayList<Integer>();

        private double[] getRssis() {
            double[] result = new double[rssis.size()];
            for (int i = 0; i < result.length; i++) {
                result[i] = rssis.get(i) == 99 ? 0 : rssis.get(i);
            }
            return result;
        }

        private int getCount() {
            return rssis.size();
        }

        private void addRssi(int rssi) {
            rssis.add(rssi);
        }

    }

    private static TelephonyManager telephonyManager;

    private static final List<ScanData> data = new ArrayList<ScanData>();
    private static Period currentPeriod = new Period();

    private static final int SCAN_HISTORY_SIZE = 60;
    private static final int SCAN_CELL_COUNT_IGNORE = 1;
    private static final double MOVE_FUNCTION_THRESHOLD = 25;
    private static final int DEVIATION_THRESHOLD = 1;

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

        ScanData scanData = new ScanData(new Date());
        List<NeighboringCellInfo> neighboringCellInfo = telephonyManager.getNeighboringCellInfo();
        for (NeighboringCellInfo info : neighboringCellInfo) {
            scanData.add(info.getCid(), info.getLac(), info.getRssi());
            String string = "Cell ID: " + info.getCid() + ", LAC: " + info.getLac() + ", RSSI: " + info.getRssi();
            Helper.log("cell", string);
        }
        data.add(scanData);
        Helper.log("cell", "---Processed data size: " + data.size());

        // if not enough data, then exit
        if (data.size() < SCAN_HISTORY_SIZE + 1) {
            return;
        }

        Map<Integer, CellCalculatedInfo> map = new TreeMap<Integer, CellCalculatedInfo>();
        ListIterator<ScanData> i = data.listIterator(data.size() - 1);
        int count = SCAN_HISTORY_SIZE;
        while (count > 0) {
            for (ScanDataItem item : i.previous().scanItems) {
                CellCalculatedInfo info = map.get(item.cellId);
                if (info == null) {
                    info = new CellCalculatedInfo();
                    map.put(item.cellId, info);
                }
                info.addRssi(item.rssi);
            }

            count--;
        }

        // calculate change of RSSI
        double rssi = 0;
        int n = map.size();
        double[] cellCounts = new double[n];
        int index = 0;
        for (CellCalculatedInfo info : map.values()) {
            if (info.getCount() > SCAN_CELL_COUNT_IGNORE) {
                cellCounts[index++] = info.getCount();
                rssi += Helper.deviation(info.getRssis());
            }
        }
        cellCounts = Arrays.copyOf(cellCounts, index);

        // average and deviation
        double deviation = Helper.deviation(cellCounts);

        // calculate move function
        double moveFunction = rssi * rssi / deviation / deviation;
        if (deviation < DEVIATION_THRESHOLD) {
            moveFunction = deviation;
        }

        // calculate move periods
        boolean moved = moveFunction > MOVE_FUNCTION_THRESHOLD;
        if (moved) {
            if (currentPeriod == null) {
                currentPeriod = new Period();
                currentPeriod.name = "Moving";
                currentPeriod.start = data.get(data.size() - SCAN_HISTORY_SIZE).scanTime.getTime();

                // add current period to 'cell_scan' list
                ListManager.getOrCreateList(LIST_CELL_SCAN, createList()).add(currentPeriod);
            }

            currentPeriod.end = data.get(data.size() - 1).scanTime.getTime();
        } else if (currentPeriod != null) {
            currentPeriod = null;
        }
    }

    private SharedList<Period> createList() {
        return new FileList<Period>(new Period(), "moving_periods", getApplicationContext());
    }
}
