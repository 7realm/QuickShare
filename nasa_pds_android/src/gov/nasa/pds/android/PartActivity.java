/*
 * Copyright (C) 2012 TopCoder Inc., All Rights Reserved.
 */
package gov.nasa.pds.android;

import gov.nasa.pds.data.ImageCenter;
import gov.nasa.pds.lessons.Lesson;
import gov.nasa.pds.lessons.LessonRepository;
import gov.nasa.pds.lessons.parts.CaptionedPart;
import gov.nasa.pds.lessons.parts.ImagePart;
import gov.nasa.pds.lessons.parts.TextPart;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.markupartist.android.widget.ActionBar.AbstractAction;
import com.markupartist.android.widget.ActionBarActivity;

/**
 * Activity that is used to edit lesson part.
 *
 * @author TCSASSEMBLER
 * @version 1.0
 */
public class PartActivity extends ActionBarActivity {
    /** Lesson ID intent extra. */
    public static final String EXTRA_LESSON_ID = "LESSON_ID";
    /** Part ID intent extra. */
    public static final String EXTRA_PART_ID = "PART_ID";

    private Lesson lesson;
    private CaptionedPart lessonPart;
    private EditText captionEdit;
    private EditText descriptionEdit;

    /** Change history per view for undo. */
    private final Map<View, Stack<CharSequence>> changeHistory = new HashMap<View, Stack<CharSequence>>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_part);

        // find edit views
        captionEdit = (EditText) findViewById(R.id.partCaptionEdit);
        descriptionEdit = (EditText) findViewById(R.id.partDescriptionEdit);

        // set undo text change listener
        captionEdit.addTextChangedListener(new UndoTextWatcher(captionEdit));
        descriptionEdit.addTextChangedListener(new UndoTextWatcher(descriptionEdit));

        // get lesson part from intent
        int lessonId = getIntent().getIntExtra(EXTRA_LESSON_ID, 0);
        int partId = getIntent().getIntExtra(EXTRA_PART_ID, 0);
        lesson = LessonRepository.getLesson(lessonId);
        lessonPart = (CaptionedPart) lesson.getParts().get(partId);

        // set text fields
        updateTextForce();

        // set content view based on lesson type
        if (lessonPart instanceof ImagePart) {
            ImagePart imagePart = (ImagePart) lessonPart;

            // load image
            ImageView imageView = (ImageView) findViewById(R.id.partImage);
            imageView.setImageURI(Uri.fromFile(ImageCenter.getImage(imagePart.getImageId())));

            // hide description view
            findViewById(R.id.partDescriptionGroup).setVisibility(View.GONE);
        } else if (lessonPart instanceof TextPart) {
            // hide image view
            findViewById(R.id.partImage).setVisibility(View.GONE);
        } else {
            Log.e("soap", "Unknown lesson part: " + lessonPart);
        }

        // set action bar
        getActionBar().setTitle(lessonPart.getPrimaryText() + ": " + lessonPart.getSecondaryText());
        getActionBar().setUpAction(new AbstractAction(R.drawable.level_left, "Lesson") {
            @Override
            public void performAction(View view) {
                onBackPressed();
            }
        });

        // set save button
        getActionBar().addAction(new AbstractAction(R.drawable.save, "Save") {
            @Override
            public void performAction(View view) {
                saveLessonPart();
            }
        });

        // set undo button
        getActionBar().addAction(new AbstractAction(R.drawable.undo, "Undo") {
            @Override
            public void performAction(View view) {
                View focusedView = getCurrentFocus();
                if (focusedView instanceof EditText) {
                    EditText editText = (EditText) focusedView;

                    Stack<CharSequence> changes = changeHistory.get(focusedView);
                    if (changes.size() > 1) {
                        // remove current change
                        changes.pop();

                        // set last text
                        editText.setText(changes.peek());
                    }
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        for (Stack<CharSequence> changes : changeHistory.values()) {
            if (changes.size() > 1) {
                // show dialog if changes are not saved
                new AlertDialog.Builder(this).setMessage("Lesson part has unsaved changes!")
                    .setPositiveButton("Save", new OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            saveLessonPart();
                            finish();
                        }
                    })
                    .setNeutralButton("Discard", new OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    })
                    .setNegativeButton("Cancel", null).create().show();
                return;
            }
        }

        finish();
    }

    private void updateTextForce() {
        // set caption
        setTextForce(captionEdit, lessonPart.getCaption());

        if (lessonPart instanceof TextPart) {
            TextPart textPart = (TextPart) lessonPart;

            // set description
            setTextForce(descriptionEdit, textPart.getText());
        }
    }

    private void setTextForce(EditText editText, String text) {
        editText.setText(text);

        // update stack
        Stack<CharSequence> changes = changeHistory.get(editText);
        changes.clear();
        changes.push(text);
    }

    private void saveLessonPart() {
        // update lesson part
        lessonPart.setCaption(captionEdit.getText().toString());
        if (lessonPart instanceof TextPart) {
            TextPart textPart = (TextPart) lessonPart;
            textPart.setText(descriptionEdit.getText().toString());
        }

        // save changes
        LessonRepository.save();
        updateTextForce();

        // notify about save
        Toast.makeText(this, "Changes to lesson '" + lesson.getName() + "' are saved.", Toast.LENGTH_SHORT).show();
    }

    private final class UndoTextWatcher implements TextWatcher {
        private final Stack<CharSequence> changes = new Stack<CharSequence>();

        public UndoTextWatcher(View view) {
            changeHistory.put(view, changes);
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            // check if text really have changed
            if (!changes.isEmpty() && changes.peek().toString().equals(s.toString())) {
                return;
            }

            // add new text to stack
            changes.push(new String(s.toString()));
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            // do nothing
        }

        @Override
        public void afterTextChanged(Editable s) {
            // do nothing
        }
    }

}
