/*
 * Copyright (C) 2012 TopCoder Inc., All Rights Reserved.
 */
package gov.nasa.pds.data;

/**
 * SOAP query type.
 *
 * @author 7realm
 * @version 1.0
 */
public enum QueryType {
    /** Search entities by restriction and text. */
    SEARCH_BY_TYPE,

    /** Get all target types. */
    GET_TYPES_INFO,
    /** Get all targets. */
    GET_TARGETS_INFO,
    /** Get all missions. */
    GET_MISSIONS_INFO,
    /** Get all instruments. */
    GET_INSTRUMENTS_INFO,
    /** Get all documents. */
    GET_DOCUMENTS_INFO,
    /** Get all images. */
    GET_IMAGES_INFO,

    /** Get target.*/
    GET_TARGET,
    /** Get mission.*/
    GET_MISSION,
    /** Get instrument. */
    GET_INSTRUMENT,
    /** Get file.*/
    GET_FILE
}
