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

    /**
     * Constructor for BaseQuery type.
     *
     * @param queryType type of query
     */
    public BaseQuery(QueryType queryType) {
        this.queryType = queryType;
    }

    /**
     * Type of current query.
     *
     * @return the type of current query
     */
    @Override
    public QueryType getQueryType() {
        return queryType;
    }

    @Override
    public boolean equalsQuery(Query other) {
        if (other instanceof BaseQuery) {
            BaseQuery baseQuery = (BaseQuery) other;

            return baseQuery.queryType == queryType;
        }
        return false;
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof Query ? equalsQuery((Query) other) : false;
    }

    @Override
    public int hashCode() {
        return queryType.hashCode();
    }
}
