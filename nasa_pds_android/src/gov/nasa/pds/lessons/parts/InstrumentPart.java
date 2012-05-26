package gov.nasa.pds.lessons.parts;

import gov.nasa.pds.soap.entities.Instrument;
import gov.nasa.pds.soap.entities.Reference;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;

public class InstrumentPart extends TextPart {
    private String type;
    private String host;

    public InstrumentPart() {
        // empty default
    }

    public InstrumentPart(Instrument instrument) {
        setCaption("Instrument: " + instrument.getName());
        setText(instrument.getDescription());

        // add references with search links
        for (Reference reference : instrument.getReferences()) {
            addLink("http://www.google.com/#q=" + reference.getDescription(), reference.getDescription());
        }

        // store other values
        type = instrument.getType();
        host = instrument.getHosts().isEmpty() ? "" : instrument.getHosts().get(0).getName();
    }

    @Override
    protected void renderText(File filesDir, StringBuilder page) {
        // append host and type before text
        page.append("<h3>").append("Host of instrument: ").append(host).append("</h3><br>");
        page.append("<h3>").append("Instrument type: ").append(type).append("</h3><br>");

        super.renderText(filesDir, page);
    }

    @Override
    public void save(DataOutputStream out) throws IOException {
        super.save(out);
        out.writeUTF(type);
        out.writeUTF(host);
    }

    @Override
    public void load(DataInputStream in) throws IOException {
        super.load(in);
        type = in.readUTF();
        host = in.readUTF();
    }
}
