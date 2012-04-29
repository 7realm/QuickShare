package gov.nasa.pds.data.queries;

import gov.nasa.pds.data.QueryType;
import gov.nasa.pds.soap.calls.GetTargetTypesInfoRequest;
import gov.nasa.pds.soap.calls.GetTargetTypesInfoResponse;
import gov.nasa.pds.soap.entities.EntityInfo;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapSerializationEnvelope;

public class TypesPagedQuery extends PagedQuery {

    public TypesPagedQuery() {
        super(QueryType.GET_TYPES_INFO);
    }

    @Override
    public SoapSerializationEnvelope getEnvelope() {
        return new SoapSerializationEnvelope(SoapEnvelope.VER11).addRequest(new GetTargetTypesInfoRequest())
            .addMapping("getTargetTypesInfo", GetTargetTypesInfoRequest.class)
            .addMapping("getTargetTypesInfoResponse", GetTargetTypesInfoResponse.class)
            .addMapping("results", EntityInfo.class);
    }

}