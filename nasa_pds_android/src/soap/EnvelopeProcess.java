package soap;

import gov.nasa.pds.soap.entities.DataHandler;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Map;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.serialization.marshals.MarshalAttachment;
import org.kxml2.io.KXmlParser;
import org.kxml2.io.KXmlSerializer;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;

public class EnvelopeProcess {
    private static final String CONTENT_TYPE_XML_CHARSET_UTF_8 = "text/xml;charset=utf-8";
    private static final String CONTENT_TYPE_SOAP_XML_CHARSET_UTF_8 = "application/soap+xml;charset=utf-8";
    private static final String URL = "http://50.19.174.233:8080/nasa_pds_ws/services/PlanetaryDataSystemPort";

    public static Object parseEnvelope(SoapSerializationEnvelope envelope) throws IllegalStateException, IOException, XmlPullParserException {
        // execute soap call
        byte[] requestData = createRequestData(envelope);

        String requestDump = new String(requestData);
        System.out.println("Request: " + requestDump);

        // prepare POST method
        HttpPost request = new HttpPost(URL);
        request.setHeader("User-Agent", "mobile");
        if (envelope.version != SoapEnvelope.VER12) {
            request.setHeader("SOAPAction", "\"\"");
        }

        // set Content-Type
        if (envelope.version == SoapEnvelope.VER12) {
            request.setHeader("Content-Type", CONTENT_TYPE_SOAP_XML_CHARSET_UTF_8);
        } else {
            request.setHeader("Content-Type", CONTENT_TYPE_XML_CHARSET_UTF_8);
        }
        request.setEntity(new ByteArrayEntity(requestData));

        HttpClient client = new DefaultHttpClient();
        HttpResponse response = client.execute(request);

        System.out.println("Response status: " + response.getStatusLine());
        for (Header header : response.getAllHeaders()) {
            System.out.println(header.getName() + ": " + header.getValue());
        }

        Header contentTypeHeader = response.getHeaders("Content-Type")[0];
        byte[] boundary = getBoundary(contentTypeHeader.getValue());
        System.out.println("Boundary: " + new String(boundary));

        MultipartStream multipartStream = new MultipartStream(response.getEntity().getContent(), boundary);
        boolean nextPart = multipartStream.skipPreamble();
        if (nextPart) {
            String header = multipartStream.readHeaders();
            System.out.println("Part header: " + header);

            // create some output stream
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            System.out.println("Parts body length: " + multipartStream.readBodyData(output));
            parseResponse(envelope, new ByteArrayInputStream(output.toByteArray()));
            nextPart = multipartStream.readBoundary();
        }

        while (nextPart) {
            String header = multipartStream.readHeaders();
            int start = header.indexOf("Content-ID: <");
            int end = header.indexOf(">");
            String contentId = header.substring(start + "Content-ID: <".length(), end);
            System.out.println("Content ID : " + contentId);

            ByteArrayOutputStream output = new ByteArrayOutputStream();
            System.out.println("Content length: " + multipartStream.readBodyData(output));
            DataHandler dataHandler = MarshalAttachment.ATTACHMENTS.get("cid:" + contentId);
            dataHandler.setContent(output.toByteArray());

            nextPart = multipartStream.readBoundary();
        }

        return envelope.getResponse();
    }

    /**
     * Serializes the request.
     */
    protected static byte[] createRequestData(SoapEnvelope envelope) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bos.write("".getBytes());

        XmlSerializer xw = new KXmlSerializer();
        xw.setOutput(bos, null);
        envelope.write(xw);
        xw.flush();

        bos.write('\r');
        bos.write('\n');
        bos.flush();
        return bos.toByteArray();
    }

    /**
     * Sets up the parsing to hand over to the envelope to deserialize.
     */
    protected static void parseResponse(SoapEnvelope envelope, InputStream is) throws XmlPullParserException, IOException {
        XmlPullParser xp = new KXmlParser();
        xp.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, true);
        xp.setInput(is, null);
        envelope.parse(xp);
    }

    protected static byte[] getBoundary(String contentType) {
        ParameterParser parser = new ParameterParser();
        parser.setLowerCaseNames(true);
        // Parameter parser can handle null input
        Map<String, String> params = parser.parse(contentType, new char[] { ';', ',' });
        String boundaryStr = params.get("boundary");

        if (boundaryStr == null) {
            return null;
        }
        byte[] boundary;
        try {
            boundary = boundaryStr.getBytes("ISO-8859-1");
        } catch (UnsupportedEncodingException e) {
            boundary = boundaryStr.getBytes();
        }
        return boundary;
    }

}
