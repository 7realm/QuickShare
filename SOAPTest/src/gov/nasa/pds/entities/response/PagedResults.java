package gov.nasa.pds.entities.response;

import gov.nasa.pds.entities.BaseObject;

import java.util.ArrayList;
import java.util.List;

public class PagedResults extends BaseObject {

    protected final List<Object> results = new ArrayList<Object>();
    protected long total;

    public List<Object> getResults() {
        return this.results;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long value) {
        this.total = value;
    }

}
