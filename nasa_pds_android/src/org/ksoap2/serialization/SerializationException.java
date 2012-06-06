/*
 * Copyright (C) 2012 TopCoder Inc., All Rights Reserved.
 */
package org.ksoap2.serialization;

/**
 * Exception that will be thrown on serialization.
 *
 * @author TCSASSEMBLER
 * @version 1.0
 */
public class SerializationException extends RuntimeException {
    private static final long serialVersionUID = 600881428710444284L;

    public SerializationException(String detailMessage) {
        super(detailMessage);
    }

    public SerializationException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

}
