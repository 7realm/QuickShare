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
import java.util.List;
import java.util.Map;
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

        private double[] getRssis() {
            double[] result = new double[rssis.size()];
            for (int i = 0; i < result.length; i++) {
                if (rssis.get(i) == 99) {
                    continue;
                }
                result[i] = rssis.get(i);
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
    public static final List<ScanData> DATA = new ArrayList<ScanData>();
    public static final Pattern DATA_PATTERN = Pattern.compile("Cell ID: (\\d*), LAC: (\\d*), RSSI: (\\d*)");
    public static final DecimalFormat DECIMAL = new DecimalFormat("0.00");
    public static final int COUNT_OF_MEASURES = 60;
    public static final int IGNORE_CELLS = 1;

    public static void main(String[] args) throws Exception {
        // first group
        // readFile("logs_13-04/LOG [13-04-2012 at 00-14].txt");
        // readFile("logs_13-04/LOG [13-04-2012 at 07-51].txt");
        // readFile("logs_13-04/LOG [13-04-2012 at 08-14].txt");
        // readFile("logs_13-04/LOG [13-04-2012 at 09-17].txt");
        // readFile("logs_13-04/LOG [13-04-2012 at 09-59].txt");
        // readFile("logs_13-04/LOG [13-04-2012 at 14-47].txt");
        // readFile("logs_13-04/LOG [13-04-2012 at 19-20].txt");
        // readFile("logs_13-04/LOG [13-04-2012 at 22-32].txt");

        // second group
        // readFile("logs_14-04/LOG [14-04-2012 at 09-28].txt");
        // readFile("logs_14-04/LOG [14-04-2012 at 14-39].txt");
        // readFile("logs_14-04/LOG [14-04-2012 at 17-43].txt");
        // readFile("logs_14-04/LOG [14-04-2012 at 18-40].txt");
        // readFile("logs_14-04/LOG [14-04-2012 at 22-47].txt");

        // third group
        readFile("logs_15-04/LOG [15-04-2012 at 10-47].txt");
        readFile("logs_15-04/LOG [15-04-2012 at 16-13].txt");
        readFile("logs_15-04/LOG [15-04-2012 at 17-39].txt");
        readFile("logs_15-04/LOG [15-04-2012 at 18-15].txt");

        // moving periods for main and more precise function
        List<Period> subMovingPeriods = calculateMovingPeriods(60, 25, 0);
        List<Period> mainMovingPeriods = calculateMovingPeriods(60, 20, 1);
        // correct main periods
        for (int i = 0; i < mainMovingPeriods.size(); i++) {
            mainMovingPeriods.get(i).start = subMovingPeriods.get(i).start;
        }

        System.out.println("Scanned, size: " + DATA.size());

        System.out.println("Main periods.");
        for (Period period : mainMovingPeriods) {
            System.out.println("Moved: " + DATE_LONG.format(period.start) + " - " + DATE_LONG.format(period.end));
        }
    }

    private static List<Period> calculateMovingPeriods(int scanPeriod, int threshold, int k) {
        List<Period> mainMovingPeriods = new ArrayList<Period>();
        Period mainCurrentPeriod = null;
        for (int i = 0; i < DATA.size() - scanPeriod; i++) {
            Map<Integer, CellCalculatedInfo> map = new TreeMap<Integer, CellCalculatedInfo>();

            // get scan data items
            ScanData currentScanData = DATA.get(i + k * scanPeriod);

            // fill map for next n measures
            int j = 0;
            while (j < scanPeriod) {
                for (CellData item : DATA.get(i + j).cellData) {
                    CellCalculatedInfo info = map.get(item.cellId);
                    if (info == null) {
                        info = new CellCalculatedInfo();
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

            // calculate move periods
            boolean moved = currentScanData.moveFunction > threshold;
            if (moved) {
                if (mainCurrentPeriod == null) {
                    mainCurrentPeriod = new Period();
                    mainCurrentPeriod.start = currentScanData.scanTime;
                    mainMovingPeriods.add(mainCurrentPeriod);
                }

                mainCurrentPeriod.end = currentScanData.scanTime;
            } else if (mainCurrentPeriod != null) {
                mainCurrentPeriod = null;
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
