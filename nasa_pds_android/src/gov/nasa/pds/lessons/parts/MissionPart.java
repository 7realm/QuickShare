/*
 * Copyright (C) 2012 TopCoder Inc., All Rights Reserved.
 */
package gov.nasa.pds.lessons.parts;

import gov.nasa.pds.android.R;
import gov.nasa.pds.data.DataCenter;
import gov.nasa.pds.soap.entities.Mission;
import gov.nasa.pds.soap.entities.Reference;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;

/**
 * Lesson part that represents {@link Mission}.
 *
 * @author TCSASSEMBLER
 * @version 1.0
 */
public class MissionPart extends TextPart {
    private String duration;
    private String name;

    public MissionPart() {
        // empty default
    }

    @Override
    public String getPrimaryText() {
        return "Mission";
    }

    @Override
    public String getSecondaryText() {
        return name;
    }

    @Override
    public int getIconId() {
        return R.drawable.object_mission;
    }

    public MissionPart(Mission mission) {
        setCaption("Mission: " + mission.getName());
        setText(mission.getDescription());

        // add references with search links
        for (Reference reference : mission.getReferences()) {
            addLink(reference.getDescription());
        }

        // store duration and name
        duration = DataCenter.formatPeriod(mission.getStartDate(), mission.getEndDate());
        name = mission.getName();
    }

    @Override
    protected void renderText(File filesDir, StringBuilder page) {
        // append duration before text
        page.append("<h3>").append("Duration: ").append(duration).append("</h3>");

        super.renderText(filesDir, page);
    }

    @Override
    public void save(DataOutputStream out) throws IOException {
        super.save(out);
        out.writeUTF(name);
        out.writeUTF(duration);
    }

    @Override
    public void load(DataInputStream in) throws IOException {
        super.load(in);
        name = in.readUTF();
        duration = in.readUTF();
    }
}
