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
package org.kuali.rice.kim.document.rule;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.rice.kim.api.type.KimTypeAttribute;
import org.kuali.rice.kim.bo.ui.KimDocumentAttributeDataBusinessObjectBase;
import org.kuali.rice.kim.impl.common.attribute.KimAttributeBo;
import org.kuali.rice.kim.impl.common.attribute.KimAttributeDataBo;
import org.kuali.rice.kim.util.KimConstants;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.service.KRADServiceLocator;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.KRADConstants;
import org.kuali.rice.krad.util.KRADPropertyConstants;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This is a description of what this class does - wliang don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public class AttributeValidationHelper {
	private static final Logger LOG = Logger.getLogger(AttributeValidationHelper.class);
	
	protected BusinessObjectService businessObjectService;
    protected Map<String,KimAttributeBo> attributeDefinitionMap = new HashMap<String,KimAttributeBo>();
    
    protected KimAttributeBo getAttributeDefinition( String id ) {
    	KimAttributeBo attributeImpl = attributeDefinitionMap.get( id );
    	
    	if ( attributeImpl == null ) {
			Map<String,String> criteria = new HashMap<String,String>();
			criteria.put( KimConstants.PrimaryKeyConstants.KIM_ATTRIBUTE_ID, id );
			attributeImpl = (KimAttributeBo)getBusinessObjectService().findByPrimaryKey( KimAttributeBo.class, criteria );
			attributeDefinitionMap.put( id, attributeImpl );
    	}
    	return attributeImpl;
    }
    
	public Map<String, String> convertAttributesToMap(List<? extends KimAttributeDataBo> attributes) {
		Map<String, String> m = new HashMap<String, String>();
		for(KimAttributeDataBo data: attributes) {
			KimAttributeBo attrib = getAttributeDefinition(data.getKimAttributeId());
			if(attrib != null){
				m.put(attrib.getAttributeName(), data.getAttributeValue());
			} else {
				LOG.error("Unable to get attribute name for ID:" + data.getKimAttributeId());
			}
		}
		return m;
	}

	public Map<String, String> convertQualifiersToMap( List<? extends KimDocumentAttributeDataBusinessObjectBase> qualifiers ) {
		Map<String, String> m = new HashMap<String, String>();
		for ( KimDocumentAttributeDataBusinessObjectBase data : qualifiers ) {
			KimAttributeBo attrib = getAttributeDefinition( data.getKimAttrDefnId() );
			if ( attrib != null ) {
				m.put( attrib.getAttributeName(), data.getAttrVal() );
			} else {
				LOG.error("Unable to get attribute name for ID:" + data.getKimAttrDefnId() );
			}
		}
		return m;
	}

	public Map<String, String> getBlankValueQualifiersMap(List<KimTypeAttribute> attributes) {
		Map<String, String> m = new HashMap<String, String>();
		for(KimTypeAttribute attribute: attributes){
			KimAttributeBo attrib = getAttributeDefinition(attribute.getId());
			if ( attrib != null ) {
				m.put( attrib.getAttributeName(), "" );
			} else {
				LOG.error("Unable to get attribute name for ID:" + attribute.getId());
			}
		}
		return m;
	}

	public Map<String, String> convertQualifiersToAttrIdxMap( List<? extends KimDocumentAttributeDataBusinessObjectBase> qualifiers ) {
		Map<String, String> m = new HashMap<String, String>();
		int i = 0;
		for ( KimDocumentAttributeDataBusinessObjectBase data : qualifiers ) {
			KimAttributeBo attrib = getAttributeDefinition( data.getKimAttrDefnId() );
			if ( attrib != null ) {
				m.put( attrib.getAttributeName(), Integer.toString(i) );
			} else {
				LOG.error("Unable to get attribute name for ID:" + data.getKimAttrDefnId() );
			}
			i++;
		}
		return m;
	}
	
	public BusinessObjectService getBusinessObjectService() {
		if(businessObjectService == null){
			businessObjectService = KRADServiceLocator.getBusinessObjectService();
		}
		return businessObjectService;
	}
	
    public void moveValidationErrorsToErrorMap(Map<String, String> validationErrors) {
		// FIXME: This does not use the correct error path yet - may need to be moved up so that the error path is known
		// Also, the above code would overwrite messages on the same attributes (namespaceCode) but on different rows
		for ( String key : validationErrors.keySet() ) {
    		String[] errorMsg = StringUtils.split(validationErrors.get( key ), ":");
    		
			GlobalVariables.getMessageMap().putError( key, errorMsg[0], errorMsg.length > 1 ? StringUtils.split(errorMsg[1], ";") : new String[] {} );
		}
    }

	public Map<String, String> convertErrorsForMappedFields(String errorPath, Map<String, String> localErrors) {
		Map<String, String> errors = new HashMap<String, String>();
		if (errorPath == null) {
			errorPath = KRADConstants.EMPTY_STRING;
		}
		else if (StringUtils.isNotEmpty(errorPath)) {
			errorPath = errorPath + ".";
		}
		for ( String key : localErrors.keySet() ) {
			Map<String,String> criteria = new HashMap<String,String>();
			criteria.put(KRADPropertyConstants.ATTRIBUTE_NAME, key);
			KimAttributeBo attribute = getBusinessObjectService().findByPrimaryKey(KimAttributeBo.class, criteria);
			String attributeDefnId = attribute==null?"":attribute.getId();
			errors.put(errorPath+"qualifier("+attributeDefnId+").attrVal", localErrors.get(key));
		}
		return errors;
	}

	public Map<String, String> convertErrors(String errorPath, Map<String, String> attrIdxMap, Map<String, String> localErrors) {
		Map<String, String> errors = new HashMap<String, String>();
		if (errorPath == null) {
			errorPath = KRADConstants.EMPTY_STRING;
		}
		else if (StringUtils.isNotEmpty(errorPath)) {
			errorPath = errorPath + ".";
		}
		for ( String key : localErrors.keySet() ) {
			errors.put(errorPath+"qualifiers["+attrIdxMap.get(key)+"].attrVal", localErrors.get(key));
		}
		return errors;
	}
}
