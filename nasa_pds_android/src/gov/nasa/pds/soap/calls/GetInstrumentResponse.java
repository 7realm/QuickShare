/*
 * Copyright (C) 2012 TopCoder Inc., All Rights Reserved.
 */
package gov.nasa.pds.soap.calls;

import gov.nasa.pds.soap.BaseObject;
import gov.nasa.pds.soap.entities.Instrument;

/**
 * <p>
 * Java class for getInstrumentResponse complex type.
 *
 * <p>
 * The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="getInstrumentResponse">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="return" type="{http://pds.nasa.gov/}instrument" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 * @author 7realm
 * @version 1.0
 */
public class GetInstrumentResponse extends BaseObject {
    protected Instrument _return;

    /**
     * Gets the value of the return property.
     *
     * @return possible object is {@link Instrument }
     *
     */
    public Instrument getReturn() {
        return _return;
    }

    /**
     * Sets the value of the return property.
     *
     * @param value allowed object is {@link Instrument }
     *
     */
    public void setReturn(Instrument value) {
        this._return = value;
    }

}
