package gov.nasa.pds.lessons;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;

public abstract class LessonPart {
    public abstract void render(File filesDir, StringBuilder page);

    public abstract void save(DataOutputStream out) throws IOException;

    public abstract void load(DataInputStream in) throws IOException;
}
