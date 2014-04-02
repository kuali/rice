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
package org.kuali.rice.krad.util;

import org.kuali.rice.kew.api.KewApiServiceLocator;
import org.kuali.rice.krad.UserSession;
import org.kuali.rice.krad.data.KradDataServiceLocator;
import org.kuali.rice.krad.datadictionary.DocumentEntry;
import org.kuali.rice.krad.service.KRADServiceLocatorWeb;

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

    public static boolean isKnsEnabled() {
        return ld.isKnsEnabled();
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

    /**
     * Performs the specified {@link Callable} inside of the legacy context.
     *
     * @param callable the method to call inside of the new contexts
     * @param <T> the return type of the callable
     * @return the result of the callable
     * @throws Exception any exception thrown during the execution of the context
     */
    public static <T> T doInLegacyContext(Callable<T> callable) throws Exception {
        beginLegacyContext();
        try {
            return callable.call();
        } finally {
            endLegacyContext();
        }
    }

    /**
     * Performs the specified {@link Callable} inside of both the legacy context (if necessary as dependent on the
     * {@code documentId}) and in new {@link GlobalVariables} as specified by the given {@code userSession}.
     *
     * @param documentId id of the document for which to establish the data context
     * @param userSession the new user session to establish
     * @param callable the method to call inside of the new contexts
     * @param <T> the return type of the callable
     * @return the result of the callable
     * @throws Exception any exception thrown during the execution of the context
     */
    public static <T> T doInLegacyContext(String documentId, UserSession userSession, Callable<T> callable) throws Exception {
        boolean inLegacyContext = establishLegacyDataContextIfNecessary(documentId);
        try {
            return GlobalVariables.doInNewGlobalVariables(userSession, callable);
        } finally {
            clearLegacyDataContextIfExists(inLegacyContext);
        }
    }

    /**
     * Establish a legacy data context if it deems that it's necessary to do so for the document being processed.
     * Unfortunately for us here, there is really no easy way to tell if the original document was submitted from a
     * legacy context (i.e. KNS + OJB) or if it was submitted from a non-legacy context.
     *
     * <p>This is really only a problem for us if the data object happens to be mapped and configured in both the legacy
     * and non-legacy data frameworks (which may be the case while a conversion is in-progress). In the case that the
     * document or the maintainable happens to be configured for both legacy and non-legacy data frameworks, the best we
     * can do is use the non-legacy framework by default. We will, however, ensure that the document entry is not one of
     * the KNS subclasses, as that will tell us that they have, in fact, converted the document over from KNS to KRAD.
     * </p>
     *
     * @param documentId id of the document for which to establish the data context
     * @return true if a legacy data context was established, false otherwise
     */
    private static boolean establishLegacyDataContextIfNecessary(String documentId) {
        String documentTypeName = KewApiServiceLocator.getWorkflowDocumentService().getDocumentTypeName(documentId);
        DocumentEntry documentEntry = KRADServiceLocatorWeb.getDocumentDictionaryService().getDocumentEntry(documentTypeName);

        if (isKnsDocumentEntry(documentEntry)) {
            beginLegacyContext();

            return true;
        }

        return false;
    }

    /**
     * If the given boolean is true, will end any existing legacy data context.
     *
     * @param inLegacyContext whether or not the legacy data context has been established
     */
    private static void clearLegacyDataContextIfExists(boolean inLegacyContext) {
        if (inLegacyContext) {
            endLegacyContext();
        }
    }

}