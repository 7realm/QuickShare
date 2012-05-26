package gov.nasa.pds.lessons.parts;

import gov.nasa.pds.lessons.LessonPart;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class TextPart extends LessonPart {
    private final Map<String, String> links = new HashMap<String, String>();
    private String caption;
    private String text;

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void addLink(String address, String description) {
        links.put(address, description);
    }

    @SuppressWarnings("unused")
    protected void renderCaption(File filesDir, StringBuilder page) {
        page.append("<h2>").append(caption).append("</h2>").append("<br>");
    }

    @SuppressWarnings("unused")
    protected void renderText(File filesDir, StringBuilder page) {
        page.append(text).append("<br>");
    }

    @SuppressWarnings("unused")
    protected void renderLinks(File filesDir, StringBuilder page) {
        for (Entry<String, String> entry : links.entrySet()) {
            page.append("<a href=\"").append(entry.getKey()).append("\">").append(entry.getValue()).append("</a><br>");
        }
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
