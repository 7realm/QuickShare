/*
 * Copyright (C) 2012 TopCoder Inc., All Rights Reserved.
 */
package gov.nasa.pds.soap;

/**
 * <p>
 * Java class for namedEntity complex type.
 *
 * <p>
 * The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="namedEntity">
 *   &lt;complexContent>
 *     &lt;extension base="{http://pds.nasa.gov/}identifiableEntity">
 *       &lt;sequence>
 *         &lt;element name="name" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
public abstract class NamedEntity extends IdentifiableEntity {
    protected String name;

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
        this.name = value;
    }

}
