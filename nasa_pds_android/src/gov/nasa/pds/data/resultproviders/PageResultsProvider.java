/*
 * Copyright (C) 2012 TopCoder Inc., All Rights Reserved.
 */
package gov.nasa.pds.data.resultproviders;

import gov.nasa.pds.data.DataCenter;
import gov.nasa.pds.data.EntityType;
import gov.nasa.pds.data.queries.PagedQuery;

/**
 * Data adapter that will fill results from paged query to list.
 *
 * @author 7realm
 * @version 1.0
 */
public class PageResultsProvider extends ResultsProvider {
    private final PagedQuery pagedQuery;
    private final int itemsPerPage;

    private int lastPageSize;
    private int total;

    /**
     * Constructor for PageResultsProvider type.
     *
     * @param pagedQuery the query that will provide page data
     */
    public PageResultsProvider(PagedQuery pagedQuery) {
        super(pagedQuery.getQueryType());
        this.pagedQuery = pagedQuery;
        this.itemsPerPage = pagedQuery.getPage().getItemsPerPage();
    }

    /**
     * The query result entity type.
     *
     * @return query result entity type
     */
    @Override
    public EntityType getEntityType() {
        return pagedQuery.getEntityType();
    }

    /**
     * The page count.
     *
     * @return the page count
     */
    @Override
    public int getPageCount() {
        return total / itemsPerPage + (lastPageSize == 0 ? 0 : 1);
    }

    /**
     * The current page.
     *
     * @return the current page
     */
    @Override
    public int getCurrentPage() {
        return pagedQuery.getPage().getPageNumber();
    }

    /**
     * The size of current page.
     *
     * @return the current page size
     */
    @Override
    public int getCurrentPageSize() {
        // last page is exactly full or we have not last page
        return lastPageSize == 0 || getPageCount() > getCurrentPage() ? itemsPerPage : lastPageSize;
    }

    /**
     * Move to page with given number.
     *
     * @param pageIndex the index of new page
     */
    @Override
    public void moveToPage(int pageIndex) {
        // set new page index
        pagedQuery.getPage().setPageNumber(pageIndex);

        // get last results from data center
        lastResult = DataCenter.executePagedQuery(pagedQuery);

        // update total and last page size
        total = (int) lastResult.getTotal();
        lastPageSize = total % itemsPerPage;
    }
}