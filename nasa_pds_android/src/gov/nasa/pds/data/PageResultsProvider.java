package gov.nasa.pds.data;

import gov.nasa.pds.data.queries.PagedQuery;

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