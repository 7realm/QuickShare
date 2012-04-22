package gov.nasa.pds.entities.request;

public class Restriction {

    protected String restrictionEntityClass;
    protected long restrictionEntityId;

    public String getRestrictionEntityClass() {
        return restrictionEntityClass;
    }

    public void setRestrictionEntityClass(String value) {
        this.restrictionEntityClass = value;
    }

    public long getRestrictionEntityId() {
        return restrictionEntityId;
    }

    public void setRestrictionEntityId(long value) {
        this.restrictionEntityId = value;
    }
}
