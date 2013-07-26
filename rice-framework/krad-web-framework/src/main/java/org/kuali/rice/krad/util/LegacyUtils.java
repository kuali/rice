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
package org.kuali.rice.krad.util;

import org.kuali.rice.krad.data.DataObjectUtils;
import org.kuali.rice.krad.data.KradDataServiceLocator;
import org.kuali.rice.krad.datadictionary.DocumentEntry;
import org.kuali.rice.krad.service.KRADServiceLocatorWeb;

import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.Callable;

/**
 * Exposes legacy detection functionality statically.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public final class LegacyUtils {

    private static LegacyDetector ld = new LegacyDetector(KradDataServiceLocator.getMetadataRepository(),
            KRADServiceLocatorWeb.getDataDictionaryService());

    private LegacyUtils() {
        throw new UnsupportedOperationException(LegacyUtils.class + " should not be constructed");
    }

    /**
     * Indicates whether the given type is managed by the legacy data framework. Note that it's possible for a given
     * type to managed by both the legacy and the non-legacy data framework, in which case this method will return true
     * as well.
     *
     * @param type the type to check
     * @return true if managed by the legacy framework, false otherwise
     */
    public static boolean isLegacyManaged(Class<?> type) {
        return ld.isLegacyManaged(type);
    }

    /**
     * Indicates whether the given type is managed by the non-legacy data framework. Note that it's possible for a given
     * type to managed by both the legacy and the non-legacy data framework, in which case this method will return true
     * as well.
     *
     * @param type the type to check
     * @return true if managed by the non-legacy krad-data framework, false otherwise
     */
    public static boolean isKradDataManaged(Class<?> type) {
        return ld.isKradDataManaged(type);
    }

    /**
     * Indicates whether or not the given DocumentEntry represents a legacy KNS document entry.
     *
     * @param documentEntry the document entry to check
     * @return true if the given entry is a KNS entry, false otherwise
     */
    public static boolean isKnsDocumentEntry(DocumentEntry documentEntry) {
        return "org.kuali.rice.kns.datadictionary".equals(documentEntry.getClass().getPackage().getName());
    }

    /**
     * Return whether the legacy data framework is enabled
     * @return whether the legacy data framework is enabled
     */
    public static boolean isLegacyDataFrameworkEnabled() {
        return ld.isLegacyDataFrameworkEnabled();
    }

    /**
     * Return whether objects of the given class should be handled via the legacy data framework
     * @param dataObjectClass the data object class
     * @return whether objects of the given class should be handled via the legacy data framework
     */
    public static boolean useLegacy(Class<?> dataObjectClass) {
        return ld.useLegacy(dataObjectClass);
    }

    /**
     * Return whether the object should be handled via the legacy data framework
     * @param dataObject the data object
     * @return whether the object should be handled via the legacy data framework
     */
    public static boolean useLegacyForObject(Object dataObject) {
        return ld.useLegacyForObject(dataObject);
    }

    /**
     * @return whether we are currently in a legacy calling context
     */
    public static boolean isInLegacyContext() {
        return ld.isInLegacyContext();
    }

    public static void beginLegacyContext() {
        ld.beginLegacyContext();
    }

    public static void endLegacyContext() {
        ld.endLegacyContext();
    }

    public static <T> T doInLegacyContext(Callable<T> callable) throws Exception {
        beginLegacyContext();
        try {
            return callable.call();
        } finally {
            endLegacyContext();
        }
    }

}