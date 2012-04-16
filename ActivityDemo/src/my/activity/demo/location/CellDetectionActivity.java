package my.activity.demo.location;

import my.activity.demo.listmanager.ListManager;
import my.activity.demo.listmanager.SharedList;
import my.activity.demo.listmanager.SharedListUpdateListener;
import my.activity.demo.listmanager.inmemory.InMemoryList;
import android.app.ListActivity;
import android.content.Context;
import android.os.Bundle;
import android.telephony.NeighboringCellInfo;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class CellDetectionActivity extends ListActivity implements SharedListUpdateListener {
    private BaseAdapter adapter;
    private SharedList<NeighboringCellInfo> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // set up list adapter
        list = ListManager.getOrCreateList("cell_list", new InMemoryList<NeighboringCellInfo>());
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

            NeighboringCellInfo item = getItem(position);

            // create list item caption
            String caption = "[" + position + "] Cell ID: " + item.getCid() + ", LAC: " + item.getLac();

            // create list item description
            String text = "RSSI: " + item.getRssi();

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
        public NeighboringCellInfo getItem(int position) {
            return list.get(position);
        }

        @Override
        public int getCount() {
            return list.size();
        }
    }
}
