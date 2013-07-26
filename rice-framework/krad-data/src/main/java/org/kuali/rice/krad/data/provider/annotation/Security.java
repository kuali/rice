/**
 * Copyright 2005-2013 The Kuali Foundation
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

import org.kuali.rice.krad.data.metadata.DataObjectAttributeSecurity;
import org.kuali.rice.krad.data.metadata.impl.security.DataObjectAttributeMaskFormatter;

/**
 * Allows you to define {@link DataObjectAttributeSecurity} for a given property.
 */
@Target({ ElementType.FIELD, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Security {
	boolean readOnly() default false;

	boolean mask() default false;

	boolean partialMask() default false;

	boolean hidden() default false;

	Class<? extends DataObjectAttributeMaskFormatter> maskFormatter() default NullMaskFormatter.class;

	Class<? extends DataObjectAttributeMaskFormatter> partialMaskFormatter() default NullMaskFormatter.class;

	static final class NullMaskFormatter implements DataObjectAttributeMaskFormatter {
		private static final long serialVersionUID = 1L;
		@Override
		public String maskValue(Object value) {
			return null;
		}
	}
}
