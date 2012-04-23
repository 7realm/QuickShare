package gov.nasa.pds.entities.calls;

import gov.nasa.pds.entities.BaseObject;
import gov.nasa.pds.entities.request.Page;
import gov.nasa.pds.entities.request.Restriction;


/**
 * <p>Java class for getTargetsInfo complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="getTargetsInfo">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="page" type="{http://pds.nasa.gov/}page" minOccurs="0"/>
 *         &lt;element name="restriction" type="{http://pds.nasa.gov/}restriction" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
public class GetTargetsInfoRequest extends BaseObject  {
    protected Page page;
    protected Restriction restriction;

    /**
     * Gets the value of the page property.
     *
     * @return
     *     possible object is
     *     {@link Page }
     *
     */
    public Page getPage() {
        return page;
    }

    /**
     * Sets the value of the page property.
     *
     * @param value
     *     allowed object is
     *     {@link Page }
     *
     */
    public void setPage(Page value) {
        this.page = value;
    }

    /**
     * Gets the value of the restriction property.
     *
     * @return
     *     possible object is
     *     {@link Restriction }
     *
     */
    public Restriction getRestriction() {
        return restriction;
    }

    /**
     * Sets the value of the restriction property.
     *
     * @param value
     *     allowed object is
     *     {@link Restriction }
     *
     */
    public void setRestriction(Restriction value) {
        this.restriction = value;
    }

}
