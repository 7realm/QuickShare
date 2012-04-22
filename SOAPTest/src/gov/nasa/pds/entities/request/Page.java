package gov.nasa.pds.entities.request;

import gov.nasa.pds.entities.BaseObject;

public class Page extends BaseObject {

    protected int itemsPerPage;
    protected int pageNumber;

    public int getItemsPerPage() {
        return itemsPerPage;
    }

    public void setItemsPerPage(int value) {
        this.itemsPerPage = value;
    }

    public int getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(int value) {
        this.pageNumber = value;
    }
}
