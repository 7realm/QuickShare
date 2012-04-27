package gov.nasa.pds.data;

import gov.nasa.pds.data.queries.ObjectQuery;
import gov.nasa.pds.data.queries.PagedQuery;
import gov.nasa.pds.soap.entities.PagedResults;

public class DataCenter {
    public static PagedResults executePagedQuery(PagedQuery query) {
        switch (query.getQueryType()) {
        case GET_TYPES_INFO:

            break;

        default:
            break;
        }
        PagedResults result = new PagedResults();
        result.setTotal(0);
        return result;
    }

    public static <T> T executeObjectQuery(ObjectQuery<T> query) {
        return null;
    }

    public static boolean isCached(Query query) {
        return false;
    }
}
