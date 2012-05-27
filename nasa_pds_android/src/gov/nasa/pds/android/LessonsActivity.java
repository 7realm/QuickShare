/*
 * Copyright (C) 2012 TopCoder Inc., All Rights Reserved.
 */
package gov.nasa.pds.android;

import gov.nasa.pds.data.DataCenter;
import gov.nasa.pds.data.QueryType;
import gov.nasa.pds.data.queries.ObjectQuery;
import gov.nasa.pds.lessons.Lesson;
import gov.nasa.pds.lessons.parts.ImagePart;
import gov.nasa.pds.lessons.parts.InstrumentPart;
import gov.nasa.pds.lessons.parts.MissionPart;
import gov.nasa.pds.soap.entities.Instrument;
import gov.nasa.pds.soap.entities.Mission;

import java.io.File;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.markupartist.android.widget.ActionBar;
import com.markupartist.android.widget.ActionBar.AbstractAction;

public class LessonsActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lessons);

        // set action bar
        ActionBar actionBar = (ActionBar) findViewById(R.id.actionbar);
        actionBar.setTitle("Lessons");
        actionBar.addAction(new AbstractAction(R.drawable.compare_add, "Add") {
            @Override
            public void performAction(View view) {
                Toast.makeText(getApplicationContext(), "Add", Toast.LENGTH_SHORT).show();
            }
        });

        actionBar.addAction(new AbstractAction(R.drawable.remove, "Remove") {
            @Override
            public void performAction(View view) {
                Toast.makeText(getApplicationContext(), "Remove", Toast.LENGTH_SHORT).show();
            }
        });
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                Mission mission = DataCenter.executeObjectQuery(new ObjectQuery<Mission>(QueryType.GET_MISSION, 3));
                Instrument instrument = DataCenter.executeObjectQuery(new ObjectQuery<Instrument>(QueryType.GET_INSTRUMENT, 5));

                Lesson lesson = new Lesson();
                lesson.setName("First lesson");
                lesson.getParts().add(new MissionPart(mission));
                lesson.getParts().add(new InstrumentPart(instrument));
                lesson.getParts().add(new ImagePart(2642, "Tempel coordinates"));
                lesson.getParts().add(new ImagePart(2331, "Example preview"));

                lesson.render(LessonsActivity.this);

                return null;
            };

            @Override
            protected void onPostExecute(Void result) {
                Uri uri = Uri.fromFile(new File(getExternalCacheDir(), "index.html"));
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                intent.setClassName("com.android.browser", "com.android.browser.BrowserActivity");
//                startActivity(intent);
            }
        }.execute();

    }
}
