package gov.nasa.pds.data;

import org.ksoap2.serialization.SoapSerializationEnvelope;

public interface Query {
    QueryType getQueryType();

    /**
     * Creates SOAP envelope for this query.
     *
     * @return created envelope
     */
    SoapSerializationEnvelope getEnvelope();
}
