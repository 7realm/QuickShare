package gov.nasa.pds.data.queries;

import gov.nasa.pds.data.QueryType;
import gov.nasa.pds.data.temp.Page;

public class PagedQuery extends BaseQuery {
    private final Page page;

    public PagedQuery(QueryType queryType, Page page) {
        super(queryType);
        this.page = page;
    }

    public Page getPage() {
        return page;
    }
}
