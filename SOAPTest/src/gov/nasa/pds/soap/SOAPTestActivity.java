package gov.nasa.pds.soap;

import gov.nasa.pds.entities.calls.GetTargetTypesInfoRequest;
import gov.nasa.pds.entities.calls.GetTargetTypesInfoResponse;
import gov.nasa.pds.entities.calls.SearchEntitiesRequest;
import gov.nasa.pds.entities.calls.SearchEntitiesResponse;
import gov.nasa.pds.entities.response.EntityInfo;
import gov.nasa.pds.entities.response.PagedResults;

import java.io.IOException;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.SoapFault;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

public class SOAPTestActivity extends Activity {
    private static final String METHOD_NAME = "getTargetTypesInfo";
    private static final String SOAP_ACTION = "http://pds.nasa.gov/PlanetaryDataSystemService";
    private static final String NAMESPACE = "http://pds.nasa.gov/";
    private static final String URL = "http://192.168.0.101:8080/nasa_pds_ws/services/PlanetaryDataSystemPort";

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        new CallService().execute();
    }

    private class CallService extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... arg0) {
            try {
                SoapSerializationEnvelope envelope = searchEntities();

                executeMethod(envelope);
            } catch (Exception e) {
                Log.e("soap", "General error: " + e.getMessage());
                e.printStackTrace();
            }
            return null;
        }

        private SoapSerializationEnvelope getTargetTypesInfo() {
            GetTargetTypesInfoRequest request = new GetTargetTypesInfoRequest();

            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.setOutputSoapObject(request);

            envelope.addMapping(NAMESPACE, "getTargetTypesInfo", GetTargetTypesInfoRequest.class);
            envelope.addMapping(NAMESPACE, "getTargetTypesInfoResponse", GetTargetTypesInfoResponse.class);
            envelope.addMapping(NAMESPACE, "return", PagedResults.class);
            envelope.addMapping(NAMESPACE, "entityInfo", EntityInfo.class);
            return envelope;
        }

        private SoapSerializationEnvelope searchEntities() {
            SearchEntitiesRequest request = new SearchEntitiesRequest();
            request.setSearchText("deep");

            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.setOutputSoapObject(request);

            envelope.addMapping(NAMESPACE, "searchEntities", SearchEntitiesRequest.class);
            envelope.addMapping(NAMESPACE, "searchEntitiesResponse", SearchEntitiesResponse.class);
            envelope.addMapping(NAMESPACE, "entityInfo", EntityInfo.class);

            return envelope;
        }

        private void executeMethod(SoapSerializationEnvelope envelope) throws IOException, XmlPullParserException, SoapFault {
            HttpTransportSE httpTransport = new HttpTransportSE(URL);
            httpTransport.debug = true;

            httpTransport.call(null, envelope);

            Log.d("soap", "Request DUMP: " + httpTransport.requestDump);
            Log.d("soap", "Response DUMP: " + httpTransport.responseDump);
            try {
                Object result = envelope.getResponse();
                // Log.i("soap", "Result property count: " + result.getPropertyCount());
                // for (int i = 0; i < result.getPropertyCount(); i++) {
                // Log.i("soap", "Result [" + i + "] property : " + result.getProperty(i).getClass());
                // }
                Log.i("soap", "Result: " + result);
            } catch (SoapFault soapFault) {
                Log.e("soap", "Error: " + soapFault.faultstring);
                throw soapFault;
            }
        }
    }

    public void callService(View v) {
        new CallService().execute();
    }
}