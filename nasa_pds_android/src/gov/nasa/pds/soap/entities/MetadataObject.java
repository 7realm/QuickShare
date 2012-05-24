/*
 * Copyright (C) 2012 TopCoder Inc., All Rights Reserved.
 */
package gov.nasa.pds.soap.entities;

import gov.nasa.pds.soap.NamedEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * Java class for metadataObject complex type.
 *
 * <p>
 * The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="metadataObject">
 *   &lt;complexContent>
 *     &lt;extension base="{http://pds.nasa.gov/}namedEntity">
 *       &lt;sequence>
 *         &lt;element name="children" type="{http://pds.nasa.gov/}metadataObject" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="properties" type="{http://pds.nasa.gov/}property" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 * @author 7realm
 * @version 1.0
 */
public class MetadataObject extends NamedEntity {

    protected List<MetadataObject> children;
    protected List<Property> properties;

    /**
     * Gets the value of the children property.
     *
     * <p>
     * This accessor method returns a reference to the live list, not a snapshot. Therefore any modification you make to
     * the returned list will be present inside the JAXB object. This is why there is not a <CODE>set</CODE> method for
     * the children property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     *
     * <pre>
     * getChildren().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list {@link MetadataObject }
     *
     *
     */
    public List<MetadataObject> getChildren() {
        if (children == null) {
            children = new ArrayList<MetadataObject>();
        }
        return this.children;
    }

    /**
     * Gets the value of the properties property.
     *
     * <p>
     * This accessor method returns a reference to the live list, not a snapshot. Therefore any modification you make to
     * the returned list will be present inside the JAXB object. This is why there is not a <CODE>set</CODE> method for
     * the properties property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     *
     * <pre>
     * getProperties().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list {@link Property }
     *
     *
     */
    public List<Property> getProperties() {
        if (properties == null) {
            properties = new ArrayList<Property>();
        }
        return this.properties;
    }

}
