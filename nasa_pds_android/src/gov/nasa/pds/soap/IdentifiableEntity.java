/*
 * Copyright (C) 2012 TopCoder Inc., All Rights Reserved.
 */
package gov.nasa.pds.soap;

/**
 * <p>Java class for identifiableEntity complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="identifiableEntity">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="id" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
public abstract class IdentifiableEntity extends BaseObject {

    protected long id;

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
        this.id = value;
    }

}
