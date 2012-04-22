package gov.nasa.pds.entities.calls;

import gov.nasa.pds.entities.BaseObject;
import gov.nasa.pds.entities.response.PagedResults;

public class GetTargetTypesInfoResponse extends BaseObject {

    protected PagedResults _return;

    public PagedResults getReturn() {
        return _return;
    }

    public void setReturn(PagedResults value) {
        this._return = value;
    }
}
