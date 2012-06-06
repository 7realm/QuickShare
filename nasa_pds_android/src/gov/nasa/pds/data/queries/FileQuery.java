/*
 * Copyright (C) 2012 TopCoder Inc., All Rights Reserved.
 */
package gov.nasa.pds.data.queries;

import gov.nasa.pds.data.QueryType;

/**
 * Special object query for files.
 *
 * @author TCSASSEMBLER
 * @version 1.0
 */
public class FileQuery extends ObjectQuery<Object> {

    public FileQuery(QueryType queryType, long id) {
        super(queryType, id);
    }
}
