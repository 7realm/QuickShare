/*
 * Copyright (C) 2012 TopCoder Inc., All Rights Reserved.
 */
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

/**
 * Activity that has action bar.
 *
 * @author TCSASSEMBLER
 * @version 1.0
 */
public class ActionBarActivity extends Activity {
    private ActionBar actionBar;

    /**
     * Gets action bar.
     *
     * @return the action bar
     */
    public ActionBar getActionBar() {
        if (actionBar == null) {
            actionBar = (ActionBar) findViewById(R.id.actionbarLayout).getParent();
        }
        return actionBar;
    }

    /**
     * Start indication progress.
     */
    public void startProgress() {
        actionBar.setProgressBarVisible(true);
    }

    /**
     * Stop indicating progress.
     */
    public void stopProgress() {
        actionBar.setProgressBarVisible(false);
    }

    /**
     * Add tab to tab host.
     *
     * @param tabHost tab host
     * @param layout the tab layout
     * @param caption the tab caption
     * @param icon the tab icon
     * @param content the tab content view
     */
    public static void addTab(final TabHost tabHost, int layout, String caption, int icon, int content) {
        // inflate and prepare view
        View tabIndicatorView = LayoutInflater.from(tabHost.getContext()).inflate(layout, null);
        ((ImageView) tabIndicatorView.findViewById(android.R.id.icon)).setImageResource(icon);
        ((TextView) tabIndicatorView.findViewById(android.R.id.title)).setText(caption.toUpperCase());

        // add tab specification
        tabHost.addTab(tabHost.newTabSpec(caption.toLowerCase()).setIndicator(tabIndicatorView).setContent(content));
    }

    /**
     * Hide keyboard for the view.
     *
     * @param viewId the view id
     */
    public void hideKeyboard(int viewId) {
        InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(findViewById(viewId).getWindowToken(), 0);
    }

    /**
     * Show keyboard for the view.
     *
     * @param viewId the view id
     */
    public void showKeyboard(int viewId) {
        InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.showSoftInput(findViewById(viewId), 0);
    }
}
