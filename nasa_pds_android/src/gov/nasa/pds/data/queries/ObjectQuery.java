/*
 * Copyright (C) 2012 TopCoder Inc., All Rights Reserved.
 */
package gov.nasa.pds.data.queries;

import gov.nasa.pds.data.QueryType;
import gov.nasa.pds.soap.calls.GetDataFileResponse;
import gov.nasa.pds.soap.calls.GetInstrumentResponse;
import gov.nasa.pds.soap.calls.GetMissionResponse;
import gov.nasa.pds.soap.calls.GetObjectRequest;
import gov.nasa.pds.soap.calls.GetTargetResponse;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapSerializationEnvelope;

import android.util.Log;

/**
 * Query that will retrieve information on particular entity.
 *
 * @author TCSASSEMBLER
 * @version 1.0
 */
public class ObjectQuery<T> extends BaseQuery {
    private final long id;

    public ObjectQuery(QueryType queryType, long id) {
        super(queryType);
        this.id = id;
    }

    public long getId() {
        return id;
    }

    @Override
    public SoapSerializationEnvelope getEnvelope() {
        GetObjectRequest request = new GetObjectRequest();
        request.setId(id);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11).addRequest(request);

        switch (getQueryType()) {
        case GET_TARGET:
            envelope.addMapping("getTarget", GetObjectRequest.class);
            envelope.addMapping("getTargetResponse", GetTargetResponse.class);
            break;
        case GET_MISSION:
            envelope.addMapping("getMission", GetObjectRequest.class);
            envelope.addMapping("getMissionResponse", GetMissionResponse.class);
            break;
        case GET_INSTRUMENT:
            envelope.addMapping("getInstrument", GetObjectRequest.class);
            envelope.addMapping("getInstrumentResponse", GetInstrumentResponse.class);
            break;
        case GET_FILE:
            envelope.addMapping("getDataFile", GetObjectRequest.class);
            envelope.addMapping("getDataFileResponse", GetDataFileResponse.class);
        default:
            Log.w("soap", "Not expected object request: " + getQueryType());
            break;
        }
        return envelope;
    }

}
