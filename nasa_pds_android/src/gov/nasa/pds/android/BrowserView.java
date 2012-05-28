package gov.nasa.pds.android;

import gov.nasa.pds.data.DataCenter;
import gov.nasa.pds.data.EntityType;
import gov.nasa.pds.data.resultproviders.ResultsProvider;
import gov.nasa.pds.soap.entities.EntityInfo;

import java.text.MessageFormat;
import java.util.concurrent.atomic.AtomicBoolean;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.markupartist.android.widget.actionbar.R;

public class BrowserView extends FrameLayout {
    private ViewFlipper viewFlipper;
    private final AtomicBoolean firstRun = new AtomicBoolean();
    private ResultsProvider provider;
    private Filter filter;
    private EntityType entityType;
    private BrowserEventHandler eventHandler;

    public BrowserView(Context context, AttributeSet attrs) {
        super(context, attrs);

        // inflate layout
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        layoutInflater.inflate(R.layout.view_browse, this, true);

        // set view flipper
        viewFlipper = (ViewFlipper) findViewById(R.id.browserFlipper);

        findViewById(R.id.browserNextButton).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                goToNext();
            }
        });

        findViewById(R.id.browserPreviousButton).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                goToPrevious();
            }
        });
    }

    public void setEventHandler(BrowserEventHandler eventHandler) {
        this.eventHandler = eventHandler;
    }

    public void setFilter(Filter filter) {
        this.filter = filter;
    }

    public void setEntityType(EntityType entityType) {
        this.entityType = entityType;
    }

    public void refresh() {
        // create provider for current filter
        provider = filter.createProvider(entityType);

        // set click handlers
        provider.setOnGotoButtonListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoEntity((Long) v.getTag());
            }
        });
        provider.setOnOpenListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                openEntity((EntityInfo) v.getTag());
            }
        });

        // load first page
        firstRun.set(true);
        new DataLoadTast().execute(1);
    }

    /**
     * Invokes activity that will display information about entity with given id.
     *
     * @param id the id of entity to display
     */
    protected void gotoEntity(long id) {
        // do nothing for target type
        if (entityType == EntityType.TARGET_TYPE) {
            Log.w("soap", "Tried to go to target type.");
            return;
        }

        // put corresponding object query to intent
        Intent intent = new Intent(getContext(), ObjectViewActivity.class);
        intent.putExtra(ObjectViewActivity.EXTRA_QUERY_TYPE, entityType.getObjectQuery());
        intent.putExtra(ObjectViewActivity.EXTRA_OBJECT_ID, id);
        getContext().startActivity(intent);
    }

    /**
     * Navigates inside clicked entity.
     *
     * @param entityInfo the entity, where we should navigate
     */
    protected void openEntity(EntityInfo entityInfo) {
        // if we on lowest level, open the file
        if (entityType.isLowest()) {
            gotoEntity(entityInfo.getId());
        } else {
            // call handler
            if (eventHandler != null) {
                eventHandler.onGoInside(filter, entityInfo);
            }
        }
    }

    private void goToNext() {
        // get number of next page
        int nextPage = provider.getCurrentPage() + 1;
        if (nextPage == provider.getPageCount() + 1) {
            nextPage = 1;
        }

        // move to next page
        new DataLoadTast().execute(nextPage);

        // slide the view
        viewFlipper.showNext();
    }

    private void goToPrevious() {
        // get number of previous page
        int previousPage = provider.getCurrentPage() - 1;
        if (previousPage == 0) {
            previousPage = provider.getPageCount();
        }

        // move to previous page
        new DataLoadTast().execute(previousPage);

        // slide the view
        viewFlipper.showPrevious();
    }

    private void setPageCaption(int nextPage) {
        switch (provider.getPageCount()) {
        case 0:
            setText(R.id.browserPageCaption, "No results");
            break;
        case 1:
            setText(R.id.browserPageCaption, "Showing " + provider.getTotal() + " result(s)");
            break;
        default:
            long fromIndex = (nextPage - 1) * DataCenter.ITEMS_PER_PAGE + 1;
            long toIndex = fromIndex + provider.getCurrentPageSize() - 1;
            String captionText = MessageFormat.format("Showing {0}-{1} of {2} result(s)", fromIndex, toIndex, provider.getTotal());
            setText(R.id.browserPageCaption, captionText);
            break;
        }

        // hide navigation buttons if we have one page of results
        findViewById(R.id.browserNextButton).setVisibility(provider.getPageCount() > 1 ? View.VISIBLE : View.INVISIBLE);
        findViewById(R.id.browserPreviousButton).setVisibility(provider.getPageCount() > 1 ? View.VISIBLE : View.INVISIBLE);
    }

    private void setText(int viewId, String text) {
        ((TextView) findViewById(viewId)).setText(text);
    }

    /**
     * Task that will load data for all entities.
     *
     * @author 7realm
     * @version 1.0
     */
    private final class DataLoadTast extends AsyncTask<Integer, Void, Void> {

        @Override
        protected Void doInBackground(Integer... params) {
            provider.moveToPage(params[0]);
            return null;
        }

        @Override
        protected void onPreExecute() {
            if (eventHandler != null) {
                eventHandler.onStartDataLoad();
            }
        }

        @Override
        protected void onPostExecute(Void result) {
            // add views on first run
            if (firstRun.get()) {
                // clear flipper and add new views
                viewFlipper.removeAllViews();
                for (int i = 0; i < provider.getPageCount(); i++) {
                    ListView view = new ListView(getContext());
                    view.setAdapter(provider);
                    viewFlipper.addView(view, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
                }
                firstRun.set(false);
                setPageCaption(1);
            } else {
                setPageCaption(provider.getCurrentPage());
            }

            // notify list about content change
            provider.notifyDataSetInvalidated();

            if (eventHandler != null) {
                eventHandler.onEndDataLoad();
            }
        }
    }

    public static interface BrowserEventHandler {
        void onStartDataLoad();

        void onEndDataLoad();

        void onGoInside(Filter filter, EntityInfo entityInfo);
    }
}
