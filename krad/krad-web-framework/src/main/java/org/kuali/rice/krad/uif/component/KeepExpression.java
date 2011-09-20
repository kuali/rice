package org.kuali.rice.krad.uif.component;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation that can be used on a {@link Configurable} field to specify any expressions configured for
 * that field should not be picked up and processed as normal, but left as the property value to be handled
 * by the component itself or some other process
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface KeepExpression {

}
