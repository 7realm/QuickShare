package com.markupartist.android.widget;

import android.app.Activity;

import com.markupartist.android.widget.actionbar.R;

public class ActionBarActivity extends Activity {
    private ActionBar actionBar;

    public ActionBar getActionBar() {
        if (actionBar == null) {
            actionBar = (ActionBar) findViewById(R.id.actionbarLayout).getParent();
        }
        return actionBar;
    }

    public void startProgress() {
        actionBar.setProgressBarVisible(true);
    }

    public void stopProgress() {
        actionBar.setProgressBarVisible(false);
    }
}
