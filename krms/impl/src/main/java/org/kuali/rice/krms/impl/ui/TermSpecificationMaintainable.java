/**
 * Copyright 2005-2011 The Kuali Foundation
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
package org.kuali.rice.krms.impl.ui;

import org.apache.commons.collections.CollectionUtils;
import org.kuali.rice.core.api.uif.DataType;
import org.kuali.rice.core.api.uif.RemotableAttributeField;
import org.kuali.rice.core.api.uif.RemotableTextInput;
import org.kuali.rice.krad.document.MaintenanceDocument;
import org.kuali.rice.krad.maintenance.MaintainableImpl;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.service.KRADServiceLocator;
import org.kuali.rice.krad.uif.container.CollectionGroup;
import org.kuali.rice.krad.uif.container.Container;
import org.kuali.rice.krad.uif.view.View;
import org.kuali.rice.krad.web.form.MaintenanceForm;
import org.kuali.rice.krms.impl.repository.ContextBo;
import org.kuali.rice.krms.impl.repository.ContextValidTermBo;
import org.kuali.rice.krms.impl.repository.TermBo;
import org.kuali.rice.krms.impl.repository.TermResolverBo;
import org.kuali.rice.krms.impl.repository.TermResolverParameterSpecificationBo;
import org.kuali.rice.krms.impl.repository.TermSpecificationBo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * {@link org.kuali.rice.krad.maintenance.Maintainable} for the {@link AgendaEditor}
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public class TermSpecificationMaintainable extends MaintainableImpl {
	
	private static final long serialVersionUID = 1L;

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(TermSpecificationMaintainable.class);

	/**
	 * @return the boService
	 */
	public BusinessObjectService getBoService() {
		return KRADServiceLocator.getBusinessObjectService();
	}

    @Override
    public Object retrieveObjectForEditOrCopy(MaintenanceDocument document, Map<String, String> dataObjectKeys) {

        TermSpecificationBo termSpecificationBo = (TermSpecificationBo) super.retrieveObjectForEditOrCopy(document,
                dataObjectKeys);

        // find contexts for this term spec
        Collection<ContextValidTermBo> validContextMappings =
                getBoService().findMatching(ContextValidTermBo.class,
                        Collections.singletonMap("termSpecificationId", termSpecificationBo.getId()));

        if (!CollectionUtils.isEmpty(validContextMappings)) for (ContextValidTermBo validContextMapping : validContextMappings) {
            ContextBo context = getBoService().findBySinglePrimaryKey(ContextBo.class, validContextMapping.getContextId());
            termSpecificationBo.getContexts().add(context);
        }

        return termSpecificationBo;
    }

    /**
	 * {@inheritDoc}
	 */
	@Override
	public void processAfterNew(MaintenanceDocument document,
		Map<String, String[]> requestParameters) {

		super.processAfterNew(document, requestParameters);
        document.getDocumentHeader().setDocumentDescription("New Term Specification Document");

	}

    /**
     * {@inheritDoc}
     */
    @Override
    public void processAfterEdit(MaintenanceDocument document, Map<String, String[]> requestParameters) {


        super.processAfterEdit(document,
                requestParameters);

        document.getDocumentHeader().setDocumentDescription("Edited Term Specification Document");
    }

    @Override
    public void saveDataObject() {
        TermSpecificationBo termSpec = (TermSpecificationBo) getDataObject();

        super.saveDataObject();    // save it, it should get an id assigned

        if (termSpec.getId() != null) {
            // clear all context valid term mappings
            getBoService().deleteMatching(ContextValidTermBo.class,
                    Collections.singletonMap("termSpecificationId", termSpec.getId()));

            // add a new mapping for each context in the collection
            if (!CollectionUtils.isEmpty(termSpec.getContexts())) for (ContextBo context : termSpec.getContexts()) {
                ContextValidTermBo contextValidTerm = new ContextValidTermBo();
                contextValidTerm.setContextId(context.getId());
                contextValidTerm.setTermSpecificationId(termSpec.getId());
                getBoService().save(contextValidTerm);
            }
        }

    }

    @Override
    public Class getDataObjectClass() {
        return TermSpecificationBo.class;
    }

    @Override
    protected void processBeforeAddLine(View view, CollectionGroup collectionGroup, Object model, Object addLine) {
        super.processBeforeAddLine(view, collectionGroup, model, addLine);
    }


}