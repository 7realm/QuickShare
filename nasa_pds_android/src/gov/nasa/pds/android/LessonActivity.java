/*
 * Copyright (C) 2012 TopCoder Inc., All Rights Reserved.
 */
package gov.nasa.pds.android;

import gov.nasa.pds.lessons.Lesson;
import gov.nasa.pds.lessons.LessonPart;
import gov.nasa.pds.lessons.LessonRepository;

import java.io.File;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.lib.ReordableListView;
import com.lib.ReordableListView.DragAndDropListner;
import com.lib.ReordableListView.ReordableAdapter;
import com.lib.ZipUtility;
import com.markupartist.android.widget.ActionBar.AbstractAction;
import com.markupartist.android.widget.ActionBar.TitleChangeListener;
import com.markupartist.android.widget.ActionBar.TitleType;
import com.markupartist.android.widget.ActionBarActivity;

/**
 * Activity that will display lesson.
 *
 * @author TCSASSEMBLER
 * @version 1.0
 */
public class LessonActivity extends ActionBarActivity {
    public static final String EXTRA_LESSON_ID = "intent.extra.lesson.id";
    private Lesson lesson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lesson);

        // get lesson from intent
        int lessonId = getIntent().getIntExtra(EXTRA_LESSON_ID, -1);
        lesson = lessonId == -1 ? LessonRepository.addLesson("New Lesson") : LessonRepository.getLesson(lessonId);

        // set adapter
        ReordableListView listView = (ReordableListView) findViewById(R.id.lessonPartsList);
        listView.setAdapter(new LessonPartAdapter());

        // set listener for drag and drop events
        listView.setDragAndDropListner(new LessonPartAdapter());

        // get action bar
        getActionBar().setTitleType(TitleType.EDIT);
        getActionBar().setTitle(lesson.getName(), lessonId == -1);
        getActionBar().setTitleChangeListener(new TitleChangeListener() {
            @Override
            public void onTitleChanged(CharSequence newTitle, int newTitlePosition) {
                if (!lesson.getName().equals(newTitle)) {
                    // update title
                    lesson.setName(newTitle.toString());
                    LessonRepository.save();
                }

                // hide keyboard for action bar edit field
                hideKeyboard(R.id.actionbarTitleEdit);
                findViewById(R.id.actionbarLayout).requestFocus();
            }
        });

        // set up action
        getActionBar().setUpAction(new AbstractAction(R.drawable.level_up, "Lessons") {
            @Override
            public void performAction(View view) {
                finish();
            }
        });

        // add preview action
        getActionBar().addAction(new AbstractAction(R.drawable.search_text, "Preview") {
            @Override
            public void performAction(View view) {
                new RenderTast().execute(PostRenderAction.PREVIEW);
            }
        });

        // add share action
        getActionBar().addAction(new AbstractAction(R.drawable.lesson_share, "Share") {
            @Override
            public void performAction(View view) {
                new RenderTast().execute(PostRenderAction.SHARE);
            }
        });

        // add remove action
        getActionBar().addAction(new AbstractAction(R.drawable.lesson_remove, "Remove") {
            @Override
            public void performAction(View view) {
                // display dialog to confirm remove
                new AlertDialog.Builder(LessonActivity.this)
                    .setTitle("Remove lesson '" + lesson.getName() + "'?")
                    .setPositiveButton("Yes", new OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            LessonRepository.removeLesson(lesson.getId());
                            finish();
                        }
                    })
                    .setNegativeButton("No", null)
                    .create().show();
            }
        });

        // hide or show lesson help
        findViewById(R.id.lessonHelpText).setVisibility(lesson.getParts().size() == 0 ? View.VISIBLE : View.GONE);
    }

    /**
     * Task that will render the lesson to file.
     *
     * @author TCSASSEMBLER
     */
    private final class RenderTast extends AsyncTask<PostRenderAction, Void, PostRenderAction> {
        private ProgressDialog dialog;

        private File getCompressedFile() {
            return new File(getExternalCacheDir(), "lesson.zip");
        }

        @Override
        protected PostRenderAction doInBackground(PostRenderAction... params) {
            // render lesson
            lesson.render(LessonActivity.this);

            // archive render directory if needed
            if (params[0] == PostRenderAction.SHARE) {
                File compressedLesson = getCompressedFile();
                ZipUtility.zipFolder(getExternalCacheDir(), compressedLesson);
            }

            return params[0];
        }

        @Override
        protected void onPreExecute() {
            dialog = ProgressDialog.show(LessonActivity.this, null, "Rendering lesson...", true, false);
        }

        @Override
        protected void onPostExecute(PostRenderAction result) {
            dialog.dismiss();

            switch (result) {
            case PREVIEW:
                // execute preview intent
                Uri uri = Uri.fromFile(new File(getExternalCacheDir(), "index.html"));
                Intent intent = new Intent(LessonActivity.this, WebViewActivity.class);
                intent.putExtra(WebViewActivity.EXTRA_WEB_URL, uri.toString());
                startActivity(intent);
                break;
            case SHARE:
                // execute share intent
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("application/zip");
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Lesson '" + lesson.getName() + "'");
                shareIntent.putExtra(Intent.EXTRA_TEXT, "Please unzip and open index.html file in your browser.");
                shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(getCompressedFile()));
                startActivity(Intent.createChooser(shareIntent, "Share lesson"));
                break;
            }
        }
    }

    /**
     * Adapter that will load data to lesson part list.
     *
     * @author TCSASSEMBLER
     * @version 1.0
     */
    private final class LessonPartAdapter extends ReordableAdapter<LessonPart> implements DragAndDropListner {
        private final View editBasket;
        private final View removeBasket;

        public LessonPartAdapter() {
            super(android.R.layout.simple_list_item_2, android.R.id.text1, lesson.getParts());

            // store basket views
            editBasket = findViewById(R.id.lessonPartEdit);
            removeBasket = findViewById(R.id.lessonPartRemove);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = View.inflate(LessonActivity.this, R.layout.item_lesson_part, null);
            }

            LessonPart lessonPart = getItem(position);

            // set primary text
            TextView primaryText = (TextView) convertView.findViewById(android.R.id.text1);
            primaryText.setText(lessonPart.getPrimaryText());

            // secondary text view
            TextView secondaryText = (TextView) convertView.findViewById(android.R.id.text2);
            secondaryText.setText(lessonPart.getSecondaryText());

            // set icon
            ImageView imageView = (ImageView) convertView.findViewById(android.R.id.icon);
            imageView.setImageResource(lessonPart.getIconId());

            return convertView;
        }

        @Override
        public boolean checkDropView(int x, int y, int index) {
            // check if we dropped view to edit basket
            if (isPointInsideView(x, y, editBasket)) {
                // start edit activity
                Intent intent = new Intent(LessonActivity.this, PartActivity.class);
                intent.putExtra(PartActivity.EXTRA_LESSON_ID, lesson.getId());
                intent.putExtra(PartActivity.EXTRA_PART_ID, index);
                startActivity(intent);
                return true;
            }

            // check if we dropped view to remove basket
            if (isPointInsideView(x, y, removeBasket)) {
                Toast.makeText(LessonActivity.this, "Lesson part is removed.", Toast.LENGTH_SHORT).show();
                content.remove(index);
                return true;
            }

            return false;
        }

        @Override
        public void onStartDrag(int position, View v) {
            findViewById(R.id.lessonPartMenu).setVisibility(View.VISIBLE);
        }

        @Override
        public void onEndDrag(int position, View v) {
            findViewById(R.id.lessonPartMenu).setVisibility(View.GONE);
        }
        @Override
        public void onDrag(int x, int y, int position, View v) {
            editBasket.setSelected(isPointInsideView(x, y, editBasket));
            removeBasket.setSelected(isPointInsideView(x, y, removeBasket));
        }
    }

    /**
     * Action that will be done after rendering of the lesson.
     *
     * @author TCSASSEMBLER
     * @version 1.0
     */
    private static enum PostRenderAction {
        /** Lesson will be previewed. */
        PREVIEW,
        /** Lesson will be shared. */
        SHARE
    }
}
