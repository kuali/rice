/**
 * Copyright 2005-2014 The Kuali Foundation
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl2.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
 * <p>In the {@link AnnotationMetadataProviderImpl} implemention, only MERGE and REMOVE are supported.</p>
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@Target({ ElementType.TYPE, ElementType.FIELD, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MergeAction {

    /**
     * Gets the indicator on how metadata merging should occur.
     *
     * @return the indicator on how metadata merging should occur.
     */
	MetadataMergeAction value();
}
