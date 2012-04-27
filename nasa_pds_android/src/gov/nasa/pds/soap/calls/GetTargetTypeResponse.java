package gov.nasa.pds.soap.calls;

import gov.nasa.pds.soap.BaseObject;
import gov.nasa.pds.soap.entities.TargetType;

/**
 * <p>
 * Java class for getTargetTypeResponse complex type.
 *
 * <p>
 * The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="getTargetTypeResponse">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="return" type="{http://pds.nasa.gov/}targetType" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
public class GetTargetTypeResponse extends BaseObject {
    protected TargetType _return;

    /**
     * Gets the value of the return property.
     *
     * @return possible object is {@link TargetType }
     *
     */
    public TargetType getReturn() {
        return _return;
    }

    /**
     * Sets the value of the return property.
     *
     * @param value allowed object is {@link TargetType }
     *
     */
    public void setReturn(TargetType value) {
        this._return = value;
    }

}
