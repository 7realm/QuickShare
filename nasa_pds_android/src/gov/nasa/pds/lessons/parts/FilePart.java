package gov.nasa.pds.lessons.parts;

import gov.nasa.pds.soap.entities.WsDataFile;

public class FilePart extends TextPart {
    public FilePart() {
        // default empty constructor
    }

    public FilePart(WsDataFile dataFile) {
        setCaption(dataFile.getName());

        // set content from corresponding source
        setText(dataFile.getContent() != null ? dataFile.getContent() : new String(dataFile.getDataHandler().getContent()));
    }
}
