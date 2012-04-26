package gov.nasa.pds.android;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabContentFactory;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

public class NasaSearchActivity extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search);

        final TabHost tabs = (TabHost) findViewById(R.id.tabhost);
        tabs.setup();
        EmptyTabFactory factory = new EmptyTabFactory();
        tabs.addTab(tabs.newTabSpec("tab0").setIndicator("Target").setContent(factory));
        tabs.addTab(tabs.newTabSpec("tab1").setIndicator("Mission").setContent(factory));
        tabs.addTab(tabs.newTabSpec("tab2").setIndicator("Instrument").setContent(factory));
        tabs.addTab(tabs.newTabSpec("tab3").setIndicator("File").setContent(factory));

        tabs.setOnTabChangedListener(new OnTabChangeListener() {
            @Override
            public void onTabChanged(String tabId) {
                View restrictionTarget = findViewById(R.id.restrictionTarget);
                View restrictionMission = findViewById(R.id.restrictionMission);
                View restrictionInstrument = findViewById(R.id.restrictionInstrument);

                restrictionTarget.setVisibility(View.GONE);
                restrictionMission.setVisibility(View.GONE);
                restrictionInstrument.setVisibility(View.GONE);

                int tabIndex = tabs.getCurrentTab();
                if (tabIndex > 0) {
                    restrictionTarget.setVisibility(View.VISIBLE);
                }
                if (tabIndex > 1) {
                    restrictionMission.setVisibility(View.VISIBLE);
                }
                if (tabIndex > 2) {
                    restrictionInstrument.setVisibility(View.VISIBLE);
                }
            }
        });

        final EditText searhTextEdit = (EditText) findViewById(R.id.searchText);
        searhTextEdit.setOnEditorActionListener(new OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                Log.i("nasa", "Action: " + actionId);
                return false;
            }
        });
    }

    private class EmptyTabFactory implements TabContentFactory {

        @Override
        public View createTabContent(String tag) {
            return new View(NasaSearchActivity.this);
        }

    }
}