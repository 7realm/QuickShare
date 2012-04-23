package gov.nasa.pds.entities.response;

import gov.nasa.pds.entities.BaseObject;


public class SearchResults extends BaseObject {
    protected PagedResults dataFiles;
    protected PagedResults datasets;
    protected PagedResults instruments;
    protected PagedResults missions;
    protected PagedResults targetTypes;
    protected PagedResults targets;

    public PagedResults getDataFiles() {
        return dataFiles;
    }

    public void setDataFiles(PagedResults value) {
        this.dataFiles = value;
    }

    public PagedResults getDatasets() {
        return datasets;
    }

    public void setDatasets(PagedResults value) {
        this.datasets = value;
    }

    public PagedResults getInstruments() {
        return instruments;
    }

    public void setInstruments(PagedResults value) {
        this.instruments = value;
    }

    public PagedResults getMissions() {
        return missions;
    }

    public void setMissions(PagedResults value) {
        this.missions = value;
    }

    public PagedResults getTargetTypes() {
        return targetTypes;
    }

    public void setTargetTypes(PagedResults value) {
        this.targetTypes = value;
    }

    public PagedResults getTargets() {
        return targets;
    }

    public void setTargets(PagedResults value) {
        this.targets = value;
    }
}
