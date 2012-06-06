/*
 * Copyright (C) 2012 TopCoder Inc., All Rights Reserved.
 */
package gov.nasa.pds.lessons.parts;

import gov.nasa.pds.lessons.LessonPart;

/**
 * Lesson part with caption.
 *
 * @author TCSASSEMBLER
 * @version 1.0
 */
public abstract class CaptionedPart implements LessonPart {
    protected String caption;

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

}