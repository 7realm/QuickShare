package my.activity.demo.period;

import my.activity.demo.Helper;
import my.activity.demo.listmanager.ListManager;
import my.activity.demo.listmanager.SharedList;
import my.activity.demo.listmanager.SharedListUpdateListener;
import my.activity.demo.listmanager.inmemory.InMemoryList;
import android.app.ListActivity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class PeriodActivity extends ListActivity implements SharedListUpdateListener {
    public static final String LIST_NAME = "list_name";

    private BaseAdapter adapter;
    private SharedList<Period> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // set up list adapter
        String listName = getIntent().getStringExtra(LIST_NAME);
        list = ListManager.getList(listName, Period.class);
        if (list == null) {
            Toast.makeText(getApplicationContext(), "Failed to find list '" + listName + "'.", Toast.LENGTH_LONG).show();
            list = new InMemoryList<Period>();
        }
        list.registerUpdateListener(this);

        adapter = new PeriodAdapter();
        setListAdapter(adapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        list.unregisterUpdateListener(this);
    }

    @Override
    public void onUpdate() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adapter.notifyDataSetInvalidated();
            }
        });
    }

    private final class PeriodAdapter extends BaseAdapter {
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(android.R.layout.simple_list_item_2, parent, false);
            }

            Period item = getItem(position);
            long periodTime = (item.end == 0 ? System.currentTimeMillis() : item.end) - item.start;

            // create list item caption
            String caption = item.name + " for " + Helper.formatElapsed(periodTime);

            // create list item description
            String text = Helper.format(item.start) + " - " + (item.end == 0 ? " Present " : Helper.format(item.end));

            // update text fields
            ((TextView) convertView.findViewById(android.R.id.text1)).setText(caption);
            ((TextView) convertView.findViewById(android.R.id.text2)).setText(text);

            return convertView;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public Period getItem(int position) {
            return list.get(position);
        }

        @Override
        public int getCount() {
            return list.size();
        }
    }
}
