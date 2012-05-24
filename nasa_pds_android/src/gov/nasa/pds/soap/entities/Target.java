/*
 * Copyright (C) 2012 TopCoder Inc., All Rights Reserved.
 */
package gov.nasa.pds.soap.entities;

import gov.nasa.pds.soap.ReferencedEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * Java class for target complex type.
 *
 * <p>
 * The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="target">
 *   &lt;complexContent>
 *     &lt;extension base="{http://pds.nasa.gov/}namedEntity">
 *       &lt;sequence>
 *         &lt;element name="references" type="{http://pds.nasa.gov/}reference" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="types" type="{http://pds.nasa.gov/}targetType" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 * @author 7realm
 * @version 1.0
 */
public class Target extends ReferencedEntity {
    protected List<TargetType> types;

    /**
     * Gets the value of the types property.
     *
     * <p>
     * This accessor method returns a reference to the live list, not a snapshot. Therefore any modification you make to
     * the returned list will be present inside the JAXB object. This is why there is not a <CODE>set</CODE> method for
     * the types property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     *
     * <pre>
     * getTypes().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list {@link TargetType }
     *
     *
     */
    public List<TargetType> getTypes() {
        if (types == null) {
            types = new ArrayList<TargetType>();
        }
        return this.types;
    }

}
