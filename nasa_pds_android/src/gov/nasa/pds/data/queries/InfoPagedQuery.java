package gov.nasa.pds.data.queries;

import gov.nasa.pds.data.QueryType;
import gov.nasa.pds.soap.calls.GetEntitiesInfoRequest;
import gov.nasa.pds.soap.calls.GetMissionsInfoRequest;
import gov.nasa.pds.soap.calls.GetMissionsInfoResponse;
import gov.nasa.pds.soap.calls.GetTargetsInfoRequest;
import gov.nasa.pds.soap.calls.GetTargetsInfoResponse;
import gov.nasa.pds.soap.entities.PagedResults;
import gov.nasa.pds.soap.entities.Restriction;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapSerializationEnvelope;

import android.util.Log;

/**
 *
 * @author TCSASSEMBLER
 * @version 1.0
 */
public class InfoPagedQuery extends PagedQuery {
    private final Restriction restriction;

    public InfoPagedQuery(QueryType queryType, int pageNumber) {
        super(queryType, pageNumber);
        restriction = null;
    }

    public InfoPagedQuery(QueryType queryType, int pageNumber, RestrictionType restrictionType, long idValue) {
        super(queryType, pageNumber);
        restriction = new Restriction();
        restriction.setRestrictionEntityClass(restrictionType.getClassName());
        restriction.setRestrictionEntityId(idValue);
    }

    /**
     * Creates SOAP envelope for this query.
     *
     * @return created envelope
     */
    @Override
    public SoapSerializationEnvelope getEnvelope() {
        // create entities request based on page and restriction
        GetEntitiesInfoRequest request = new GetEntitiesInfoRequest();
        request.setPage(getPage());
        request.setRestriction(restriction);

        // create base envelope
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11).addRequest(request)
            .addMapping("return", PagedResults.class);

        // add mappings based on query type
        switch (getQueryType()) {
        case GET_TARGETS_INFO:
            envelope.addMapping("getTargetsInfo", GetTargetsInfoRequest.class);
            envelope.addMapping("getTargetsInfoResponse", GetTargetsInfoResponse.class);
            break;
        case GET_MISSIONS_INFO:
            envelope.addMapping("getMissionsInfo", GetMissionsInfoRequest.class);
            envelope.addMapping("getMissionsInfoResponse", GetMissionsInfoResponse.class);
            break;
        default:
            Log.w("soap", "Not expected entities info request: " + getQueryType());
            break;
        }

        return envelope;
    }
}
