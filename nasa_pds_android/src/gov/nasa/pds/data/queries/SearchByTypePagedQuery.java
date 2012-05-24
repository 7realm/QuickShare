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

/**
 * Query that will execute searchEntitiesByType request.
 *
 * @author 7realm
 * @version 1.0
 */
public class SearchByTypePagedQuery extends PagedQuery {
    private final String text;
    private final EntityType entityType;

    /**
     * Constructor for SearchByTypePagedQuery type.
     *
     * @param text the search text
     * @param entityType the result entity type
     */
    public SearchByTypePagedQuery(String text, EntityType entityType) {
        this(text, entityType, null);
    }

    /**
     * Constructor for SearchByTypePagedQuery type.
     *
     * @param text the search text
     * @param entityType the result entity type
     * @param restriction the search restriction
     */
    public SearchByTypePagedQuery(String text, EntityType entityType, Restriction restriction) {
        super(QueryType.SEARCH_BY_TYPE, restriction);
        this.text = text;
        this.entityType = entityType;
    }

    /**
     * Creates SOAP envelope for this query.
     *
     * @return created envelope
     */
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

    /**
     * Get result entity type
     *
     * @return the result entity type
     */
    @Override
    public EntityType getEntityType() {
        return entityType;
    }
}
