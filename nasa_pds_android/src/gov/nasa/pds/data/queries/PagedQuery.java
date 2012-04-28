package gov.nasa.pds.data.queries;

import gov.nasa.pds.data.QueryType;
import gov.nasa.pds.soap.entities.Page;
import gov.nasa.pds.soap.entities.Restriction;

public abstract class PagedQuery extends BaseQuery {
    private static final int ITEMS_PER_PAGE = 20;
    private final Page page;
    private final Restriction restriction;

    public PagedQuery(QueryType queryType) {
        this(queryType, null);
    }

    public PagedQuery(QueryType queryType, Restriction restriction) {
        super(queryType);
        this.restriction = restriction;
        this.page = new Page();
        page.setPageNumber(1);
        page.setItemsPerPage(ITEMS_PER_PAGE);
    }

    public Page getPage() {
        return page;
    }

    public Restriction getRestriction() {
        return restriction;
    }
}
