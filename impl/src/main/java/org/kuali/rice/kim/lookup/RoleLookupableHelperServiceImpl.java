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
package org.kuali.rice.kim.lookup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.kuali.rice.kew.util.KEWConstants;
import org.kuali.rice.kim.bo.impl.RoleImpl;
import org.kuali.rice.kim.bo.types.dto.AttributeDefinitionMap;
import org.kuali.rice.kim.bo.types.impl.KimTypeImpl;
import org.kuali.rice.kim.dao.KimRoleDao;
import org.kuali.rice.kim.service.support.KimTypeService;
import org.kuali.rice.kim.util.KimCommonUtils;
import org.kuali.rice.kim.util.KimConstants;
import org.kuali.rice.kns.authorization.BusinessObjectRestrictions;
import org.kuali.rice.kns.bo.BusinessObject;
import org.kuali.rice.kns.datadictionary.AttributeDefinition;
import org.kuali.rice.kns.datadictionary.BusinessObjectEntry;
import org.kuali.rice.kns.datadictionary.KimDataDictionaryAttributeDefinition;
import org.kuali.rice.kns.datadictionary.KimNonDataDictionaryAttributeDefinition;
import org.kuali.rice.kns.lookup.HtmlData;
import org.kuali.rice.kns.lookup.KualiLookupableHelperServiceImpl;
import org.kuali.rice.kns.lookup.HtmlData.AnchorHtmlData;
import org.kuali.rice.kns.lookup.keyvalues.KeyValuesFinder;
import org.kuali.rice.kns.service.KNSServiceLocator;
import org.kuali.rice.kns.service.ModuleService;
import org.kuali.rice.kns.util.BeanPropertyComparator;
import org.kuali.rice.kns.util.KNSConstants;
import org.kuali.rice.kns.util.UrlFactory;
import org.kuali.rice.kns.web.struts.form.LookupForm;
import org.kuali.rice.kns.web.ui.Field;
import org.kuali.rice.kns.web.ui.KeyLabelPair;
import org.kuali.rice.kns.web.ui.Row;

