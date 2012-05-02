/*
 * Copyright (C) 2012 TopCoder Inc., All Rights Reserved.
 */
package gov.nasa.pds.data.queries;

import gov.nasa.pds.data.EntityType;
import gov.nasa.pds.data.QueryType;
import gov.nasa.pds.soap.calls.SearchEntitiesByTypeRequest;
import gov.nasa.pds.soap.calls.SearchEntitiesByTypeResponse;
import gov.nasa.pds.soap.entities.EntityInfo;
import gov.nasa.pds.soap.entities.Restriction;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapSerializationEnvelope;

public class SearchByTypePagedQuery extends PagedQuery {
    private final String text;
    private final EntityType entityType;

    public SearchByTypePagedQuery(String text, EntityType entityType) {
        this(text, entityType, null);
    }

    public SearchByTypePagedQuery(String text, EntityType entityType, Restriction restriction) {
        super(QueryType.SEARCH_BY_TYPE, restriction);
        this.text = text;
        this.entityType = entityType;
    }

    @Override
    public SoapSerializationEnvelope getEnvelope() {
        SearchEntitiesByTypeRequest request = new SearchEntitiesByTypeRequest();
        request.setSearchText(text);
        request.setPage(getPage());
        request.setRestriction(getRestriction());
        request.setEntityType(entityType.getClassName());

        return new SoapSerializationEnvelope(SoapEnvelope.VER11).addRequest(request)
            .addMapping("searchEntitiesByType", SearchEntitiesByTypeRequest.class)
            .addMapping("searchEntitiesByTypeResponse", SearchEntitiesByTypeResponse.class)
            .addMapping("results", EntityInfo.class);
    }

    @Override
    public EntityType getEntityType() {
        return entityType;
    }
}
