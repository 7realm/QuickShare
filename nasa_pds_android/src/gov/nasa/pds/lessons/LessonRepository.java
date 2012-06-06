/*
 * Copyright (C) 2012 TopCoder Inc., All Rights Reserved.
 */
package gov.nasa.pds.lessons;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.util.Log;

/**
 * The lesson repository. Manages lessons.
 *
 * @author TCSASSEMBLER
 * @version 1.0
 */
public class LessonRepository {
    private static final String LESSON_DATA_FILE = "lesson.data.file";
    private static final Map<Integer, Lesson> LESSONS = new HashMap<Integer, Lesson>();

    private static int lastLessonId = 0;
    private static Context context;

    /**
     * Load lessons.
     *
     * @param context the context
     */
    public static void load(Context context) {
        LessonRepository.context = context;
        DataInputStream in = null;
        try {
            // open data file
            FileInputStream fileInput = context.openFileInput(LESSON_DATA_FILE);
            in = new DataInputStream(fileInput);

            // read lessons
            int size = in.readInt();
            for (int i = 0; i < size; i++) {
                // read and add lesson
                Lesson lesson = new Lesson();
                lesson.load(in);
                lesson.setId(lastLessonId++);
                LESSONS.put(lesson.getId(), lesson);
            }
        } catch (FileNotFoundException e) {
            Log.i("soap", "Lessons file does not exist, creating.");
            save();
        } catch (IOException e) {
            Log.e("soap", "Failed to load lessons.", e);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    // ignore exception
                }
            }
        }
    }

    /**
     * Save lessons.
     */
    public static void save() {
        DataOutputStream out = null;
        try {
            // open data file
            FileOutputStream fileOutput = context.openFileOutput(LESSON_DATA_FILE, Context.MODE_PRIVATE);
            out = new DataOutputStream(fileOutput);

            // write lessons
            out.writeInt(LESSONS.size());
            for (Lesson lesson : LESSONS.values()) {
                lesson.save(out);
            }
        } catch (IOException e) {
            Log.e("soap", "Failed to save lessons.", e);
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    // ignore exception
                }
            }
        }
    }

    /**
     * Get all lessons.
     *
     * @return lessons
     */
    public static List<Lesson> getLessons() {
        return Collections.unmodifiableList(new ArrayList<Lesson>(LESSONS.values()));
    }

    /**
     * Count of lessons.
     *
     * @return the count of lessons
     */
    public static int size() {
        return LESSONS.size();
    }

    /**
     * Get lesson by id.
     *
     * @param id the lesson id
     * @return the lesson
     */
    public static Lesson getLesson(int id) {
        return LESSONS.get(id);
    }

    /**
     * Add lesson.
     *
     * @param name lesson name
     * @return created lesson
     */
    public static Lesson addLesson(String name) {
        // create and add new lesson
        Lesson lesson = new Lesson();
        lesson.setName(name);
        lesson.setId(lastLessonId++);
        LESSONS.put(lesson.getId(), lesson);

        // save lessons
        save();
        return lesson;
    }

    /**
     * Remove lesson by id.
     *
     * @param id the lesson id
     */
    public static void removeLesson(int id) {
        LESSONS.remove(id);
        save();
    }
}
