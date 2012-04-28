package gov.nasa.pds.data.resultproviders;

import gov.nasa.pds.data.DataCenter;
import gov.nasa.pds.data.QueryType;
import gov.nasa.pds.data.queries.TypesPagedQuery;

/**
 *
 * @author TCSDESIGNER, TCSDEVELOPER
 * @version 1.0
 */
public class TypesResultsProvider extends ResultsProvider {
    private int total;

    public TypesResultsProvider() {
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
            lastResult = DataCenter.executePagedQuery(new TypesPagedQuery());

            // update total and last page size
            total = (int) lastResult.getTotal();
        }
    }
}
