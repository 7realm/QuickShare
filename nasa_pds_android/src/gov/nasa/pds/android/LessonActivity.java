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
import com.markupartist.android.widget.ActionBar.AbstractAction;
import com.markupartist.android.widget.ActionBar.TitleChangeListener;
import com.markupartist.android.widget.ActionBar.TitleType;
import com.markupartist.android.widget.ActionBarActivity;

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
        listView.setRemoveViewId(R.id.lessonPartRemove);
        listView.setAdapter(new LessonPartAdapter());

        // set listener for drag and drop events
        listView.setDragAndDropListner(new DragAndDropListner() {
            @Override
            public void onStartDrag(int position, View v) {
                findViewById(R.id.lessonPartRemove).setVisibility(View.VISIBLE);
            }

            @Override
            public void onEndDrag(int position, View v) {
                findViewById(R.id.lessonPartRemove).setVisibility(View.GONE);
            }
        });

        // get action bar
        getActionBar().setTitleType(TitleType.EDIT);
        getActionBar().setTitle(lesson.getName());
        getActionBar().setTitleChangeListener(new TitleChangeListener() {
            @Override
            public void onTitleChanged(CharSequence newTitle, int newTitlePosition) {
                if (!lesson.getName().equals(newTitle)) {
                    // update title
                    lesson.setName(newTitle.toString());
                    LessonRepository.save();
                }
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
                new RenderTast().execute();
            }
        });

        // add share action
        getActionBar().addAction(new AbstractAction(R.drawable.lesson_share, "Share") {
            @Override
            public void performAction(View view) {
                Toast.makeText(getApplicationContext(), "Share", Toast.LENGTH_SHORT).show();
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
    }

    /**
     * Task that will render the lesson to file.
     *
     * @author TCSASSEMBLER
     */
    private final class RenderTast extends AsyncTask<Void, Void, Void> {
        private ProgressDialog dialog;

        @Override
        protected Void doInBackground(Void... params) {
            // render lesson
            lesson.render(LessonActivity.this);
            return null;
        }

        @Override
        protected void onPreExecute() {
            dialog = ProgressDialog.show(LessonActivity.this, null, "Rendering lesson...", true, false);
        }

        @Override
        protected void onPostExecute(Void result) {
            dialog.dismiss();

            // execute preview intent
            Uri uri = Uri.fromFile(new File(getExternalCacheDir(), "index.html"));
            Intent intent = new Intent(LessonActivity.this, WebViewActivity.class);
            intent.putExtra(WebViewActivity.EXTRA_WEB_URL, uri.toString());
            startActivity(intent);
        }
    }

    private final class LessonPartAdapter extends ReordableAdapter<LessonPart> {
        public LessonPartAdapter() {
            super(android.R.layout.simple_list_item_2, android.R.id.text1, lesson.getParts());
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
    }
}
