/*
 * Copyright (C) 2012 TopCoder Inc., All Rights Reserved.
 */
package gov.nasa.pds.data.queries;

import gov.nasa.pds.data.DataCenter;
import gov.nasa.pds.data.EntityType;
import gov.nasa.pds.data.Query;
import gov.nasa.pds.data.QueryType;
import gov.nasa.pds.soap.entities.Page;
import gov.nasa.pds.soap.entities.Restriction;

/**
 * Base query that return results organized to pages.
 *
 * @author TCSASSEMBLER
 * @version 1.0
 */
public abstract class PagedQuery extends BaseQuery {
    private final Page page;
    private final Restriction restriction;

    /**
     * Constructor for PagedQuery type.
     *
     * @param queryType the query type
     */
    public PagedQuery(QueryType queryType) {
        this(queryType, null);
    }

    /**
     * Constructor for PagedQuery type.
     *
     * @param queryType the query type
     * @param restriction the search restriction
     */
    public PagedQuery(QueryType queryType, Restriction restriction) {
        super(queryType);
        this.restriction = restriction;
        page = new Page();
        page.setPageNumber(1);
        page.setItemsPerPage(DataCenter.ITEMS_PER_PAGE);
    }

    /**
     * Get page value.
     *
     * @return the page value
     */
    public Page getPage() {
        return page;
    }

    /**
     * Get search restriction.
     *
     * @return the search restriction
     */
    public Restriction getRestriction() {
        return restriction;
    }

    /**
     * Get result entity type
     *
     * @return the result entity type
     */
    public abstract EntityType getEntityType();

    @Override
    public boolean equalsQuery(Query other) {
        if (other instanceof PagedQuery) {
            PagedQuery pagedQuery = (PagedQuery) other;

            if (restriction == null) {
                return super.equalsQuery(pagedQuery) && page.getPageNumber() == pagedQuery.page.getPageNumber();
            }

            return super.equalsQuery(pagedQuery) && page.getPageNumber() == pagedQuery.page.getPageNumber()
                && pagedQuery.restriction != null
                && restriction.getRestrictionEntityId() == pagedQuery.restriction.getRestrictionEntityId()
                && restriction.getRestrictionEntityClass().equals(pagedQuery.restriction.getRestrictionEntityClass());
        }
        return false;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode() * 23 + page.getPageNumber();
        if (restriction != null) {
            result = result * 23 + restriction.getRestrictionEntityClass().hashCode();
            result = result * 23 + new Long(restriction.getRestrictionEntityId()).hashCode();
        }
        return result;
    }
}
