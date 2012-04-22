package gov.nasa.pds.entities.response;

import java.util.ArrayList;
import java.util.List;

import javax.xml.datatype.XMLGregorianCalendar;

public class SearchCriteria {

    protected long dataSetId;
    protected List<String> instrumentHosts;
    protected List<String> instruments;
    protected List<String> missions;
    protected XMLGregorianCalendar startDate;
    protected XMLGregorianCalendar stopDate;
    protected List<String> targetTypes;
    protected List<String> targets;

    public long getDataSetId() {
        return dataSetId;
    }

    public void setDataSetId(long value) {
        this.dataSetId = value;
    }

    public List<String> getInstrumentHosts() {
        if (instrumentHosts == null) {
            instrumentHosts = new ArrayList<String>();
        }
        return this.instrumentHosts;
    }

    public List<String> getInstruments() {
        if (instruments == null) {
            instruments = new ArrayList<String>();
        }
        return this.instruments;
    }

    public List<String> getMissions() {
        if (missions == null) {
            missions = new ArrayList<String>();
        }
        return this.missions;
    }

    public XMLGregorianCalendar getStartDate() {
        return startDate;
    }

    public void setStartDate(XMLGregorianCalendar value) {
        this.startDate = value;
    }

    public XMLGregorianCalendar getStopDate() {
        return stopDate;
    }

    public void setStopDate(XMLGregorianCalendar value) {
        this.stopDate = value;
    }

    public List<String> getTargetTypes() {
        if (targetTypes == null) {
            targetTypes = new ArrayList<String>();
        }
        return this.targetTypes;
    }

    public List<String> getTargets() {
        if (targets == null) {
            targets = new ArrayList<String>();
        }
        return this.targets;
    }
}
