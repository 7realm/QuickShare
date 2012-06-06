/*
 * Copyright (C) 2012 TopCoder Inc., All Rights Reserved.
 */
package gov.nasa.pds.lessons;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.util.Log;

import com.lib.Streams;

/**
 * Represents lesson.
 *
 * @author TCSASSEMBLER
 * @version 1.0
 */
public class Lesson {
    /** Lesson name. */
    private String name;
    /** Lesson id.  */
    private int id;
    /** List of lesson parts. */
    private final List<LessonPart> parts = new ArrayList<LessonPart>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<LessonPart> getParts() {
        return parts;
    }

    /**
     * Saves lesson.
     *
     * @param out the output stream
     * @throws IOException if error occurs
     */
    public void save(DataOutputStream out) throws IOException {
        // write general lesson information
        out.writeUTF(name);
        out.writeInt(parts.size());

        // write each lesson part
        for (LessonPart part : parts) {
            // write class of the part
            out.writeUTF(part.getClass().getName());

            // write part
            part.save(out);
        }
    }

    /**
     * Loads lesson.
     *
     * @param in input stream
     * @throws IOException if error occurs
     */
    public void load(DataInputStream in) throws IOException {
        try {
            // read general lesson information
            name = in.readUTF();
            int size = in.readInt();

            // read lesson parts
            for (int i = 0; i < size; i++) {
                // create lesson based on class name
                String partClass = in.readUTF();
                LessonPart part = (LessonPart) Class.forName(partClass).newInstance();

                // load part from stream
                part.load(in);
                parts.add(part);
            }
        } catch (IOException e) {
            throw e;
        } catch (Exception e) {
            throw new IOException("Failed to create lesson part.", e);
        }
    }

    /**
     * Renders lesson to external cache dir.
     *
     * @param context the context
     */
    public void render(Context context) {
        // create files dir
        File renderDir = context.getExternalCacheDir();
        File filesDir = new File(renderDir, "files");
        filesDir.mkdirs();

        // copy styles from assets
        try {
            Streams.copy(context.getAssets().open("android.css"), new FileOutputStream(new File(filesDir, "android.css")), true);
            Streams.copy(context.getAssets().open("desktop.css"), new FileOutputStream(new File(filesDir, "desktop.css")), true);
        } catch (IOException e) {
            Log.e("soap", "Failed to copy style files from assets to external storage.", e);
        }

        // create header with title
        StringBuilder page = new StringBuilder();
        page.append("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01//EN\" \"http://www.w3.org/TR/html4/strict.dtd\">");
        page.append("<html><head><title>").append(name).append("</title>");
        page.append("<meta name=\"viewport\" content=\"user-scalable=no, width=device-width\" />");

        // append styles
        page.append("<link rel=\"stylesheet\" type=\"text/css\" href=\"files/android.css\" media=\"only screen and (max-width: 480px)\" />");
        page.append("<link rel=\"stylesheet\" type=\"text/css\" href=\"files/desktop.css\" media=\"screen and (min-width: 481px)\" />");

        // append head
        page.append("</head><body>").append("<h1>").append(name).append("</h1><hr>");

        // append each lesson part
        for (LessonPart part : parts) {
            part.render(filesDir, page);
            page.append("<hr>");
        }

        // append end tags
        page.append("</body>");

        try {
            Streams.writeToStream(new FileOutputStream(new File(renderDir, "index.html")), page.toString());
        } catch (IOException e) {
            Log.e("soap", "Failed to save index file to external storage.", e);
        }
    }
}
