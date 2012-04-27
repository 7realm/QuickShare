package gov.nasa.pds.data.queries;

import gov.nasa.pds.data.QueryType;

import org.ksoap2.serialization.SoapSerializationEnvelope;

public class ObjectQuery<T> extends BaseQuery {
    private final long id;

    public ObjectQuery(QueryType queryType, long id) {
        super(queryType);
        this.id = id;
    }

    public long getId() {
        return id;
    }

    @Override
    public SoapSerializationEnvelope getEnvelope() {
        // TODO Auto-generated method stub
        return null;
    }

}
