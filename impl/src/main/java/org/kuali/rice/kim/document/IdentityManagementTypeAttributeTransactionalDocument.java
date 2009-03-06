/*
 * Copyright 2007 The Kuali Foundation
 *
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl1.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.rice.kim.document;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.kim.bo.impl.KimAttributes;
import org.kuali.rice.kim.bo.types.dto.AttributeDefinitionMap;
import org.kuali.rice.kim.bo.types.impl.KimTypeImpl;
import org.kuali.rice.kim.bo.ui.KimDocumentRoleMember;
import org.kuali.rice.kim.service.KIMServiceLocator;
import org.kuali.rice.kim.service.support.KimTypeService;
import org.kuali.rice.kim.util.KimCommonUtils;
import org.kuali.rice.kim.util.KimConstants;
import org.kuali.rice.kns.datadictionary.AttributeDefinition;
import org.kuali.rice.kns.datadictionary.KimDataDictionaryAttributeDefinition;
import org.kuali.rice.kns.datadictionary.KimNonDataDictionaryAttributeDefinition;
import org.kuali.rice.kns.service.KNSServiceLocator;
import org.kuali.rice.kns.service.SequenceAccessorService;
import org.kuali.rice.kns.util.TypedArrayList;

/**
 * This is a description of what this class does - shyu don't forget to fill
 * this in.
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 * 
 */
public class IdentityManagementTypeAttributeTransactionalDocument extends IdentityManagementKimDocument {

	private transient KimTypeService kimTypeService;
	protected KimTypeImpl kimType = new KimTypeImpl();
	protected List<? extends KimAttributes> attributes;
	
	private transient SequenceAccessorService sequenceAccessorService;
	private transient AttributeDefinitionMap definitions;
	private transient Map<String,Object> attributeEntry;
	
	/**
	 * @return the attributes
	 */
	public List<? extends KimAttributes> getAttributes() {
		return this.attributes;
	}
	/**
	 * @param attributes the attributes to set
	 */
	public void setAttributes(List<? extends KimAttributes> attributes) {
		this.attributes = attributes;
	}
	/**
	 * @return the kimType
	 */
	public KimTypeImpl getKimType() {
		return this.kimType;
	}
	/**
	 * @param kimType the kimType to set
	 */
	public void setKimType(KimTypeImpl kimType) {
		this.kimType = kimType;
	}

	public Map<String,Object> getAttributeEntry() {
		if(attributeEntry==null || attributeEntry.isEmpty())
			attributeEntry = KIMServiceLocator.getUiDocumentService().getAttributeEntries(getDefinitions());
		return attributeEntry;
	}

	public String getCommaDelimitedAttributesLabels(String commaDelimitedAttributesNamesList){
		String[] names = StringUtils.splitByWholeSeparator(commaDelimitedAttributesNamesList, KimConstants.KimUIConstants.COMMA_SEPARATOR);
		StringBuffer commaDelimitedAttributesLabels = new StringBuffer();
		for(String name: names){
			commaDelimitedAttributesLabels.append(getAttributeEntry().get(name.trim())+KimConstants.KimUIConstants.COMMA_SEPARATOR);
		}
        if(commaDelimitedAttributesLabels.toString().endsWith(KimConstants.KimUIConstants.COMMA_SEPARATOR))
        	commaDelimitedAttributesLabels.delete(commaDelimitedAttributesLabels.length()-KimConstants.KimUIConstants.COMMA_SEPARATOR.length(), commaDelimitedAttributesLabels.length());
        return commaDelimitedAttributesLabels.toString();
	}
	
	public AttributeDefinitionMap getDefinitions() {
		if (definitions == null || definitions.isEmpty()) {
	        KimTypeService kimTypeService = getKimTypeService(getKimType());
	        if(kimTypeService!=null)
				setDefinitions(kimTypeService.getAttributeDefinitions(getKimType().getKimTypeId()));
		}
		return this.definitions;
	}

	public void setDefinitions(AttributeDefinitionMap definitions) {
		this.definitions = definitions;
	}

	public KimTypeService getKimTypeService(KimTypeImpl kimType){
		if(this.kimTypeService==null){
	    	this.kimTypeService = KimCommonUtils.getKimTypeService(kimType);
		}
		return this.kimTypeService;
	}

	protected SequenceAccessorService getSequenceAccessorService(){
		if(this.sequenceAccessorService==null){
	    	this.sequenceAccessorService = KNSServiceLocator.getSequenceAccessorService();
		}
		return this.sequenceAccessorService;
	}

    public String getKimAttributeDefnId(AttributeDefinition definition){
    	if (definition instanceof KimDataDictionaryAttributeDefinition) {
    		return ((KimDataDictionaryAttributeDefinition)definition).getKimAttrDefnId();
    	} else {
    		return ((KimNonDataDictionaryAttributeDefinition)definition).getKimAttrDefnId();
    	}
    }
    

}