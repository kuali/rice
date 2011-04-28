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
package org.kuali.rice.kim.document;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.rice.core.util.AttributeSet;
import org.kuali.rice.kew.util.KEWConstants;
import org.kuali.rice.kim.api.services.KimApiServiceLocator;
import org.kuali.rice.kim.bo.impl.ResponsibilityImpl;
import org.kuali.rice.kim.bo.impl.ReviewResponsibility;
import org.kuali.rice.kim.bo.role.dto.KimResponsibilityTemplateInfo;
import org.kuali.rice.kim.bo.role.impl.KimResponsibilityImpl;
import org.kuali.rice.kim.util.KimConstants;
import org.kuali.rice.kns.bo.BusinessObject;
import org.kuali.rice.kns.bo.PersistableBusinessObject;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.kns.maintenance.KualiMaintainableImpl;
import org.kuali.rice.kns.maintenance.Maintainable;
import org.kuali.rice.kns.web.ui.Field;
import org.kuali.rice.kns.web.ui.Row;
import org.kuali.rice.kns.web.ui.Section;

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

	protected static String reviewTemplateId = null;

    public List getSections(MaintenanceDocument document, Maintainable oldMaintainable) {
        List<Section> sections = super.getSections(document, oldMaintainable);
        if(document.isEdit()){
        	for (Section section : sections) {
                for (Row row : section.getRows()) {
                    for (Field field : row.getFields()) {
                    	if(ReviewResponsibility.ACTION_DETAILS_AT_ROLE_MEMBER_LEVEL_FIELD_NAME.equals(field.getPropertyName())){
                    		field.setReadOnly(true);
                    	}
                    }
                }
        	}
        }
        return sections;
    }
                
	/**
	 * Saves the responsibility via the responsibility update service
	 * 
	 * @see org.kuali.rice.kns.maintenance.KualiMaintainableImpl#saveBusinessObject()
	 */
	@Override
	public void saveBusinessObject() {
		try {
			if ( LOG.isInfoEnabled() ) {
				LOG.info( "Attempting to save ReviewResponsibility BO via ResponsibilityUpdateService:" + getBusinessObject() );
			}
			// find the template ID if needed
			if ( reviewTemplateId == null ) {
				populateReviewTemplateInfo();
			}
			ReviewResponsibility resp = (ReviewResponsibility)getBusinessObject();
			// build the AttributeSet with the details
			AttributeSet details = new AttributeSet();
			details.put( KimConstants.AttributeConstants.DOCUMENT_TYPE_NAME, resp.getDocumentTypeName() );
			details.put( KimConstants.AttributeConstants.ROUTE_NODE_NAME, resp.getRouteNodeName() );
			details.put( KimConstants.AttributeConstants.REQUIRED, resp.isRequired()?"true":"false" );
			details.put( KimConstants.AttributeConstants.ACTION_DETAILS_AT_ROLE_MEMBER_LEVEL, resp.isActionDetailsAtRoleMemberLevel()?"true":"false" );
			if ( StringUtils.isNotBlank(resp.getQualifierResolverProvidedIdentifier()) ) {
				details.put( KimConstants.AttributeConstants.QUALIFIER_RESOLVER_PROVIDED_IDENTIFIER, resp.getQualifierResolverProvidedIdentifier() );
			}
			
			KimApiServiceLocator.getResponsibilityUpdateService().saveResponsibility( resp.getResponsibilityId(),
					reviewTemplateId,
					resp.getNamespaceCode(), 
					resp.getName(), 
					resp.getDescription(), 
					resp.isActive(), 
					details );
		} catch ( RuntimeException ex ) {
			LOG.error( "Exception in saveBusinessObject()", ex );
			throw ex;
		}
	}
	
	protected void populateReviewTemplateInfo() {
		KimResponsibilityTemplateInfo template = KimApiServiceLocator.getResponsibilityService().getResponsibilityTemplateByName( KEWConstants.KEW_NAMESPACE, KEWConstants.DEFAULT_RESPONSIBILITY_TEMPLATE_NAME);
		
		reviewTemplateId = template.getResponsibilityTemplateId();
	}
	
	/**
	 * This overridden method ...
	 * 
	 * @see org.kuali.rice.kns.maintenance.KualiMaintainableImpl#getBoClass()
	 */
	@Override
	public Class<? extends PersistableBusinessObject> getBoClass() {
		return ReviewResponsibility.class;
	}
	
	/**
	 * This overridden method ...
	 * 
	 * @see org.kuali.rice.kns.maintenance.KualiMaintainableImpl#isExternalBusinessObject()
	 */
	@Override
	public boolean isExternalBusinessObject() {
		return true;
	}
	
	/**
	 * This overridden method ...
	 * 
	 * @see org.kuali.rice.kns.maintenance.KualiMaintainableImpl#prepareBusinessObject(org.kuali.rice.kns.bo.BusinessObject)
	 */
	@Override
	public void prepareBusinessObject(BusinessObject businessObject) {
		try {
			if ( businessObject == null ) {
				throw new RuntimeException( "Configuration ERROR: ReviewResponsibilityMaintainable.prepareBusinessObject passed a null object." );
			}
			if ( businessObject instanceof ResponsibilityImpl ) {
				KimResponsibilityImpl resp = getBusinessObjectService().findBySinglePrimaryKey(KimResponsibilityImpl.class, ((ResponsibilityImpl)businessObject).getResponsibilityId() );
				businessObject = new ReviewResponsibility( resp );
			} else if ( businessObject instanceof ReviewResponsibility ) {
				// lookup the KimResponsibilityImpl and convert to a ReviewResponsibility
				KimResponsibilityImpl resp = getBusinessObjectService().findBySinglePrimaryKey(KimResponsibilityImpl.class, ((ReviewResponsibility)businessObject).getResponsibilityId() );		
				((ReviewResponsibility)businessObject).loadFromKimResponsibility(resp);
			} else {
				throw new RuntimeException( "Configuration ERROR: ReviewResponsibilityMaintainable passed an unsupported object type: " + businessObject.getClass() );
			}
			if ( businessObject instanceof PersistableBusinessObject ) {
				setBusinessObject( (PersistableBusinessObject)businessObject );
			}
			super.prepareBusinessObject(businessObject);
		} catch ( RuntimeException ex ) {
			LOG.error( "Exception in prepareBusinessObject()", ex );
			throw ex;
		}
	}
	
}
