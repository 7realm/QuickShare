/*
 * Copyright (C) 2012 TopCoder Inc., All Rights Reserved.
 */
package gov.nasa.pds.data;

/**
 * Entity type, that is used in application.
 *
 * @author 7realm
 * @version 1.0
 */
public enum EntityType {
    /** Target type entity. */
    TARGET_TYPE("TargetType"),
    /** Target entity. */
    TARGET("Target"),
    /** Mission entity. */
    MISSION("Mission"),
    /** Instrument entity. */
    INSTRUMENT("Instrument"),
    /** Data file entity. */
    FILE("DataFile");

    private String className;

    private EntityType(String className) {
        this.className = className;
    }

    /**
     * Gets enum value by ordinal.
     *
     * @param ordinal the ordinal value
     * @return the corresponding enum
     */
    public static EntityType valueOf(int ordinal) {
        for (EntityType value : values()) {
            if (value.ordinal() == ordinal) {
                return value;
            }
        }

        return null;
    }

    /**
     * Get last enum value.
     *
     * @return the last enum value
     */
    public static EntityType lowest() {
        return values()[values().length - 1];
    }

    /**
     * Get class name that relates to given entity type.
     *
     * @return the corresponding class name
     */
    public String getClassName() {
        return className;
    }

    /**
     * Checks if enum is lower then given enum.
     *
     * @param other the enum to check with
     * @return true if current enum is lower
     */
    public boolean isLowerThan(EntityType other) {
        return ordinal() > other.ordinal();
    }

    /**
     * Gets upper enum value.
     *
     * @return the upper enum
     */
    public EntityType upper() {
        return valueOf(ordinal() - 1);
    }

    /**
     * Gets lower enum value.
     *
     * @return the lower enum
     */
    public EntityType lower() {
        return valueOf(ordinal() + 1);
    }

    /**
     * Get corresponding object query.
     *
     * @return the object query
     */
    public QueryType getObjectQuery() {
        switch (this) {
        case TARGET:
            return QueryType.GET_TARGET;
        case MISSION:
            return QueryType.GET_MISSION;
        case INSTRUMENT:
            return QueryType.GET_INSTRUMENT;
        case FILE:
            return QueryType.GET_FILE;
        case TARGET_TYPE:
        default:
            return null;
        }
    }

    /**
     * Get corresponding object info query.
     *
     * @return the object info query
     */
    public QueryType getObjectsInfoQuery() {
        switch (this) {
        case TARGET:
            return QueryType.GET_TARGETS_INFO;
        case MISSION:
            return QueryType.GET_MISSIONS_INFO;
        case INSTRUMENT:
            return QueryType.GET_INSTRUMENTS_INFO;
        case FILE:
            return QueryType.GET_DOCUMENTS_INFO;
        case TARGET_TYPE:
        default:
            return QueryType.GET_TYPES_INFO;
        }
    }
}
