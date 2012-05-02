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
 *
 *
 * @author TCSDESIGNER, TCSDEVELOPER
 * @version 1.0
 */
public class HomeActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // restore URL from preferences
        String url = getApplicationContext().getSharedPreferences("general", MODE_PRIVATE)
            .getString("host", DataCenter.getUrl());
        DataCenter.setUrl(url);
    }

    @Override
    protected void onStart() {
        super.onStart();

        // test connection
        new TestConnectionTask().execute();
    }

    @SuppressWarnings("unused")
    public void onTouchScreenClick(View v) {
        startBrowsingFor(EntityType.TARGET_TYPE);
    }

    @SuppressWarnings("unused")
    public void onTargetsButtonClick(View v) {
        startBrowsingFor(EntityType.TARGET);
    }

    @SuppressWarnings("unused")
    public void onMissionsButtonClick(View v) {
        startBrowsingFor(EntityType.MISSION);
    }

    @SuppressWarnings("unused")
    public void onInstrumentsButtonClick(View v) {
        startBrowsingFor(EntityType.INSTRUMENT);
    }

    private void startBrowsingFor(EntityType entityType) {
        Intent intent = new Intent(this, PageViewActivity.class);
        intent.putExtra(PageViewActivity.EXTRA_ENTITY_TYPE, entityType);
        startActivity(intent);
    }

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
                    .setNegativeButton("Cancel", null).setPositiveButton("OK",
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
