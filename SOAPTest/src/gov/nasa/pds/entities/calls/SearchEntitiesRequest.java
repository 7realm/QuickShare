package gov.nasa.pds.entities.calls;

import gov.nasa.pds.entities.BaseObject;
import gov.nasa.pds.entities.request.Page;

public class SearchEntitiesRequest extends BaseObject {

    protected String searchText;
    protected Page page;

    public String getSearchText() {
        return searchText;
    }

    public void setSearchText(String value) {
        this.searchText = value;
    }

    public Page getPage() {
        return page;
    }
    public void setPage(Page value) {
        this.page = value;
    }
}
