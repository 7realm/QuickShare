/*
 * Copyright (C) 2012 TopCoder Inc., All Rights Reserved.
 */
package gov.nasa.pds.data.queries;

import gov.nasa.pds.data.DataCenter;
import gov.nasa.pds.data.EntityType;
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

    public PagedQuery(QueryType queryType) {
        this(queryType, null);
    }

    public PagedQuery(QueryType queryType, Restriction restriction) {
        super(queryType);
        this.restriction = restriction;
        this.page = new Page();
        page.setPageNumber(1);
        page.setItemsPerPage(DataCenter.ITEMS_PER_PAGE);
    }

    public Page getPage() {
        return page;
    }

    public Restriction getRestriction() {
        return restriction;
    }

    public abstract EntityType getEntityType();
}
