/*
 * Copyright (C) 2012 TopCoder Inc., All Rights Reserved.
 */
package gov.nasa.pds.lessons;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;

public interface LessonPart {
    String getPrimaryText();

    String getSecondaryText();

    int getIconId();

    void render(File filesDir, StringBuilder page);

    void save(DataOutputStream out) throws IOException;

    void load(DataInputStream in) throws IOException;
}
