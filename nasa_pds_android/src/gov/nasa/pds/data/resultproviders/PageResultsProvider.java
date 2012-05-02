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
 * @author TCSASSEMBLER
 * @version 1.0
 */
public class PageResultsProvider extends ResultsProvider {
    private final PagedQuery pagedQuery;
    private final int itemsPerPage;

    private int lastPageSize;
    private int total;

    public PageResultsProvider(PagedQuery pagedQuery) {
        super(pagedQuery.getQueryType());
        this.pagedQuery = pagedQuery;
        this.itemsPerPage = pagedQuery.getPage().getItemsPerPage();
    }

    @Override
    public EntityType getEntityType() {
        return pagedQuery.getEntityType();
    }

    /**
     *
     *
     * @return
     */
    @Override
    public int getPageCount() {
        return total / itemsPerPage + (lastPageSize == 0 ? 0 : 1);
    }

    /**
     *
     *
     * @return
     */
    @Override
    public int getCurrentPage() {
        return pagedQuery.getPage().getPageNumber();
    }

    /**
     *
     *
     * @return
     */
    @Override
    public int getCurrentPageSize() {
        // last page is exactly full or we have not last page
        return lastPageSize == 0 || getPageCount() > getCurrentPage() ? itemsPerPage : lastPageSize;
    }

    /**
     *
     *
     * @param pageIndex
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