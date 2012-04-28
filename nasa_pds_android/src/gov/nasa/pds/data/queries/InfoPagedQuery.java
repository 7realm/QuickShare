package gov.nasa.pds.data.queries;

import gov.nasa.pds.data.QueryType;
import gov.nasa.pds.soap.calls.GetEntitiesInfoRequest;
import gov.nasa.pds.soap.calls.GetEntitiesInfoResponse;
import gov.nasa.pds.soap.entities.EntityInfo;
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

    public InfoPagedQuery(QueryType queryType, int pageNumber, EntityType restrictionType, long idValue) {
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
            .addMapping("results", EntityInfo.class);

        // add mappings based on query type
        switch (getQueryType()) {
        case GET_TYPES_INFO:
            // use super implementation for getTypesInfo request
            return super.getEnvelope();
        case GET_TARGETS_INFO:
            envelope.addMapping("getTargetsInfo", GetEntitiesInfoRequest.class);
            envelope.addMapping("getTargetsInfoResponse", GetEntitiesInfoResponse.class);
            break;
        case GET_MISSIONS_INFO:
            envelope.addMapping("getMissionsInfo", GetEntitiesInfoRequest.class);
            envelope.addMapping("getMissionsInfoResponse", GetEntitiesInfoResponse.class);
            break;
        case GET_INSTRUMENTS_INFO:
            envelope.addMapping("getInstrumentsInfo", GetEntitiesInfoRequest.class);
            envelope.addMapping("getInstrumentsInfoResponse", GetEntitiesInfoResponse.class);
            break;
        default:
            Log.w("soap", "Not expected entities info request: " + getQueryType());
            break;
        }

        return envelope;
    }
}
