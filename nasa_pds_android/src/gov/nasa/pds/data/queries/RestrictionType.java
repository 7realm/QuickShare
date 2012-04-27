package gov.nasa.pds.data.queries;

public enum RestrictionType {
    TARGET_TYPE("TargetType"),
    TARGET("Target"),
    MISSION("Mission"),
    INSTRUMENT("Instrument");

    private String className;

    private RestrictionType(String className) {
        this.className = className;
    }

    public String getClassName() {
        return className;
    }
}
