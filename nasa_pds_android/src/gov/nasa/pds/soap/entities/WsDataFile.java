/*
 * Copyright (C) 2012 TopCoder Inc., All Rights Reserved.
 */
package gov.nasa.pds.soap.entities;

import gov.nasa.pds.soap.BaseObject;

/**
 * Java class for wsDataFile complex type.
 * <p>
 * The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="wsDataFile">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="content" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="dataHandler" type="{http://www.w3.org/2001/XMLSchema}base64Binary" minOccurs="0"/>
 *         &lt;element name="filename" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="id" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="name" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * @author 7realm
 * @version 1.0
 */
public class WsDataFile extends BaseObject {

    protected String content;
    protected DataHandler dataHandler;
    protected String filename;
    protected long id;
    protected String name;

    /**
     * Gets the value of the content property.
     * 
     * @return possible object is {@link String }
     * 
     */
    public String getContent() {
        return content;
    }

    /**
     * Sets the value of the content property.
     * 
     * @param value allowed object is {@link String }
     * 
     */
    public void setContent(String value) {
        content = value;
    }

    /**
     * Gets the value of the dataHandler property.
     * 
     * @return possible object is {@link DataHandler }
     * 
     */
    public DataHandler getDataHandler() {
        return dataHandler;
    }

    /**
     * Sets the value of the dataHandler property.
     * 
     * @param value allowed object is {@link DataHandler }
     * 
     */
    public void setDataHandler(DataHandler value) {
        dataHandler = value;
    }

    /**
     * Gets the value of the filename property.
     * 
     * @return possible object is {@link String }
     * 
     */
    public String getFilename() {
        return filename;
    }

    /**
     * Sets the value of the filename property.
     * 
     * @param value allowed object is {@link String }
     * 
     */
    public void setFilename(String value) {
        filename = value;
    }

    /**
     * Gets the value of the id property.
     * 
     */
    public long getId() {
        return id;
    }

    /**
     * Sets the value of the id property.
     * 
     */
    public void setId(long value) {
        id = value;
    }

    /**
     * Gets the value of the name property.
     * 
     * @return possible object is {@link String }
     * 
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the value of the name property.
     * 
     * @param value allowed object is {@link String }
     * 
     */
    public void setName(String value) {
        name = value;
    }

}
