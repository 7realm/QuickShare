/*
 * Copyright (C) 2012 TopCoder Inc., All Rights Reserved.
 */
package gov.nasa.pds.soap.entities;

import gov.nasa.pds.soap.ReferencedEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * Java class for instrumentHost complex type.
 *
 * <p>
 * The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="instrumentHost">
 *   &lt;complexContent>
 *     &lt;extension base="{http://pds.nasa.gov/}namedEntity">
 *       &lt;sequence>
 *         &lt;element name="otherChildren" type="{http://pds.nasa.gov/}metadataObject" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="references" type="{http://pds.nasa.gov/}reference" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="textId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 * @author 7realm
 * @version 1.0
 */
public class InstrumentHost extends ReferencedEntity {

    protected List<MetadataObject> otherChildren;
    protected String textId;

    /**
     * Gets the value of the otherChildren property.
     *
     * <p>
     * This accessor method returns a reference to the live list, not a snapshot. Therefore any modification you make to
     * the returned list will be present inside the JAXB object. This is why there is not a <CODE>set</CODE> method for
     * the otherChildren property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     *
     * <pre>
     * getOtherChildren().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list {@link MetadataObject }
     *
     *
     */
    public List<MetadataObject> getOtherChildren() {
        if (otherChildren == null) {
            otherChildren = new ArrayList<MetadataObject>();
        }
        return this.otherChildren;
    }

    /**
     * Gets the value of the textId property.
     *
     * @return possible object is {@link String }
     *
     */
    public String getTextId() {
        return textId;
    }

    /**
     * Sets the value of the textId property.
     *
     * @param value allowed object is {@link String }
     *
     */
    public void setTextId(String value) {
        this.textId = value;
    }

}
