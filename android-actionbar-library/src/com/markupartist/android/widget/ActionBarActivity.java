package com.markupartist.android.widget;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;

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

    public static void addTab(final TabHost tabHost, int layout, String caption, int icon, int content) {
        // inflate and prepare view
        View tabIndicatorView = LayoutInflater.from(tabHost.getContext()).inflate(layout, null);
        ((ImageView) tabIndicatorView.findViewById(android.R.id.icon)).setImageResource(icon);
        ((TextView) tabIndicatorView.findViewById(android.R.id.title)).setText(caption.toUpperCase());

        // add tab specification
        tabHost.addTab(tabHost.newTabSpec(caption.toLowerCase()).setIndicator(tabIndicatorView).setContent(content));
    }

    public void hideKeyboard(int viewId) {
        InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(findViewById(viewId).getWindowToken(), 0);
    }

    public void showKeyboard(int viewId) {
        InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.showSoftInput(findViewById(viewId), 0);
    }
}
