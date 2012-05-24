/**
 * Copyright (c) 2003,2004, Stefan Haustein, Oberhausen, Rhld., Germany
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the Software without restriction, including without limitation the
 * rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the
 * Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR
 * OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package org.ksoap2.serialization;

import gov.nasa.pds.soap.SerializationException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.SoapFault;
import org.ksoap2.SoapFault12;
import org.ksoap2.serialization.marshals.MarshalAttachment;
import org.ksoap2.serialization.marshals.MarshalDate;
import org.ksoap2.serialization.marshals.MarshalDefault;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;

/**
 * @author Stefan Haustein
 *
 *         This class extends the SoapEnvelope with Soap Serialization functionality.
 */
public class SoapSerializationEnvelope extends SoapEnvelope {
    public static final String DEFAULT_NAMESPACE = "http://pds.nasa.gov/";
    private static final String ANY_TYPE_LABEL = "anyType";
    private static final String ROOT_LABEL = "root";
    private static final String TYPE_LABEL = "type";
    private static final String ITEM_LABEL = "item";
    private static final Marshal DEFAULT_MARSHAL = new MarshalDefault();

    /**
     * Public properties that will be passed to getPropertyInfo method.
     * <p>
     * This field is not used int this class.
     */
    public Hashtable properties = new Hashtable();

    /**
     * Map from XML qualified names to Java classes
     */
    protected Map<QNameBase, Object> qNameToClass = new Hashtable();

    /**
     * Map from Java class names to XML type and namespace pairs
     */
    protected Map<String, QNameInfo> classToQName = new HashMap<String, QNameInfo>();

    public SoapSerializationEnvelope(int version) {
        super(version);
        DEFAULT_MARSHAL.register(this);
        new MarshalDate().register(this);
        new MarshalAttachment().register(this);
    }

    @Override
    public void parseBody(XmlPullParser parser) throws IOException, XmlPullParserException {
        bodyIn = null;
        parser.nextTag();
        if (parser.getEventType() == XmlPullParser.START_TAG && parser.getNamespace().equals(env)
            && parser.getName().equals("Fault")) {
            SoapFault fault;
            if (version < SoapEnvelope.VER12) {
                fault = new SoapFault(version);
            } else {
                fault = new SoapFault12(version);
            }
            fault.parse(parser);
            bodyIn = fault;
        } else {
            while (parser.getEventType() == XmlPullParser.START_TAG) {
                String rootAttr = parser.getAttributeValue(enc, ROOT_LABEL);

                Object object = readObject(parser, parser.getNamespace(), parser.getName(), PropertyInfo.OBJECT_TYPE);
                if ("1".equals(rootAttr) || bodyIn == null) {
                    bodyIn = object;
                }
                parser.nextTag();
            }
        }
    }

    protected void readSerializable(XmlPullParser parser, String namespace, KvmSerializable obj) throws IOException,
        XmlPullParserException {
        int propertyCount = obj.getPropertyCount();
        while (parser.nextTag() != XmlPullParser.END_TAG) {
            String parsedName = parser.getName();
            String parsedNamespace = "".equals(namespace) ? parser.getNamespace() : namespace;

            int foundIndex = -1;
            PropertyInfo propertyInfo = null;
            for (int i = 0; i < propertyCount; i++) {
                propertyInfo = obj.getPropertyInfo(i, properties);
                if ((propertyInfo.namespace == null || propertyInfo.namespace.equals(parsedNamespace))
                    && parsedName.equals(propertyInfo.name)) {
                    foundIndex = i;
                    break;
                }
            }

            if (foundIndex != -1 && propertyInfo != null) {

                if (propertyInfo.type.equals(List.class)) {
                    Object value = readObject(parser, parsedNamespace, parsedName, propertyInfo.elementType);
                    List<Object> list = (List<Object>) obj.getProperty(foundIndex);
                    if (list == null) {
                        list = new ArrayList<Object>();
                        obj.setProperty(foundIndex, list);
                    }
                    list.add(value);
                } else {
                    Object value = readObject(parser, parsedNamespace, parsedName, propertyInfo);
                    obj.setProperty(foundIndex, value);
                }
            } else {
                throw new RuntimeException("Failed to find property " + parsedName + " in " + obj);
            }
        }
        parser.require(XmlPullParser.END_TAG, null, null);
    }

