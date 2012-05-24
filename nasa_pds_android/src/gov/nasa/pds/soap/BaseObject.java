/*
 * Copyright (C) 2012 TopCoder Inc., All Rights Reserved.
 */
package gov.nasa.pds.soap;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.List;

import org.ksoap2.serialization.KvmSerializable;
import org.ksoap2.serialization.PropertyInfo;

/**
 * Base serializable object for all entities and requests.
 *
 * @author 7realm
 * @version 1.0
 */
public abstract class BaseObject implements KvmSerializable {
    private Field[] fields;

    /**
     * Constructor for BaseObject type.
     */
    public BaseObject() {
        super();

        List<Field> fieldList = new ArrayList<Field>();
        Class<?> clazz = this.getClass();
        while (!clazz.equals(BaseObject.class)) {
            fieldList.addAll(Arrays.asList(clazz.getDeclaredFields()));

            clazz = clazz.getSuperclass();
        }
        fields = fieldList.toArray(new Field[fieldList.size()]);
        Arrays.sort(fields, new Comparator<Field>() {
            @Override
            public int compare(Field lhs, Field rhs) {
                return lhs.getName().compareTo(rhs.getName());
            }
        });

        // make fields accessible
        for (Field field : fields) {
            field.setAccessible(true);
        }
    }

    /**
     * Gets property value.
     *
     * @param i the index of property
     * @return the property value
     */
    @Override
    public Object getProperty(int i) {
        try {
            return fields[i].get(this);
        } catch (Exception e) {
            throw new SerializationException("Failed to get field value for #" + i + " in class " + getClass().getName(), e);
        }
    }

    /**
     * Gets count of properties.
     *
     * @return the count of properties
     */
    @Override
    public int getPropertyCount() {
        return fields.length;
    }

    /**
     * Gets information about the property.
     *
     * @param i the property index
     * @param hashtable the common data
     * @return the property information
     */
    @Override
    @SuppressWarnings("rawtypes")
    public PropertyInfo getPropertyInfo(int i, Hashtable hashtable) {
        PropertyInfo propertyinfo = new PropertyInfo();
        propertyinfo.name = getFieldName(i);
        propertyinfo.type = fields[i].getType();
        if (fields[i].getType().equals(List.class)) {
            ParameterizedType stringListType = (ParameterizedType) fields[i].getGenericType();
            propertyinfo.elementType = new PropertyInfo();
            propertyinfo.elementType.name = getElementName(getFieldName(i));
            propertyinfo.elementType.type = stringListType.getActualTypeArguments()[0];
        }

        return propertyinfo;
    }

    /**
     * Gets name of elements.
     *
     * @param fieldName the list property name
     * @return the name of list elements
     */
    @SuppressWarnings({"static-method", "unused"})
    protected String getElementName(String fieldName) {
        return "items";
    }

    private String getFieldName(int i) {
        String fieldName = fields[i].getName();
        return fieldName.startsWith("_") ? fieldName.substring(1) : fieldName;
    }

    /**
     * Sets property value.
     *
     * @param i the property index
     * @param obj the property value
     */
    @Override
    public void setProperty(int i, Object obj) {
        try {
            fields[i].set(this, obj);
        } catch (Exception e) {
            throw new SerializationException("Failed to set field value for #" + i + " in class " + getClass().getName(), e);
        }
    }
}