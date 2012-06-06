/*
 * Copyright (C) 2012 TopCoder Inc., All Rights Reserved.
 */
package gov.nasa.pds.data.queries;

import gov.nasa.pds.data.EntityType;
import gov.nasa.pds.data.Query;
import gov.nasa.pds.data.QueryType;
import gov.nasa.pds.soap.calls.GetPreviewImageURLRequest;
import gov.nasa.pds.soap.calls.GetPreviewImageURLResponse;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapSerializationEnvelope;

public class GetPreviewImageQuery extends PagedQuery {
    private final long imageId;

    /**
     * Default constructor for GetPreviewImageQuery.
     */
    public GetPreviewImageQuery(long imageId) {
        super(QueryType.GET_PREVIEW);
        this.imageId = imageId;
    }

    /**
     * The result entity type of this query.
     *
     * @return the result entity type
     */
    @Override
    public EntityType getEntityType() {
        return EntityType.FILE;
    }

    /**
     * Creates SOAP envelope for this query.
     *
     * @return created envelope
     */
    @Override
    public SoapSerializationEnvelope getEnvelope() {
        // set request
        GetPreviewImageURLRequest request = new GetPreviewImageURLRequest();
        request.setImageFileId(imageId);

        // set envelope
        return new SoapSerializationEnvelope(SoapEnvelope.VER11).addRequest(request)
            .addMapping("getPreviewImageURL", GetPreviewImageURLRequest.class)
            .addMapping("getPreviewImageURLResponse", GetPreviewImageURLResponse.class);
    }

    @Override
    public boolean equalsQuery(Query other) {
        if (other instanceof GetPreviewImageQuery) {
            GetPreviewImageQuery imageQuery = (GetPreviewImageQuery) other;

            return super.equalsQuery(other) && imageId == imageQuery.imageId;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return super.hashCode() * 24 + new Long(imageId).hashCode();
    }
}
