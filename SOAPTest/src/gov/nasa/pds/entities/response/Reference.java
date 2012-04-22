
package gov.nasa.pds.entities.response;

import gov.nasa.pds.entities.IdentifiableEntity;



/**
 * <p>Java class for reference complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="reference">
 *   &lt;complexContent>
 *     &lt;extension base="{http://pds.nasa.gov/}identifiableEntity">
 *       &lt;sequence>
 *         &lt;element name="description" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="keyTextId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
public class Reference
    extends IdentifiableEntity
{

    protected String description;
    protected String keyTextId;

    /**
     * Gets the value of the description property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the value of the description property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setDescription(String value) {
        this.description = value;
    }

    /**
     * Gets the value of the keyTextId property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getKeyTextId() {
        return keyTextId;
    }

    /**
     * Sets the value of the keyTextId property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setKeyTextId(String value) {
        this.keyTextId = value;
    }

}
