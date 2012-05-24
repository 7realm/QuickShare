/* Copyright (c) 2003,2004, Stefan Haustein, Oberhausen, Rhld., Germany
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or
 * sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The  above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS
 * IN THE SOFTWARE. */

package org.ksoap2.serialization.marshals;

import gov.nasa.pds.soap.entities.DataHandler;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.ksoap2.serialization.Marshal;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;

/**
 * This class is not public, so save a few bytes by using a short class name (MarshalDefault stands for DefaultMarshal)...
 */
public class MarshalAttachment implements Marshal {
    public static final Map<String, DataHandler> ATTACHMENTS = new HashMap<String, DataHandler>();

    @Override
    public Object readInstance(XmlPullParser parser, String namespace, String name, PropertyInfo expected)
        throws IOException, XmlPullParserException {
        DataHandler dataHandler = null;
        parser.require(XmlPullParser.START_TAG, null, "dataHandler");
        if (parser.next() == XmlPullParser.START_TAG) {
            parser.require(XmlPullParser.START_TAG, null, "Include");
            String value = parser.getAttributeValue(0);
            dataHandler = new DataHandler();
            ATTACHMENTS.put(value, dataHandler);
            if (parser.getEventType() != XmlPullParser.END_TAG) {
                parser.nextTag();
            }
            parser.require(XmlPullParser.END_TAG, null, "Include");
        }
        parser.nextTag();
        parser.require(XmlPullParser.END_TAG, null, "dataHandler");
        return dataHandler;
    }

    /**
     * Write the instance out. In case it is an AttributeContainer write those our first though.
     * 
     * @param writer the xml serializer.
     * @param instance
     * @throws IOException
     */
    @Override
    public void writeInstance(XmlSerializer writer, Object instance) throws IOException {
        throw new UnsupportedOperationException("Writing of attachments is not supported.");
    }

    @Override
    public void register(SoapSerializationEnvelope cm) {
        cm.addMapping(SoapSerializationEnvelope.DEFAULT_NAMESPACE, "dataHandler", DataHandler.class, this);
    }
}
