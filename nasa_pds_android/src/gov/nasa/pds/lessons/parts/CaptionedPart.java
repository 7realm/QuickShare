package gov.nasa.pds.lessons.parts;

import gov.nasa.pds.lessons.LessonPart;

public abstract class CaptionedPart  implements LessonPart  {
    protected String caption;

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

}