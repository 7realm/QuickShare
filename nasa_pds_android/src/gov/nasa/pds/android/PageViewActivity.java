/*
 * Copyright (C) 2012 TopCoder Inc., All Rights Reserved.
 */
package gov.nasa.pds.android;

import gov.nasa.pds.android.BrowserView.BrowserEventHandler;
import gov.nasa.pds.android.Filter.NamedRestriction;
import gov.nasa.pds.data.EntityType;
import gov.nasa.pds.soap.entities.EntityInfo;

import java.util.Iterator;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;

import com.markupartist.android.widget.ActionBar;
import com.markupartist.android.widget.ActionBar.AbstractAction;
import com.markupartist.android.widget.ActionBar.TitleChangeListener;
import com.markupartist.android.widget.ActionBar.TitleType;

/**
 * Activity that will browse objects.
 *
 * @author 7realm
 * @version 1.0
 */
public class PageViewActivity extends Activity {
    /** Intent extra for query type. */
    public static final String EXTRA_ENTITY_TYPE = "query_type";
    private final Filter filter = new Filter();
    private EntityType entityType;
    private TextView searchTextView;
    private ActionBar actionBar;
    private ViewGroup searchGroup;
    private BrowserView browserTab;
    private BrowserView browserTab0;
    private BrowserView browserTab1;
    private TabHost tabs;

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

        // set tab host
        tabs = (TabHost) findViewById(R.id.browserTabs);
        tabs.setup();
        tabs.addTab(tabs.newTabSpec("documents").setIndicator("Documents").setContent(R.id.browserTab0));
        tabs.addTab(tabs.newTabSpec("images").setIndicator("Images").setContent(R.id.browserTab1));

        // get browser tabs
        BrowserEventHandler eventHandler = new BrowserEventHandlerImpl();
        browserTab = (BrowserView) findViewById(R.id.browserTab);
        browserTab.setFilter(filter);
        browserTab.setEventHandler(eventHandler);
        browserTab0 = (BrowserView) findViewById(R.id.browserTab0);
        browserTab0.setFilter(filter);
        browserTab0.setEntityType(EntityType.FILE);
        browserTab0.setEventHandler(eventHandler);
        browserTab1 = (BrowserView) findViewById(R.id.browserTab1);
        browserTab1.setFilter(filter);
        browserTab1.setEntityType(EntityType.IMAGE);
        browserTab1.setEventHandler(eventHandler);

        // hide search group
        searchGroup = (ViewGroup) findViewById(R.id.browserSearchGroup);
        searchGroup.setVisibility(View.GONE);

        // set search view
        searchTextView = (TextView) findViewById(R.id.browserSearchText);

        // set action bar
        actionBar = (ActionBar) findViewById(R.id.actionbar);
        actionBar.setUpTriangle(true);
        actionBar.setUpAction(new AbstractAction(R.drawable.level_up, "Lessons") {
            @Override
            public void performAction(View view) {
                finish();
            }
        });

        // set title
        actionBar.setTitleType(TitleType.DROP_DOWN);
        actionBar.setTitleChangeListener(new TitleChangeListener() {
            @Override
            public void onTitleChanged(CharSequence newTitle, int newTitlePosition) {
                // get entity type by ordinal value
                EntityType newEntityType = EntityType.valueOf(newTitlePosition);
                if (newEntityType == null) {
                    Log.w("soap", "Selected item at unexpected position.");
                    return;
                }

                // remove text filter
                filter.setText("");

                // set new type
                setEntityType(newEntityType);
            }
        });

        // add text filter action
        actionBar.addAction(new AbstractAction(R.drawable.search_text, "Filter") {
            @Override
            public void performAction(View view) {
                searchGroup.setVisibility(View.VISIBLE);
                searchTextView.requestFocus();
            }
        });

        // set query for base type from intent
        EntityType entityType = (EntityType) getIntent().getSerializableExtra(EXTRA_ENTITY_TYPE);
        setEntityType(entityType == null ? EntityType.TARGET_TYPE : entityType);
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

        refresh();
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

            refresh();
        }

        // hide search group
        searchGroup.setVisibility(View.GONE);
    }

    /**
     * When user press search button.
     *
     * @param v the clicked view
     */
    @SuppressWarnings("unused")
    public void onSearchCancelButtonClick(View v) {
        // clear filter by text
        if (!filter.getText().equals("")) {
            filter.setText("");

            refresh();
        }

        // hide search group
        searchGroup.setVisibility(View.GONE);
    }

    /**
     * When back button is pressed.
     */
    @Override
    public void onBackPressed() {
        // hide search bar if it is visible
        if (searchGroup.getVisibility() == View.VISIBLE) {
            searchGroup.setVisibility(View.GONE);
            return;
        }

        // if we are on top level then finish activity
        if (entityType == EntityType.TARGET_TYPE) {
            super.onBackPressed();
        } else {
            // remove text filter if hierarchy change
            filter.setText("");

            setEntityType(entityType.upper());
        }
    }

    private void setEntityType(EntityType newEntityType) {
        // do nothing if entity type is not changed
        if (newEntityType == entityType) {
            return;
        }

        // set entity type
        entityType = newEntityType;

        // update action bar
        actionBar.setTitle(entityType.ordinal(), getResources().getStringArray(R.array.entities_type));

        refresh();
    }

    private void refresh() {

        if (entityType.isLowest()) {
            // show tabs
            tabs.setVisibility(View.VISIBLE);
            browserTab.setVisibility(View.GONE);

            // refresh two providers
            browserTab0.refresh();
            browserTab1.refresh();
        } else {
            // show single browser view
            tabs.setVisibility(View.GONE);
            browserTab.setVisibility(View.VISIBLE);

            // refresh provider
            browserTab.setEntityType(entityType);
            browserTab.refresh();
        }

        // set filter text section
        searchTextView.setText(filter.getText());

        refreshRestrictionGroup();
    }

    private void refreshRestrictionGroup() {
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

    private final class BrowserEventHandlerImpl implements BrowserEventHandler {
        @Override
        public void onStartDataLoad() {
            setProgressBarIndeterminateVisibility(true);

        }

        @Override
        public void onGoInside(Filter filter, EntityInfo entityInfo) {
            // add restriction
            filter.addRestriction(entityInfo, entityType);

            // remove text filter if hierarchy change
            filter.setText("");

            // go to lower level
            setEntityType(entityType.lower());
        }

        @Override
        public void onEndDataLoad() {
            setProgressBarIndeterminateVisibility(false);
        }
    }

}
