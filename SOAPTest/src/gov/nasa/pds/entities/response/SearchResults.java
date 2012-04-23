package gov.nasa.pds.entities.response;



/**
 * <p>Java class for searchResults complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="searchResults">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="dataFiles" type="{http://pds.nasa.gov/}pagedResults" minOccurs="0"/>
 *         &lt;element name="datasets" type="{http://pds.nasa.gov/}pagedResults" minOccurs="0"/>
 *         &lt;element name="instruments" type="{http://pds.nasa.gov/}pagedResults" minOccurs="0"/>
 *         &lt;element name="missions" type="{http://pds.nasa.gov/}pagedResults" minOccurs="0"/>
 *         &lt;element name="targetTypes" type="{http://pds.nasa.gov/}pagedResults" minOccurs="0"/>
 *         &lt;element name="targets" type="{http://pds.nasa.gov/}pagedResults" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
public class SearchResults {

    protected PagedResults dataFiles;
    protected PagedResults datasets;
    protected PagedResults instruments;
    protected PagedResults missions;
    protected PagedResults targetTypes;
    protected PagedResults targets;

    /**
     * Gets the value of the dataFiles property.
     *
     * @return
     *     possible object is
     *     {@link PagedResults }
     *
     */
    public PagedResults getDataFiles() {
        return dataFiles;
    }

    /**
     * Sets the value of the dataFiles property.
     *
     * @param value
     *     allowed object is
     *     {@link PagedResults }
     *
     */
    public void setDataFiles(PagedResults value) {
        this.dataFiles = value;
    }

    /**
     * Gets the value of the datasets property.
     *
     * @return
     *     possible object is
     *     {@link PagedResults }
     *
     */
    public PagedResults getDatasets() {
        return datasets;
    }

    /**
     * Sets the value of the datasets property.
     *
     * @param value
     *     allowed object is
     *     {@link PagedResults }
     *
     */
    public void setDatasets(PagedResults value) {
        this.datasets = value;
    }

    /**
     * Gets the value of the instruments property.
     *
     * @return
     *     possible object is
     *     {@link PagedResults }
     *
     */
    public PagedResults getInstruments() {
        return instruments;
    }

    /**
     * Sets the value of the instruments property.
     *
     * @param value
     *     allowed object is
     *     {@link PagedResults }
     *
     */
    public void setInstruments(PagedResults value) {
        this.instruments = value;
    }

    /**
     * Gets the value of the missions property.
     *
     * @return
     *     possible object is
     *     {@link PagedResults }
     *
     */
    public PagedResults getMissions() {
        return missions;
    }

    /**
     * Sets the value of the missions property.
     *
     * @param value
     *     allowed object is
     *     {@link PagedResults }
     *
     */
    public void setMissions(PagedResults value) {
        this.missions = value;
    }

    /**
     * Gets the value of the targetTypes property.
     *
     * @return
     *     possible object is
     *     {@link PagedResults }
     *
     */
    public PagedResults getTargetTypes() {
        return targetTypes;
    }

    /**
     * Sets the value of the targetTypes property.
     *
     * @param value
     *     allowed object is
     *     {@link PagedResults }
     *
     */
    public void setTargetTypes(PagedResults value) {
        this.targetTypes = value;
    }

    /**
     * Gets the value of the targets property.
     *
     * @return
     *     possible object is
     *     {@link PagedResults }
     *
     */
    public PagedResults getTargets() {
        return targets;
    }

    /**
     * Sets the value of the targets property.
     *
     * @param value
     *     allowed object is
     *     {@link PagedResults }
     *
     */
    public void setTargets(PagedResults value) {
        this.targets = value;
    }

}
