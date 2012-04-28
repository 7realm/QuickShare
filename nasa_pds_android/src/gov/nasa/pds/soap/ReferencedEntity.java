package gov.nasa.pds.soap;

import java.util.ArrayList;
import java.util.List;

import gov.nasa.pds.soap.entities.Reference;

public abstract class ReferencedEntity extends NamedEntity {

    protected List<Reference> references;

    public ReferencedEntity() {
        super();
    }

    /**
     * Gets the value of the references property.
     *
     * <p>
     * This accessor method returns a reference to the live list, not a snapshot. Therefore any modification you make to
     * the returned list will be present inside the JAXB object. This is why there is not a <CODE>set</CODE> method for
     * the references property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     *
     * <pre>
     * getReferences().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list {@link Reference }
     *
     *
     */
    public List<Reference> getReferences() {
        if (references == null) {
            references = new ArrayList<Reference>();
        }
        return this.references;
    }

}