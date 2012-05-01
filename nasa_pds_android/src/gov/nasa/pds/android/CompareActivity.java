/*
 * Created by 7realm at 30.04.2012 9:24:18. All rights reserved.
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
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

/**
 *
 *
 * @author TCSDESIGNER, TCSDEVELOPER
 * @version 1.0
 */
public class CompareActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compare);

        // load data
        new DataLoadTast().execute();
    }

    private int getColumnWidth() {
        return getWindow().getDecorView().getWidth() / 2;
    }

    private void fillActivity(Set<String> commonInstruments) {
        TableRow nameRow = (TableRow) findViewById(R.id.compareNameRow);
        TableRow startDateRow = (TableRow) findViewById(R.id.compareStartDateRow);
        TableRow endDateRow = (TableRow) findViewById(R.id.compareEndDateRow);
        TableRow durationRow = (TableRow) findViewById(R.id.compareDurationRow);
        TableRow buttonsRow = (TableRow) findViewById(R.id.compareButtonsRow);

        TableLayout commonTable = (TableLayout) findViewById(R.id.compareCommonInstrumentsTable);
        LinearLayout uniqueTable = (LinearLayout) findViewById(R.id.compareUniqueInstrumentsTable);

        // clear rows
        nameRow.removeAllViews();
        startDateRow.removeAllViews();
        endDateRow.removeAllViews();
        durationRow.removeAllViews();
        buttonsRow.removeAllViews();

        // clear tables
        commonTable.removeAllViews();
        uniqueTable.removeAllViews();

        int index = 0;
        int maxInstruments = 0;
        for (CompareItem item : Compare.ITEMS) {
            int color = getColor(index++);
            Mission mission = item.getMission();

            // add simple properties
            addCell(nameRow, color, mission.getName());
            addCell(startDateRow, color, DataCenter.formatLong(mission.getStartDate()));
            addCell(endDateRow, color, DataCenter.formatLong(mission.getEndDate()));
            addCell(durationRow, color, DataCenter.formatPeriod(mission.getStartDate(), mission.getEndDate()));

            // add goto and remove buttons with proper tag
            View buttonsView = LayoutInflater.from(this).inflate(R.layout.view_compare_buttons, null);
            buttonsView.setBackgroundColor(color);
            buttonsRow.addView(buttonsView, getColumnWidth(), LayoutParams.WRAP_CONTENT);
            buttonsView.findViewById(R.id.compareDeleteButton).setTag(mission.getId());
            buttonsView.findViewById(R.id.compareGotoButton).setTag(mission.getId());

            // calculate max number of instruments for unique table rows
            maxInstruments = Math.max(item.getInstruments().size(), maxInstruments);
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

        // TODO remove size of common instruments
        maxInstruments = maxInstruments - commonInstruments.size();

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
            uniqueTable.addView(newRow, getColumnWidth(), LayoutParams.MATCH_PARENT);
        }
    }

    public void onGotoButtonClick(View v) {
        long id = (Long) v.getTag();

        // put corresponding object query to intent
        Intent intent = new Intent(this, ObjectViewActivity.class);
        intent.putExtra(ObjectViewActivity.EXTRA_QUERY_TYPE, QueryType.GET_MISSION);
        intent.putExtra(ObjectViewActivity.EXTRA_OBJECT_ID, id);
        startActivity(intent);
    }

    public void onDeleteButtonClick(View v) {
        long id = (Long) v.getTag();

        // remove mission from compare
        Compare.removeMission(id);

        // refill activity
        Set<String> commonInstruments = Compare.findCommonInstruments();
        fillActivity(commonInstruments);
    }

    private void addCell(LinearLayout row, int color, CharSequence text) {
        TextView textView = new TextView(this);
        textView.setText(text);
        textView.setBackgroundColor(color);
        row.addView(textView, getColumnWidth(), LayoutParams.WRAP_CONTENT);
    }

    private static int getColor(int index) {
        return index % 2 == 0 ? Color.GRAY : Color.LTGRAY;
    }

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
        protected void onPostExecute(Void result) {
            Set<String> commonInstruments = Compare.findCommonInstruments();

            // fill activity, pass common instruments
            fillActivity(commonInstruments);
        }
    }

}
