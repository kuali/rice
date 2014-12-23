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
package org.kuali.rice.kns.util;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class KNSConstants {
    public static final String AUDIT_ERRORS = "AuditErrors";
    public static final String EXCEPTION_ON_MISSING_FIELD_CONVERSION_ATTRIBUTE = "rice.kns.exceptionOnMissingFieldConversionAttribute";

    public static final String ZERO = "0";

    public static class HttpHeaderResponse {
        public static final String ATTACHMENT_CONTENT_TYPE = "attachment";
        public static final String INLINE_CONTENT_TYPE = "inline";
        public static final String CONTENT_DIPOSITION = "Content-disposition";
        public static final String EXPIRES = "Expires";
        public static final String CACHE_CONTROL = "Cache-Control";
        public static final String FILENAME = "filename";
        public static final String PRAGMA = "Pragma";
        public static final String CACHE_CONTROL_REVALIDATE_PRE_POST_CHECK_ZERO = "must-revalidate, post-check=0, pre-check=0";
        public static final String PUBLIC = "public";
    }
}
