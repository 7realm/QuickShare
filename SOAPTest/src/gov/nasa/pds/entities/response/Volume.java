
package gov.nasa.pds.entities.response;

import gov.nasa.pds.entities.NamedEntity;

import java.util.ArrayList;
import java.util.List;


/**
 * <p>Java class for volume complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="volume">
 *   &lt;complexContent>
 *     &lt;extension base="{http://pds.nasa.gov/}namedEntity">
 *       &lt;sequence>
 *         &lt;element name="description" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="otherChildren" type="{http://pds.nasa.gov/}metadataObject" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="otherProperties" type="{http://pds.nasa.gov/}property" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="seriesName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="setName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="setTextId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="textId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
public class Volume
    extends NamedEntity
{

    protected String description;
    protected List<MetadataObject> otherChildren;
    protected List<Property> otherProperties;
    protected String seriesName;
    protected String setName;
    protected String setTextId;
    protected String textId;

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
     * Gets the value of the otherChildren property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the otherChildren property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getOtherChildren().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link MetadataObject }
     *
     *
     */
    public List<MetadataObject> getOtherChildren() {
        if (otherChildren == null) {
            otherChildren = new ArrayList<MetadataObject>();
        }
        return this.otherChildren;
    }

    /**
     * Gets the value of the otherProperties property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the otherProperties property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getOtherProperties().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Property }
     *
     *
     */
    public List<Property> getOtherProperties() {
        if (otherProperties == null) {
            otherProperties = new ArrayList<Property>();
        }
        return this.otherProperties;
    }

    /**
     * Gets the value of the seriesName property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getSeriesName() {
        return seriesName;
    }

    /**
     * Sets the value of the seriesName property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setSeriesName(String value) {
        this.seriesName = value;
    }

    /**
     * Gets the value of the setName property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getSetName() {
        return setName;
    }

    /**
     * Sets the value of the setName property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setSetName(String value) {
        this.setName = value;
    }

    /**
     * Gets the value of the setTextId property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getSetTextId() {
        return setTextId;
    }

    /**
     * Sets the value of the setTextId property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setSetTextId(String value) {
        this.setTextId = value;
    }

    /**
     * Gets the value of the textId property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getTextId() {
        return textId;
    }

    /**
     * Sets the value of the textId property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setTextId(String value) {
        this.textId = value;
    }

}
