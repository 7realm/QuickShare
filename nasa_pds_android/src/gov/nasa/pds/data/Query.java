/*
 * Copyright (C) 2012 TopCoder Inc., All Rights Reserved.
 */
package gov.nasa.pds.data;

import org.ksoap2.serialization.SoapSerializationEnvelope;

/**
 * Interface for different SOAP queries.
 *
 * @author 7realm
 * @version 1.0
 */
public interface Query {
    /**
     * Type of current query.
     *
     * @return the type of current query
     */
    QueryType getQueryType();

    /**
     * Creates SOAP envelope for this query.
     *
     * @return created envelope
     */
    SoapSerializationEnvelope getEnvelope();
}
