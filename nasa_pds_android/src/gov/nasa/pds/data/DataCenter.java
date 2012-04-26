package gov.nasa.pds.data;

import gov.nasa.pds.data.queries.ObjectQuery;
import gov.nasa.pds.data.queries.PagedQuery;
import gov.nasa.pds.data.temp.PagedResults;

public class DataCenter {
    public static PagedResults executePagedQuery(PagedQuery query) {
        return null;
    }

    public static <T> T executeObjectQuery(ObjectQuery<T> query) {
        return null;
    }

    public static boolean isCached(Query query) {
        return false;
    }
}
