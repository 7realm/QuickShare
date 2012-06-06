/*
 * Copyright (C) 2012 TopCoder Inc., All Rights Reserved.
 */
package gov.nasa.pds.lessons;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;

/**
 * Base lesson part.
 *
 * @author TCSASSEMBLER
 * @version 1.0
 */
public interface LessonPart {
    /**
     * Primary text.
     *
     * @return the primary descriptive text
     */
    String getPrimaryText();

    /**
     * The secondary text.
     *
     * @return the secondary text
     */
    String getSecondaryText();

    /**
     * The part icon id.
     *
     * @return the part icon id.
     */
    int getIconId();

    /**
     * Render part.
     *
     * @param filesDir the files directory, where resources can be stored.
     * @param page the rendered html page
     */
    void render(File filesDir, StringBuilder page);

    /**
     * Save lesson part.
     *
     * @param out the output
     * @throws IOException if error occurs
     */
    void save(DataOutputStream out) throws IOException;

    /**
     * Load lesson part.
     *
     * @param in the input stream
     * @throws IOException if error occurs
     */
    void load(DataInputStream in) throws IOException;
}
