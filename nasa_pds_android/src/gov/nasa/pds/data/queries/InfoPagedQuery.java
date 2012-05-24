/*
 * Copyright (C) 2012 TopCoder Inc., All Rights Reserved.
 */
package gov.nasa.pds.data.queries;

import gov.nasa.pds.data.EntityType;
import gov.nasa.pds.data.QueryType;
import gov.nasa.pds.soap.calls.GetEntitiesInfoRequest;
import gov.nasa.pds.soap.calls.GetEntitiesInfoResponse;
import gov.nasa.pds.soap.entities.EntityInfo;
import gov.nasa.pds.soap.entities.Restriction;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapSerializationEnvelope;

import android.util.Log;

/**
 * Query that will retrieve list of objects using XXXInfo requests.
 *
 * @author 7realm
 * @version 1.0
 */
public class InfoPagedQuery extends PagedQuery {

    /**
     * Constructor for InfoPagedQuery type.
     *
     * @param queryType query type
     */
    public InfoPagedQuery(QueryType queryType) {
        this(queryType, null);
    }

    /**
     * Constructor for InfoPagedQuery type.
     *
     * @param queryType query type
     * @param restriction query restriction
     */
    public InfoPagedQuery(QueryType queryType, Restriction restriction) {
        super(queryType, restriction);
    }

    /**
     * Get result entity type
     *
     * @return the result entity type
     */
    @Override
    public EntityType getEntityType() {
        switch (getQueryType()) {
        case GET_TYPES_INFO:
            return EntityType.TARGET_TYPE;
        case GET_TARGETS_INFO:
            return EntityType.TARGET;
        case GET_MISSIONS_INFO:
            return EntityType.MISSION;
        case GET_INSTRUMENTS_INFO:
            return EntityType.INSTRUMENT;
        case GET_DOCUMENTS_INFO:
        default:
            return EntityType.FILE;
        }
    }

    /**
     * Creates SOAP envelope for this query.
     *
     * @return created envelope
     */
    @Override
    public SoapSerializationEnvelope getEnvelope() {
        // create entities request based on page and restriction
        GetEntitiesInfoRequest request = new GetEntitiesInfoRequest();
        request.setPage(getPage());
        request.setRestriction(getRestriction());

        // create base envelope
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11).addRequest(request)
            .addMapping("results", EntityInfo.class);

        // add mappings based on query type
        switch (getQueryType()) {
        case GET_TARGETS_INFO:
            envelope.addMapping("getTargetsInfo", GetEntitiesInfoRequest.class);
            envelope.addMapping("getTargetsInfoResponse", GetEntitiesInfoResponse.class);
            break;
        case GET_MISSIONS_INFO:
            envelope.addMapping("getMissionsInfo", GetEntitiesInfoRequest.class);
            envelope.addMapping("getMissionsInfoResponse", GetEntitiesInfoResponse.class);
            break;
        case GET_INSTRUMENTS_INFO:
            envelope.addMapping("getInstrumentsInfo", GetEntitiesInfoRequest.class);
            envelope.addMapping("getInstrumentsInfoResponse", GetEntitiesInfoResponse.class);
            break;
        case GET_DOCUMENTS_INFO:
            envelope.addMapping("getDocumentsInfo", GetEntitiesInfoRequest.class);
            envelope.addMapping("getDocumentsInfoResponse", GetEntitiesInfoResponse.class);
            break;
        default:
            Log.w("soap", "Not expected entities info request: " + getQueryType());
            break;
        }

        return envelope;
    }
}
