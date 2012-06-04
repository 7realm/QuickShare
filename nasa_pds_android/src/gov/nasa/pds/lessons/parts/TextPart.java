/*
 * Copyright (C) 2012 TopCoder Inc., All Rights Reserved.
 */
package gov.nasa.pds.lessons.parts;

import gov.nasa.pds.data.DataCenter;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public abstract class TextPart extends CaptionedPart {
    private final Map<String, String> links = new HashMap<String, String>();
    private String text;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void processText(String text) {
        this.text = DataCenter.processDescription(text).trim()
            .replaceAll("(\\r\\n){2,}", "<br><br>\n")
            .replaceAll("(?m)^(.*?)([=|_|-]{2,})(.*?)$", "<br>$1$2$3<br>");
    }

    public void addLink(String description) {
        description = description.replaceAll("\\s+", " ").trim();
        links.put("http://www.google.com/#q=" + description, description);
    }

    @SuppressWarnings("unused")
    protected void renderCaption(File filesDir, StringBuilder page) {
        page.append("<h2>").append(caption).append("</h2>");
    }

    @SuppressWarnings("unused")
    protected void renderText(File filesDir, StringBuilder page) {
        page.append("<div class=\"description\">").append(text).append("</div>");
    }

    @SuppressWarnings("unused")
    protected void renderLinks(File filesDir, StringBuilder page) {
        page.append("<ul class=\"links\">");
        for (Entry<String, String> entry : links.entrySet()) {
            page.append("<li><a href=\"").append(entry.getKey()).append("\">").append(entry.getValue()).append("</a>");
        }
        page.append("</ul>");
    }

    @Override
    public void render(File filesDir, StringBuilder page) {
        // render all parts
        renderCaption(filesDir, page);
        renderText(filesDir, page);
        renderLinks(filesDir, page);
    }

    @Override
    public void save(DataOutputStream out) throws IOException {
        out.writeUTF(caption);
        out.writeUTF(text);

        // write all links
        out.writeInt(links.size());
        for (Entry<String, String> entry : links.entrySet()) {
            out.writeUTF(entry.getKey());
            out.writeUTF(entry.getValue());
        }
    }

    @Override
    public void load(DataInputStream in) throws IOException {
        caption = in.readUTF();
        text = in.readUTF();

        // read all links
        int size = in.readInt();
        for (int i = 0; i < size; i++) {
            links.put(in.readUTF(), in.readUTF());
        }
    }
}
