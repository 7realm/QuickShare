/*
 * Copyright (C) 2012 TopCoder Inc., All Rights Reserved.
 */
package gov.nasa.pds.soap.calls;

import gov.nasa.pds.soap.BaseObject;
import gov.nasa.pds.soap.entities.PagedResults;

/**
 * Base class for SOAP response that get list of entities.
 *
 * @author 7realm
 * @version 1.0
 */
public class GetEntitiesInfoResponse extends BaseObject {
    protected PagedResults _return;

    /**
     * Gets the value of the return property.
     *
     * @return possible object is {@link PagedResults }
     *
     */
    public PagedResults getReturn() {
        return _return;
    }

    /**
     * Sets the value of the return property.
     *
     * @param value allowed object is {@link PagedResults }
     *
     */
    public void setReturn(PagedResults value) {
        this._return = value;
    }

}
