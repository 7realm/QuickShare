package gov.nasa.pds.data.temp;

public class Page {

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
