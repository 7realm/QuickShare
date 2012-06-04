/*
 * Copyright (C) 2012 TopCoder Inc., All Rights Reserved.
 */
package gov.nasa.pds.android;

import gov.nasa.pds.data.DataCenter;
import gov.nasa.pds.data.ImageCenter;
import gov.nasa.pds.data.QueryType;
import gov.nasa.pds.data.queries.FileQuery;
import gov.nasa.pds.data.queries.ObjectQuery;
import gov.nasa.pds.lessons.Lesson;
import gov.nasa.pds.lessons.LessonPart;
import gov.nasa.pds.lessons.LessonRepository;
import gov.nasa.pds.lessons.parts.FilePart;
import gov.nasa.pds.lessons.parts.ImagePart;
import gov.nasa.pds.lessons.parts.InstrumentPart;
import gov.nasa.pds.lessons.parts.MissionPart;
import gov.nasa.pds.soap.ReferencedEntity;
import gov.nasa.pds.soap.entities.Instrument;
import gov.nasa.pds.soap.entities.InstrumentHost;
import gov.nasa.pds.soap.entities.Mission;
import gov.nasa.pds.soap.entities.Reference;
import gov.nasa.pds.soap.entities.WsDataFile;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.markupartist.android.widget.ActionBar.AbstractAction;
import com.markupartist.android.widget.ActionBar.Action;
import com.markupartist.android.widget.ActionBarActivity;
import com.markupartist.android.widget.actionbar.R;

/**
 * Activity that will view specific objects.
 *
 * @author 7realm
 * @version 1.0
 */
public class ObjectViewActivity extends ActionBarActivity {
    /** Intent extra name for query type. */
    public static final String EXTRA_QUERY_TYPE = "query_type";
    /** Intent extra name for object id. */
    public static final String EXTRA_OBJECT_ID = "object_id";
    private ObjectQuery<Object> query;
    private Object currentObject;
    private long id;

    /**
     * Life-cycle handler for activity creation.
     *
     * @param savedInstanceState the saved instance state
     */
    @SuppressWarnings("unchecked")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // get query data form intent
        QueryType queryType = (QueryType) getIntent().getExtras().get(EXTRA_QUERY_TYPE);
        id = getIntent().getLongExtra(EXTRA_OBJECT_ID, 0);

        // set content view based on object type
        if (queryType == QueryType.GET_FILE || queryType == QueryType.GET_IMAGE) {
            setContentView(R.layout.activity_file);
            query = new FileQuery(queryType, id);
        } else {
            setContentView(R.layout.activity_object);
            query = new ObjectQuery<Object>(queryType, id);
        }

