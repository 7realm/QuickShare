package gov.nasa.pds.data;

import gov.nasa.pds.android.R;
import gov.nasa.pds.data.queries.PagedQuery;
import gov.nasa.pds.data.temp.EntityInfo;
import gov.nasa.pds.data.temp.PagedResults;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class PageResultsProvider {
    private final String caption;
    private final PagedQuery pagedQuery;
    private final int itemsPerPage;

    private long lastPageSize;
    private long total;
    private PagedResults lastResult;

    public PageResultsProvider(String caption, PagedQuery pagedQuery) {
        super();
        this.caption = caption;
        this.pagedQuery = pagedQuery;
        this.itemsPerPage = pagedQuery.getPage().getItemsPerPage();
    }

    public long getPageCount() {
        return total / itemsPerPage + (lastPageSize == 0 ? 0 : lastPageSize);
    }

    public long getPageSize(int pageIndex) {
        int itemsPerPage = pagedQuery.getPage().getItemsPerPage();
        long lastPageSize = total % itemsPerPage;

        // last page is exactly full or we have not last page
        return lastPageSize == 0 || getPageCount() > pageIndex + 1 ? itemsPerPage : lastPageSize;
    }

    public void fillView(int itemIndex, View pageView) {
        // for safety skip empty items
        EntityInfo item = getItem(itemIndex);
        if (item == null) {
            return;
        }

        // set caption of the item
        TextView captionTextView = (TextView) pageView.findViewById(R.id.entityNameText);
        captionTextView.setText(item.getName());

        // show or hide select button depending on query
        View selectButton = pageView.findViewById(R.id.entitySelectButton);
        if (pagedQuery.getQueryType() == QueryType.SEARCH_BY_TYPE) {
            selectButton.setVisibility(View.GONE);
        } else {
            selectButton.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Moves to specified page. Can be also used to move to first page.
     *
     * @param pageIndex the index of target page
     */
    public void moveToPage(int pageIndex) {
        // set new page index
        pagedQuery.getPage().setPageNumber(pageIndex);

        // get last results from data center
        lastResult = DataCenter.executePagedQuery(pagedQuery);

        // update total and last page size
        total = lastResult.getTotal();
        lastPageSize = total % itemsPerPage;
    }

    public EntityInfo getItem(int itemIndex) {
        if (lastResult == null) {
            return null;
        }

        if (lastResult.getResults().size() >= itemIndex + 1) {
            Log.w("page_result", "Getting item beyond the result size.");
            return null;
        }

        return (EntityInfo) lastResult.getResults().get(itemIndex);
    }

    public String getCaption() {
        return caption;
    }
}