/*
 * Copyright (C) 2012 TopCoder Inc., All Rights Reserved.
 */
package com.lib;

import java.util.List;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Reordable list view.
 *
 * @author TCSASSEMBLER
 * @version 1.0
 */
public class ReordableListView extends ListView implements View.OnTouchListener {
    /** Indicates if user is dragging view item. */
    private boolean isDragging;
    /** Start position of the item. */
    private int startPosition;
    /** Start offset to item's top. */
    private int startOffset;
    /** Image view id, is used to drag items. */
    private int imageViewId;
    /** Id of view that will be used as bucket to remove items. */
    private int removeViewId;
    /** Drag and drop listener for this list view. */
    private DragAndDropListner dragAndDropListner;

    /** The view that is dragged. */
    private View dragView;
    /** The layout params of the view. */
    private RelativeLayout.LayoutParams layoutParams;
    private View startItem;

    /**
     * Constructor for ReordableListView type.
     *
     * @param context the context
     */
    public ReordableListView(Context context) {
        this(context, null);
    }

    /**
     * Constructor for ReordableListView type.
     *
     * @param context the context
     * @param attrs the xml attributes
     */
    public ReordableListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOnTouchListener(this);
    }

    /**
     * Sets drag image view id.
     *
     * @param id the drag image id
     */
    public void setImageViewId(int id) {
        imageViewId = id;
    }

    /**
     * Sets id of the in parent group that will be used as remove basket.
     *
     * @param removeViewId the id of the view
     */
    public void setRemoveViewId(int removeViewId) {
        this.removeViewId = removeViewId;
    }

    /**
     * Set listener that will be triggered on drag events.
     *
     * @param dragAndDropListner the listener that will be triggered
     */
    public void setDragAndDropListner(DragAndDropListner dragAndDropListner) {
        this.dragAndDropListner = dragAndDropListner;
    }

    /**
     * Checks if cursor is inside the drag image.
     *
     * @param x the x cursor position
     * @return true if cursor is inside
     */
    protected boolean isInsideImageView(int x) {
        View imageView = findViewById(imageViewId);
        if (imageView == null) {
            return true;
        }
        return imageView.getLeft() < x && x < imageView.getRight();
    }

    /**
     * Override of onTouch event.
     *
     * @param v the touched view
     * @param ev the motion event
     * @return true, if event was handled
     */
    @Override
    public boolean onTouch(View v, MotionEvent ev) {
        final int action = ev.getAction();
        final int x = (int) ev.getX();
        final int y = (int) ev.getY();

        if (action == MotionEvent.ACTION_DOWN && isInsideImageView(x)) {
            isDragging = true;
        }

        Log.i("reorder", "Dragging: " + isDragging + ", action: " + action + ", x: " + x + ", y: " + y);
        Log.i("reorder_id", "View id: " + v.getId());

        if (!isDragging) {
            return super.onTouchEvent(ev);
        }

        int firstVisiblePosition = getFirstVisiblePosition();
        int lastVisiblePosition = getLastVisiblePosition();
        switch (action) {
        case MotionEvent.ACTION_DOWN:
            startPosition = pointToPosition(x, y);
            if (startPosition != INVALID_POSITION) {
                int itemIndex = startPosition - firstVisiblePosition;
                startItem = getChildAt(itemIndex);
                startOffset = y - startItem.getTop();
                onStartDrag(itemIndex, y - startOffset);
            } else {
                isDragging = false;
            }
            break;
        case MotionEvent.ACTION_MOVE:
            onDrag(y - startOffset);
            break;
        case MotionEvent.ACTION_CANCEL:
        case MotionEvent.ACTION_UP:
        default:
            isDragging = false;

            if (startPosition != INVALID_POSITION) {
                // check if dragged view is removed
                View removeView = getParentGroup().findViewById(removeViewId);
                if (isPointInsideView(x, y, removeView)) {
                    getAdapter().onRemove(startPosition);
                } else {
                    // calculate drop position
                    int endPosition = pointToPosition(x, y);
                    if (endPosition == INVALID_POSITION) {
                        // try to drop at start to end
                        if (getChildAt(firstVisiblePosition).getTop() > y) {
                            endPosition = firstVisiblePosition;
                        } else if (getChildAt(lastVisiblePosition).getBottom() < y) {
                            endPosition = lastVisiblePosition;
                        }
                    }

                    if (endPosition != INVALID_POSITION) {
                        getAdapter().onDrop(startPosition, endPosition);
                    }
                }
            }

            // finalize dragging
            onStopDrag();
            invalidateViews();
            break;
        }
        return true;
    }

    /**
     * Overridden method to avoid casting in code.
     *
     * @return casted version of {@link ListAdapter}
     */
    @Override
    public ReordableAdapter<?> getAdapter() {
        return (ReordableAdapter<?>) super.getAdapter();
    }

    /**
     * Checks if point is inside the view.
     *
     * @param x point's x-coordinate
     * @param y point's y-coordinate
     * @param view the view to check
     * @return true if point is inside, false otherwise
     */
    private static boolean isPointInsideView(float x, float y, View view) {
        if (view == null) {
            return false;
        }

        int viewX = view.getLeft();
        int viewY = view.getTop();

        Log.i("reorder_id", "Inside? " + y + " of " + view.getTop() + ", view Y " + viewY);

        // point is inside view bounds
        return x > viewX && x < viewX + view.getWidth() && y > viewY && y < viewY + view.getHeight();
    }

    /**
     * Executed while dragging to update position of dragged item.
     *
     * @param y the y coordinate of item
     */
    private void onDrag(int y) {
        if (dragView != null) {
            layoutParams.topMargin = y;
            dragView.setLayoutParams(layoutParams);
        }
    }

    /**
     * Executed dragging is started create dragged item.
     *
     * @param itemIndex the index of
     * @param y the y coordinate of item
     */
    private void onStartDrag(int itemIndex, int y) {
        // notify listener and hide dragged item
        startItem.setVisibility(View.INVISIBLE);
        if (dragAndDropListner != null) {
            dragAndDropListner.onStartDrag(itemIndex, startItem);
        }

        // create floating item that will be dragged
        dragView = getAdapter().getView(itemIndex, null, this);
        layoutParams = new RelativeLayout.LayoutParams(startItem.getWidth(), startItem.getHeight());
        layoutParams.topMargin = y;
        getParentGroup().addView(dragView, layoutParams);
    }

    /**
     * Executed when dragging is finished to update views.
     */
    private void onStopDrag() {
        if (startItem != null) {
            // notify listener and show dragged item
            startItem.setVisibility(View.INVISIBLE);
            if (dragAndDropListner != null) {
                dragAndDropListner.onEndDrag(startPosition, startItem);
            }
            startItem.setVisibility(View.VISIBLE);
            startItem = null;
        }
        if (dragView != null) {
            getParentGroup().removeView(dragView);
            dragView = null;
        }
    }

    /**
     * Gets casted parent to {@link ViewGroup}.
     *
     * @return casted parent view
     */
    private ViewGroup getParentGroup() {
        return (ViewGroup) getParent();
    }

    /**
     * Drag and drop listener for reordable view. Can be used for special behaviour while dropping the view.
     *
     * @author TCSASSEMBLER
     * @version 1.0
     */
    public static interface DragAndDropListner {
        /**
         * Triggered when list item is started being dragged.
         *
         * @param position the position of the item in the list
         * @param v the dragged item
         */
        void onStartDrag(int position, View v);

        /**
         * Triggered when list item is being dropped.
         *
         * @param position the position of the item in the list
         * @param v the dropped item
         */
        void onEndDrag(int position, View v);
    }

    /**
     * List adapter that support reordering.
     *
     * @author TCSASSEMBLER
     * @version 1.0
     */
    public static class ReordableAdapter<T> extends BaseAdapter {

        /** The id of text item inside the layout. */
        private final int textViewId;
        /** The item layout id. */
        private final int itemLayoutId;
        /** Data content. */
        private final List<T> content;

        /**
         * Constructor for ReordableAdapter type.
         *
         * @param itemLayoutId the list item layout
         * @param textViewId the text view id inside layout
         * @param content the data content
         */
        public ReordableAdapter(int itemLayoutId, int textViewId, List<T> content) {
            this.textViewId = textViewId;
            this.itemLayoutId = itemLayoutId;
            this.content = content;
        }

        /**
         * The number of items in the list.
         *
         * @see android.widget.ListAdapter#getCount()
         */
        @Override
        public int getCount() {
            return content.size();
        }

        /**
         * Since the data comes from an array, just returning the index is sufficient to get at the data. If we were
         * using a more complex data structure, we would return whatever object represents one row in the list.
         *
         * @see android.widget.ListAdapter#getItem(int)
         */
        @Override
        public T getItem(int position) {
            return content.get(position);
        }

        /**
         * Use the array index as a unique id.
         *
         * @see android.widget.ListAdapter#getItemId(int)
         */
        @Override
        public long getItemId(int position) {
            return position;
        }

        /**
         * Make a dragView to hold each row.
         *
         * @see android.widget.ListAdapter#getView(int, android.view.View, android.view.ViewGroup)
         */
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(parent.getContext()).inflate(itemLayoutId, null);
            }

            TextView textView = (TextView) convertView.findViewById(textViewId);
            textView.setText(content.get(position).toString());
            return convertView;
        }

        /**
         * Executed when item with index is removed.
         *
         * @param index the index of the item to remove
         */
        public void onRemove(int index) {
            content.remove(index);
        }

        /**
         * Executed when adapter is requested to drop item from one position to another.
         *
         * @param from the start position of the item
         * @param to the drop position of the item
         */
        public void onDrop(int from, int to) {
            T temp = content.get(from);
            content.remove(from);
            content.add(to, temp);
        }
    }
}