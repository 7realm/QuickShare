package gov.nasa.pds.entities.response;

import gov.nasa.pds.entities.XmlElement;
import gov.nasa.pds.entities.XmlType;

import java.util.ArrayList;
import java.util.List;


/**
 * <p>Java class for pagedResults complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="pagedResults">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="results" type="{http://www.w3.org/2001/XMLSchema}anyType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="total" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlType(name = "pagedResults", propOrder = {
    "results",
    "total"
})
public class PagedResults {

    @XmlElement(nillable = true)
    protected List<Object> results;
    protected long total;

    /**
     * Gets the value of the results property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the results property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getResults().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Object }
     *
     *
     */
    public List<Object> getResults() {
        if (results == null) {
            results = new ArrayList<Object>();
        }
        return this.results;
    }

    /**
     * Gets the value of the total property.
     *
     */
    public long getTotal() {
        return total;
    }

    /**
     * Sets the value of the total property.
     *
     */
    public void setTotal(long value) {
        this.total = value;
    }

}
