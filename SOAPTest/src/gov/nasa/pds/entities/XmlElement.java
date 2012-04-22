package gov.nasa.pds.entities;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(value = RetentionPolicy.RUNTIME)
public @interface XmlElement {

    String name() default "";

    boolean nillable() default false;

}
