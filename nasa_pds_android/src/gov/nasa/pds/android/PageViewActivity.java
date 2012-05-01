package gov.nasa.pds.android;

import gov.nasa.pds.android.Filter.NamedRestriction;
import gov.nasa.pds.data.EntityType;
import gov.nasa.pds.data.resultproviders.ResultsProvider;
import gov.nasa.pds.soap.entities.EntityInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ViewFlipper;

public class PageViewActivity extends Activity {
    public static final String EXTRA_QUERY_TYPE = "query_type";
    public static final int REQUEST_SELECT_RESTRICTION = 1001;
    private ViewFlipper viewFlipper;
    private ResultsProvider provider;
    private EntityType entityType;
    private final Filter filter = new Filter();
    private final AtomicBoolean firstRun = new AtomicBoolean();
    private Spinner spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browse);

        // get view flipper
        viewFlipper = (ViewFlipper) findViewById(R.id.browserFlipper);

        // set slider
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

        // set query for base type
        setEntityType(EntityType.TARGET_TYPE);
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

        // TODO cancel current task
        // load first page
        firstRun.set(true);
        new DataLoadTast().execute(1);

        // set filter caption
        setText(R.id.browserFilterCaption, filter.toString());
    }

    @SuppressWarnings("unused")
    public void onNextButtonClick(View v) {
        goToNext();
    }

    @SuppressWarnings("unused")
    public void onPreviousButtonClick(View v) {
        goToPrevious();
    }

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

    @Override
    public void onBackPressed() {
        // if we are on top level then finish activity
        if (entityType == EntityType.TARGET_TYPE) {
            super.onBackPressed();
        } else {
            setEntityType(entityType.upper());
        }
    }

    public void openEntity(EntityInfo entityInfo) {
        // if we on lowest level, open the file
        if (entityType == EntityType.lowest()) {
            gotoEntity(entityInfo.getId());
        } else {
            // add restriction
            filter.addRestriction(entityInfo, entityType);

            // go to lower level
            setEntityType(entityType.lower());
        }
    }

    @SuppressWarnings("unused")
    public void onFilterButtonClick(View v) {
        // prepare restriction list labels
        CharSequence[] items = new String[filter.restrictions.size()];
        for (int i = 0; i < items.length; i++) {
            items[i] = filter.restrictions.get(i).toString();
        }

        // create dialog view
        View layout = LayoutInflater.from(this).inflate(R.layout.dialog_filter, null);
        final TextView dialogText = (TextView) layout.findViewById(R.id.dialogSearchText);
        dialogText.setText(filter.getText());
        final ListView dialogList = (ListView) layout.findViewById(R.id.dialogList);
        dialogList.setAdapter(new ArrayAdapter<CharSequence>(this, android.R.layout.simple_list_item_multiple_choice, items));
        dialogList.setItemsCanFocus(false);
        dialogList.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);
        for (int i = 0; i < items.length; i++) {
            dialogList.setItemChecked(i, true);
        }

        // run the dialog
        new AlertDialog.Builder(this)
            .setTitle("Enter text filter: ")
            .setView(layout)
            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    SparseBooleanArray checkedItems = dialogList.getCheckedItemPositions();
                    // For each element in the status array
                    int checkedItemsCount = checkedItems.size();

                    // check if text or restrictions are changed
                    String text = dialogText.getText().toString().trim();
                    boolean changed = !text.equals(filter.getText());
                    for (int i = 0; i < checkedItemsCount; ++i) {
                        changed = changed || !checkedItems.valueAt(i);
                    }

                    // run search if filter is changed
                    if (changed) {
                        filter.setText(text);

                        List<NamedRestriction> keepedRestrictions = new ArrayList<NamedRestriction>();
                        List<NamedRestriction> restrictions = filter.restrictions;
                        for (int i = 0; i < checkedItemsCount; ++i) {
                            if (checkedItems.valueAt(i)) {
                                keepedRestrictions.add(restrictions.get(checkedItems.keyAt(i)));
                            }
                        }

                        // refresh filter
                        restrictions.clear();
                        restrictions.addAll(keepedRestrictions);

                        refreshProvider();
                    }
                }
            })
            .setNegativeButton("Cancel", null)
            .show();
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

        setPageCaption(nextPage);
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

        setPageCaption(previousPage);
    }

    private void setPageCaption(int nextPage) {
        if (provider.getPageCount() == 0) {
            setText(R.id.browserPageCaption, "No results");
        } else {
            setText(R.id.browserPageCaption, "Page " + nextPage + " of " + provider.getPageCount());
        }
    }

    private void setText(int viewId, String text) {
        ((TextView) findViewById(viewId)).setText(text);
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
                    view.setAdapter(provider);
                    viewFlipper.addView(view, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
                }
                firstRun.set(false);
                setPageCaption(1);
            }

            // notify list about content change
            provider.notifyDataSetInvalidated();
        }
    }
}
