package gov.nasa.pds.data.queries;

import gov.nasa.pds.data.QueryType;
import gov.nasa.pds.soap.calls.GetTargetTypesInfoRequest;
import gov.nasa.pds.soap.calls.GetTargetTypesInfoResponse;
import gov.nasa.pds.soap.entities.Page;
import gov.nasa.pds.soap.entities.PagedResults;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapSerializationEnvelope;

public class PagedQuery extends BaseQuery {
    private static final int ITEMS_PER_PAGE = 20;
    private final Page page;

    public PagedQuery(QueryType queryType, int pageNumber) {
        super(queryType);
        this.page = new Page();
        page.setPageNumber(pageNumber);
        page.setItemsPerPage(ITEMS_PER_PAGE);
    }

    public Page getPage() {
        return page;
    }

    @Override
    public SoapSerializationEnvelope getEnvelope() {
        return new SoapSerializationEnvelope(SoapEnvelope.VER11).addRequest(new GetTargetTypesInfoRequest())
            .addMapping("getTargetTypesInfo", GetTargetTypesInfoRequest.class)
            .addMapping("getTargetTypesInfoResponse", GetTargetTypesInfoResponse.class)
            .addMapping("return", PagedResults.class);
    }
}
