package gov.nasa.pds.android;

import gov.nasa.pds.lessons.Lesson;
import gov.nasa.pds.lessons.LessonRepository;

import java.io.File;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.markupartist.android.widget.ActionBar;
import com.markupartist.android.widget.ActionBar.AbstractAction;
import com.markupartist.android.widget.ActionBar.TitleChangeListener;

public class LessonActivity extends Activity {
    public static final String EXTRA_LESSON_ID = "intent.extra.lesson.id";
    private Lesson lesson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lesson);

        // get lesson from intent
        int lessonId = getIntent().getIntExtra(EXTRA_LESSON_ID, -1);
        lesson = lessonId == -1 ? LessonRepository.addLesson("New Lesson") : LessonRepository.getLesson(lessonId);

        // get action bar
        final ActionBar actionBar = (ActionBar) findViewById(R.id.actionbar);
        actionBar.setTitle(lesson.getName());
        actionBar.setTitleEditable(true);
        actionBar.setTitleChangeListener(new TitleChangeListener() {
            @Override
            public void onTitleChanged(CharSequence newTitle) {
                // update title
                lesson.setName(newTitle.toString());
                LessonRepository.save();

                actionBar.requestFocus();
            }
        });

        // add preview action
        actionBar.addAction(new AbstractAction(R.drawable.search_text, "Preview") {
            @Override
            public void performAction(View view) {
                new RenderTast().execute();
            }
        });

        // add share action
        actionBar.addAction(new AbstractAction(R.drawable.search_google, "Share") {
            @Override
            public void performAction(View view) {
                Toast.makeText(getApplicationContext(), "Share", Toast.LENGTH_SHORT).show();
            }
        });

        // add remove action
        actionBar.addAction(new AbstractAction(R.drawable.remove, "Remove") {
            @Override
            public void performAction(View view) {
                LessonRepository.removeLesson(lesson.getId());
                finish();
            }
        });
    }

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
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            intent.setClassName("com.android.browser", "com.android.browser.BrowserActivity");
            startActivity(intent);
        }

    }
}
