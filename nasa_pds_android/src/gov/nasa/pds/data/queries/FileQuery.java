package gov.nasa.pds.data.queries;

import gov.nasa.pds.data.QueryType;

public class FileQuery extends ObjectQuery<Object> {

    public FileQuery(QueryType queryType, long id) {
        super(queryType, id);
    }

}
