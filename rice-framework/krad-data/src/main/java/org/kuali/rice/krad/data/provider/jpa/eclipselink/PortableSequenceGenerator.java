package org.kuali.rice.krad.data.provider.jpa.eclipselink;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({TYPE, METHOD, FIELD})
@Retention(RUNTIME)
public @interface PortableSequenceGenerator {

    /**
     * (Required) A unique generator name that can be referenced
     * by one or more classes to be the generator for primary key
     * values.
     */
    String name();

    /**
     * The name of the database sequence object from which to obtain primary key values.
     */
    String sequenceName();

    /** (Optional) The catalog of the sequence generator.
     */
    String catalog() default "";

    /** (Optional) The schema of the sequence generator.
     */
    String schema() default "";

    /**
     * (Optional) The value from which the sequence object
     * is to start generating.
     */
    int initialValue() default 1;

    /**
     * (Optional) The amount to increment by when allocating
     * sequence numbers from the sequence.
     */
    int allocationSize() default 50;

}
