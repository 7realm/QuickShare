package gov.nasa.pds.soap;

public class SerializationException extends RuntimeException {
    private static final long serialVersionUID = 600881428710444284L;

    public SerializationException(String detailMessage) {
        super(detailMessage);
    }

    public SerializationException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

}
