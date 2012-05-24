/*
 * Copyright (C) 2012 TopCoder Inc., All Rights Reserved.
 */
package gov.nasa.pds.data.resultproviders;

import gov.nasa.pds.data.DataCenter;
import gov.nasa.pds.data.EntityType;
import gov.nasa.pds.data.QueryType;
import gov.nasa.pds.data.queries.TypesPagedQuery;

/**
 * Data adapter that will fill results from getTargetTypesInfo request to list.
 *
 * @author 7realm
 * @version 1.0
 */
public class TypesResultsProvider extends ResultsProvider {
    private int total;

    /**
     * Constructor for TypesResultsProvider type.
     */
    public TypesResultsProvider() {
        super(QueryType.GET_TYPES_INFO);
    }

    /**
     * Gets entity type.
     *
     * @return the result entity type
     */
    @Override
    public EntityType getEntityType() {
        return EntityType.TARGET_TYPE;
    }

    /**
     * Gets page count.
     *
     * @return the page count
     */
    @Override
    public int getPageCount() {
        return 1;
    }

    /**
     * Gets current page number.
     *
     * @return the current page number
     */
    @Override
    public int getCurrentPage() {
        return 1;
    }

    /**
     * Gets current page size.
     *
     * @return the current page size
     */
    @Override
    public int getCurrentPageSize() {
        return total;
    }

    /**
     * Moves to specified page. This implmentaion supprts only one page.
     *
     * @param pageIndex the index of target page
     */
    @Override
    public void moveToPage(int pageIndex) {
        if (lastResult == null) {
            // get last results from data center
            lastResult = DataCenter.executePagedQuery(new TypesPagedQuery());

            // update total and last page size
            total = (int) lastResult.getTotal();
        }
    }
}
