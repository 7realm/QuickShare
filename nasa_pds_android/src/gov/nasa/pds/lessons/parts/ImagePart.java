/*
 * Copyright (C) 2012 TopCoder Inc., All Rights Reserved.
 */
package gov.nasa.pds.lessons.parts;

import gov.nasa.pds.data.ImageCenter;
import gov.nasa.pds.lessons.LessonPart;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import soap.Streams;
import android.util.Log;

public class ImagePart extends LessonPart {
    private String caption;
    private long imageId;

    @Override
    public void render(File filesDir, StringBuilder page) {
        // add image tag to page with caption below
        page.append("<img src=\"files/").append(imageId).append("\" alt=\"").append("Image is not loaded").append("\">");
        page.append("<br><i>").append(caption).append("</i><br>");

        // copy image file to render location
        File imageFile = ImageCenter.getImage(imageId);
        if (imageFile != null) {
            try {
                Streams.copy(new FileInputStream(imageFile),
                    new FileOutputStream(new File(filesDir, Long.toString(imageId))), true);
            } catch (IOException e) {
                Log.e("soap", "Failed to copy image " + imageId + " to render directory.", e);
            }
        }
    }

    @Override
    public void save(DataOutputStream out) throws IOException {
        out.writeUTF(caption);
        out.writeLong(imageId);
    }

    @Override
    public void load(DataInputStream in) throws IOException {
        caption = in.readUTF();
        imageId = in.readLong();
    }

}
