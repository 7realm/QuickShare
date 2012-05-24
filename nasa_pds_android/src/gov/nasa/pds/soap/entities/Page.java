/*
 * Copyright (C) 2012 TopCoder Inc., All Rights Reserved.
 */
package gov.nasa.pds.soap.entities;

import gov.nasa.pds.soap.BaseObject;

/**
 * <p>
 * Java class for page complex type.
 *
 * <p>
 * The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="page">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="itemsPerPage" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="pageNumber" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 * @author 7realm
 * @version 1.0
 */
public class Page extends BaseObject {

    protected int itemsPerPage;
    protected int pageNumber;

    /**
     * Gets the value of the itemsPerPage property.
     *
     */
    public int getItemsPerPage() {
        return itemsPerPage;
    }

    /**
     * Sets the value of the itemsPerPage property.
     *
     */
    public void setItemsPerPage(int value) {
        this.itemsPerPage = value;
    }

    /**
     * Gets the value of the pageNumber property.
     *
     */
    public int getPageNumber() {
        return pageNumber;
    }

    /**
     * Sets the value of the pageNumber property.
     *
     */
    public void setPageNumber(int value) {
        this.pageNumber = value;
    }

}
