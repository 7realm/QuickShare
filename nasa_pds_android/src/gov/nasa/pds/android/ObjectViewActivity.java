/*
 * Copyright (C) 2012 TopCoder Inc., All Rights Reserved.
 */
package gov.nasa.pds.android;

import gov.nasa.pds.data.DataCenter;
import gov.nasa.pds.data.QueryType;
import gov.nasa.pds.data.queries.ObjectQuery;
import gov.nasa.pds.soap.ReferencedEntity;
import gov.nasa.pds.soap.entities.Instrument;
import gov.nasa.pds.soap.entities.InstrumentHost;
import gov.nasa.pds.soap.entities.Mission;
import gov.nasa.pds.soap.entities.WsDataFile;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Activity that will view specific objects.
 *
 * @author 7realm
 * @version 1.0
 */
public class ObjectViewActivity extends Activity {
    /** Intent extra name for query type. */
    public static final String EXTRA_QUERY_TYPE = "query_type";
    /** Intent extra name for object id. */
    public static final String EXTRA_OBJECT_ID = "object_id";
    private ObjectQuery<Object> query;
    private ReferencedEntity currentObject;

    /**
     * Life-cycle handler for activity creation.
     *
     * @param savedInstanceState the saved instance state
     */
    @SuppressWarnings("unchecked")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        super.onCreate(savedInstanceState);

        // get query data form intent
        QueryType queryType = (QueryType) getIntent().getExtras().get(EXTRA_QUERY_TYPE);
        long id = getIntent().getLongExtra(EXTRA_OBJECT_ID, 0);
        query = new ObjectQuery<Object>(queryType, id);

        // set content view based on object type
        setContentView(queryType == QueryType.GET_FILE ? R.layout.activity_file : R.layout.activity_object);

        // set text and visibility of add to compare button
        Button addToCompare = (Button) findViewById(R.id.objectCompareButton);
        if (addToCompare != null) {
            addToCompare.setVisibility(queryType == QueryType.GET_MISSION ? View.VISIBLE : View.INVISIBLE);
            addToCompare.setText(Compare.exists(id) ? "Compare" : "Add to compare");
        }

