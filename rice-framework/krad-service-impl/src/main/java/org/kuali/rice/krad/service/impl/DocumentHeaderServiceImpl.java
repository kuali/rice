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
package org.kuali.rice.krad.service.impl;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.kns.service.KNSServiceLocator;
import org.kuali.rice.krad.bo.DocumentHeader;
import org.kuali.rice.krad.service.DocumentHeaderService;
import org.kuali.rice.krad.service.LegacyDataAdapter;
import org.kuali.rice.krad.util.LegacyDataFramework;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.transaction.annotation.Transactional;

/**
 * This is an implementation of {@link DocumentHeaderService} that facilitates
 * document header management and customization
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
@Transactional
@LegacyDataFramework
@Deprecated
public class DocumentHeaderServiceImpl implements DocumentHeaderService {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(DocumentHeaderServiceImpl.class);
    protected LegacyDataAdapter lda;

    @Required
    public void setLegacyDataAdapter(LegacyDataAdapter lda) {
        this.lda = lda;
    }

    /**
     * @see org.kuali.rice.krad.service.DocumentHeaderService#getDocumentHeaderBaseClass()
     * @deprecated Custom document header classes no longer supported as of Rice 2.4.
     */
    @Override
	@Deprecated
    public Class<? extends DocumentHeader> getDocumentHeaderBaseClass() {
        Class documentHeaderClass = lda.getDocumentHeaderBaseClass();
        if ( (documentHeaderClass == null) || (!DocumentHeader.class.isAssignableFrom(documentHeaderClass)) ) {
            throw new RuntimeException("invalid document header base class '" + documentHeaderClass + "' returned by '" + lda.getClass().getName() + "'");
        }
        return documentHeaderClass;
    }

    /**
     * @see org.kuali.rice.krad.service.DocumentHeaderService#getDocumentHeaderById(java.lang.String)
     */
    @Override
	public DocumentHeader getDocumentHeaderById(String documentHeaderId) {
        if (StringUtils.isBlank(documentHeaderId)) {
            throw new IllegalArgumentException("document header id given is blank");
        }
        return lda.getByDocumentHeaderId(documentHeaderId);
    }

    /**
     * @see org.kuali.rice.krad.service.DocumentHeaderService#saveDocumentHeader(org.kuali.rice.krad.bo.DocumentHeader)
     */
    @Override
	public void saveDocumentHeader(DocumentHeader documentHeader) {
        KNSServiceLocator.getBusinessObjectService().save(documentHeader);
    }

    /**
     * @see org.kuali.rice.krad.service.DocumentHeaderService#deleteDocumentHeader(org.kuali.rice.krad.bo.DocumentHeader)
     */
    @Override
	public void deleteDocumentHeader(DocumentHeader documentHeader) {
        KNSServiceLocator.getBusinessObjectService().delete(documentHeader);
    }
}