/**
 * This is a description of what this class does - shyu don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public class RoleLookupableHelperServiceImpl extends KualiLookupableHelperServiceImpl {

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(RoleLookupableHelperServiceImpl.class);

	// need this so kimtypeId value can be retained in 'rows'
	// 1st pass populate the grprows
	// 2nd pass for jsp, no populate, so return the existing one. 
	private List<Row> roleRows = new ArrayList<Row>();
	private List<Row> attrRows = new ArrayList<Row>();
	private KimRoleDao roleDao; 
	private String typeId;
	private AttributeDefinitionMap attrDefinitions;
	
    @Override
    public List<HtmlData> getCustomActionUrls(BusinessObject bo, List pkNames) {
    	RoleImpl roleImpl = (RoleImpl) bo;
        List<HtmlData> anchorHtmlDataList = new ArrayList<HtmlData>();
    	anchorHtmlDataList.add(getEditRoleUrl(roleImpl));	
    	return anchorHtmlDataList;
    }
    
    protected HtmlData getEditRoleUrl(RoleImpl roleImpl) {
    	String href = "";
    	if(!KimTypeLookupableHelperServiceImpl.hasDerivedRoleTypeService(roleImpl.getKimRoleType())){
	        Properties parameters = new Properties();
	        parameters.put(KNSConstants.DISPATCH_REQUEST_PARAMETER, KNSConstants.DOC_HANDLER_METHOD);
	        parameters.put(KNSConstants.PARAMETER_COMMAND, KEWConstants.INITIATE_COMMAND);
	        parameters.put(KNSConstants.DOCUMENT_TYPE_NAME, KimConstants.KimUIConstants.KIM_ROLE_DOCUMENT_TYPE_NAME);
	        parameters.put(KimConstants.PrimaryKeyConstants.ROLE_ID, roleImpl.getRoleId());
	        href = UrlFactory.parameterizeUrl(KimCommonUtils.getKimBasePath()+KimConstants.KimUIConstants.KIM_ROLE_DOCUMENT_ACTION, parameters);
    	}        
        AnchorHtmlData anchorHtmlData = new AnchorHtmlData(href, 
        		KNSConstants.DOC_HANDLER_METHOD, KNSConstants.MAINTENANCE_EDIT_METHOD_TO_CALL);
        //anchorHtmlData.setTarget("blank");
        return anchorHtmlData;
    }

    protected HtmlData getReturnAnchorHtmlData(BusinessObject businessObject, Properties parameters, LookupForm lookupForm, List returnKeys, BusinessObjectRestrictions businessObjectRestrictions){
    	RoleImpl roleImpl = (RoleImpl) businessObject;
    	HtmlData anchorHtmlData = super.getReturnAnchorHtmlData(businessObject, parameters, lookupForm, returnKeys, businessObjectRestrictions);
    	if(KimTypeLookupableHelperServiceImpl.hasDerivedRoleTypeService(roleImpl.getKimRoleType())){
    		((AnchorHtmlData)anchorHtmlData).setHref("");
    	}
    	return anchorHtmlData;
    }
    
    @Override
    public List<? extends BusinessObject> getSearchResults(java.util.Map<String,String> fieldValues) {
//    	String principalName = fieldValues.get("principalName");
//    	fieldValues.put("principalName","");
        String kimTypeId = null;
        for (Map.Entry<String,String> entry : fieldValues.entrySet()) {
        	if (entry.getKey().equals(KimConstants.PrimaryKeyConstants.KIM_TYPE_ID)) {
        		kimTypeId=entry.getValue();
        		break;
        	}
        }
  //  	List<RoleImpl> roles = roleDao.getRoles(fieldValues, kimTypeId);
        List<RoleImpl> baseLookup = (List<RoleImpl>)super.getSearchResults(fieldValues);

        return baseLookup;
    }

	@SuppressWarnings("unchecked")
	private List<KeyLabelPair> getRoleTypeOptions() {
		List<KeyLabelPair> options = new ArrayList<KeyLabelPair>();
		options.add(new KeyLabelPair("", ""));

		List<KimTypeImpl> kimGroupTypes = (List<KimTypeImpl>)getBusinessObjectService().findAll(KimTypeImpl.class);
		// get the distinct list of type IDs from all roles in the system
        for (KimTypeImpl kimType : kimGroupTypes) {
            if (KimTypeLookupableHelperServiceImpl.hasRoleTypeService(kimType)) {
                String value = kimType.getNamespaceCode().trim() + KNSConstants.FIELD_CONVERSION_PAIR_SEPARATOR + kimType.getName().trim();
                options.add(new KeyLabelPair(kimType.getKimTypeId(), value));
            }
        }
        Collections.sort(options, new Comparator<KeyLabelPair>() {
           public int compare(KeyLabelPair k1, KeyLabelPair k2) {
               return k1.getLabel().compareTo(k2.getLabel());
           }
        });
		return options;
	}

	private List<Row> setupAttributeRows() {
		List<Row> returnRows = new ArrayList<Row>();
		for (Row row : getRoleRows()) {
			Field field = (Field) row.getFields().get(0);
			if (field.getPropertyName().equals("kimTypeId") && StringUtils.isNotBlank(field.getPropertyValue())) {
				if (StringUtils.isBlank(getTypeId()) || !getTypeId().equals(field.getPropertyValue())) {
					setTypeId(field.getPropertyValue());
					setAttrRows(new ArrayList<Row>());
										
					Map<String,Object> pkMap = new HashMap<String,Object>();
					pkMap.put("kimTypeId", field.getPropertyValue());
					KimTypeImpl kimType = (KimTypeImpl)getBusinessObjectService().findByPrimaryKey(KimTypeImpl.class, pkMap);
					// TODO what if servicename is null.  also check other places that have similar issue
					// use default_service ?
			        KimTypeService kimTypeService = KimCommonUtils.getKimTypeService(kimType);
			        if ( kimTypeService != null ) {
				        AttributeDefinitionMap definitions = kimTypeService.getAttributeDefinitions(kimType.getKimTypeId());
				        setAttrDefinitions(definitions);
				        if(definitions!=null){
				            for ( AttributeDefinition definition : definitions.values()) {
						        List<Field> fields = new ArrayList<Field>();
								Field typeField = new Field();
								//String attrDefnId = mapEntry.getKey().substring(mapEntry.getKey().indexOf("."), mapEntry.getKey().length());
		//						String attrDefnId = definition.getId();
								String attrDefnId = getAttrDefnId(definition);
								// if it is DD, then attrDefn.getLabel() is null; has to get from DDAttrdefn
								typeField.setFieldLabel(definition.getLabel());
								// with suffix  in case name is the same as bo property 
								typeField.setPropertyName(definition.getName()+"."+attrDefnId);
								if (definition.getControl().isSelect()) {
							        try {
							            KeyValuesFinder finder = (KeyValuesFinder) definition.getControl().getValuesFinderClass().newInstance();
								        typeField.setFieldValidValues(finder.getKeyValues());
								        typeField.setFieldType(Field.DROPDOWN);
							        }
							        catch (InstantiationException e) {
							            throw new RuntimeException(e.getMessage());
							        }
							        catch (IllegalAccessException e) {
							            throw new RuntimeException(e.getMessage());
							        }
								} else {
									typeField.setMaxLength(definition.getMaxLength());
									typeField.setSize(definition.getControl().getSize());
									typeField.setFieldType(Field.TEXT);
								}
								fields.add(typeField);
								returnRows.add(new Row(fields));
				            }
				        }
		            }
				} else {
					return getAttrRows();
				}
			} else if (field.getPropertyName().equals("kimTypeId") && StringUtils.isBlank(field.getPropertyValue())) {
				setTypeId(""); 
			}
		}
		return returnRows;

	}
    private String getAttrDefnId(AttributeDefinition definition) {
    	if (definition instanceof KimDataDictionaryAttributeDefinition) {
    		return ((KimDataDictionaryAttributeDefinition)definition).getKimAttrDefnId();
    	} else {
    		return ((KimNonDataDictionaryAttributeDefinition)definition).getKimAttrDefnId();

    	}
    }
	
	public List<Row> getRoleRows() {
		return this.roleRows;
	}

	public void setRoleRows(List<Row> roleRows) {
		this.roleRows = roleRows;
	}

	public KimRoleDao getRoleDao() {
		return this.roleDao;
	}

	public void setRoleDao(KimRoleDao roleDao) {
		this.roleDao = roleDao;
	}

	public AttributeDefinitionMap getAttrDefinitions() {
		return this.attrDefinitions;
	}

	public void setAttrDefinitions(AttributeDefinitionMap attrDefinitions) {
		this.attrDefinitions = attrDefinitions;
	}

	public List<Row> getAttrRows() {
		return this.attrRows;
	}

	public void setAttrRows(List<Row> attrRows) {
		this.attrRows = attrRows;
	}

	public String getTypeId() {
		return this.typeId;
	}

	public void setTypeId(String typeId) {
		this.typeId = typeId;
	}

	@Override
	public List<Row> getRows() {
		List<Row> attributeRows = new ArrayList<Row>();
		if (getRoleRows().isEmpty()) {
			List<Row> rows = super.getRows();
			List<Row> returnRows = new ArrayList<Row>();
			for (Row row : rows) {
				Field field = (Field) row.getFields().get(0);
				if (field.getPropertyName().equals("kimTypeId")) {
					List<Field> fields = new ArrayList<Field>();
					Field typeField = new Field();
					typeField.setFieldLabel("Type");
					typeField.setPropertyName("kimTypeId");
					typeField.setFieldValidValues(getRoleTypeOptions());
					typeField.setFieldType(Field.DROPDOWN_REFRESH);
					typeField.setMaxLength(100);
					typeField.setSize(40);
					fields.add(typeField);
					// fields.add(new Field("Type", "", Field.DROPDOWN_REFRESH,
					// false, "kimTypeId", "", getGroupTypeOptions(), null));
					returnRows.add(new Row(fields));

				} else {
					returnRows.add(row);
				}
			}
			setRoleRows(returnRows);
			setAttrRows(setupAttributeRows());
		} else {			 
			attributeRows = setupAttributeRows();
			if (attributeRows.isEmpty()) {
				setAttrRows(attributeRows);				
			} else if (CollectionUtils.isEmpty(getAttrRows())) {
				setAttrRows(attributeRows);				
			}
		}
		if (getAttrRows().isEmpty()) {
			setAttrDefinitions(new AttributeDefinitionMap());
			return getRoleRows();
		} else {
			List<Row> fullRows = new ArrayList<Row>();
			fullRows.addAll(getRoleRows());
			fullRows.addAll(getAttrRows());
			return fullRows;
		}
		
	}

	@Override
	protected List<? extends BusinessObject> getSearchResultsHelper(
			Map<String, String> fieldValues, boolean unbounded) {
        List searchResults;
    	Map<String,String> nonBlankFieldValues = new HashMap<String, String>();
    	boolean includeAttr = false;
    	for (String fieldName : fieldValues.keySet()) {
    		if (StringUtils.isNotBlank(fieldValues.get(fieldName)) ) {
    			nonBlankFieldValues.put(fieldName, fieldValues.get(fieldName));
    			if (fieldName.contains(".")) {
    				includeAttr = true;
    			}
    		}
    	}

    	if (includeAttr) {
        	ModuleService eboModuleService = KNSServiceLocator.getKualiModuleService().getResponsibleModuleService( getBusinessObjectClass() );
        	BusinessObjectEntry ddEntry = eboModuleService.getExternalizableBusinessObjectDictionaryEntry(getBusinessObjectClass());
        	Map<String,String> filteredFieldValues = new HashMap<String, String>();
        	for (String fieldName : nonBlankFieldValues.keySet()) {
        		if (ddEntry.getAttributeNames().contains(fieldName) || fieldName.contains(".")) {
        			filteredFieldValues.put(fieldName, nonBlankFieldValues.get(fieldName));
        		}
        	}
        	searchResults = eboModuleService.getExternalizableBusinessObjectsListForLookup(getBusinessObjectClass(), (Map)filteredFieldValues, unbounded);

    	} else {
    		searchResults = super.getSearchResultsHelper(fieldValues, unbounded);
    	}
        List defaultSortColumns = getDefaultSortColumns();
        if (defaultSortColumns.size() > 0) {
            Collections.sort(searchResults, new BeanPropertyComparator(defaultSortColumns, true));
        }
        return searchResults;

	}
	
	private static final String ROLE_ID_URL_KEY = "&"+KimConstants.PrimaryKeyConstants.ROLE_ID+"=";
	/**
	 * @see org.kuali.rice.kns.lookup.AbstractLookupableHelperServiceImpl#getInquiryUrl(org.kuali.rice.kns.bo.BusinessObject, java.lang.String)
	 */
	@Override
	public HtmlData getInquiryUrl(BusinessObject bo, String propertyName) {
		AnchorHtmlData inquiryHtmlData = (AnchorHtmlData)super.getInquiryUrl(bo, propertyName);
		if(inquiryHtmlData!=null && StringUtils.isNotBlank(inquiryHtmlData.getHref()) && inquiryHtmlData.getHref().contains(ROLE_ID_URL_KEY))
			inquiryHtmlData.setHref(getCustomRoleInquiryHref(getBackLocation(), inquiryHtmlData.getHref()));
		return inquiryHtmlData;
	}

	static String getCustomRoleInquiryHref(String href){
		return getCustomRoleInquiryHref("", href);
	}
	
	static String getCustomRoleInquiryHref(String backLocation, String href){
        Properties parameters = new Properties();
        String hrefPart = "";
    	String docTypeName = "";
    	String docTypeAction = "";
    	if(StringUtils.isBlank(backLocation) || backLocation.contains(KimConstants.KimUIConstants.KIM_ROLE_DOCUMENT_ACTION)
    			|| !backLocation.contains(KimConstants.KimUIConstants.KIM_GROUP_DOCUMENT_ACTION)){
    		docTypeName = KimConstants.KimUIConstants.KIM_ROLE_DOCUMENT_TYPE_NAME;
    		docTypeAction = KimConstants.KimUIConstants.KIM_ROLE_DOCUMENT_ACTION;
    	} else{
    		docTypeName = KimConstants.KimUIConstants.KIM_GROUP_DOCUMENT_TYPE_NAME;
    		docTypeAction = KimConstants.KimUIConstants.KIM_GROUP_DOCUMENT_ACTION;
    	}
		if (StringUtils.isNotBlank(href) && href.indexOf(ROLE_ID_URL_KEY)!=-1) {
			int idx1 = href.indexOf("&"+KimConstants.PrimaryKeyConstants.ROLE_ID+"=");
		    int idx2 = href.indexOf("&", idx1+1);
		    if (idx2 < 0) {
		    	idx2 = href.length();
		    }
	        parameters.put(KNSConstants.DISPATCH_REQUEST_PARAMETER, KNSConstants.PARAM_MAINTENANCE_VIEW_MODE_INQUIRY);
	        parameters.put(KEWConstants.COMMAND_PARAMETER, KEWConstants.INITIATE_COMMAND);
	        parameters.put(KNSConstants.DOCUMENT_TYPE_NAME, docTypeName);
	        hrefPart = href.substring(idx1, idx2);
	    }
		return UrlFactory.parameterizeUrl(KimCommonUtils.getKimBasePath()+docTypeAction, parameters)+hrefPart;
	}

} 