        // add to lesson action
        getActionBar().addAction(new AbstractAction(R.drawable.add_to_lesson, "To Lesson") {
            @Override
            public void performAction(View view) {
                final List<Lesson> lessons = LessonRepository.getLessons();
                String[] items = new String[lessons.size()];
                int index = 0;
                for (Lesson lesson : lessons) {
                    items[index++] = lesson.getName();
                }

                new AlertDialog.Builder(ObjectViewActivity.this)
                    .setTitle("Select lesson:")
                    .setItems(items, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int item) {
                            LessonPart lessonPart = null;
                            // create lesson part for current object
                            if (currentObject instanceof Mission) {
                                lessonPart = new MissionPart((Mission) currentObject);
                            } else if (currentObject instanceof Instrument) {
                                lessonPart = new InstrumentPart((Instrument) currentObject);
                            } else if (currentObject instanceof WsDataFile) {
                                WsDataFile dataFile = (WsDataFile) currentObject;

                                // create image or file lesson part
                                lessonPart = ImageCenter.isImageFile(dataFile) ?
                                    new ImagePart(dataFile.getId(), dataFile.getName()) : new FilePart(dataFile);
                            } else if (currentObject instanceof File) {
                                File imageFile = (File) currentObject;

                                lessonPart = new ImagePart(id, imageFile.getName());
                            } else {
                                Log.w("soap", "Unknown object is not added to lesson: " + currentObject);
                            }

                            // add lesson part to lesson
                            if (lessonPart != null) {
                                lessons.get(item).getParts().add(lessonPart);
                                LessonRepository.save();

                                // show confirmation
                                Toast.makeText(ObjectViewActivity.this,
                                    lessonPart.getPrimaryText() + " is added to lesson.", Toast.LENGTH_LONG).show();
                            }
                        }
                    }).create().show();
            }
        });

        // add compare action
        if (query.getQueryType() == QueryType.GET_MISSION) {
            getActionBar().addAction(new MissionCompareAction());
        }

        // load data
        new DataLoadTast().execute(query);
    }

    /**
     * The reference button clicked.
     *
     * @param v the clicked view
     */
    public void onReferenceButtonClick(View v) {
        CharSequence searchText = ((TextView) ((View) v.getParent()).findViewById(R.id.referenceText)).getText();

        // run google with this query
        Uri uri = Uri.parse("http://www.google.com/#q=" + searchText.toString().trim());
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }

    private void setText(int viewId, CharSequence text) {
        ((TextView) findViewById(viewId)).setText(text);
    }

    private final class MissionCompareAction implements Action {
        @Override
        public String getText() {
            // get mission and check its id
            Mission mission = (Mission) currentObject;
            return mission != null && Compare.exists(mission.getId()) ? "Compare" : "To compare";
        }

        @Override
        public int getDrawable() {
            return R.drawable.compare_add;
        }

        @Override
        public void performAction(View view) {
            // get mission
            Mission mission = (Mission) currentObject;
            if (mission == null) {
                return;
            }

            // if compare already exists, then do compare
            if (Compare.exists(mission.getId())) {
                // check compare size
                if (Compare.ITEMS.size() < 2) {
                    Toast.makeText(ObjectViewActivity.this, "Please select several items to compare.", Toast.LENGTH_SHORT).show();
                } else {
                    startActivity(new Intent(ObjectViewActivity.this, CompareActivity.class));
                }
            } else {
                // add mission to compare
                Compare.addMission(mission);
            }

        }
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
            startProgress();
        }

        @Override
        protected void onPostExecute(final Object result) {
            // store result
            currentObject = result;

            // assign current object
            if (result instanceof ReferencedEntity) {
                ReferencedEntity referencedEntity = (ReferencedEntity) result;

                // set object caption based on query type
                switch (query.getQueryType()) {
                case GET_MISSION:
                    getActionBar().setTitle("Mission: " + referencedEntity.getName());
                    break;
                case GET_INSTRUMENT:
                    getActionBar().setTitle("Instrument: " + referencedEntity.getName());
                    break;
                default:
                    getActionBar().setTitle("Unknown object: " + referencedEntity.getName());
                    break;
                }

                // build list of references
                final List<String> data = new ArrayList<String>();
                List<Reference> references = referencedEntity.getReferences();
                int i = 0;
                while (i < references.size()) {
                    String description = references.get(i).getDescription();

                    // remove incorrect references
                    if (description == null || description.trim().isEmpty() ||
                        description.equalsIgnoreCase("<dummy reference>")) {
                        references.remove(i);
                        continue;
                    }

                    data.add(description.replaceAll("\\s+", " ").trim());
                    i++;
                }

                // create reference tab
                ListView listView = (ListView) findViewById(R.id.objectReferenceList);
                listView.setAdapter(new ArrayAdapter<String>(ObjectViewActivity.this, R.layout.item_reference, R.id.referenceText, data));
                listView.setOnItemClickListener(new OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        // run google with this query
                        Uri uri = Uri.parse("http://www.google.com/#q=" + data.get(position).trim());
                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                        startActivity(intent);
                    }
                });

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
                    for (int j = 1; j < hosts.size(); j++) {
                        builder.append(hosts.get(j).getName()).append("\n");
                    }
                    setText(R.id.instrumentHost, builder.toString());
                    setText(R.id.objectDescription, DataCenter.processDescription(instrument.getDescription()));
                } else {
                    Log.w("soap", "Unexpected object type: " + result);
                }

                // add tabs
                final TabHost tabHost = (TabHost) findViewById(R.id.objectTabs);
                tabHost.setup();
                addTab(tabHost, R.layout.view_tab_indicator, "General", -1, R.id.objectPropertiesView);
                addTab(tabHost, R.layout.view_tab_indicator, "Description", -1, R.id.objectDescriptionView);
                addTab(tabHost, R.layout.view_tab_indicator, "References", -1, R.id.objectReferenceList);
            } else if (result instanceof WsDataFile) {
                WsDataFile dataFile = (WsDataFile) result;

                if (ImageCenter.isImageFile(dataFile)) {
                    File imageFile = ImageCenter.getImage(dataFile.getId());
                    findViewById(R.id.fileHelpText).setVisibility(imageFile == null ? View.VISIBLE : View.GONE);

                    // set image from downloaded file
                    getActionBar().setTitle("Image: " + dataFile.getName());
                    ImageView imageView = (ImageView) findViewById(R.id.fileImage);
                    imageView.setImageURI(Uri.fromFile(imageFile));
                    findViewById(R.id.fileDocument).setVisibility(View.GONE);
                } else {
                    getActionBar().setTitle("File: " + dataFile.getName());
                    setText(R.id.fileContent, dataFile.getContent());
                    findViewById(R.id.fileImage).setVisibility(View.GONE);
                }
            } else if (result instanceof File) {
                File imageFile = (File) result;

                // set image from downloaded file
                getActionBar().setTitle("Image: " + imageFile.getName());
                ImageView imageView = (ImageView) findViewById(R.id.fileImage);
                imageView.setImageURI(Uri.fromFile(imageFile));
                findViewById(R.id.fileDocument).setVisibility(View.GONE);
            } else {
                if (query.getQueryType() == QueryType.GET_FILE || query.getQueryType() == QueryType.GET_IMAGE) {
                    findViewById(R.id.fileHelpText).setVisibility(View.VISIBLE);
                }
                Log.w("soap", "Result: " + result + " is not referenced object.");
            }

            // update actions
            getActionBar().updateActions();

            stopProgress();
        }
    }
}
