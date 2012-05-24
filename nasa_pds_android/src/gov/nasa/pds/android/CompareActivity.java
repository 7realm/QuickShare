/*
 * Copyright (C) 2012 TopCoder Inc., All Rights Reserved.
 */
package gov.nasa.pds.android;

import gov.nasa.pds.android.Compare.CompareItem;
import gov.nasa.pds.data.DataCenter;
import gov.nasa.pds.data.QueryType;
import gov.nasa.pds.data.queries.InfoPagedQuery;
import gov.nasa.pds.data.queries.PagedQuery;
import gov.nasa.pds.soap.entities.EntityInfo;
import gov.nasa.pds.soap.entities.Mission;
import gov.nasa.pds.soap.entities.PagedResults;
import gov.nasa.pds.soap.entities.Restriction;

import java.util.Set;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

/**
 * Activity that will display comparison results.
 *
 * @author 7realm
 * @version 1.0
 */
public class CompareActivity extends Activity {
    private static final int CELL_PADDING = 2;

    /**
     * Life-cycle handler for activity creation.
     *
     * @param savedInstanceState the saved instance state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compare);

        // load data
        new DataLoadTast().execute();
    }

    private int getColumnWidth() {
        View contentView = findViewById(R.id.compareContentView);
        int windowWidth = contentView.getWidth() - contentView.getPaddingLeft() - contentView.getPaddingRight();
        return Compare.ITEMS.size() == 1 ? windowWidth : windowWidth / 2;
    }

    private void fillActivity(Set<String> commonInstruments) {
        TableRow nameRow = (TableRow) findViewById(R.id.compareNameRow);
        TableRow startDateRow = (TableRow) findViewById(R.id.compareStartDateRow);
        TableRow endDateRow = (TableRow) findViewById(R.id.compareEndDateRow);
        TableRow durationRow = (TableRow) findViewById(R.id.compareDurationRow);

        TableLayout commonTable = (TableLayout) findViewById(R.id.compareCommonInstrumentsTable);
        LinearLayout uniqueTable = (LinearLayout) findViewById(R.id.compareUniqueInstrumentsTable);

        // clear rows
        nameRow.removeAllViews();
        startDateRow.removeAllViews();
        endDateRow.removeAllViews();
        durationRow.removeAllViews();

        // clear tables
        commonTable.removeAllViews();
        uniqueTable.removeAllViews();

        int index = 0;
        for (CompareItem item : Compare.ITEMS) {
            int color = getColor(index++);
            Mission mission = item.getMission();

            // add simple properties
            addCell(startDateRow, color, DataCenter.formatLong(mission.getStartDate()));
            addCell(endDateRow, color, DataCenter.formatLong(mission.getEndDate()));
            addCell(durationRow, color, DataCenter.formatPeriod(mission.getStartDate(), mission.getEndDate()));

            // add goto and remove buttons with proper tag
            View nameView = LayoutInflater.from(this).inflate(R.layout.view_compare_name, null);
            nameView.setBackgroundColor(color);
            nameView.setPadding(CELL_PADDING, CELL_PADDING, CELL_PADDING, CELL_PADDING);
            ((TextView) nameView.findViewById(R.id.compareNameCaption)).setText(mission.getName());
            nameView.findViewById(R.id.compareDeleteButton).setTag(mission.getId());
            nameView.findViewById(R.id.compareGotoButton).setTag(mission.getId());
            nameRow.addView(nameView, getColumnWidth(), LayoutParams.MATCH_PARENT);
        }

        // fill common instruments for all missions
        findViewById(R.id.compareCommonInstrumentsCaption).setVisibility(commonInstruments.isEmpty() ? View.GONE : View.VISIBLE);
        if (!commonInstruments.isEmpty()) {
            // make caption visible
            for (String instrument : commonInstruments) {
                TableRow newRow = new TableRow(this);
                for (int i = 0; i < Compare.ITEMS.size(); i++) {
                    addCell(newRow, getColor(i), instrument);
                }
                commonTable.addView(newRow);
            }
        }

        // create table of instruments
        index = 0;
        for (CompareItem item : Compare.ITEMS) {
            LinearLayout newRow = new LinearLayout(this);
            newRow.setOrientation(LinearLayout.VERTICAL);
            int color = getColor(index++);
            for (EntityInfo instrument : item.getInstruments()) {
                if (commonInstruments.contains(instrument.getName())) {
                    continue;
                }
                addCell(newRow, color, instrument.getName());
            }
            uniqueTable.addView(newRow, getColumnWidth(), LayoutParams.WRAP_CONTENT);
        }
    }

    /**
     * Goto button handler.
     *
     * @param v clicked view
     */
    public void onGotoButtonClick(View v) {
        long id = (Long) v.getTag();

        // put corresponding object query to intent
        Intent intent = new Intent(this, ObjectViewActivity.class);
        intent.putExtra(ObjectViewActivity.EXTRA_QUERY_TYPE, QueryType.GET_MISSION);
        intent.putExtra(ObjectViewActivity.EXTRA_OBJECT_ID, id);
        startActivity(intent);
    }

    /**
     * Delete button handler.
     *
     * @param v clicked view
     */
    public void onDeleteButtonClick(View v) {
        long id = (Long) v.getTag();

        // remove mission from compare
        Compare.removeMission(id);

        if (Compare.ITEMS.size() == 0) {
            // nothing to compare, finish activity
            finish();
        } else {
            // refill activity
            Set<String> commonInstruments = Compare.findCommonInstruments();
            fillActivity(commonInstruments);
        }
    }

    private void addCell(LinearLayout row, int color, String text) {
        TextView textView = new TextView(this);
        textView.setPadding(CELL_PADDING, CELL_PADDING, CELL_PADDING, CELL_PADDING);
        textView.setText(text.trim());
        textView.setBackgroundColor(color);
        row.addView(textView, getColumnWidth(), LayoutParams.MATCH_PARENT);
    }

    private int getColor(int index) {
        return getResources().getColor(index % 2 == 0 ? R.color.table_first : R.color.table_second);
    }

    /**
     * Data load task that will load data required for comparision.
     *
     * @author 7realm
     * @version 1.0
     */
    private final class DataLoadTast extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... arg0) {
            // get instruments info for each mission
            for (CompareItem item : Compare.ITEMS) {
                // set restriction to current mission
                Restriction restriction = new Restriction();
                restriction.setRestrictionEntityClass("Mission");
                restriction.setRestrictionEntityId(item.getMission().getId());

                // create and execute query that will return instruments for current mission
                PagedQuery query = new InfoPagedQuery(QueryType.GET_INSTRUMENTS_INFO, restriction);
                PagedResults results = DataCenter.executePagedQuery(query);

                // add all results to instruments data
                for (Object object : results.getResults()) {
                    item.getInstruments().add((EntityInfo) object);
                }
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            setProgressBarIndeterminateVisibility(true);
        }

        @Override
        protected void onPostExecute(Void result) {
            Set<String> commonInstruments = Compare.findCommonInstruments();

            // fill activity, pass common instruments
            fillActivity(commonInstruments);

            setProgressBarIndeterminateVisibility(false);
        }
    }
}
