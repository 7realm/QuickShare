package org.ksoap2.transport;

import gov.nasa.pds.soap.entities.DataHandler;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Map;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;

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

import android.util.Log;

import com.lib.Streams;

public class SoapEnvelopeExecutor {
    private static final String CONTENT_TYPE_XML_CHARSET_UTF_8 = "text/xml;charset=utf-8";
    private static final String CONTENT_TYPE_SOAP_XML_CHARSET_UTF_8 = "application/soap+xml;charset=utf-8";

    public static Object executeSoap(String url, SoapSerializationEnvelope envelope) throws IllegalStateException, IOException, XmlPullParserException {
        // get request data
        byte[] requestData = createRequestData(envelope);
        Log.d("soap", "Request: " + new String(requestData));

        // prepare POST method
        HttpPost request = new HttpPost(url);
        request.setHeader("User-Agent", "mobile");
        if (envelope.version != SoapEnvelope.VER12) {
            request.setHeader("SOAPAction", "\"\"");
        }

        // set Content-Type
        String contentType = envelope.version == SoapEnvelope.VER12 ?
            CONTENT_TYPE_SOAP_XML_CHARSET_UTF_8 : CONTENT_TYPE_XML_CHARSET_UTF_8;
        request.setHeader("Content-Type", contentType);
        request.setEntity(new ByteArrayEntity(requestData));

        // execute soap request
        HttpClient client = new DefaultHttpClient();
        HttpResponse response = client.execute(request);

        // log response data
        Log.d("soap", "Response status: " + response.getStatusLine());
        for (Header header : response.getAllHeaders()) {
            Log.d("soap", header.getName() + ": " + header.getValue());
        }

        Header contentTypeHeader = response.getFirstHeader("Content-Type");
        if (contentTypeHeader == null || !contentTypeHeader.getValue().contains("boundary")) {
            // parse content as for single part message
            Log.w("soap", "Missing Content-Type header in response.");
            parseResponse(envelope, response.getEntity().getContent());
        } else {
            // get multipart boundary
            byte[] boundary = getBoundary(contentTypeHeader.getValue());

            // parse content as multipart stream
            MultipartStream multipartStream = new MultipartStream(response.getEntity().getContent(), boundary);
            boolean nextPart = multipartStream.skipPreamble();
            if (nextPart) {
                multipartStream.readHeaders();

                // read part body
                ByteArrayOutputStream output = new ByteArrayOutputStream();
                multipartStream.readBodyData(output);
                parseResponse(envelope, new ByteArrayInputStream(output.toByteArray()));

                // advance to next part
                nextPart = multipartStream.readBoundary();
            }

            // process attachments
            while (nextPart) {
                // get content id index
                String header = multipartStream.readHeaders();
                int start = header.indexOf("Content-ID: <");
                int end = header.indexOf(">");
                if (start == -1 || end == -1) {
                    Log.w("soap", "Incorrect header at attachment: " + header);
                } else {
                    String contentId = header.substring(start + "Content-ID: <".length(), end);
                    Log.d("soap", "Parsing attachment with content ID : " + contentId);

                    // read attachment body
                    ByteArrayOutputStream output = new ByteArrayOutputStream();
                    multipartStream.readBodyData(output);

                    Inflater decompressor = new Inflater();
                    decompressor.setInput(output.toByteArray());
                    ByteArrayOutputStream decompressed = new ByteArrayOutputStream();
                    byte[] buf = new byte[Streams.DEFAULT_BUFFER_SIZE];
                    while (!decompressor.finished()) {
                        try {
                            int count = decompressor.inflate(buf);
                            decompressed.write(buf, 0, count);
                        } catch (DataFormatException e) {
                            Log.e("soap", "Failed to decompress attachment with content ID : " + contentId, e);
                        }
                    }

                    Streams.close(decompressed);

                    // set content to data handler
                    DataHandler dataHandler = MarshalAttachment.ATTACHMENTS.get("cid:" + contentId);
                    dataHandler.setContent(decompressed.toByteArray());
                }

                // advance to next part
                nextPart = multipartStream.readBoundary();
            }
        }
        return envelope.getResponse();
    }

    /**
     * Serializes the request.
     */
    protected static byte[] createRequestData(SoapEnvelope envelope) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bos.write("".getBytes());

        // serialize envelope
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

    /**
     * Gets multipart boundary parameter from Content-Type header.
     *
     * @param contentType content type header
     * @return boundary
     */
    protected static byte[] getBoundary(String contentType) {
        ParameterParser parser = new ParameterParser();
        parser.setLowerCaseNames(true);

        // parameter parser can handle null input
        Map<String, String> params = parser.parse(contentType, new char[] {';', ','});
        String boundaryStr = params.get("boundary");

        if (boundaryStr == null) {
            return null;
        }

        // try to parse boundary
        try {
            return boundaryStr.getBytes("ISO-8859-1");
        } catch (UnsupportedEncodingException e) {
            return boundaryStr.getBytes();
        }
    }

}
