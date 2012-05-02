/*
 * Copyright (C) 2012 TopCoder Inc., All Rights Reserved.
 */
package gov.nasa.pds.data;

/**
 * SOAP query type.
 *
 * @author TCSASSEMBLER
 * @version 1.0
 */
public enum QueryType {
    SEARCH_BY_TYPE,

    GET_TYPES_INFO,
    GET_TARGETS_INFO,
    GET_MISSIONS_INFO,
    GET_INSTRUMENTS_INFO,
    GET_DOCUMENTS_INFO,
    GET_IMAGES_INFO,

    GET_TARGET,
    GET_MISSION,
    GET_INSTRUMENT,
    GET_FILE
}
