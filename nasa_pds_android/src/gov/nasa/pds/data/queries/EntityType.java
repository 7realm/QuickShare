package gov.nasa.pds.data.queries;

public enum EntityType {
    TARGET_TYPE("TargetType"),
    TARGET("Target"),
    MISSION("Mission"),
    INSTRUMENT("Instrument");

    private String className;

    private EntityType(String className) {
        this.className = className;
    }

    public String getClassName() {
        return className;
    }
}
