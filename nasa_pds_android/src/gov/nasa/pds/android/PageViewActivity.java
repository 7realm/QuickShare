/*
 * Copyright (C) 2012 TopCoder Inc., All Rights Reserved.
 */
package gov.nasa.pds.android;

import gov.nasa.pds.android.Filter.NamedRestriction;
import gov.nasa.pds.data.DataCenter;
import gov.nasa.pds.data.EntityType;
import gov.nasa.pds.data.resultproviders.ResultsProvider;
import gov.nasa.pds.soap.entities.EntityInfo;

import java.text.MessageFormat;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicBoolean;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ViewFlipper;

/**
 * Activity that will browse objects.
 *
 * @author 7realm
 * @version 1.0
 */
public class PageViewActivity extends Activity {
    /** Intent extra for query type. */
    public static final String EXTRA_ENTITY_TYPE = "query_type";
    private ViewFlipper viewFlipper;
    private ResultsProvider provider;
    private EntityType entityType;
    private final Filter filter = new Filter();
    private final AtomicBoolean firstRun = new AtomicBoolean();
    private Spinner spinner;
    private TextView searchTextView;
    private CheckBox checkBox;

    /**
     * Life-cycle handler for activity creation.
     *
     * @param savedInstanceState the saved instance state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browse);

        // set view flipper
        viewFlipper = (ViewFlipper) findViewById(R.id.browserFlipper);
        viewFlipper.setOnTouchListener(new OnTouchListener() {
            private float fromPosition;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    fromPosition = event.getX();
                    break;
                case MotionEvent.ACTION_UP:
                    float toPosition = event.getX();
                    double scrollMinDistance = viewFlipper.getWidth() * 0.2;
                    if (fromPosition - toPosition > scrollMinDistance) {
                        goToNext();
                    } else if (scrollMinDistance < toPosition - fromPosition) {
                        goToPrevious();
                    }
                default:
                    break;
                }
                return true;
            }
        });

        // set drop down
        spinner = (Spinner) findViewById(R.id.browserSpinner);
        spinner.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterview, View view, int pos, long id) {
                // get entity type by ordinal value
                EntityType newEntityType = EntityType.valueOf(pos);
                if (newEntityType == null) {
                    Log.w("soap", "Selected item at unexpected position.");
                    return;
                }

                // set new type
                setEntityType(newEntityType);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterview) {
                // do nothing
            }
        });

        // set search check box
        searchTextView = (TextView) findViewById(R.id.browserSearchText);
        checkBox = (CheckBox) findViewById(R.id.browserCheckBox);
        checkBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                String text = searchTextView.getText().toString().trim();

                // do not refresh provider only if search text is empty
                if (!text.isEmpty()) {
                    filter.setText(isChecked ? text : "");
                    refreshProvider();
                }

                // enable or disable search group views
                searchTextView.setEnabled(isChecked);
                findViewById(R.id.browserSearchButton).setEnabled(isChecked);

            }
        });

        // set query for base type from intent
        EntityType entityType = (EntityType) getIntent().getSerializableExtra(EXTRA_ENTITY_TYPE);
        setEntityType(entityType == null ? EntityType.TARGET_TYPE : entityType);
    }

    private void setEntityType(EntityType newEntityType) {
        // do nothing if entity type is not changed
        if (newEntityType == entityType) {
            return;
        }

        // set entity type
        entityType = newEntityType;

        // update spinner
        spinner.setSelection(entityType.ordinal());

        refreshProvider();
    }

    private void refreshProvider() {
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

        // set filter text section
        boolean hasFilterText = !filter.getText().isEmpty();
        checkBox.setChecked(hasFilterText);
        searchTextView.setEnabled(hasFilterText);
        findViewById(R.id.browserSearchButton).setEnabled(hasFilterText);

        // set restriction group
        ViewGroup restrictionGroup0 = (ViewGroup) findViewById(R.id.browserRestrictionGroup0);
        ViewGroup restrictionGroup1 = (ViewGroup) findViewById(R.id.browserRestrictionGroup1);
        restrictionGroup0.removeAllViews();
        restrictionGroup1.removeAllViews();
        int index = 0;
        for (Iterator<NamedRestriction> i = filter.getRestrictions().iterator(); i.hasNext();) {
            NamedRestriction restriction = i.next();

            // create and fill restriction view
            View restrictionView = LayoutInflater.from(this).inflate(R.layout.view_restriction, null);
            if (index < 2) {
                restrictionGroup0.addView(restrictionView);
            } else {
                restrictionGroup1.addView(restrictionView);
            }
            index++;

            // set object icon
            ImageView objectIcon = (ImageView) restrictionView.findViewById(R.id.restrictionObjectIcon);
            switch (restriction.getEntityType()) {
            case TARGET:
                objectIcon.setImageResource(R.drawable.object_target);
                break;
            case MISSION:
                objectIcon.setImageResource(R.drawable.object_mission);
                break;
            case INSTRUMENT:
                objectIcon.setImageResource(R.drawable.object_instrument);
                break;
            case TARGET_TYPE:
            default:
                objectIcon.setImageResource(R.drawable.object_target_type);
                break;
            }

            // set object text
            ((TextView) restrictionView.findViewById(R.id.restrictionCaption)).setText(restriction.getValue());

            // add delete button for lowest restriction
            if (!i.hasNext()) {
                restrictionView.findViewById(R.id.restrictionDeleteButton).setVisibility(View.VISIBLE);
            }
        }
    }

    /**
     * When user deletes restriction.
     *
     * @param v the clicked view
     */
    @SuppressWarnings("unused")
    public void onRestrictionDeleteButtonClick(View v) {
        // remove lowest restriction
        filter.removeLowestRestriction();

        refreshProvider();
    }

