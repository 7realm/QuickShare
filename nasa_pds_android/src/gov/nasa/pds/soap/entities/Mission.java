/*
 * Copyright (C) 2012 TopCoder Inc., All Rights Reserved.
 */
package gov.nasa.pds.soap.entities;

import gov.nasa.pds.soap.ReferencedEntity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * Java class for mission complex type.
 *
 * <p>
 * The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="mission">
 *   &lt;complexContent>
 *     &lt;extension base="{http://pds.nasa.gov/}namedEntity">
 *       &lt;sequence>
 *         &lt;element name="description" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="endDate" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="otherChildren" type="{http://pds.nasa.gov/}metadataObject" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="references" type="{http://pds.nasa.gov/}reference" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="startDate" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 * @author 7realm
 * @version 1.0
 */
public class Mission extends ReferencedEntity {

    protected String description;
    protected Date endDate;
    protected List<MetadataObject> otherChildren;
    protected Date startDate;

    /**
     * Gets the value of the description property.
     *
     * @return possible object is {@link String }
     *
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the value of the description property.
     *
     * @param value allowed object is {@link String }
     *
     */
    public void setDescription(String value) {
        this.description = value;
    }

    /**
     * Gets the value of the endDate property.
     *
     * @return possible object is {@link Date }
     *
     */
    public Date getEndDate() {
        return endDate;
    }

    /**
     * Sets the value of the endDate property.
     *
     * @param value allowed object is {@link Date }
     *
     */
    public void setEndDate(Date value) {
        this.endDate = value;
    }

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
     * Gets the value of the startDate property.
     *
     * @return possible object is {@link Date }
     *
     */
    public Date getStartDate() {
        return startDate;
    }

    /**
     * Sets the value of the startDate property.
     *
     * @param value allowed object is {@link Date }
     *
     */
    public void setStartDate(Date value) {
        this.startDate = value;
    }

}
