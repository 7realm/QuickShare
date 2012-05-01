package gov.nasa.pds.data;

import gov.nasa.pds.data.queries.ObjectQuery;
import gov.nasa.pds.data.queries.PagedQuery;
import gov.nasa.pds.soap.entities.PagedResults;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.ksoap2.SoapFault;
import org.ksoap2.serialization.KvmSerializable;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.ksoap2.transport.Transport;
import org.xmlpull.v1.XmlPullParserException;

import android.util.Log;

public class DataCenter {
    private static final int DAYS_PER_YEAR = 365;
    private static final int HOURS_PER_DAY = 24;
    private static final int MINUTES_PER_HOUR = 60;
    private static final int MILLISECONDS_PER_MINUTE = 60 * 1000;
    private static DateFormat DATE_LONG = new SimpleDateFormat("MMMMM dd yyyy 'at' HH:mm");

    private static final String URL_PATTERN = "http://%s:8080/nasa_pds_ws/services/PlanetaryDataSystemPort";

    private static String url = "192.168.0.100";

    public static String formatLong(Date date) {
        return date == null ? "" : DATE_LONG.format(date);
    }

    @SuppressWarnings("deprecation")
    public static String formatPeriod(Date startDate, Date endDate) {
        // handle null values
        if (endDate == null) {
            endDate = new Date();
        } else if (startDate == null) {
            startDate = endDate;
        }

        // calculate duration
        long duration = endDate.getTime() - startDate.getTime();

        // get time periods
        long minutes = duration / MILLISECONDS_PER_MINUTE;
        long hours = minutes / MINUTES_PER_HOUR;
        long days = hours / HOURS_PER_DAY;
        long years = days / DAYS_PER_YEAR;

        // correct days because of Feb 29
        days -= endDate.getYear() / 4 - startDate.getYear() / 4;

        return String.format("%d years %d days %02d hours %02d mins",
            years, days % DAYS_PER_YEAR, hours % HOURS_PER_DAY, minutes % MINUTES_PER_HOUR);
    }

    public static PagedResults executePagedQuery(PagedQuery query) {
        Log.i("soap", "Executing paged query: " + query.getQueryType());

        PagedResults result = (PagedResults) executeMethod(query.getEnvelope());

        if (result == null) {
            result = new PagedResults();
            result.setTotal(0);
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    public static <T> T executeObjectQuery(ObjectQuery<T> query) {
        Log.i("soap", "Executing object query: " + query.getQueryType());

        return (T) executeMethod(query.getEnvelope());
    }

    @SuppressWarnings("unused")
    public static boolean isCached(Query query) {
        return false;
    }

    private static Object executeMethod(SoapSerializationEnvelope envelope) {
        Transport httpTransport = new HttpTransportSE(String.format(URL_PATTERN, url));
        httpTransport.debug = true;

        try {
            // execute soap call
            httpTransport.call(null, envelope);

            // dump result and response
            Log.d("soap", "Request DUMP: " + httpTransport.requestDump);
            Log.d("soap", "Response DUMP: " + httpTransport.responseDump);

            Object result = envelope.getResponse();

            // dump result
            Log.i("soap", "Result: " + result);
            if (result instanceof KvmSerializable) {
                dumpProperties((KvmSerializable) result, "");
            }

            return result;
        } catch (SoapFault soapFault) {
            Log.e("soap", "Soap fault : " + soapFault.faultstring);
        } catch (IOException e) {
            Log.e("soap", "I/O error: " + e.getMessage(), e);
        } catch (XmlPullParserException e) {
            Log.e("soap", "Xml parser error: " + e.getMessage(), e);
        } catch (Exception e) {
            Log.e("soap", "Unexpected exception when calling soap method: " + e.getMessage(), e);
        }
        return null;

    }

    private static void dumpProperties(KvmSerializable object, String indent) {
        for (int i = 0; i < object.getPropertyCount(); i++) {
            PropertyInfo info = object.getPropertyInfo(i, null);
            Object value = object.getProperty(i);
            Log.i("soap", indent + info.getName() + " = " + value);
            if (value instanceof KvmSerializable) {
                dumpProperties((KvmSerializable) value, indent + "\t");
            } else if (value instanceof List) {
                List<?> list = (List<?>) value;
                for (Object item : list) {
                    if (item instanceof KvmSerializable) {
                        dumpProperties((KvmSerializable) item, indent + " ");
                    }
                }
            }
        }
    }

    public static boolean testConnection() {
        try {
            // execute simple request
            HttpClient httpclient = new DefaultHttpClient();
            HttpGet httpget = new HttpGet(String.format(URL_PATTERN, url));
            httpclient.execute(httpget);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public static String getUrl() {
        return url;
    }

    public static void setUrl(String url) {
        DataCenter.url = url;
    }
}
