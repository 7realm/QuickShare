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

import soap.Streams;
import android.util.Log;

public class Lesson {
    private String name;
    private final List<LessonPart> lessonsParts = new ArrayList<LessonPart>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<LessonPart> getParts() {
        return lessonsParts;
    }

    public void save(DataOutputStream out) throws IOException {
        // write general lesson information
        out.writeUTF(name);
        out.writeInt(lessonsParts.size());

        // write each lesson part
        for (LessonPart part : lessonsParts) {
            // write class of the part
            out.writeUTF(part.getClass().getName());

            // write part
            part.save(out);
        }
    }

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
            }
        } catch (IOException e) {
            throw e;
        } catch (Exception e) {
            throw new IOException("Failed to create lesson part.", e);
        }
    }

    public void render(File renderDir) {
        File filesDir = new File(renderDir, "files");

        StringBuilder page = new StringBuilder();

        // append lesson title
        page.append("<h1>").append(name).append("</h1>");

        // append each lesson part
        for (LessonPart part : lessonsParts) {
            part.render(filesDir, page);
            page.append("<br>");
        }

        try {
            Streams.writeToStream(new FileOutputStream(new File(renderDir, "index.html")), page.toString());
        } catch (IOException e) {
            Log.e("soap", "Failed to save index file to external storage.", e);
        }
    }
}
