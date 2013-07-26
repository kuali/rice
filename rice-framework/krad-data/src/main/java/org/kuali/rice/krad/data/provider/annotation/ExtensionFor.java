package org.kuali.rice.krad.data.provider.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Identifies the annotated class as an extension object for the given baseline class.
 * 
 * Inclusion of this annotation will perform the necessary wiring within JPA.
 * 
 * @author jonathan
 * 
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ExtensionFor {

	/** (Required) The class for which this one is an extension. */
	Class<?> value();

	/** (Optional) The name of the property on the source object which will hold the extension object. */
	String extensionPropertyName() default "extension";
}
