/*
 * Copyright (C) 2012 TopCoder Inc., All Rights Reserved.
 */
package gov.nasa.pds.soap.calls;

import gov.nasa.pds.soap.BaseObject;
import gov.nasa.pds.soap.entities.WsDataFile;

/**
 * <p>
 * Java class for getDataFileResponse complex type.
 *
 * <p>
 * The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="getDataFileResponse">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="return" type="{http://pds.nasa.gov/}wsDataFile" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 * @author 7realm
 * @version 1.0
 */
public class GetDataFileResponse extends BaseObject {

    protected WsDataFile _return;

    /**
     * Gets the value of the return property.
     *
     * @return possible object is {@link WsDataFile }
     *
     */
    public WsDataFile getReturn() {
        return _return;
    }

    /**
     * Sets the value of the return property.
     *
     * @param value allowed object is {@link WsDataFile }
     *
     */
    public void setReturn(WsDataFile value) {
        this._return = value;
    }

}
