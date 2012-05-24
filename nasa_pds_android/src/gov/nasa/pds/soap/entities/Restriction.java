/*
 * Copyright (C) 2012 TopCoder Inc., All Rights Reserved.
 */
package gov.nasa.pds.soap.entities;

import gov.nasa.pds.soap.BaseObject;

/**
 * Java class for restriction complex type.
 *
 * <p>
 * The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="restriction">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="restrictionEntityClass" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="restrictionEntityId" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 * @author 7realm
 * @version 1.0
 */
public class Restriction extends BaseObject {

    protected String restrictionEntityClass;
    protected long restrictionEntityId;

    /**
     * Gets the value of the restrictionEntityClass property.
     *
     * @return possible object is {@link String }
     *
     */
    public String getRestrictionEntityClass() {
        return restrictionEntityClass;
    }

    /**
     * Sets the value of the restrictionEntityClass property.
     *
     * @param value allowed object is {@link String }
     *
     */
    public void setRestrictionEntityClass(String value) {
        this.restrictionEntityClass = value;
    }

    /**
     * Gets the value of the restrictionEntityId property.
     *
     */
    public long getRestrictionEntityId() {
        return restrictionEntityId;
    }

    /**
     * Sets the value of the restrictionEntityId property.
     *
     */
    public void setRestrictionEntityId(long value) {
        this.restrictionEntityId = value;
    }

}