        // load data
        new DataLoadTast().execute(query);
    }

    /**
     * Compare button clicked.
     *
     * @param v the clicked view
     */
    public void onCompareButtonClick(View v) {
        // get mission from tag attribute
        Mission mission = (Mission) v.getTag();
        if (mission == null) {
            return;
        }

        // if compare already exists, then do compare
        if (Compare.exists(mission.getId())) {
            // check compare size
            if (Compare.ITEMS.size() < 2) {
                Toast.makeText(this, "Please select several items to compare.", Toast.LENGTH_SHORT).show();
            } else {
                startActivity(new Intent(this, CompareActivity.class));
            }
        } else {
            // add mission to compare and change label
            Compare.addMission(mission);
            setText(R.id.objectCompareButton, "Compare");
        }
    }

    /**
     * The reference button clicked.
     *
     * @param v the clicked view
     */
    public void onReferenceButtonClick(View v) {
        CharSequence searchText = ((TextView) ((View) v.getParent()).findViewById(R.id.referenceText)).getText();

        // run google with this query
        Uri uri = Uri.parse("http://www.google.com/#q=" + searchText);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }

    private void setText(int viewId, CharSequence text) {
        ((TextView) findViewById(viewId)).setText(text);
    }

    /**
     * Task that will load data for given entity.
     *
     * @author 7realm
     * @version 1.0
     */
    private final class DataLoadTast extends AsyncTask<ObjectQuery<Object>, Void, Object> {

        @Override
        protected Object doInBackground(ObjectQuery<Object>... queries) {
            return DataCenter.executeObjectQuery(queries[0]);
        }

        @Override
        protected void onPreExecute() {
            setProgressBarIndeterminateVisibility(true);
        }

        @Override
        protected void onPostExecute(final Object result) {
            // set tag
            View compareButton = findViewById(R.id.objectCompareButton);
            if (compareButton != null) {
                compareButton.setTag(result);
            }

            // assign current object
            if (result instanceof ReferencedEntity) {
                currentObject = (ReferencedEntity) result;

                // set object caption based on query type
                switch (query.getQueryType()) {
                case GET_MISSION:
                    setText(R.id.objectCaption, "Mission: " + currentObject.getName());
                    break;
                case GET_INSTRUMENT:
                    setText(R.id.objectCaption, "Instrument: " + currentObject.getName());
                    break;
                default:
                    setText(R.id.objectCaption, "Unknown object");
                    break;
                }

                // build list of references
                String[] data = new String[currentObject.getReferences().size()];
                for (int i = 0; i < data.length; i++) {
                    String description = currentObject.getReferences().get(i).getDescription();
                    data[i] = description == null ? "" : description.replaceAll("\\s+", " ").trim();
                }

                // create reference tab
                ((ListView) findViewById(R.id.objectReferenceList)).setAdapter(
                    new ArrayAdapter<String>(ObjectViewActivity.this, R.layout.item_reference, R.id.referenceText, data));

                // get object container
                final ViewGroup objectContainer = (ViewGroup) findViewById(R.id.objectPropertiesView);
                objectContainer.removeAllViews();
                if (currentObject instanceof Mission) {
                    Mission mission = (Mission) currentObject;

                    // inflate and fill mission view
                    LayoutInflater.from(ObjectViewActivity.this).inflate(R.layout.view_mission, objectContainer);
                    setText(R.id.missionName, mission.getName());
                    setText(R.id.missionStartDate, DataCenter.formatLong(mission.getStartDate()));
                    setText(R.id.missionEndDate, DataCenter.formatLong(mission.getEndDate()));
                    setText(R.id.missionDuration, DataCenter.formatPeriod(mission.getStartDate(), mission.getEndDate()));
                    setText(R.id.objectDescription, DataCenter.processDescription(mission.getDescription()));
                } else if (currentObject instanceof Instrument) {
                    Instrument instrument = (Instrument) currentObject;

                    // inflate and fill instrument view
                    LayoutInflater.from(ObjectViewActivity.this).inflate(R.layout.view_instrument, objectContainer);
                    setText(R.id.instrumentName, instrument.getName());
                    setText(R.id.instrumentType, instrument.getType());

                    // fill instrument hosts
                    List<InstrumentHost> hosts = instrument.getHosts();
                    StringBuilder builder = new StringBuilder(hosts.isEmpty() ? "" : hosts.get(0).getName());
                    for (int i = 1; i < hosts.size(); i++) {
                        builder.append(hosts.get(i).getName()).append("\n");
                    }
                    setText(R.id.instrumentHost, builder.toString());
                    setText(R.id.objectDescription, DataCenter.processDescription(instrument.getDescription()));
                } else {
                    Log.w("soap", "Unexpected object type: " + result);
                }

                // add tabs
                final TabHost tabHost = (TabHost) findViewById(R.id.objectTabs);
                tabHost.setup();
                tabHost.addTab(tabHost.newTabSpec("general").setIndicator("General").setContent(R.id.objectPropertiesView));
                tabHost.addTab(tabHost.newTabSpec("description").setIndicator("Description").setContent(R.id.objectDescriptionView));
                tabHost.addTab(tabHost.newTabSpec("references").setIndicator("References").setContent(R.id.objectReferenceList));
            } else if (result instanceof WsDataFile) {
                WsDataFile dataFile = (WsDataFile) result;

                setText(R.id.fileCaption, dataFile.getName());
                setText(R.id.fileContent, dataFile.getContent());
            } else {
                Log.w("soap", "Result: " + result + " is not referenced object.");
            }

            setProgressBarIndeterminateVisibility(false);
        }
    }
}
