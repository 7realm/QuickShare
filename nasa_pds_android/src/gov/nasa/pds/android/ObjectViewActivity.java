package gov.nasa.pds.android;

import gov.nasa.pds.data.DataCenter;
import gov.nasa.pds.data.QueryType;
import gov.nasa.pds.data.queries.ObjectQuery;
import gov.nasa.pds.soap.ReferencedEntity;
import gov.nasa.pds.soap.entities.Instrument;
import gov.nasa.pds.soap.entities.InstrumentHost;
import gov.nasa.pds.soap.entities.MetadataObject;
import gov.nasa.pds.soap.entities.Mission;
import gov.nasa.pds.soap.entities.Property;
import gov.nasa.pds.soap.entities.Target;
import gov.nasa.pds.soap.entities.TargetType;
import gov.nasa.pds.soap.entities.WsDataFile;

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

        // get query data form intent
        QueryType queryType = (QueryType) getIntent().getExtras().get(EXTRA_QUERY_TYPE);
        long id = getIntent().getLongExtra(EXTRA_OBJECT_ID, 0);
        query = new ObjectQuery<Object>(queryType, id);

        // set content view based on object type
        setContentView(queryType == QueryType.GET_FILE ? R.layout.activity_file : R.layout.activity_object);

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
        case GET_FILE:
            setText(R.id.fileCaption, "File");
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
                    String description = currentObject.getReferences().get(i).getDescription();
                    data[i] = description == null ? "" : description.replaceAll("\\s+", " ").trim();
                }
                findListView(R.id.objectReferenceList).setAdapter(new ArrayAdapter<String>(ObjectViewActivity.this,
                    R.layout.item_reference, R.id.referenceText, data));

                // create object view
                ViewGroup objectViewContainer = (ViewGroup) findViewById(R.id.objectView);
                if (currentObject instanceof Target) {
                    Target target = (Target) currentObject;

                    // inflate target view
                    LayoutInflater.from(ObjectViewActivity.this).inflate(R.layout.view_target, objectViewContainer, true);
                    setText(R.id.targetName, target.getName());

                    // set list of targets
                    List<TargetType> types = target.getTypes();
                    StringBuilder builder = new StringBuilder(types.isEmpty() ? "" : types.get(0).getName());
                    for (int i = 1; i < types.size(); i++) {
                        builder.append(", ").append(types.get(i).getName());
                    }
                    setText(R.id.targetTypes, builder.toString());
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
                } else if (currentObject instanceof Instrument) {
                    Instrument instrument = (Instrument) currentObject;

                    // inflate and fill instrument view
                    LayoutInflater.from(ObjectViewActivity.this).inflate(R.layout.view_instrument, objectViewContainer, true);
                    setText(R.id.instrumentName, instrument.getName());
                    setText(R.id.instrumentType, instrument.getType());
                    setText(R.id.instrumentDescription, instrument.getDescription());

                    // fill instrument hosts
                    List<InstrumentHost> hosts = instrument.getHosts();
                    StringBuilder builder = new StringBuilder(hosts.isEmpty() ? "" : hosts.get(0).getName());
                    for (int i = 1; i < hosts.size(); i++) {
                        builder.append(hosts.get(i).getName()).append("\n");
                    }
                    setText(R.id.instrumentHost, builder.toString());

                } else {
                    Log.w("soap", "Unexpected object type: " + result);
                }
            } else if (result instanceof WsDataFile) {
                WsDataFile dataFile = (WsDataFile) result;

                setText(R.id.fileCaption, dataFile.getName());
                setText(R.id.fileContent, dataFile.getContent());
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
