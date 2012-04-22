package gov.nasa.pds.entities.calls;

import gov.nasa.pds.entities.XmlType;
import gov.nasa.pds.entities.request.Page;


/**
 * <p>Java class for getTargetTypesInfo complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="getTargetTypesInfo">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="page" type="{http://pds.nasa.gov/}page" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlType(name = "getTargetTypesInfo", propOrder = {
    "page"
})
public class GetTargetTypesInfo {

    protected Page page;

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

}
