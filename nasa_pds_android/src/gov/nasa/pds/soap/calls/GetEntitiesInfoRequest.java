/*
 * Copyright (C) 2012 TopCoder Inc., All Rights Reserved.
 */
package gov.nasa.pds.soap.calls;

import gov.nasa.pds.soap.BaseObject;
import gov.nasa.pds.soap.entities.Page;
import gov.nasa.pds.soap.entities.Restriction;

/**
 * Base class for SOAP request to get list of entities.
 *
 * @author 7realm
 * @version 1.0
 */
public class GetEntitiesInfoRequest extends BaseObject {
    protected Page page;
    protected Restriction restriction;

    public GetEntitiesInfoRequest() {
        super();
    }

    /**
     * Gets the value of the page property.
     *
     * @return possible object is {@link Page }
     *
     */
    public Page getPage() {
        return page;
    }

    /**
     * Sets the value of the page property.
     *
     * @param value allowed object is {@link Page }
     *
     */
    public void setPage(Page value) {
        this.page = value;
    }

    /**
     * Gets the value of the restriction property.
     *
     * @return possible object is {@link Restriction }
     *
     */
    public Restriction getRestriction() {
        return restriction;
    }

    /**
     * Sets the value of the restriction property.
     *
     * @param value allowed object is {@link Restriction }
     *
     */
    public void setRestriction(Restriction value) {
        this.restriction = value;
    }

}