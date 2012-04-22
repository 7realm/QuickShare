package gov.nasa.pds.entities.calls;

import gov.nasa.pds.entities.XmlElement;
import gov.nasa.pds.entities.XmlType;
import gov.nasa.pds.entities.response.PagedResults;


@XmlType(name = "getTargetTypesInfoResponse", propOrder = {
    "_return"
})
public class GetTargetTypesInfoResponse {

    @XmlElement(name = "return")
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
