/*
 * Copyright (C) 2012 TopCoder Inc., All Rights Reserved.
 */
package gov.nasa.pds.soap.calls;

import gov.nasa.pds.soap.BaseObject;
import gov.nasa.pds.soap.entities.Page;
import gov.nasa.pds.soap.entities.Restriction;

/**
 * <p>
 * Java class for searchEntitiesByType complex type.
 *
 * <p>
 * The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="searchEntitiesByType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="entityType" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="searchText" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="page" type="{http://pds.nasa.gov/}page" minOccurs="0"/>
 *         &lt;element name="restriction" type="{http://pds.nasa.gov/}restriction" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 * @author 7realm
 * @version 1.0
 */
public class SearchEntitiesByTypeRequest extends BaseObject {

    protected String entityType;
    protected String searchText;
    protected Page page;
    protected Restriction restriction;

    /**
     * Gets the value of the entityType property.
     *
     * @return possible object is {@link String }
     *
     */
    public String getEntityType() {
        return entityType;
    }

    /**
     * Sets the value of the entityType property.
     *
     * @param value allowed object is {@link String }
     *
     */
    public void setEntityType(String value) {
        this.entityType = value;
    }

    /**
     * Gets the value of the searchText property.
     *
     * @return possible object is {@link String }
     *
     */
    public String getSearchText() {
        return searchText;
    }

    /**
     * Sets the value of the searchText property.
     *
     * @param value allowed object is {@link String }
     *
     */
    public void setSearchText(String value) {
        this.searchText = value;
    }

    /**
     * Gets the value of the page property.
     *
     * @return possible object is {@link Page }
     *
     */
    public Page getPage() {
        return page;
    }

    /**
     * Sets the value of the page property.
     *
     * @param value allowed object is {@link Page }
     *
     */
    public void setPage(Page value) {
        this.page = value;
    }

    /**
     * Gets the value of the restriction property.
     *
     * @return possible object is {@link Restriction }
     *
     */
    public Restriction getRestriction() {
        return restriction;
    }

    /**
     * Sets the value of the restriction property.
     *
     * @param value allowed object is {@link Restriction }
     *
     */
    public void setRestriction(Restriction value) {
        this.restriction = value;
    }

}
