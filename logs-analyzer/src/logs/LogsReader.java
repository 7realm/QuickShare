package logs;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LogsReader {
    public static class CellData {
        private final int cellId;
        private final int lac;
        private final int rssi;

        private CellData(int cellId, int lac, int rssi) {
            this.cellId = cellId;
            this.lac = lac;
            this.rssi = rssi;
        }
    }

    public static class CellCalculatedInfo {
        private final List<Integer> rssis = new ArrayList<Integer>();
        private final int cellId;

        private CellCalculatedInfo(int cellId) {
            this.cellId = cellId;
        }

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

    public static class ScanData {
        private final String caption;
        private final List<CellData> cellData = new ArrayList<CellData>();
        private final Date scanTime;
        private double moveFunction;

        private ScanData(String caption) throws ParseException {
            this.caption = caption;

            // calculate scan time
            scanTime = DATE_LONG.parse(caption.substring(17));
        }

        private void addData(int cellId, int lac, int rssi) {
            cellData.add(new CellData(cellId, lac, rssi));
        }

        private void addData(String cellId, String lac, String rssi) {
            addData(Integer.parseInt(cellId), Integer.parseInt(lac), Integer.parseInt(rssi));
        }
    }

    private static class Period {
        private Date start;
        private Date end;
    }

    public static final DateFormat DATE_LONG = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss.SSS");
    public static final DateFormat DATE_ELAPSED = new SimpleDateFormat("HH:mm:ss");

    public static final List<ScanData> DATA = new ArrayList<ScanData>();
    public static final Pattern DATA_PATTERN = Pattern.compile("Cell ID: (\\d*), LAC: (\\d*), RSSI: (\\d*)");
    public static final DecimalFormat DECIMAL = new DecimalFormat("0.00");
    public static final int COUNT_OF_MEASURES = 60;
    public static final int IGNORE_CELLS = 1;

    public static final boolean REMOVE_EMPTY_SCANS = false;
    public static final boolean DUMP_MOVE_FUNCTION = true;

    public static void main(String[] args) throws Exception {
        DATE_ELAPSED.setTimeZone(TimeZone.getTimeZone("UTC"));

        // 13-04 group
        // readFile("logs_13-04/LOG [13-04-2012 at 00-14].txt");
        // readFile("logs_13-04/LOG [13-04-2012 at 07-51].txt");
        // readFile("logs_13-04/LOG [13-04-2012 at 08-14].txt");
        // readFile("logs_13-04/LOG [13-04-2012 at 09-17].txt");
        // readFile("logs_13-04/LOG [13-04-2012 at 09-59].txt");
        // readFile("logs_13-04/LOG [13-04-2012 at 14-47].txt");
        // readFile("logs_13-04/LOG [13-04-2012 at 19-20].txt");
        // readFile("logs_13-04/LOG [13-04-2012 at 22-32].txt");

        // 14-04 group
        // readFile("logs_14-04/LOG [14-04-2012 at 09-28].txt");
        // readFile("logs_14-04/LOG [14-04-2012 at 14-39].txt");
        // readFile("logs_14-04/LOG [14-04-2012 at 17-43].txt");
        // readFile("logs_14-04/LOG [14-04-2012 at 18-40].txt");
        // readFile("logs_14-04/LOG [14-04-2012 at 22-47].txt");

        // 15-04 group
        // readFile("logs_15-04/LOG [15-04-2012 at 10-47].txt");
        // readFile("logs_15-04/LOG [15-04-2012 at 16-13].txt");
        // readFile("logs_15-04/LOG [15-04-2012 at 17-39].txt");
        // readFile("logs_15-04/LOG [15-04-2012 at 18-15].txt");
        // readFile("logs_15-04/LOG [15-04-2012 at 21-05].txt");

        // 16-04 group
        // readFile("logs_16-04/LOG [16-04-2012 at 00-45].txt");
        // readFile("logs_16-04/LOG [16-04-2012 at 07-20].txt");
        // readFile("logs_16-04/LOG [16-04-2012 at 09-05].txt");
        // readFile("logs_16-04/LOG [16-04-2012 at 16-46].txt");
        // readFile("logs_16-04/LOG [16-04-2012 at 20-59].txt");

        // 17-04 group
        readFile("logs_17-04/LOG [17-04-2012 at 09-11].txt");
        readFile("logs_17-04/LOG [17-04-2012 at 14-09].txt");

        // remove empty scan
        removeEmptyScans();
        System.out.println("Scanned, size: " + DATA.size());

        // moving periods for main function
        List<Period> mainMovingPeriods = calculateMovingPeriods(60, 25);
        System.out.println("Main periods.");
        for (Period period : mainMovingPeriods) {
            System.out.println("Moved for " + DATE_ELAPSED.format(period.end.getTime() - period.start.getTime())
                + " from " + DATE_LONG.format(period.start) + " - " + DATE_LONG.format(period.end));
        }

        // moving periods for precise function
//        List<Period> subMovingPeriods = calculateMovingPeriods(40, 2);
//        System.out.println("Sub periods.");
//        for (Period period : subMovingPeriods) {
//            System.out.println("Moved for " + DATE_ELAPSED.format(period.end.getTime() - period.start.getTime())
//                + " from " + DATE_LONG.format(period.start) + " - " + DATE_LONG.format(period.end));
//        }
    }

    private static void removeEmptyScans() {
        if (!REMOVE_EMPTY_SCANS) {
            return;
        }

        for (Iterator<ScanData> i = DATA.iterator(); i.hasNext();) {
            if (i.next().cellData.size() == 0) {
                i.remove();
            }
        }
    }

    private static List<Period> calculateMovingPeriods(int scanPeriod, int threshold) {
        List<Period> mainMovingPeriods = new ArrayList<Period>();
        Period mainCurrentPeriod = null;
        for (int i = 0; i < DATA.size() - scanPeriod; i++) {
            Map<Integer, CellCalculatedInfo> map = new TreeMap<Integer, CellCalculatedInfo>();

            // get scan data items
            ScanData currentScanData = DATA.get(i + scanPeriod);

            // fill map for next n measures
            int j = 0;
            while (j < scanPeriod) {
                for (CellData item : DATA.get(i + j).cellData) {
                    CellCalculatedInfo info = map.get(item.cellId);
                    if (info == null) {
                        info = new CellCalculatedInfo(item.cellId);
                        map.put(item.cellId, info);
                    }
                    info.addRssi(item.rssi);
                }

                j++;
            }

            // fill measures to array
            // calculate change of RSSI
            double rssi = 0;
            int n = map.size();
            double[] data = new double[n];
            int index = 0;
            for (CellCalculatedInfo info : map.values()) {
                if (info.getCount() > IGNORE_CELLS) {
                    data[index++] = info.getCount();
                    rssi += deviation(info.getRssis());
                }
            }
            data = Arrays.copyOf(data, index);

            // average and deviation
            double deviation = deviation(data);

            // calculate move function
            currentScanData.moveFunction = rssi * rssi / deviation / deviation;
            if (deviation < 1) {
                currentScanData.moveFunction = deviation;
            }

            // calculate move periods
            boolean moved = currentScanData.moveFunction > threshold;
            if (moved) {
                if (mainCurrentPeriod == null) {
                    mainCurrentPeriod = new Period();
                    mainCurrentPeriod.start = DATA.get(i).scanTime;
                    mainMovingPeriods.add(mainCurrentPeriod);
                }

                mainCurrentPeriod.end = DATA.get(i).scanTime;
            } else if (mainCurrentPeriod != null) {
                mainCurrentPeriod = null;
            }

            if (DUMP_MOVE_FUNCTION) {
                System.out.println(currentScanData.caption + "\t" + DECIMAL.format(currentScanData.moveFunction)
//                    + "\tdeviation: " + DECIMAL.format(deviation)
                    + "\t" + DECIMAL.format(rssi)
                    // + "\tcounts: " + Arrays.toString(data)
                    );
            }
        }
        return mainMovingPeriods;
    }

    private static double average(double[] data) {
        double avg = 0;
        for (double element : data) {
            avg += element / data.length;
        }
        return avg;
    }

    private static double deviation(double[] data) {
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

    private static void readFile(String fileName) throws IOException, ParseException {
        BufferedReader reader = new BufferedReader(new FileReader(fileName));
        try {
            // current scan item
            ScanData currentScanData = null;

            while (true) {
                String line = reader.readLine();
                if (line == null) {
                    break;
                }

                // remove date-time from log line
                line = line.substring(32);

                if (line.startsWith("---Scan")) {
                    currentScanData = new ScanData(line);
                    DATA.add(currentScanData);
                } else if (line.startsWith("---Processed")) {
                    currentScanData = null;
                } else if (currentScanData != null) {
                    Matcher matcher = DATA_PATTERN.matcher(line);
                    if (matcher.matches()) {
                        currentScanData.addData(matcher.group(1), matcher.group(2), matcher.group(3));
                    }
                }
            }
        } finally {
            reader.close();
        }
    }
}
