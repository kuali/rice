package org.kuali.rice.krad.data.provider.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.kuali.rice.krad.data.metadata.MetadataMergeAction;
import org.kuali.rice.krad.data.provider.annotation.impl.AnnotationMetadataProviderImpl;

/**
 * Indicates that, for other annotation metadata on the current class, field, how they should be handled.
 * 
 * In the {@link AnnotationMetadataProviderImpl} implemention, only MERGE and REMOVE are supported.
 */
@Target({ ElementType.TYPE, ElementType.FIELD, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MergeAction {
	MetadataMergeAction value();
}
