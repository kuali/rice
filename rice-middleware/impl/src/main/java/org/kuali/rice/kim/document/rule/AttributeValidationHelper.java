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
package org.kuali.rice.kim.document.rule;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.rice.core.api.criteria.QueryByCriteria;
import org.kuali.rice.core.api.uif.RemotableAttributeError;
import org.kuali.rice.core.impl.services.CoreImplServiceLocator;
import org.kuali.rice.kim.api.common.attribute.KimAttribute;
import org.kuali.rice.kim.api.type.KimTypeAttribute;
import org.kuali.rice.kim.bo.ui.KimDocumentAttributeDataBusinessObjectBase;
import org.kuali.rice.kim.impl.common.attribute.KimAttributeBo;
import org.kuali.rice.kim.impl.common.attribute.KimAttributeDataBo;
import org.kuali.rice.krad.data.KradDataServiceLocator;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.KRADConstants;
import org.kuali.rice.krad.util.KRADPropertyConstants;
import org.springframework.cache.Cache;
import org.springframework.cache.Cache.ValueWrapper;
import org.springframework.cache.CacheManager;

/**
 * This is a description of what this class does - wliang don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public class AttributeValidationHelper {
	private static final Logger LOG = Logger.getLogger(AttributeValidationHelper.class);

    private static final String DOCUMENT_PROPERTY_PREFIX = KRADConstants.DOCUMENT_PROPERTY_NAME + ".";

//    protected Map<String,KimAttribute> attributeDefinitionMap = new HashMap<String,KimAttribute>();
    
    
    
    protected KimAttribute getAttributeDefinitionById( String id ) {
        CacheManager cm = CoreImplServiceLocator.getCacheManagerRegistry().getCacheManagerByCacheName(KimAttribute.Cache.NAME);
        Cache cache = cm.getCache(KimAttribute.Cache.NAME);
        String cacheKey = "id=" + id;
        ValueWrapper valueWrapper = cache.get( cacheKey );
        
        if ( valueWrapper != null ) {
            return (KimAttribute) valueWrapper.get();
        }

		KimAttributeBo attributeImpl = KradDataServiceLocator.getDataObjectService().find( KimAttributeBo.class, id );
		KimAttribute attribute = KimAttributeBo.to(attributeImpl);
		cache.put( cacheKey, attribute );

    	return attribute;
    }

    protected KimAttribute getAttributeDefinitionByName( String attributeName ) {
        CacheManager cm = CoreImplServiceLocator.getCacheManagerRegistry().getCacheManagerByCacheName(KimAttribute.Cache.NAME);
        Cache cache = cm.getCache(KimAttribute.Cache.NAME);
        String cacheKey = "name=" + attributeName;
        ValueWrapper valueWrapper = cache.get( cacheKey );
        
        if ( valueWrapper != null ) {
            return (KimAttribute) valueWrapper.get();
        }

        List<KimAttributeBo> attributeImpls = KradDataServiceLocator.getDataObjectService().findMatching( KimAttributeBo.class, QueryByCriteria.Builder.forAttribute(KRADPropertyConstants.ATTRIBUTE_NAME, attributeName).build()).getResults();
        KimAttribute attribute = null;
        if ( !attributeImpls.isEmpty() ) {
            attribute = KimAttributeBo.to(attributeImpls.get(0)); 
        }
        
        cache.put( cacheKey, attribute );

        return attribute;
    }
    
	public Map<String, String> convertAttributesToMap(List<? extends KimAttributeDataBo> attributes) {
		Map<String, String> m = new HashMap<String, String>();
		for(KimAttributeDataBo data: attributes) {
			KimAttribute attrib = getAttributeDefinitionById(data.getKimAttributeId());
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
			KimAttribute attrib = getAttributeDefinitionById( data.getKimAttrDefnId() );
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
   			KimAttribute attrib = getAttributeDefinitionById(attribute.getKimAttribute().getId());
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
			KimAttribute attrib = getAttributeDefinitionById( data.getKimAttrDefnId() );
			if ( attrib != null ) {
				m.put( attrib.getAttributeName(), Integer.toString(i) );
			} else {
				LOG.error("Unable to get attribute name for ID:" + data.getKimAttrDefnId() );
			}
			i++;
		}
		return m;
	}
	
    public void moveValidationErrorsToErrorMap(List<RemotableAttributeError> validationErrors) {
		// FIXME: the above code would overwrite messages on the same attributes (namespaceCode) but on different rows
		for ( RemotableAttributeError error : validationErrors) {
    		for (String errMsg : error.getErrors()) {
                String[] splitMsg = StringUtils.split(errMsg, ":");

                // if the property name starts with "document." then don't prefix with the error path
                if (error.getAttributeName().startsWith(DOCUMENT_PROPERTY_PREFIX)) {
                    GlobalVariables.getMessageMap().putErrorWithoutFullErrorPath( error.getAttributeName(), splitMsg[0], splitMsg.length > 1 ? StringUtils.split(splitMsg[1], ";") : new String[] {} );
                } else {
                    GlobalVariables.getMessageMap().putError( error.getAttributeName(), splitMsg[0], splitMsg.length > 1 ? StringUtils.split(splitMsg[1], ";") : new String[] {} );
                }
            }
		}
    }

	public List<RemotableAttributeError> convertErrorsForMappedFields(String errorPath, List<RemotableAttributeError> localErrors) {
		List<RemotableAttributeError> errors = new ArrayList<RemotableAttributeError>();
		if (errorPath == null) {
			errorPath = KRADConstants.EMPTY_STRING;
		}
		else if (StringUtils.isNotEmpty(errorPath)) {
			errorPath = errorPath + ".";
		}
		for ( RemotableAttributeError error : localErrors) {
			KimAttribute attribute = getAttributeDefinitionByName(error.getAttributeName());
			String attributeDefnId = attribute==null?"":attribute.getId();
			errors.add(RemotableAttributeError.Builder.create(errorPath+"qualifier("+attributeDefnId+").attrVal", error.getErrors()).build());
		}
		return errors;
	}

	public List<RemotableAttributeError> convertErrors(String errorPath, Map<String, String> attrIdxMap, List<RemotableAttributeError> localErrors) {
		List<RemotableAttributeError> errors = new ArrayList<RemotableAttributeError>();
		if (errorPath == null) {
			errorPath = KRADConstants.EMPTY_STRING;
		}
		else if (StringUtils.isNotEmpty(errorPath)) {
			errorPath = errorPath + ".";
		}
		for ( RemotableAttributeError error : localErrors ) {
			for (String errMsg : error.getErrors()) {
                errors.add(RemotableAttributeError.Builder.create(errorPath+"qualifiers["+attrIdxMap.get(error.getAttributeName())+"].attrVal", errMsg).build());
            }
		}
		return errors;
	}
}
