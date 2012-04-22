package gov.nasa.pds.entities.calls;

import gov.nasa.pds.entities.BaseObject;
import gov.nasa.pds.entities.response.SearchResults;

public class SearchEntitiesResponse extends BaseObject {
    protected SearchResults _return;

    public SearchResults getReturn() {
        return _return;
    }

    public void setReturn(SearchResults value) {
        this._return = value;
    }
}
