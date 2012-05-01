package gov.nasa.pds.data;

import gov.nasa.pds.data.queries.ObjectQuery;
import gov.nasa.pds.data.queries.PagedQuery;
import gov.nasa.pds.soap.entities.PagedResults;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.ksoap2.SoapFault;
import org.ksoap2.serialization.KvmSerializable;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.ksoap2.transport.Transport;
import org.xmlpull.v1.XmlPullParserException;

import android.util.Log;

public class DataCenter {
    private static DateFormat DATE_LONG = new SimpleDateFormat("HH:mm M dd yyyy");
    private static DateFormat DATE_PERIOD = new SimpleDateFormat("yyyy 'years' D 'days' HH 'hours' mm 'minutes.'");

    private static final String URL = "http://192.168.0.101:8080/nasa_pds_ws/services/PlanetaryDataSystemPort";

    public static String formatLong(Date date) {
        return date == null ? "" : DATE_LONG.format(date);
    }

    public static String formatPeriod(Date date) {
        // TODO
        DATE_PERIOD.setTimeZone(TimeZone.getTimeZone("UTC"));
        return date == null ? "" : DATE_PERIOD.format(date);
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
        Transport httpTransport = new HttpTransportSE(URL);
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
}
