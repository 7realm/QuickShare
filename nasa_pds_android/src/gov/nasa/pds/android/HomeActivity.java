/*
 * Copyright (C) 2012 TopCoder Inc., All Rights Reserved.
 */
package gov.nasa.pds.android;

import gov.nasa.pds.data.DataCenter;
import gov.nasa.pds.data.EntityType;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

/**
 * Home activity of application, it is displayed first.
 *
 * @author 7realm
 * @version 1.0
 */
public class HomeActivity extends Activity {
    private int retryCount = 0;

    /**
     * Life-cycle handler for activity creation.
     *
     * @param savedInstanceState the saved instance state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
    }

    /**
     * Life-cycle handler for activity first displayed on screen.
     */
    @Override
    protected void onStart() {
        super.onStart();

        // if not connected after 3 retires
        retryCount++;
        if (retryCount < 3) {
            // test connection
            new TestConnectionTask().execute();
        }
    }

    /**
     * User touched the screen.
     *
     * @param v touched view
     */
    @SuppressWarnings("unused")
    public void onTouchScreenClick(View v) {
        startBrowsingFor(EntityType.TARGET_TYPE);
    }

    /**
     * User touched targets button.
     *
     * @param v touched view
     */
    @SuppressWarnings("unused")
    public void onTargetsButtonClick(View v) {
        startBrowsingFor(EntityType.TARGET);
    }

    /**
     * User clicked missions button.
     *
     * @param v clicked view
     */
    @SuppressWarnings("unused")
    public void onMissionsButtonClick(View v) {
        startBrowsingFor(EntityType.MISSION);
    }

    /**
     * User clicked instruments button.
     *
     * @param v clicked view
     */
    @SuppressWarnings("unused")
    public void onInstrumentsButtonClick(View v) {
        startBrowsingFor(EntityType.INSTRUMENT);
    }

    private void startBrowsingFor(EntityType entityType) {
        Intent intent = new Intent(this, PageViewActivity.class);
        intent.putExtra(PageViewActivity.EXTRA_ENTITY_TYPE, entityType);
        startActivity(intent);
    }

    /**
     * Task that will test the connection.
     *
     * @author 7realm
     * @version 1.0
     */
    private class TestConnectionTask extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... params) {
            // just test connection
            return DataCenter.testConnection();
        }

        @Override
        protected void onPostExecute(Boolean testSuccessful) {
            if (!testSuccessful) {
                Log.w("soap", "Connection failed to: " + DataCenter.getUrl());

                // let user adjust url
                final EditText editText = new EditText(HomeActivity.this);
                editText.setText(DataCenter.getUrl());
                new AlertDialog.Builder(HomeActivity.this).setTitle("Enter host url:").setView(editText)
                    .setNegativeButton("Cancel", new OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            retryCount = Integer.MAX_VALUE;
                        }
                    }).setPositiveButton("OK",
                        new OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                DataCenter.setUrl(editText.getText().toString());

                                // save URL
                                getApplicationContext().getSharedPreferences("general", MODE_PRIVATE)
                                    .edit().putString("host", DataCenter.getUrl()).commit();

                                // test connection with new URL
                                new TestConnectionTask().execute();
                            }
                        }).show();
            }
        }
    }
}
