/*
 * Copyright (C) 2012 TopCoder Inc., All Rights Reserved.
 */
package gov.nasa.pds.data.queries;

import gov.nasa.pds.data.Query;
import gov.nasa.pds.data.QueryType;

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
