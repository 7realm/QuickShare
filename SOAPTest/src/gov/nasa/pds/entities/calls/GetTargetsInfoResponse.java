package gov.nasa.pds.entities.calls;

import gov.nasa.pds.entities.BaseObject;
import gov.nasa.pds.entities.response.PagedResults;


/**
 * <p>Java class for getTargetsInfoResponse complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="getTargetsInfoResponse">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="return" type="{http://pds.nasa.gov/}pagedResults" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
public class GetTargetsInfoResponse extends BaseObject {
    protected PagedResults _return;

    /**
     * Gets the value of the return property.
     *
     * @return
     *     possible object is
     *     {@link PagedResults }
     *
     */
    public PagedResults getReturn() {
        return _return;
    }

    /**
     * Sets the value of the return property.
     *
     * @param value
     *     allowed object is
     *     {@link PagedResults }
     *
     */
    public void setReturn(PagedResults value) {
        this._return = value;
    }

}
