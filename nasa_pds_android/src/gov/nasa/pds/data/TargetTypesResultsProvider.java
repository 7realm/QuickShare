package gov.nasa.pds.data;

import gov.nasa.pds.data.queries.PagedQuery;

/**
 *
 * @author TCSDESIGNER, TCSDEVELOPER
 * @version 1.0
 */
public class TargetTypesResultsProvider extends ResultsProvider {
    private int total;

    public TargetTypesResultsProvider() {
        super(QueryType.GET_TYPES_INFO);
    }

    /**
     *
     *
     * @return
     */
    @Override
    public int getPageCount() {
        return 1;
    }

    /**
     *
     *
     * @return
     */
    @Override
    public int getCurrentPage() {
        return 1;
    }

    /**
     *
     *
     * @return
     */
    @Override
    public int getCurrentPageSize() {
        return total;
    }

    /**
     *
     *
     * @param pageIndex
     */
    @Override
    public void moveToPage(int pageIndex) {
        if (lastResult == null) {
            // get last results from data center
            lastResult = DataCenter.executePagedQuery(new PagedQuery(queryType, 0));

            // update total and last page size
            total = (int) lastResult.getTotal();
        }
    }
}
