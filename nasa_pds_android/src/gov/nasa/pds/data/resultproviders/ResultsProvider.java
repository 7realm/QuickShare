/*
 * Copyright (C) 2012 TopCoder Inc., All Rights Reserved.
 */
package gov.nasa.pds.data.resultproviders;

import gov.nasa.pds.android.R;
import gov.nasa.pds.data.EntityType;
import gov.nasa.pds.data.QueryType;
import gov.nasa.pds.soap.entities.EntityInfo;
import gov.nasa.pds.soap.entities.PagedResults;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Base data adapter that will fill results from paged query to list.
 *
 * @author 7realm
 * @version 1.0
 */
public abstract class ResultsProvider extends BaseAdapter {
    protected PagedResults lastResult;
    protected QueryType queryType;
    private OnClickListener onGotoButtonListener;
    private OnClickListener onOpenListener;

    /**
     * Constructor for ResultsProvider type.
     *
     * @param queryType the query type
     */
    protected ResultsProvider(QueryType queryType) {
        this.queryType = queryType;
    }

    /**
     * Set handler for goto events.
     *
     * @param onGotoButtonListener the handler for goto click
     */
    public void setOnGotoButtonListener(OnClickListener onGotoButtonListener) {
        this.onGotoButtonListener = onGotoButtonListener;
    }

    /**
     * Set handler for open events
     *
     * @param onOpenListener the open event handler
     */
    public void setOnOpenListener(OnClickListener onOpenListener) {
        this.onOpenListener = onOpenListener;
    }

    /**
     * The query type.
     *
     * @return the query type
     */
    public QueryType getQueryType() {
        return queryType;
    }

    /**
     * Fills list item view.
     *
     * @param itemIndex the item index
     * @param pageView the list item parent view
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

        // set tag and listener for goto button
        View gotoButton = pageView.findViewById(R.id.entityGotoButton);
        gotoButton.setTag(item.getId());
        gotoButton.setOnClickListener(onGotoButtonListener);

        // set tag and listener for whole view
        pageView.setTag(item);
        pageView.setOnClickListener(onOpenListener);

        // set object icon
        ImageView objectIcon = (ImageView) pageView.findViewById(R.id.entityObjectIcon);
        switch (getEntityType()) {
        case TARGET_TYPE:
            objectIcon.setImageResource(R.drawable.object_target_type);
            break;
        case TARGET:
            objectIcon.setImageResource(R.drawable.object_target);
            break;
        case MISSION:
            objectIcon.setImageResource(R.drawable.object_mission);
            break;
        case INSTRUMENT:
            objectIcon.setImageResource(R.drawable.object_instrument);
            break;
        case FILE:
        default:
            objectIcon.setImageResource(R.drawable.object_file);
            break;
        }

        // hide goto button if needed
        switch (queryType) {
        case GET_MISSIONS_INFO:
        case GET_INSTRUMENTS_INFO:
            gotoButton.setVisibility(View.VISIBLE);
            break;
        default:
            gotoButton.setVisibility(View.INVISIBLE);
            break;
        }
    }

    /**
     * Gets item on page.
     *
     * @param itemIndex the index of item to get
     * @return the item value
     */
    @Override
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

    /**
     * Gets page count.
     *
     * @return the page count
     */
    public abstract int getPageCount();

    /**
     * Gets current page number.
     *
     * @return the current page number
     */
    public abstract int getCurrentPage();

    /**
     * Gets current page size.
     *
     * @return the current page size
     */
    public abstract int getCurrentPageSize();

    /**
     * Gets result entity type.
     *
     * @return the result entity type
     */
    public abstract EntityType getEntityType();

    /**
     * Gets total count of results.
     *
     * @return the total count
     */
    public long getTotal() {
        return lastResult == null ? 0 : lastResult.getTotal();
    }

    /**
     * List adapter callback for item view creation.
     *
     * @param i the item index
     * @param view the converted view that can be reused
     * @param viewgroup the parent view
     * @return the created and fileld view
     */
    @Override
    public View getView(int i, View view, ViewGroup viewgroup) {
        if (view == null) {
            view = LayoutInflater.from(viewgroup.getContext()).inflate(R.layout.item_entity, null, true);
        }
        fillView(i, view);
        return view;
    }

    /**
     * Gets item id.
     *
     * @param i the item id
     * @return the item id
     */
    @Override
    public long getItemId(int i) {
        return i;
    }

    /**
     * Gets count of items on current page.
     *
     * @return count of items on current page
     */
    @Override
    public int getCount() {
        return getCurrentPageSize();
    }

}