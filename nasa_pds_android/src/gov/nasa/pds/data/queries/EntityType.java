package gov.nasa.pds.data.queries;

import gov.nasa.pds.data.QueryType;

public enum EntityType {
    TARGET_TYPE("TargetType"),
    TARGET("Target"),
    MISSION("Mission"),
    INSTRUMENT("Instrument");

    private String className;

    private EntityType(String className) {
        this.className = className;
    }

    public static EntityType valueOf(int ordinal) {
        for (EntityType value : values()) {
            if (value.ordinal() == ordinal) {
                return value;
            }
        }

        return null;
    }

    public String getClassName() {
        return className;
    }

    public String getHumanReadable() {
        if (this == TARGET_TYPE) {
            return "target type";
        }
        return className.toLowerCase();
    }

    public boolean isLowerThan(EntityType other) {
        return ordinal() > other.ordinal();
    }

    public EntityType upper() {
        return valueOf(ordinal() - 1);
    }

    public EntityType lower() {
        return valueOf(ordinal() + 1);
    }

    public QueryType getObjectQuery() {
        switch (this) {
        case TARGET:
            return QueryType.GET_TARGET;
        case MISSION:
            return QueryType.GET_MISSION;
        case INSTRUMENT:
            return QueryType.GET_INSTRUMENT;
        default:
            return null;
        }
    }

    public QueryType getObjectsInfoQuery() {
        switch (this) {
        case TARGET:
            return QueryType.GET_TARGETS_INFO;
        case MISSION:
            return QueryType.GET_MISSIONS_INFO;
        case INSTRUMENT:
            return QueryType.GET_INSTRUMENTS_INFO;
        case TARGET_TYPE:
        default:
            return QueryType.GET_TYPES_INFO;
        }
    }
}
