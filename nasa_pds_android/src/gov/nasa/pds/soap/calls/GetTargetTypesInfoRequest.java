/*
 * Copyright (C) 2012 TopCoder Inc., All Rights Reserved.
 */
package gov.nasa.pds.soap.calls;

import gov.nasa.pds.soap.BaseObject;
import gov.nasa.pds.soap.entities.Page;

/**
 * <p>
 * Java class for getTargetTypesInfo complex type.
 *
 * <p>
 * The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="getTargetTypesInfo">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="page" type="{http://pds.nasa.gov/}page" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 * @author 7realm
 * @version 1.0
 */
public class GetTargetTypesInfoRequest extends BaseObject {

    protected Page page;

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

}
