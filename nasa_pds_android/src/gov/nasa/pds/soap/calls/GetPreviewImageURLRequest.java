/*
 * Copyright (C) 2012 TopCoder Inc., All Rights Reserved.
 */
package gov.nasa.pds.soap.calls;

/**
 * <p>
 * Java class for getPreviewImageURL complex type.
 *
 * <p>
 * The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="getPreviewImageURL">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="imageFileId" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
public class GetPreviewImageURLRequest {

    protected long imageFileId;

    /**
     * Gets the value of the imageFileId property.
     *
     */
    public long getImageFileId() {
        return imageFileId;
    }

    /**
     * Sets the value of the imageFileId property.
     *
     */
    public void setImageFileId(long value) {
        this.imageFileId = value;
    }

}
