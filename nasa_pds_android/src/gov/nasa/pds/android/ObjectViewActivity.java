package gov.nasa.pds.android;

import gov.nasa.pds.data.DataCenter;
import gov.nasa.pds.data.QueryType;
import gov.nasa.pds.data.queries.ObjectQuery;
import gov.nasa.pds.soap.ReferencedEntity;
import gov.nasa.pds.soap.entities.Target;
import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
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
                    data[i] = currentObject.getReferences().get(i).getDescription();
                }
                findListView(R.id.objectReferenceList).setAdapter(new ArrayAdapter<String>(ObjectViewActivity.this,
                    android.R.layout.simple_list_item_1, android.R.id.text1, data));

                // create object view
                ViewGroup objectViewContainer = (ViewGroup) findViewById(R.id.objectView);
                if (currentObject instanceof Target) {
                    Target target = (Target) currentObject;

                    // fill object view
                    LayoutInflater.from(ObjectViewActivity.this).inflate(R.layout.view_target, objectViewContainer, true);
                    setText(R.id.targetName, target.getName());

                    // set list of targets
                    data = new String[target.getTypes().size()];
                    for (int i = 0; i < data.length; i++) {
                        data[i] = target.getTypes().get(i).getName();
                    }
                    findListView(R.id.targetTypesList).setAdapter(new ArrayAdapter<String>(ObjectViewActivity.this,
                        android.R.layout.simple_list_item_1, android.R.id.text1, data));
                } else {
                    Log.w("soap", "Unexpected object type: " + result);
                }
            } else {
                Log.w("soap", "Result: " + result + " is not referenced object.");
            }
        }

    }
}
