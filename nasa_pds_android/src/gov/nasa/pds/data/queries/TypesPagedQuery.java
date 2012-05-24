/*
 * Copyright (C) 2012 TopCoder Inc., All Rights Reserved.
 */
package gov.nasa.pds.data.queries;

import gov.nasa.pds.data.EntityType;
import gov.nasa.pds.data.QueryType;
import gov.nasa.pds.soap.calls.GetTargetTypesInfoRequest;
import gov.nasa.pds.soap.calls.GetTargetTypesInfoResponse;
import gov.nasa.pds.soap.entities.EntityInfo;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapSerializationEnvelope;

/**
 * Query that will return all Target Types.
 *
 * @author 7realm
 * @version 1.0
 */
public class TypesPagedQuery extends PagedQuery {

    /**
     * Default constructor for TypesPagedQuery type.
     */
    public TypesPagedQuery() {
        super(QueryType.GET_TYPES_INFO);
    }

    /**
     * The result entity type of this query.
     *
     * @return the result entity type
     */
    @Override
    public EntityType getEntityType() {
        return EntityType.TARGET_TYPE;
    }

    /**
     * Creates SOAP envelope for this query.
     *
     * @return created envelope
     */
    @Override
    public SoapSerializationEnvelope getEnvelope() {
        return new SoapSerializationEnvelope(SoapEnvelope.VER11).addRequest(new GetTargetTypesInfoRequest())
            .addMapping("getTargetTypesInfo", GetTargetTypesInfoRequest.class)
            .addMapping("getTargetTypesInfoResponse", GetTargetTypesInfoResponse.class)
            .addMapping("results", EntityInfo.class);
    }

}