    /**
     * When user press search button.
     *
     * @param v the clicked view
     */
    @SuppressWarnings("unused")
    public void onSearchButtonClick(View v) {
        // check if text is changed
        String text = searchTextView.getText().toString().trim();
        if (!filter.getText().equals(text)) {
            filter.setText(text);

            refreshProvider();
        }
    }

    /**
     * When user tries to navigate to next view.
     *
     * @param v the clicked view
     */
    @SuppressWarnings("unused")
    public void onNextButtonClick(View v) {
        goToNext();
    }

    /**
     * When user tries to navigate to previous view.
     *
     * @param v the clicked view
     */
    @SuppressWarnings("unused")
    public void onPreviousButtonClick(View v) {
        goToPrevious();
    }

    /**
     * Invokes activity that will display information about entity with given id.
     *
     * @param id the id of entity to display
     */
    public void gotoEntity(long id) {
        // do nothing for target type
        if (entityType == EntityType.TARGET_TYPE) {
            Log.w("soap", "Tried to go to target type.");
            return;
        }

        // put corresponding object query to intent
        Intent intent = new Intent(this, ObjectViewActivity.class);
        intent.putExtra(ObjectViewActivity.EXTRA_QUERY_TYPE, entityType.getObjectQuery());
        intent.putExtra(ObjectViewActivity.EXTRA_OBJECT_ID, id);
        startActivity(intent);
    }

    /**
     * When back button is pressed.
     */
    @Override
    public void onBackPressed() {
        // if we are on top level then finish activity
        if (entityType == EntityType.TARGET_TYPE) {
            super.onBackPressed();
        } else {
            // remove text filter if hierarchy change
            filter.setText("");

            setEntityType(entityType.upper());
        }
    }

    /**
     * Navigates inside clicked entity.
     *
     * @param entityInfo the entity, where we should navigate
     */
    public void openEntity(EntityInfo entityInfo) {
        // if we on lowest level, open the file
        if (entityType == EntityType.lowest()) {
            gotoEntity(entityInfo.getId());
        } else {
            // add restriction
            filter.addRestriction(entityInfo, entityType);

            // remove text filter if hierarchy change
            filter.setText("");

            // go to lower level
            setEntityType(entityType.lower());
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
            setProgressBarIndeterminateVisibility(true);
        }

        @Override
        protected void onPostExecute(Void result) {
            // add views on first run
            if (firstRun.get()) {
                // clear flipper and add new views
                viewFlipper.removeAllViews();
                for (int i = 0; i < provider.getPageCount(); i++) {
                    ListView view = new ListView(PageViewActivity.this);
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

            setProgressBarIndeterminateVisibility(false);
        }
    }
}
