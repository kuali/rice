/*
 * Copyright 2007-2009 The Kuali Foundation
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
package org.kuali.rice.kim.impl.responsibility;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.rice.core.util.AttributeSet;
import org.kuali.rice.kew.util.KEWConstants;
import org.kuali.rice.kim.api.common.template.Template;
import org.kuali.rice.kim.api.responsibility.Responsibility;
import org.kuali.rice.kim.api.services.KimApiServiceLocator;
import org.kuali.rice.kim.util.KimConstants;
import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.rice.krad.bo.PersistableBusinessObject;
import org.kuali.rice.krad.document.MaintenanceDocument;
import org.kuali.rice.krad.maintenance.KualiMaintainableImpl;
import org.kuali.rice.krad.maintenance.Maintainable;
import org.kuali.rice.krad.web.ui.Field;
import org.kuali.rice.krad.web.ui.Row;
import org.kuali.rice.krad.web.ui.Section;

import java.util.List;

/**
 * This is a description of what this class does - jonathan don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public class ReviewResponsibilityMaintainable extends KualiMaintainableImpl {

	private static final Logger LOG = Logger.getLogger( ReviewResponsibilityMaintainable.class );
	private static final long serialVersionUID = -8102504656976243468L;

	private static Template REVIEW_TEMPLATE;

    public List getSections(MaintenanceDocument document, Maintainable oldMaintainable) {
        List<Section> sections = super.getSections(document, oldMaintainable);
        if(document.isEdit()){
        	for (Section section : sections) {
                for (Row row : section.getRows()) {
                    for (Field field : row.getFields()) {
                    	if(ReviewResponsibilityBo.ACTION_DETAILS_AT_ROLE_MEMBER_LEVEL_FIELD_NAME.equals(field.getPropertyName())){
                    		field.setReadOnly(true);
                    	}
                    }
                }
        	}
        }
        return sections;
    }
                
	/**
	 * Saves the responsibility via the responsibility service
	 * 
	 * @see org.kuali.rice.krad.maintenance.KualiMaintainableImpl#saveBusinessObject()
	 */
	@Override
	public void saveBusinessObject() {
        if ( LOG.isInfoEnabled() ) {
            LOG.info( "Attempting to save ReviewResponsibilityBo BO via ResponsibilityService:" + getBusinessObject() );
        }
        populateReviewTemplateInfo();

        ReviewResponsibilityBo resp = (ReviewResponsibilityBo)getBusinessObject();
        // build the AttributeSet with the details
        AttributeSet details = new AttributeSet();
        details.put( KimConstants.AttributeConstants.DOCUMENT_TYPE_NAME, resp.getDocumentTypeName() );
        details.put( KimConstants.AttributeConstants.ROUTE_NODE_NAME, resp.getRouteNodeName() );
        details.put( KimConstants.AttributeConstants.REQUIRED, resp.isRequired()?"true":"false" );
        details.put( KimConstants.AttributeConstants.ACTION_DETAILS_AT_ROLE_MEMBER_LEVEL, resp.isActionDetailsAtRoleMemberLevel()?"true":"false" );
        if ( StringUtils.isNotBlank(resp.getQualifierResolverProvidedIdentifier()) ) {
            details.put( KimConstants.AttributeConstants.QUALIFIER_RESOLVER_PROVIDED_IDENTIFIER, resp.getQualifierResolverProvidedIdentifier() );
        }

        Responsibility.Builder b = Responsibility.Builder.create(resp.getNamespaceCode(), resp.getName(), Template.Builder.create(REVIEW_TEMPLATE));
        b.setDescription(resp.getDescription());
        b.setAttributes(resp.getAttributes());
        b.setActive(resp.isActive());

        KimApiServiceLocator.getResponsibilityService().createResponsibility(b.build());
	}
	
	private static synchronized void populateReviewTemplateInfo() {
		if ( REVIEW_TEMPLATE == null ) {
            List<Template> template = KimApiServiceLocator.getResponsibilityService().findRespTemplatesByNamespaceCodeAndName(KEWConstants.KEW_NAMESPACE, KEWConstants.DEFAULT_RESPONSIBILITY_TEMPLATE_NAME);
		
		    REVIEW_TEMPLATE = template.get(0);
        }
	}

	@Override
	public Class<? extends BusinessObject> getBoClass() {
		return ReviewResponsibilityBo.class;
	}

	@Override
	public void prepareBusinessObject(BusinessObject businessObject) {
        if ( businessObject == null ) {
            throw new RuntimeException( "Configuration ERROR: ReviewResponsibilityBoMaintainable.prepareBusinessObject passed a null object." );
        }
        if ( businessObject instanceof ResponsibilityBo ) {
            ResponsibilityBo resp = getBusinessObjectService().findBySinglePrimaryKey(ResponsibilityBo.class, ((ResponsibilityBo)businessObject).getId() );
            businessObject = new ReviewResponsibilityBo( resp );
            setBusinessObject( (PersistableBusinessObject)businessObject );
        } else {
            throw new RuntimeException( "Configuration ERROR: ReviewResponsibilityBoMaintainable passed an unsupported object type: " + businessObject.getClass() );
        }
        super.prepareBusinessObject(businessObject);
	}
	
}
