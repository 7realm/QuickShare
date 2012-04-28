package gov.nasa.pds.android;

import gov.nasa.pds.data.DataCenter;
import gov.nasa.pds.data.QueryType;
import gov.nasa.pds.data.queries.ObjectQuery;
import gov.nasa.pds.soap.ReferencedEntity;
import gov.nasa.pds.soap.entities.MetadataObject;
import gov.nasa.pds.soap.entities.Mission;
import gov.nasa.pds.soap.entities.Property;
import gov.nasa.pds.soap.entities.Target;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class ObjectViewActivity extends Activity {
    public static final String EXTRA_QUERY_TYPE = "query_type";
    public static final String EXTRA_OBJECT_ID = "object_id";
    private ObjectQuery<Object> query;
    private ReferencedEntity currentObject;

    @SuppressWarnings("unchecked")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.object_result);

        // get query data form intent
        QueryType queryType = QueryType.valueOf(getIntent().getStringExtra(EXTRA_QUERY_TYPE));
        long id = getIntent().getLongExtra(EXTRA_OBJECT_ID, 0);
        query = new ObjectQuery<Object>(queryType, id);

        // load data
        new DataLoadTast().execute(query);

        // set object caption based on query type
        switch (queryType) {
        case GET_TARGET:
            setText(R.id.objectCaption, "Target");
            break;
        case GET_MISSION:
            setText(R.id.objectCaption, "Mission");
            break;
        case GET_INSTRUMENT:
            setText(R.id.objectCaption, "Instrument");
            break;
        default:
            break;
        }
    }

    private void setText(int viewId, String text) {
        ((TextView) findViewById(viewId)).setText(text);
    }

    private ListView findListView(int viewId) {
        return (ListView) findViewById(viewId);
    }

    private final class DataLoadTast extends AsyncTask<ObjectQuery<Object>, Void, Object> {

        @Override
        protected Object doInBackground(ObjectQuery<Object>... queries) {
            return DataCenter.executeObjectQuery(queries[0]);
        }

        @Override
        protected void onPostExecute(Object result) {
            // assign current object
            if (result instanceof ReferencedEntity) {
                currentObject = (ReferencedEntity) result;

                // fill reference section
                String[] data = new String[currentObject.getReferences().size()];
                for (int i = 0; i < data.length; i++) {
                    // TODO chck null description
                    data[i] = currentObject.getReferences().get(i).getDescription().trim();
                }
                findListView(R.id.objectReferenceList).setAdapter(new ArrayAdapter<String>(ObjectViewActivity.this,
                    android.R.layout.simple_list_item_1, android.R.id.text1, data));

                // create object view
                ViewGroup objectViewContainer = (ViewGroup) findViewById(R.id.objectView);
                if (currentObject instanceof Target) {
                    Target target = (Target) currentObject;

                    // inflate target view
                    LayoutInflater.from(ObjectViewActivity.this).inflate(R.layout.view_target, objectViewContainer, true);
                    setText(R.id.targetName, target.getName());

                    // set list of targets
                    data = new String[target.getTypes().size()];
                    for (int i = 0; i < data.length; i++) {
                        data[i] = target.getTypes().get(i).getName();
                    }
                    findListView(R.id.targetTypesList).setAdapter(new ArrayAdapter<String>(ObjectViewActivity.this,
                        android.R.layout.simple_list_item_1, android.R.id.text1, data));
                } else if (currentObject instanceof Mission) {
                    Mission mission = (Mission) currentObject;

                    // inflate and fill mission view
                    LayoutInflater.from(ObjectViewActivity.this).inflate(R.layout.view_mission, objectViewContainer, true);
                    setText(R.id.missionName, mission.getName());
                    setText(R.id.missionPeriod, "From " + mission.getStartDate() + " to " + mission.getEndDate() + ".");
                    setText(R.id.missionDescription, mission.getDescription());

                    // fill metadata
                    findListView(R.id.metaDataList).setAdapter(new SimpleAdapter(ObjectViewActivity.this,
                        buildMetaData(mission), android.R.layout.simple_list_item_2,
                        new String[] {"name", "description"}, new int[] {android.R.id.text1, android.R.id.text2}));

                } else {
                    Log.w("soap", "Unexpected object type: " + result);
                }
            } else {
                Log.w("soap", "Result: " + result + " is not referenced object.");
            }
        }

        private List<Map<String, Object>> buildMetaData(Mission mission) {
            List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
            doBuild(list, "", mission.getOtherChildren());
            return list;
        }

        private void doBuild(List<Map<String, Object>> list, String prefix, List<MetadataObject> metaDataList) {
            if (metaDataList == null) {
                return;
            }
            for (MetadataObject metadataObject : metaDataList) {
                String newPrefix = prefix + "." + metadataObject.getName();

                Map<String, Object> item = new HashMap<String, Object>();
                item.put("name", newPrefix);
                StringBuilder builder = new StringBuilder();
                for (Property property : metadataObject.getProperties()) {
                    builder.append("{").append(property.getName()).append(" = ");
                    for (String propertyValue : property.getValues()) {
                        builder.append(propertyValue).append("; ");
                    }
                    builder.append("}");
                }
                item.put("description", builder.toString());
                list.add(item);

                // add children
                doBuild(list, prefix, metadataObject.getChildren());
            }
        }

    }
}
