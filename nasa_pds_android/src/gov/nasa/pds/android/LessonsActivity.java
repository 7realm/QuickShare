/*
 * Copyright (C) 2012 TopCoder Inc., All Rights Reserved.
 */
package gov.nasa.pds.android;

import gov.nasa.pds.data.EntityType;
import gov.nasa.pds.lessons.Lesson;
import gov.nasa.pds.lessons.LessonRepository;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.markupartist.android.widget.ActionBar.AbstractAction;
import com.markupartist.android.widget.ActionBarActivity;

public class LessonsActivity extends ActionBarActivity {
    private BaseAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lessons);

        // set action bar
        getActionBar().setTitle("Lessons");
        getActionBar().setUpAction(new AbstractAction(R.drawable.logo, null) {

            @Override
            public void performAction(View view) {
                // do nothing
            }
        });

        // add new lesson action
        getActionBar().addAction(new AbstractAction(R.drawable.lesson_add, "Add") {
            @Override
            public void performAction(View view) {
                startActivity(new Intent(LessonsActivity.this, LessonActivity.class));
            }
        });

        // browse action
        getActionBar().addAction(new AbstractAction(R.drawable.level_down, "Browse") {
            @Override
            public void performAction(View view) {
                Intent intent = new Intent(LessonsActivity.this, PageViewActivity.class);
                intent.putExtra(PageViewActivity.EXTRA_ENTITY_TYPE, EntityType.TARGET_TYPE);
                startActivity(intent);
            }
        });

        // set empty adapter
        adapter = new BaseAdapter() {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                if (convertView == null) {
                    convertView = LayoutInflater.from(LessonsActivity.this).inflate(R.layout.item_lesson, null);
                }

                // update lesson view
                final Lesson lesson = LessonRepository.getLessons().get(position);
                TextView captionView = (TextView) convertView.findViewById(android.R.id.text1);
                captionView.setText(lesson.getName());
                captionView.setTag(lesson.getId());
                convertView.setTag(lesson.getId());
                convertView.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(LessonsActivity.this, LessonActivity.class);
                        intent.putExtra(LessonActivity.EXTRA_LESSON_ID, (Integer) v.getTag());
                        startActivity(intent);
                    }
                });
                return convertView;
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public String getItem(int position) {
                Lesson lesson = LessonRepository.getLessons().get(position);
                return lesson == null ? "" : lesson.getName();
            }

            @Override
            public int getCount() {
                return LessonRepository.size();
            }
        };
        ListView listView = (ListView) findViewById(R.id.lessonsList);
        listView.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // refresh adapter
        adapter.notifyDataSetChanged();
    }
}
