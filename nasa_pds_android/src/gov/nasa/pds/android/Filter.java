/*
 * Copyright (C) 2012 TopCoder Inc., All Rights Reserved.
 */
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

/**
 * Class that contains information about filter.
 *
 * @author 7realm
 * @version 1.0
 */
class Filter {
    private String text = "";
    private final List<NamedRestriction> restrictions = new ArrayList<NamedRestriction>();

    /**
     * Get search text.
     *
     * @return search text, can be empty
     */
    public String getText() {
        return text;
    }

    /**
     * Set filter text.
     *
     * @param text search text, can be empty
     */
    public void setText(String text) {
        this.text = text;
    }

    /**
     * Add restriction to filter.
     *
     * @param entityInfo the restricted entity data
     * @param entityType the restricted entity type
     */
    public void addRestriction(EntityInfo entityInfo, EntityType entityType) {
        restrictions.add(new NamedRestriction(entityInfo, entityType));
    }

    /**
     * Clear all permission for entities that are not greater then given one.
     *
     * @param entityType the lowest possible entity
     */
    public void clearNotGreaterPermissions(EntityType entityType) {
        for (Iterator<NamedRestriction> i = restrictions.iterator(); i.hasNext();) {
            if (!entityType.isLowerThan(i.next().entityType)) {
                i.remove();
            }
        }
    }

    /**
     * Gets lowest(strongest) permission.
     *
     * @return the lowest permission
     */
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

    /**
     * Remove last(lowest) permission.
     */
    public void removeLowestRestriction() {
        if (restrictions.size() > 0) {
            restrictions.remove(restrictions.size() - 1);
        }
    }

    /**
     * Creates results provider based on filter data.
     *
     * @param entityType the result entity type
     * @return the created results provider
     */
    public ResultsProvider createProvider(EntityType entityType) {
        clearNotGreaterPermissions(entityType);

        if (text.isEmpty()) {
            if (entityType == EntityType.TARGET_TYPE) {
                return new TypesResultsProvider();
            }

            return new PageResultsProvider(new InfoPagedQuery(entityType.getObjectsInfoQuery(), getLowestRestriction()));
        }

        return new PageResultsProvider(new SearchByTypePagedQuery(text, entityType, getLowestRestriction()));
    }

    /**
     * Get list of all restrictions.
     *
     * @return the list of restrictions
     */
    public List<NamedRestriction> getRestrictions() {
        return restrictions;
    }

    /**
     * Restriction that contains not only class and id, but also name.
     *
     * @author 7realm
     * @version 1.0
     */
    static class NamedRestriction {
        private final EntityType entityType;
        private final EntityInfo entityInfo;

        /**
         * Constructor for NamedRestriction type.
         *
         * @param entityInfo the entity id and value
         * @param entityType the entity type
         */
        public NamedRestriction(EntityInfo entityInfo, EntityType entityType) {
            this.entityInfo = entityInfo;
            this.entityType = entityType;
        }

        /**
         * Get {@link Restriction} entity for SOAP requests.
         *
         * @return the created restricion entity
         */
        public Restriction getRestriction() {
            Restriction result = new Restriction();
            result.setRestrictionEntityId(entityInfo.getId());
            result.setRestrictionEntityClass(entityType.getClassName());
            return result;
        }

        /**
         * Get entity type.
         *
         * @return the entity type
         */
        public EntityType getEntityType() {
            return entityType;
        }

        /**
         * Get restriction entity name.
         *
         * @return the restriction entity name
         */
        public String getValue() {
            return entityInfo.getName();
        }
    }
}