package gov.nasa.pds.lessons.parts;

import gov.nasa.pds.data.DataCenter;
import gov.nasa.pds.soap.entities.Mission;
import gov.nasa.pds.soap.entities.Reference;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;

public class MissionPart extends TextPart {
    private String duration;

    public MissionPart() {
        // empty default
    }

    public MissionPart(Mission mission) {
        setCaption("Mission: " + mission.getName());
        setText(mission.getDescription());

        // add references with search links
        for (Reference reference : mission.getReferences()) {
            addLink("http://www.google.com/#q=" + reference.getDescription(), reference.getDescription());
        }

        // store duration
        duration = DataCenter.formatPeriod(mission.getStartDate(), mission.getEndDate());
    }

    @Override
    protected void renderText(File filesDir, StringBuilder page) {
        // append duration before text
        page.append("<h3>").append("Duration: ").append(duration).append("</h3><br>");

        super.renderText(filesDir, page);
    }

    @Override
    public void save(DataOutputStream out) throws IOException {
        super.save(out);
        out.writeUTF(duration);
    }

    @Override
    public void load(DataInputStream in) throws IOException {
        super.load(in);
        duration = in.readUTF();
    }
}
