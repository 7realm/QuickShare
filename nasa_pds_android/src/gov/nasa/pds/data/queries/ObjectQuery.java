package gov.nasa.pds.data.queries;

import gov.nasa.pds.data.QueryType;

public class ObjectQuery<T> extends BaseQuery {
    private final long id;

    public ObjectQuery(QueryType queryType, long id) {
        super(queryType);
        this.id = id;
    }

    public long getId() {
        return id;
    }

}
