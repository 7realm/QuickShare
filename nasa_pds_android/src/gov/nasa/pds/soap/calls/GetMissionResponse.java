/*
 * Copyright (C) 2012 TopCoder Inc., All Rights Reserved.
 */
package gov.nasa.pds.soap.calls;

import gov.nasa.pds.soap.BaseObject;
import gov.nasa.pds.soap.entities.Mission;

/**
 * <p>
 * Java class for getMissionResponse complex type.
 *
 * <p>
 * The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="getMissionResponse">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="return" type="{http://pds.nasa.gov/}mission" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 * @author 7realm
 * @version 1.0
 */
public class GetMissionResponse extends BaseObject {
    protected Mission _return;

    /**
     * Gets the value of the return property.
     *
     * @return possible object is {@link Mission }
     *
     */
    public Mission getReturn() {
        return _return;
    }

    /**
     * Sets the value of the return property.
     *
     * @param value allowed object is {@link Mission }
     *
     */
    public void setReturn(Mission value) {
        this._return = value;
    }

}
