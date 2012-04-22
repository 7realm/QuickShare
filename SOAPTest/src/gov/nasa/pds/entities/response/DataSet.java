
package gov.nasa.pds.entities.response;

import gov.nasa.pds.entities.NamedEntity;

import java.util.ArrayList;
import java.util.List;

import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for dataSet complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="dataSet">
 *   &lt;complexContent>
 *     &lt;extension base="{http://pds.nasa.gov/}namedEntity">
 *       &lt;sequence>
 *         &lt;element name="description" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="instruments" type="{http://pds.nasa.gov/}instrument" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="missions" type="{http://pds.nasa.gov/}mission" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="otherChildren" type="{http://pds.nasa.gov/}metadataObject" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="rating" type="{http://www.w3.org/2001/XMLSchema}float" minOccurs="0"/>
 *         &lt;element name="references" type="{http://pds.nasa.gov/}reference" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="startDate" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="stopDate" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="targets" type="{http://pds.nasa.gov/}target" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="textId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="volumes" type="{http://pds.nasa.gov/}volume" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
public class DataSet
    extends NamedEntity
{

    protected String description;
    protected List<Instrument> instruments;
    protected List<Mission> missions;
    protected List<MetadataObject> otherChildren;
    protected Float rating;
    protected List<Reference> references;
    protected XMLGregorianCalendar startDate;
    protected XMLGregorianCalendar stopDate;
    protected List<Target> targets;
    protected String textId;
    protected List<Volume> volumes;

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
     * Gets the value of the instruments property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the instruments property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getInstruments().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Instrument }
     *
     *
     */
    public List<Instrument> getInstruments() {
        if (instruments == null) {
            instruments = new ArrayList<Instrument>();
        }
        return this.instruments;
    }

    /**
     * Gets the value of the missions property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the missions property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getMissions().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Mission }
     *
     *
     */
    public List<Mission> getMissions() {
        if (missions == null) {
            missions = new ArrayList<Mission>();
        }
        return this.missions;
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
     * Gets the value of the rating property.
     *
     * @return
     *     possible object is
     *     {@link Float }
     *
     */
    public Float getRating() {
        return rating;
    }

    /**
     * Sets the value of the rating property.
     *
     * @param value
     *     allowed object is
     *     {@link Float }
     *
     */
    public void setRating(Float value) {
        this.rating = value;
    }

    /**
     * Gets the value of the references property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the references property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getReferences().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Reference }
     *
     *
     */
    public List<Reference> getReferences() {
        if (references == null) {
            references = new ArrayList<Reference>();
        }
        return this.references;
    }

    /**
     * Gets the value of the startDate property.
     *
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *
     */
    public XMLGregorianCalendar getStartDate() {
        return startDate;
    }

    /**
     * Sets the value of the startDate property.
     *
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *
     */
    public void setStartDate(XMLGregorianCalendar value) {
        this.startDate = value;
    }

    /**
     * Gets the value of the stopDate property.
     *
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *
     */
    public XMLGregorianCalendar getStopDate() {
        return stopDate;
    }

    /**
     * Sets the value of the stopDate property.
     *
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *
     */
    public void setStopDate(XMLGregorianCalendar value) {
        this.stopDate = value;
    }

    /**
     * Gets the value of the targets property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the targets property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getTargets().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Target }
     *
     *
     */
    public List<Target> getTargets() {
        if (targets == null) {
            targets = new ArrayList<Target>();
        }
        return this.targets;
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

    /**
     * Gets the value of the volumes property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the volumes property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getVolumes().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Volume }
     *
     *
     */
    public List<Volume> getVolumes() {
        if (volumes == null) {
            volumes = new ArrayList<Volume>();
        }
        return this.volumes;
    }

}
