package gov.nasa.pds.data;

import gov.nasa.pds.android.R;
import gov.nasa.pds.soap.entities.EntityInfo;
import gov.nasa.pds.soap.entities.PagedResults;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public abstract class ResultsProvider {
    protected PagedResults lastResult;
    protected QueryType queryType;

    protected ResultsProvider(QueryType queryType) {
        this.queryType = queryType;
    }

    public QueryType getQueryType() {
        return queryType;
    }

    /**
     *
     *
     * @param itemIndex
     * @param pageView
     */
    public void fillView(int itemIndex, View pageView) {
        // for safety skip empty items
        EntityInfo item = getItem(itemIndex);
        if (item == null) {
            return;
        }

        // set caption of the item
        TextView captionTextView = (TextView) pageView.findViewById(R.id.entityNameText);
        captionTextView.setText(item.getName());

        // set id tag to goto button
        pageView.findViewById(R.id.entityGotoButton).setTag(item.getId());
    }

    /**
     *
     *
     * @param itemIndex
     * @return
     */
    public EntityInfo getItem(int itemIndex) {
        if (lastResult == null) {
            return null;
        }

        if (lastResult.getResults().size() < itemIndex + 1) {
            Log.w("page_result", "Getting item beyond the result size.");
            return null;
        }

        return (EntityInfo) lastResult.getResults().get(itemIndex);
    }

    /**
     * Moves to specified page. Can be also used to move to first page.
     *
     * @param pageIndex the index of target page
     */
    public abstract void moveToPage(int pageIndex);

    public abstract int getPageCount();

    public abstract int getCurrentPage();

    public abstract int getCurrentPageSize();

}