/*
 * Copyright (C) 2012 TopCoder Inc., All Rights Reserved.
 */
package gov.nasa.pds.data.queries;

import gov.nasa.pds.data.Query;
import gov.nasa.pds.data.QueryType;

/**
 * Base query class, it contains query type.
 *
 * @author TCSASSEMBLER
 * @version 1.0
 */
public abstract class BaseQuery implements Query {
    protected final QueryType queryType;

    public BaseQuery(QueryType queryType) {
        this.queryType = queryType;
    }

    @Override
    public QueryType getQueryType() {
        return queryType;
    }
}