    /**
     * Builds an object from the XML stream. This method is public for usage in conjunction with Marshal subclasses.
     * Precondition: On the start tag of the object or property, so href can be read.
     */

    public Object readObject(XmlPullParser parser, String namespace, String name,
        PropertyInfo expected) throws IOException, XmlPullParserException {
        String parsedName = parser.getName();

        Object obj = qNameToClass.get(new QNameBase(namespace, name));
        if (obj == null) {
            // get namespace and name from type attribute
            String type = parser.getAttributeValue(xsi, TYPE_LABEL);
            if (type != null) {
                int cut = type.indexOf(':');
                name = type.substring(cut + 1);
                String prefix = cut == -1 ? "" : type.substring(0, cut);
                namespace = parser.getNamespace(prefix);
                obj = qNameToClass.get(new QNameBase(namespace, name));
            } else if (expected != null) {
                QNameInfo info = getInfo(expected.type, null);
                if (info != null) {
                    name = info.type;
                    namespace = info.namespace;
                    obj = qNameToClass.get(info);
                    if (obj == null) {
                        obj = info.marshal;
                    }
                } else {
                    obj = expected.type;
                }
            }
        }

        if (obj == null) {
            throw new RuntimeException("No mapping for " + parsedName);
        } else if (obj instanceof Marshal) {
            Marshal marshal = (Marshal) obj;
            return marshal.readInstance(parser, namespace, name, expected);
        }

        try {
            obj = ((Class) obj).newInstance();
            if (obj instanceof KvmSerializable) {
                readSerializable(parser, namespace, (KvmSerializable) obj);
            } else {
                throw new RuntimeException("no deserializer for " + obj.getClass());
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to create instance.", e);
        }

        parser.require(XmlPullParser.END_TAG, null, parsedName);
        return obj;
    }

    /**
     * Returns a string array containing the namespace, type, id and Marshal object for the given java object. This
     * method is used by the SoapWriter in order to map Java objects to the corresponding SOAP section five XML code.
     */
    public QNameInfo getInfo(Object type, Object instance) {
        if (type == null) {
            type = instance.getClass();
        }
        if (type != PropertyInfo.OBJECT_CLASS) {
            QNameInfo tmp = classToQName.get(((Class) type).getName());
            if (tmp != null) {
                return tmp;
            }
        }
        return null;
    }

    /**
     * Defines a direct mapping from a namespace and type to a java class (and vice versa), using the given marshal
     * mechanism
     */
    public SoapSerializationEnvelope addMapping(String namespace, String name, Class clazz, Marshal marshal) {
        qNameToClass.put(new QNameBase(namespace, name), marshal == null ? (Object) clazz : marshal);
        classToQName.put(clazz.getName(), new QNameInfo(namespace, name, marshal));
        return this;
    }

    /**
     * Defines a direct mapping from a namespace and type to a java class (and vice versa)
     */
    public SoapSerializationEnvelope addMapping(String namespace, String name, Class clazz) {
        return addMapping(namespace, name, clazz, null);
    }

    /**
     * Defines a direct mapping from a namespace and type to a java class (and vice versa)
     */
    public SoapSerializationEnvelope addMapping(String name, Class clazz) {
        return addMapping(DEFAULT_NAMESPACE, name, clazz, null);
    }

    public SoapSerializationEnvelope addRequest(Object request) {
        setOutputSoapObject(request);
        return this;
    }

    /**
     * Response from the soap call. Pulls the object from the wrapper object and returns it.
     *
     * @return response from the soap call.
     * @throws SoapFault if response is fault
     */
    public Object getResponse() throws SoapFault {
        if (bodyIn instanceof SoapFault) {
            throw (SoapFault) bodyIn;
        }
        KvmSerializable serailizableBody = (KvmSerializable) bodyIn;

        if (serailizableBody.getPropertyCount() == 0) {
            return null;
        } else if (serailizableBody.getPropertyCount() == 1) {
            return serailizableBody.getProperty(0);
        } else {
            List<Object> result = new ArrayList<Object>();
            for (int i = 0; i < serailizableBody.getPropertyCount(); i++) {
                result.add(serailizableBody.getProperty(i));
            }
            return result;
        }
    }

    /**
     * Serializes the request object to the given XmlSerliazer object
     *
     * @param writer XmlSerializer object to write the body into.
     */
    @Override
    public void writeBody(XmlSerializer writer) throws IOException {
        // allow an empty body without any tags in it
        // see http://code.google.com/p/ksoap2-android/issues/detail?id=77
        if (bodyOut != null) {
            QNameInfo qName = getInfo(null, bodyOut);
            if (qName == null) {
                throw new SerializationException("Not mapped class: " + bodyOut);
            }
            writer.startTag(qName.namespace, qName.type);
            writeElement(writer, bodyOut, null, qName.marshal);
            writer.endTag(qName.namespace, qName.type);
        }
    }

    /**
     * Writes the body of an KvmSerializable object. This method is public for access from Marshal subclasses.
     */
    public void writeObjectBody(XmlSerializer writer, KvmSerializable obj) throws IOException {
        for (int i = 0; i < obj.getPropertyCount(); i++) {
            // get the property
            Object propertyValue = obj.getProperty(i);
            // and importantly also get the property info which holds the type potentially!
            PropertyInfo propertyInfo = obj.getPropertyInfo(i, properties);

            if (propertyValue == null) {
                // skip null properties
            } else {
                // skip transient properties
                if ((propertyInfo.flags & PropertyInfo.TRANSIENT) == 0) {
                    writer.startTag(propertyInfo.namespace, propertyInfo.name);
                    writeProperty(writer, propertyValue, propertyInfo);
                    writer.endTag(propertyInfo.namespace, propertyInfo.name);
                }
            }
        }
    }

    protected void writeProperty(XmlSerializer writer, Object obj, PropertyInfo type) throws IOException {
        if (obj == null) {
            throw new SerializationException("Property value should not be null.");
        }
        QNameInfo qName = getInfo(null, obj);
        if (obj.getClass() != type.type) {
            String prefix = writer.getPrefix(qName.namespace, true);
            writer.attribute(xsi, TYPE_LABEL, prefix + ":" + qName.type);
        }
        writeElement(writer, obj, type, qName == null ? null : qName.marshal);
    }

    private void writeElement(XmlSerializer writer, Object element, PropertyInfo type, Object marshal)
        throws IOException {
        if (marshal != null) {
            ((Marshal) marshal).writeInstance(writer, element);
        } else if (element instanceof KvmSerializable) {
            writeObjectBody(writer, (KvmSerializable) element);
        } else if (element instanceof List) {
            writeListBody(writer, (List<?>) element, type.elementType);
        } else {
            throw new RuntimeException("Cannot serialize: " + element);
        }
    }

    protected void writeListBody(XmlSerializer writer, List<?> list, PropertyInfo elementType)
        throws IOException {
        String itemsTagName = ITEM_LABEL;
        String itemsNamespace = null;

        if (elementType == null) {
            elementType = PropertyInfo.OBJECT_TYPE;
        } else if (elementType.name != null) {
            itemsTagName = elementType.name;
            itemsNamespace = elementType.namespace;
        }

        boolean skipped = false;
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i) == null) {
                skipped = true;
            } else {
                writer.startTag(itemsNamespace, itemsTagName);
                if (skipped) {
                    writer.attribute(enc, "position", "[" + i + "]");
                    skipped = false;
                }
                writeProperty(writer, list.get(i), elementType);
                writer.endTag(itemsNamespace, itemsTagName);
            }
        }
    }

    private static class QNameBase {
        protected final String namespace;
        protected final String type;

        protected QNameBase(String namespace, String type) {
            super();
            this.namespace = namespace;
            this.type = type;
        }

        @Override
        public boolean equals(Object otherObject) {
            if (!(otherObject instanceof QNameBase)) {
                return false;
            }
            QNameBase other = (QNameBase) otherObject;
            return type.equals(other.type) &&
                (namespace == null ? other.namespace == null : namespace.equals(other.namespace));
        }

        @Override
        public int hashCode() {
            return (type == null ? 0 : type.hashCode()) ^
                (namespace == null ? 0 : namespace.hashCode());
        }
    }

    private static class QNameInfo extends QNameBase {

        private final Marshal marshal;

        private QNameInfo(String namespace, String type, Marshal marshal) {
            super(namespace, type);
            this.marshal = marshal;
        }
    }
}
