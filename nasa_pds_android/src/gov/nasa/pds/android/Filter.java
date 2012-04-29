package gov.nasa.pds.android;

import gov.nasa.pds.data.EntityType;
import gov.nasa.pds.data.queries.InfoPagedQuery;
import gov.nasa.pds.data.queries.SearchByTypePagedQuery;
import gov.nasa.pds.data.resultproviders.PageResultsProvider;
import gov.nasa.pds.data.resultproviders.ResultsProvider;
import gov.nasa.pds.data.resultproviders.TypesResultsProvider;
import gov.nasa.pds.soap.entities.EntityInfo;
import gov.nasa.pds.soap.entities.Restriction;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

class Filter {
    private String text = "";
    final List<NamedRestriction> restrictions = new ArrayList<NamedRestriction>();

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void addRestriction(EntityInfo entityInfo, EntityType entityType) {
        restrictions.add(new NamedRestriction(entityInfo, entityType));
    }

    public void clearNotGreaterPermissions(EntityType entityType) {
        for (Iterator<NamedRestriction> i = restrictions.iterator(); i.hasNext();) {
            if (!entityType.isLowerThan(i.next().entityType)) {
                i.remove();
            }
        }
    }

    public Restriction getLowestRestriction() {
        NamedRestriction namedRestriction = null;
        for (NamedRestriction restriction : restrictions) {
            if (namedRestriction == null) {
                namedRestriction = restriction;
                continue;
            }
            if (restriction.entityType.isLowerThan(namedRestriction.entityType)) {
                namedRestriction = restriction;
            }
        }

        return namedRestriction == null ? null : namedRestriction.getRestriction();
    }

    public ResultsProvider createProvider(EntityType entityType) {
        clearNotGreaterPermissions(entityType);

        if (text.isEmpty()) {
            if (entityType == EntityType.TARGET_TYPE) {
                return new TypesResultsProvider();
            }

            return new PageResultsProvider(new InfoPagedQuery(entityType.getObjectsInfoQuery(), getLowestRestriction()));
        }

        return new PageResultsProvider(new SearchByTypePagedQuery(text, entityType.getClassName(), getLowestRestriction()));
    }

    @Override
    public String toString() {
        if (text.isEmpty() && restrictions.isEmpty()) {
            return "<empty filter>";
        }

        StringBuilder builder = new StringBuilder();
        if (!text.isEmpty()) {
            builder.append("[text = ").append(text).append("]\n");
        }
        for (NamedRestriction restriction : restrictions) {
            builder.append(restriction).append("\n");
        }
        return builder.toString().trim();
    }

    static class NamedRestriction {
        private final EntityType entityType;
        private final EntityInfo entityInfo;

        public NamedRestriction(EntityInfo entityInfo, EntityType entityType) {
            this.entityInfo = entityInfo;
            this.entityType = entityType;
        }

        public Restriction getRestriction() {
            Restriction result = new Restriction();
            result.setRestrictionEntityId(entityInfo.getId());
            result.setRestrictionEntityClass(entityType.getClassName());
            return result;
        }

        @Override
        public String toString() {
            return new StringBuilder("[").append(entityType.getHumanReadable())
                .append(" = ").append(entityInfo.getName()).append("]").toString();
        }
    }
}