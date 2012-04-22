package gov.nasa.pds.entities.calls;

import gov.nasa.pds.entities.BaseObject;
import gov.nasa.pds.entities.request.Page;

public class GetTargetTypesInfoRequest extends BaseObject {

    protected Page page;

    public Page getPage() {
        return page;
    }

    public void setPage(Page value) {
        this.page = value;
    }

}
