/*
 * Copyright (C) 2012 TopCoder Inc., All Rights Reserved.
 */
package gov.nasa.pds.lessons.parts;

import gov.nasa.pds.android.R;
import gov.nasa.pds.data.ImageCenter;
import gov.nasa.pds.lessons.LessonPart;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import org.ksoap2.transport.Streams;

import android.util.Log;

public class ImagePart implements LessonPart {
    private String caption;
    private long imageId;

    public ImagePart() {
        // empty default constructor
    }

    public ImagePart(long imageId, String caption) {
        this.imageId = imageId;
        this.caption = caption;
    }

    @Override
    public String getPrimaryText() {
        return "Image";
    }

    @Override
    public String getSecondaryText() {
        return caption;
    }

    @Override
    public int getIconId() {
        return R.drawable.object_file;
    }

    @Override
    public void render(File filesDir, StringBuilder page) {
        // add image tag to page with caption below
        page.append("<div class=\"imageBlock\">")
            .append("<img class=\"image\" src=\"files/").append(imageId).append("\" alt=\"").append("Image is not loaded").append("\">")
            .append("<br>").append("<div class=\"imageCaption\">").append(caption).append("</div>")
            .append("</div>");

        // copy image file to render location
        File imageFile = ImageCenter.getImage(imageId);
        if (imageFile != null) {
            try {
                // create rendered image file
                File renderedImageFile = new File(filesDir, Long.toString(imageId));
                renderedImageFile.createNewFile();
                Streams.copy(new FileInputStream(imageFile), new FileOutputStream(renderedImageFile), true);
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
