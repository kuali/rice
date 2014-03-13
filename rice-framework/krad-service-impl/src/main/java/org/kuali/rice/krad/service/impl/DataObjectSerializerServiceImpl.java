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
package org.kuali.rice.krad.service.impl;

import org.kuali.rice.krad.data.provider.annotation.SerializationContext;
import org.kuali.rice.krad.data.provider.annotation.SerializationContext;
import org.kuali.rice.krad.service.BusinessObjectSerializerService;
import org.kuali.rice.krad.service.DocumentDictionaryService;
import org.kuali.rice.krad.service.KRADServiceLocatorWeb;
import org.kuali.rice.krad.util.documentserializer.AlwaysTruePropertySerializibilityEvaluator;
import org.kuali.rice.krad.util.documentserializer.DataObjectPropertySerializabilityEvaluator;
import org.kuali.rice.krad.util.documentserializer.PropertySerializabilityEvaluator;
import org.kuali.rice.krad.util.documentserializer.SerializationState;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class DataObjectSerializerServiceImpl extends SerializerServiceBase implements BusinessObjectSerializerService {

    private DocumentDictionaryService documentDictionaryService;

    /**
     * @see org.kuali.rice.krad.service.DocumentSerializerService#serializeDocumentToXmlForRouting(org.kuali.rice.krad.document.Document)
     */
    public String serializeBusinessObjectToXml(Object businessObject) {
        PropertySerializabilityEvaluator propertySerizabilityEvaluator =
                getPropertySerizabilityEvaluator(businessObject);
        evaluators.set(propertySerizabilityEvaluator);
        SerializationState state = new SerializationState(); //createNewDocumentSerializationState(document);
        serializationStates.set(state);

        //Object xmlWrapper = null;//wrapDocumentWithMetadata(document);
        String xml;
        if (propertySerizabilityEvaluator instanceof AlwaysTruePropertySerializibilityEvaluator) {
            xml = getXmlObjectSerializerService().toXml(businessObject);
        } else {
            xml = xstream.toXML(businessObject);
        }

        evaluators.set(null);
        serializationStates.set(null);
        return xml;
    }

    public PropertySerializabilityEvaluator getPropertySerizabilityEvaluator(Object businessObject) {
        PropertySerializabilityEvaluator evaluator = new DataObjectPropertySerializabilityEvaluator(
                SerializationContext.MAINTENANCE);

        return evaluator;
    }

    protected DocumentDictionaryService getDocumentDictionaryService() {
        if (documentDictionaryService == null) {
            this.documentDictionaryService = KRADServiceLocatorWeb.getDocumentDictionaryService();
        }
        return documentDictionaryService;
    }

    public void setDocumentDictionaryService(DocumentDictionaryService documentDictionaryService) {
        this.documentDictionaryService = documentDictionaryService;
    }
}
