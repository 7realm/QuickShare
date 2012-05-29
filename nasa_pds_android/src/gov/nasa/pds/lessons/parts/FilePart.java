/*
 * Copyright (C) 2012 TopCoder Inc., All Rights Reserved.
 */
package gov.nasa.pds.lessons.parts;

import gov.nasa.pds.android.R;
import gov.nasa.pds.soap.entities.WsDataFile;

public class FilePart extends TextPart {
    public FilePart() {
        // default empty constructor
    }

    @Override
    public String getPrimaryText() {
        return "File";
    }

    @Override
    public String getSecondaryText() {
        return caption;
    }

    @Override
    public int getIconId() {
        return R.drawable.object_file;
    }

    public FilePart(WsDataFile dataFile) {
        setCaption("File: " + dataFile.getName());

        // set content from corresponding source
        setText(dataFile.getContent() != null ? dataFile.getContent() : new String(dataFile.getDataHandler().getContent()));
    }
}
