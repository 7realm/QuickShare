package gov.nasa.pds.android;

import gov.nasa.pds.data.PageResultsProvider;
import gov.nasa.pds.data.QueryType;
import gov.nasa.pds.data.ResultsProvider;
import gov.nasa.pds.data.TargetTypesResultsProvider;
import gov.nasa.pds.data.queries.InfoPagedQuery;

import java.util.concurrent.atomic.AtomicBoolean;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

public class PageViewActivity extends Activity {
    public static final String EXTRA_QUERY_TYPE = "query_type";
    public static final int REQUEST_SELECT_RESTRICTION = 1001;
    private ViewFlipper viewFlipper;
    private ResultsProvider provider;
    private final AtomicBoolean firstRun = new AtomicBoolean();
    private final PageResultsAdapter adapter = new PageResultsAdapter();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.page_results);

        // set query from intent
        setQueryType(QueryType.GET_TYPES_INFO);

        // get view flipper
        viewFlipper = (ViewFlipper) findViewById(R.id.browserFlipper);

        // set slider
        findViewById(R.id.browserLayout).setOnTouchListener(new OnTouchListener() {
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

        // find and setup spinner
        Spinner spinner = (Spinner) findViewById(R.id.browserSpinner);
        // set first selected item
        spinner.setSelection(0);
        spinner.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterview, View view, int pos, long id) {
                switch (pos) {
                case 0:
                    setQueryType(QueryType.GET_TYPES_INFO);
                    break;
                case 1:
                    setQueryType(QueryType.GET_TARGETS_INFO);
                    break;
                case 2:
                    setQueryType(QueryType.GET_MISSIONS_INFO);
                    break;
                case 3:
                    setQueryType(QueryType.GET_INSTRUMENTS_INFO);
                default:
                    Log.w("soap", "Selected item at unexpected position.");
                    break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterview) {
                // do nothing
            }
        });
    }

    private void setQueryType(QueryType queryType) {
        provider = queryType == QueryType.GET_TYPES_INFO ?
            new TargetTypesResultsProvider() : new PageResultsProvider(new InfoPagedQuery(queryType, 1));

        // load first page
        firstRun.set(true);
        new DataLoadTast().execute(1);
    }

    @SuppressWarnings("unused")
    public void onNextButtonClick(View v) {
        goToNext();
    }

    @SuppressWarnings("unused")
    public void onPreviousButtonClick(View v) {
        goToPrevious();
    }

    public void onGotoButtonClick(View v) {
        Intent intent = new Intent(this, ObjectViewActivity.class);

        // put corresponding object query to intent
        intent.putExtra(ObjectViewActivity.EXTRA_QUERY_TYPE, convert(provider.getQueryType()));
        intent.putExtra(ObjectViewActivity.EXTRA_OBJECT_ID, (Long) v.getTag());
        startActivity(intent);
    }

    @SuppressWarnings("unused")
    public void onFilterButtonClick(View v) {
        Toast.makeText(this, "Filter", Toast.LENGTH_LONG).show();
    }

    private static String convert(QueryType queryType) {
        switch (queryType) {
        case GET_TARGETS_INFO:
            return QueryType.GET_TARGET.name();
        case GET_MISSIONS_INFO:
            return QueryType.GET_MISSION.name();
        case GET_INSTRUMENTS_INFO:
            return QueryType.GET_INSTRUMENT.name();
        default:
            Log.w("soap", "Unexpected query type for goto button: " + queryType);
            return QueryType.GET_TARGET.name();
        }
    }

    private void goToNext() {
        Toast.makeText(this, "Go Next", Toast.LENGTH_SHORT).show();

        // get number of next page
        int nextPage = provider.getCurrentPage() + 1;
        if (nextPage == provider.getPageCount() + 1) {
            nextPage = 1;
        }

        // move to next page
        new DataLoadTast().execute(nextPage);

        // slide the view
        viewFlipper.showNext();

        setPageCaption(nextPage);
    }

    private void goToPrevious() {
        Toast.makeText(this, "Go Previous", Toast.LENGTH_SHORT).show();
        // get number of previous page
        int previousPage = provider.getCurrentPage() - 1;
        if (previousPage == 0) {
            previousPage = provider.getPageCount();
        }

        // move to previous page
        new DataLoadTast().execute(previousPage);

        // slide the view
        viewFlipper.showPrevious();

        setPageCaption(previousPage);
    }

    private void setPageCaption(int nextPage) {
        ((TextView) findViewById(R.id.pageCaption)).setText("Page " + nextPage + " of " + provider.getPageCount());
    }

    private final class DataLoadTast extends AsyncTask<Integer, Void, Void> {

        @Override
        protected Void doInBackground(Integer... params) {
            provider.moveToPage(params[0]);
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            // add views on first run
            if (firstRun.get()) {
                // clear flipper and add new views
                viewFlipper.removeAllViews();
                for (int i = 0; i < provider.getPageCount(); i++) {
                    ListView view = new ListView(PageViewActivity.this);
                    view.setAdapter(adapter);
                    viewFlipper.addView(view, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
                }
                firstRun.set(false);
                setPageCaption(1);
            }

            // notify list about content change
            adapter.notifyDataSetInvalidated();
        }
    }

    private final class PageResultsAdapter extends BaseAdapter {
        @Override
        public View getView(int i, View view, ViewGroup viewgroup) {
            if (view == null) {
                view = LayoutInflater.from(PageViewActivity.this).inflate(R.layout.entity_item, null, true);
            }
            provider.fillView(i, view);
            return view;
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public Object getItem(int i) {
            return provider.getItem(i);
        }

        @Override
        public int getCount() {
            return provider.getCurrentPageSize();
        }
    }
}
