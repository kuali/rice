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
package org.kuali.rice.kim.lookup;

import static org.kuali.rice.core.api.criteria.PredicateFactory.and;
import static org.kuali.rice.core.api.criteria.PredicateFactory.equal;
import static org.kuali.rice.core.api.criteria.PredicateFactory.in;
import static org.kuali.rice.core.api.criteria.PredicateFactory.like;
import static org.kuali.rice.core.api.criteria.PredicateFactory.likeIgnoreCase;
import static org.kuali.rice.core.api.criteria.PredicateFactory.or;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.kuali.rice.core.api.CoreApiServiceLocator;
import org.kuali.rice.core.api.criteria.Predicate;
import org.kuali.rice.core.api.criteria.QueryByCriteria;
import org.kuali.rice.core.api.membership.MemberType;
import org.kuali.rice.core.api.util.ConcreteKeyValue;
import org.kuali.rice.core.api.util.KeyValue;
import org.kuali.rice.core.api.util.Truth;
import org.kuali.rice.kew.api.KewApiConstants;
import org.kuali.rice.kim.api.KimConstants;
import org.kuali.rice.kim.api.identity.principal.Principal;
import org.kuali.rice.kim.api.permission.Permission;
import org.kuali.rice.kim.api.responsibility.Responsibility;
import org.kuali.rice.kim.api.responsibility.ResponsibilityQueryResults;
import org.kuali.rice.kim.api.role.Role;
import org.kuali.rice.kim.api.role.RoleMember;
import org.kuali.rice.kim.api.services.KimApiServiceLocator;
import org.kuali.rice.kim.api.type.KimAttributeField;
import org.kuali.rice.kim.api.type.KimType;
import org.kuali.rice.kim.impl.KIMPropertyConstants;
import org.kuali.rice.kim.impl.role.RoleBo;
import org.kuali.rice.kim.impl.type.KimTypeBo;
import org.kuali.rice.kim.impl.type.KimTypeLookupableHelperServiceImpl;
import org.kuali.rice.kim.util.KimCommonUtilsInternal;
import org.kuali.rice.kim.web.struts.form.IdentityManagementRoleDocumentForm;
import org.kuali.rice.kns.document.authorization.BusinessObjectRestrictions;
import org.kuali.rice.kns.lookup.HtmlData;
import org.kuali.rice.kns.web.struts.form.KualiForm;
import org.kuali.rice.kns.web.struts.form.LookupForm;
import org.kuali.rice.kns.web.ui.Field;
import org.kuali.rice.kns.web.ui.Row;
import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.rice.krad.datadictionary.BusinessObjectEntry;
import org.kuali.rice.krad.service.KRADServiceLocatorWeb;
import org.kuali.rice.krad.service.ModuleService;
import org.kuali.rice.krad.util.BeanPropertyComparator;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.KRADConstants;
import org.kuali.rice.krad.util.UrlFactory;

/**
 * This is a description of what this class does - shyu don't forget to fill this in.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public class RoleLookupableHelperServiceImpl extends KimLookupableHelperServiceImpl {
    private static final long serialVersionUID = 1L;

    protected static final String GROUP_CRITERIA = "group";
    protected static final String RESPONSIBILITY_CRITERIA = "resp";
    protected static final String PERMISSION_CRITERIA = "perm";
    protected static final String ROLE_MEMBER_ATTRIBUTE_CRITERIA = "attr";
    protected static final String OTHER_CRITERIA = "lookupNames";

    protected static final String LOOKUP_PARM_PERMISSION_TEMPLATE_NAMESPACE = "permTmplNamespaceCode";
    protected static final String LOOKUP_PARM_PERMISSION_TEMPLATE_NAME = "permTmplName";
    protected static final String LOOKUP_PARM_PERMISSION_NAMESPACE = "permNamespaceCode";
    protected static final String LOOKUP_PARM_PERMISSION_NAME = "permName";

    protected static final String LOOKUP_PARM_RESP_TEMPLATE_NAMESPACE = "respTmplNamespaceCode";
    protected static final String LOOKUP_PARM_RESP_TEMPLATE_NAME = "respTmplName";
    protected static final String LOOKUP_PARM_RESP_NAMESPACE = "respNamespaceCode";
    protected static final String LOOKUP_PARM_RESP_NAME = "respName";

	// need this so kimtypeId value can be retained in 'rows'
	// 1st pass populate the grprows
	// 2nd pass for jsp, no populate, so return the existing one.
	private List<Row> roleRows = new ArrayList<Row>();
	private List<Row> attrRows = new ArrayList<Row>();
	private String typeId;
	private List<KimAttributeField> attrDefinitions;

	/**
	 * This overridden method ...
	 *
	 * @see org.kuali.rice.kns.lookup.AbstractLookupableHelperServiceImpl#allowsMaintenanceNewOrCopyAction()
	 */
	@Override
	public boolean allowsMaintenanceNewOrCopyAction() {
        Map<String, String> permissionDetails = new HashMap<String, String>();
        permissionDetails.put(KimConstants.AttributeConstants.DOCUMENT_TYPE_NAME,KimConstants.KimUIConstants.KIM_ROLE_DOCUMENT_TYPE_NAME);
        permissionDetails.put(KRADConstants.MAINTENANCE_ACTN, KRADConstants.MAINTENANCE_NEW_ACTION);

        return !KimApiServiceLocator.getPermissionService().isPermissionDefinedByTemplate(KRADConstants.KNS_NAMESPACE,
                KimConstants.PermissionTemplateNames.CREATE_MAINTAIN_RECORDS, permissionDetails)
                || KimApiServiceLocator.getPermissionService().isAuthorizedByTemplate(GlobalVariables.getUserSession().getPrincipalId(), KRADConstants.KNS_NAMESPACE,
                KimConstants.PermissionTemplateNames.CREATE_MAINTAIN_RECORDS, permissionDetails,
                new HashMap<String, String>());
	}

	/**
	 * This overridden method ...
	 *
	 * @see org.kuali.rice.kns.lookup.AbstractLookupableHelperServiceImpl#allowsMaintenanceEditAction(org.kuali.rice.krad.bo.BusinessObject)
	 */
	@Override
	protected boolean allowsMaintenanceEditAction(BusinessObject businessObject) {
        Map<String, String> permissionDetails = new HashMap<String, String>(2);
        permissionDetails.put(KimConstants.AttributeConstants.DOCUMENT_TYPE_NAME,KimConstants.KimUIConstants.KIM_ROLE_DOCUMENT_TYPE_NAME);
        permissionDetails.put(KRADConstants.MAINTENANCE_ACTN, KRADConstants.MAINTENANCE_EDIT_ACTION);

        return !KimApiServiceLocator.getPermissionService().isPermissionDefinedByTemplate(KRADConstants.KNS_NAMESPACE,
                KimConstants.PermissionTemplateNames.CREATE_MAINTAIN_RECORDS, permissionDetails)
                || KimApiServiceLocator.getPermissionService().isAuthorizedByTemplate(GlobalVariables.getUserSession().getPrincipalId(), KRADConstants.KNS_NAMESPACE,
                KimConstants.PermissionTemplateNames.CREATE_MAINTAIN_RECORDS, permissionDetails,
                new HashMap<String, String>());
	}

    @Override
    public List<HtmlData> getCustomActionUrls(BusinessObject bo, List pkNames) {
    	RoleBo roleBo = (RoleBo) bo;
        List<HtmlData> anchorHtmlDataList = new ArrayList<HtmlData>();
    	if(allowsNewOrCopyAction(KimConstants.KimUIConstants.KIM_ROLE_DOCUMENT_TYPE_NAME)){
    		anchorHtmlDataList.add(getEditRoleUrl(roleBo));
    	}
    	return anchorHtmlDataList;
    }

    protected HtmlData getEditRoleUrl(RoleBo roleBo) {
        Properties parameters = new Properties();
        parameters.put(KRADConstants.DISPATCH_REQUEST_PARAMETER, KRADConstants.DOC_HANDLER_METHOD);
        parameters.put(KRADConstants.PARAMETER_COMMAND, KewApiConstants.INITIATE_COMMAND);
        parameters.put(KRADConstants.DOCUMENT_TYPE_NAME, KimConstants.KimUIConstants.KIM_ROLE_DOCUMENT_TYPE_NAME);
        parameters.put(KimConstants.PrimaryKeyConstants.SUB_ROLE_ID, roleBo.getId());
        if (StringUtils.isNotBlank(getReturnLocation())) {
        	parameters.put(KRADConstants.RETURN_LOCATION_PARAMETER, getReturnLocation());
		}
        String href = UrlFactory.parameterizeUrl(KimCommonUtilsInternal.getKimBasePath()+KimConstants.KimUIConstants.KIM_ROLE_DOCUMENT_ACTION, parameters);

        HtmlData.AnchorHtmlData anchorHtmlData = new HtmlData.AnchorHtmlData(href,
        		KRADConstants.DOC_HANDLER_METHOD, KRADConstants.MAINTENANCE_EDIT_METHOD_TO_CALL);
        return anchorHtmlData;
    }

    @Override
	protected HtmlData getReturnAnchorHtmlData(BusinessObject businessObject, Properties parameters, LookupForm lookupForm, List returnKeys, BusinessObjectRestrictions businessObjectRestrictions){
    	RoleBo roleBo = (RoleBo) businessObject;
    	HtmlData anchorHtmlData = super.getReturnAnchorHtmlData(businessObject, parameters, lookupForm, returnKeys, businessObjectRestrictions);

    	// prevent derived roles from being selectable (except for identityManagementRoleDocuments)
    	KualiForm myForm = (KualiForm) GlobalVariables.getUserSession().retrieveObject(getDocFormKey());
    	if (myForm == null || !(myForm instanceof IdentityManagementRoleDocumentForm)){
    		if(KimTypeLookupableHelperServiceImpl.hasDerivedRoleTypeService(KimTypeBo.to(roleBo.getKimRoleType()))){
    			((HtmlData.AnchorHtmlData)anchorHtmlData).setHref("");
    		}
    	}
    	return anchorHtmlData;
    }

    @Override
    public List<? extends BusinessObject> getSearchResults(java.util.Map<String,String> fieldValues) {
        fieldValues.remove(KRADConstants.BACK_LOCATION);
        fieldValues.remove(KRADConstants.DOC_FORM_KEY);
        fieldValues.remove(KRADConstants.DOC_NUM);

        QueryByCriteria criteria = getRoleCriteria(fieldValues);

        List<Role> results = KimApiServiceLocator.getRoleService().findRoles(criteria).getResults();
        List<RoleBo> roles = new ArrayList<RoleBo>(results.size());
        for ( Role role : results ) {
            roles.add( RoleBo.from(role) );
        }

        return roles;
    }

	private List<KeyValue> getRoleTypeOptions() {
		List<KeyValue> options = new ArrayList<KeyValue>();
		options.add(new ConcreteKeyValue("", ""));

		Collection<KimType> kimGroupTypes = KimApiServiceLocator.getKimTypeInfoService().findAllKimTypes();
		// get the distinct list of type IDs from all roles in the system
        for (KimType kimType : kimGroupTypes) {
            if (KimTypeLookupableHelperServiceImpl.hasRoleTypeService(kimType)) {
                String value = kimType.getNamespaceCode().trim() + KRADConstants.FIELD_CONVERSION_PAIR_SEPARATOR + kimType.getName().trim();
                options.add(new ConcreteKeyValue(kimType.getId(), value));
            }
        }
        Collections.sort(options, new Comparator<KeyValue>() {
           @Override
		public int compare(KeyValue k1, KeyValue k2) {
               return k1.getValue().compareTo(k2.getValue());
           }
        });
		return options;
	}

	public List<Row> getRoleRows() {
		return this.roleRows;
	}

	public void setRoleRows(List<Row> roleRows) {
		this.roleRows = roleRows;
	}

	public List<KimAttributeField> getAttrDefinitions() {
		return this.attrDefinitions;
	}

	public void setAttrDefinitions(List<KimAttributeField> attrDefinitions) {
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
		new ArrayList<Row>();
		if (getRoleRows().isEmpty()) {
			List<Row> rows = super.getRows();
			List<Row> returnRows = new ArrayList<Row>();
			for (Row row : rows) {
				for (int i = row.getFields().size() - 1; i >= 0; i--) {
					Field field = row.getFields().get(i);
					if (field.getPropertyName().equals("kimTypeId")) {
						Field typeField = new Field();
						typeField.setFieldLabel("Type");
						typeField.setPropertyName("kimTypeId");
						typeField.setFieldValidValues(getRoleTypeOptions());
						typeField.setFieldType(Field.DROPDOWN);
						typeField.setMaxLength(100);
						typeField.setSize(40);
						// row.getFields().set(i, new Field("Type", "", Field.DROPDOWN_REFRESH,
						// false, "kimTypeId", "", getGroupTypeOptions(), null));
						row.getFields().set(i, typeField);
					}
				}
				returnRows.add(row);
			}
			setRoleRows(returnRows);
			//setAttrRows(setupAttributeRows());
		}
		if (getAttrRows().isEmpty()) {
			//setAttrDefinitions(new AttributeDefinitionMap());
			return getRoleRows();
		} else {
			List<Row> fullRows = new ArrayList<Row>();
			fullRows.addAll(getRoleRows());
			//fullRows.addAll(getAttrRows());
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
        	ModuleService eboModuleService = KRADServiceLocatorWeb.getKualiModuleService().getResponsibleModuleService( getBusinessObjectClass() );
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

	public static String getCustomRoleInquiryHref(String href){
		return getCustomRoleInquiryHref("", href);
	}

	static String getCustomRoleInquiryHref(String backLocation, String href){
        Properties parameters = new Properties();
        String hrefPart = "";
    	String docTypeAction = "";
    	if(StringUtils.isBlank(backLocation) || backLocation.contains(KimConstants.KimUIConstants.KIM_ROLE_DOCUMENT_ACTION)
    			|| !backLocation.contains(KimConstants.KimUIConstants.KIM_GROUP_DOCUMENT_ACTION)){
    		docTypeAction = KimConstants.KimUIConstants.KIM_ROLE_INQUIRY_ACTION;
    	} else{
    		docTypeAction = KimConstants.KimUIConstants.KIM_GROUP_DOCUMENT_ACTION;
    	}
		if (StringUtils.isNotBlank(href) && href.contains(ROLE_ID_URL_KEY)) {
			int idx1 = href.indexOf("&"+ KimConstants.PrimaryKeyConstants.ROLE_ID+"=");
		    int idx2 = href.indexOf("&", idx1+1);
		    if (idx2 < 0) {
		    	idx2 = href.length();
		    }
	        parameters.put(KRADConstants.DISPATCH_REQUEST_PARAMETER, KRADConstants.PARAM_MAINTENANCE_VIEW_MODE_INQUIRY);
	        hrefPart = href.substring(idx1, idx2);
	    }
		return UrlFactory.parameterizeUrl(KimCommonUtilsInternal.getKimBasePath()+docTypeAction, parameters)+hrefPart;
	}

    public QueryByCriteria getRoleCriteria(Map<String, String> fieldValues) {
        List<Predicate> criteria = new ArrayList<Predicate>();

        Map<String, Map<String, String>> criteriaMap = setupCritMaps(fieldValues);

        Map<String, String> lookupNames = criteriaMap.get(OTHER_CRITERIA);
        for (Map.Entry<String, String> entry : lookupNames.entrySet()) {
            String propertyName = entry.getKey();
            String lookupValue = entry.getValue();
            if (StringUtils.isNotBlank(lookupValue)) {
                if (!propertyName.equals(KIMPropertyConstants.Principal.PRINCIPAL_NAME)) {
                    if (propertyName.equals(KIMPropertyConstants.Principal.ACTIVE)) {
                        criteria.add( equal( propertyName, Truth.strToBooleanIgnoreCase(lookupValue) ) );
                    } else {
                        criteria.add( likeIgnoreCase(propertyName, lookupValue));
                    }
                } else {
                    Collection<String> roleIds = getRoleIdsForPrincipalName(lookupValue);
                    criteria.add( in(KimConstants.PrimaryKeyConstants.ID, roleIds) );
                }
            }
        }

//        if (!criteriaMap.get(ROLE_MEMBER_ATTRIBUTE_CRITERIA).isEmpty()) {
//            String kimTypeId = null;
//            for (Map.Entry<String, String> entry : fieldValues.entrySet()) {
//                if (entry.getKey().equals(KIMPropertyConstants.KimType.KIM_TYPE_ID)) {
//                    kimTypeId = entry.getValue();
//                    break;
//                }
//            }
//            setupAttrCriteria(criteria, criteriaMap.get(ROLE_MEMBER_ATTRIBUTE_CRITERIA), kimTypeId);
//        }
        if (!criteriaMap.get(PERMISSION_CRITERIA).isEmpty()) {
            criteria.add( in(KimConstants.PrimaryKeyConstants.ID, getPermissionRoleIds(criteriaMap.get(PERMISSION_CRITERIA))) );
        }
        if (!criteriaMap.get(RESPONSIBILITY_CRITERIA).isEmpty()) {
            criteria.add( in(KimConstants.PrimaryKeyConstants.ID, getResponsibilityRoleIds(criteriaMap.get(RESPONSIBILITY_CRITERIA))) );
        }
        if (!criteriaMap.get(GROUP_CRITERIA).isEmpty()) {
            criteria.add( in(KimConstants.PrimaryKeyConstants.ID, getGroupCriteriaRoleIds(criteriaMap.get(GROUP_CRITERIA))) );
        }

        return QueryByCriteria.Builder.fromPredicates(criteria);
    }

    protected Collection<String> getRoleIdsForPrincipalName(String principalName) {
        principalName = principalName.replace('*', '%');

        QueryByCriteria principalCriteria = QueryByCriteria.Builder.fromPredicates(
                likeIgnoreCase(KIMPropertyConstants.Principal.PRINCIPAL_NAME, principalName)
                , equal(KIMPropertyConstants.Principal.ACTIVE, Boolean.TRUE)
                );
        List<Principal> principals = KimApiServiceLocator.getIdentityService().findPrincipals(principalCriteria).getResults();

        if (principals.isEmpty()) {
            return Collections.singletonList("NOTFOUND");  // this forces a blank return.
        }
        Set<String> roleIds = new HashSet<String>();

        // Get matching principal IDs
        List<String> principalIds = new ArrayList<String>(principals.size());
        for (Principal principal : principals) {
            principalIds.add(principal.getPrincipalId());
        }

        // Get groups which the principals belong to
        Set<String> groupIds = new HashSet<String>();
        for (String principalId : principalIds) {
            List<String> principalGroupIds = KimApiServiceLocator.getGroupService().getGroupIdsByPrincipalId(principalId);
            if ( principalGroupIds.isEmpty() ) {
                groupIds.add( "NOTFOUND" );
            } else {
                groupIds.addAll(principalGroupIds);
            }
        }

        // Get roles to which this person has been added directly or via a group
        QueryByCriteria roleMemberCriteria = QueryByCriteria.Builder.fromPredicates(
                or(
                    and(
                            equal(KIMPropertyConstants.RoleMember.MEMBER_TYPE_CODE, MemberType.PRINCIPAL.getCode())
                            , in(KIMPropertyConstants.RoleMember.MEMBER_ID, principalIds)
                        ),
                    and(
                        equal(KIMPropertyConstants.RoleMember.MEMBER_TYPE_CODE, MemberType.GROUP.getCode())
                        , in(KIMPropertyConstants.RoleMember.MEMBER_ID, groupIds)
                        )
                    )
                );

        List<RoleMember> roleMembers = KimApiServiceLocator.getRoleService().findRoleMembers(roleMemberCriteria).getResults();

        DateTime now = new DateTime( CoreApiServiceLocator.getDateTimeService().getCurrentDate().getTime() );
        for (RoleMember roleMbr : roleMembers ) {
            if (roleMbr.isActive( now ) ) {
                roleIds.add(roleMbr.getRoleId());
            }
        }
        if (roleIds.isEmpty()) {
            return Collections.singletonList("NOTFOUND");  // this forces a blank return.
        }

        return roleIds;
    }

    protected static List<String> PERM_FIELD_NAMES;
    protected static List<String> RESP_FIELD_NAMES;
    static {
        PERM_FIELD_NAMES = new ArrayList<String>(4);
        PERM_FIELD_NAMES.add(LOOKUP_PARM_PERMISSION_NAME);
        PERM_FIELD_NAMES.add(LOOKUP_PARM_PERMISSION_NAMESPACE);
        PERM_FIELD_NAMES.add(LOOKUP_PARM_PERMISSION_TEMPLATE_NAME);
        PERM_FIELD_NAMES.add(LOOKUP_PARM_PERMISSION_TEMPLATE_NAMESPACE);

        RESP_FIELD_NAMES = new ArrayList<String>(4);
        RESP_FIELD_NAMES.add(LOOKUP_PARM_RESP_NAME);
        RESP_FIELD_NAMES.add(LOOKUP_PARM_RESP_NAMESPACE);
        RESP_FIELD_NAMES.add(LOOKUP_PARM_RESP_TEMPLATE_NAME);
        RESP_FIELD_NAMES.add(LOOKUP_PARM_RESP_TEMPLATE_NAMESPACE);
    }

    private Map<String, Map<String, String>> setupCritMaps(Map<String, String> fieldValues) {
        Map<String, Map<String, String>> critMapMap = new HashMap<String, Map<String, String>>();

        Map<String, String> permFieldMap = new HashMap<String, String>();
        Map<String, String> respFieldMap = new HashMap<String, String>();
//        Map<String, String> attrFieldMap = new HashMap<String, String>();
        Map<String, String> groupFieldMap = new HashMap<String, String>();
        Map<String, String> lookupNamesMap = new HashMap<String, String>();

        for (Map.Entry<String, String> entry : fieldValues.entrySet()) {
            String nameValue = entry.getValue();
            String propertyName = entry.getKey();
            if (StringUtils.isNotBlank(nameValue)) {
                if (PERM_FIELD_NAMES.contains(propertyName)) {
                    permFieldMap.put(propertyName, nameValue);
                } else if (RESP_FIELD_NAMES.contains(propertyName)) {
                    respFieldMap.put(propertyName, nameValue);
                } else if (propertyName.startsWith(KimConstants.AttributeConstants.GROUP_NAME)) {
                    groupFieldMap.put(propertyName, nameValue);
//                } else if (entry.getKey().contains(".")) {
//                    attrFieldMap.put(entry.getKey(), nameValue).replace('*', '%');
                } else {
                    lookupNamesMap.put(propertyName, nameValue);
                }
            }
        }

        critMapMap.put(PERMISSION_CRITERIA, permFieldMap);
        critMapMap.put(RESPONSIBILITY_CRITERIA, respFieldMap);
        critMapMap.put(GROUP_CRITERIA, groupFieldMap);
//        critMap.put(ROLE_MEMBER_ATTRIBUTE_CRITERIA, attrFieldMap);
        critMapMap.put(OTHER_CRITERIA, lookupNamesMap);

        return critMapMap;
    }

//    private void setupAttrCriteria(Criteria crit, Map<String, String> attrCrit, String kimTypeId) {
//        for (Map.Entry<String, String> entry : attrCrit.entrySet()) {
//            Criteria subCrit = new Criteria();
//            addLikeToCriteria(subCrit, "attributes.attributeValue", entry.getValue());
//            addEqualToCriteria(subCrit, "attributes.kimAttributeId", entry.getKey().substring(entry.getKey().indexOf(".") + 1, entry.getKey().length()));
//            addEqualToCriteria(subCrit, "attributes.kimTypeId", kimTypeId);
//            subCrit.addEqualToField("roleId", Criteria.PARENT_QUERY_PREFIX + "id");
//            crit.addExists(QueryFactory.newReportQuery(RoleMemberBo.class, subCrit));
//        }
//    }

    protected Collection<String> getPermissionRoleIds(Map<String, String> permCrit) {
        List<Predicate> criteria = new ArrayList<Predicate>();

        for (Map.Entry<String, String> entry : permCrit.entrySet()) {
            if ( StringUtils.isNotBlank(entry.getValue()) ) {
                String propertyName = entry.getKey();
                String lookupValue = entry.getValue().replace('*', '%');
                if ( propertyName.equals(LOOKUP_PARM_PERMISSION_NAME) ) {
                    criteria.add( likeIgnoreCase(KimConstants.UniqueKeyConstants.PERMISSION_NAME, lookupValue) );
                } else if ( propertyName.equals(LOOKUP_PARM_PERMISSION_NAMESPACE) ) {
                    criteria.add( like(KimConstants.UniqueKeyConstants.NAMESPACE_CODE, lookupValue) );
                } else if ( propertyName.equals(LOOKUP_PARM_PERMISSION_TEMPLATE_NAME) ) {
                    criteria.add( likeIgnoreCase("template." + KimConstants.UniqueKeyConstants.PERMISSION_TEMPLATE_NAME, lookupValue) );
                } else if ( propertyName.equals(LOOKUP_PARM_PERMISSION_TEMPLATE_NAMESPACE) ){
                    criteria.add( like("template." + KimConstants.UniqueKeyConstants.NAMESPACE_CODE, lookupValue) );
                }
            }
        }
        if(criteria.isEmpty()){
            return Collections.singletonList("NOTFOUND");  // this forces a blank return.
        }

        List<Permission> permissions = KimApiServiceLocator.getPermissionService().findPermissions( QueryByCriteria.Builder.fromPredicates(criteria) ).getResults();
        Set<String> roleIds = new HashSet<String>();

        for ( Permission permission : permissions ) {
            roleIds.addAll( KimApiServiceLocator.getPermissionService().getRoleIdsForPermission(permission.getNamespaceCode(), permission.getName()) );
        }

        if (roleIds.isEmpty()) {
            roleIds.add("NOTFOUND"); // this forces a blank return.
        }

        return roleIds;
    }

    protected Collection<String> getResponsibilityRoleIds(Map<String, String> respCrit) {
        List<Predicate> criteria = new ArrayList<Predicate>();

        for (Map.Entry<String, String> entry : respCrit.entrySet()) {
            if ( StringUtils.isNotBlank(entry.getValue()) ) {
                String propertyName = entry.getKey();
                String lookupValue = entry.getValue().replace('*', '%');
                if (propertyName.equals(LOOKUP_PARM_RESP_NAME) ) {
                    criteria.add( likeIgnoreCase(KimConstants.UniqueKeyConstants.RESPONSIBILITY_NAME, lookupValue));
                } else if ( propertyName.equals(LOOKUP_PARM_RESP_NAMESPACE) ) {
                    criteria.add( like(KimConstants.UniqueKeyConstants.NAMESPACE_CODE, lookupValue));
                } else if (propertyName.equals(LOOKUP_PARM_RESP_TEMPLATE_NAME)) {
                    criteria.add( likeIgnoreCase("template." + KimConstants.UniqueKeyConstants.RESPONSIBILITY_TEMPLATE_NAME, lookupValue));
                } else if ( propertyName.equals(LOOKUP_PARM_RESP_TEMPLATE_NAMESPACE) ){
                    criteria.add( like("template." + KimConstants.UniqueKeyConstants.NAMESPACE_CODE, lookupValue));
                }
            }
        }
        if(criteria.isEmpty()){
            return Collections.singletonList("NOTFOUND");  // this forces a blank return.
        }

        ResponsibilityQueryResults results = KimApiServiceLocator.getResponsibilityService().findResponsibilities(QueryByCriteria.Builder.fromPredicates(criteria) );
        List<Responsibility> responsibilities = results.getResults();

        Set<String> roleIds = new HashSet<String>();
        for (Responsibility responsibility : responsibilities) {
            roleIds.addAll(KimApiServiceLocator.getResponsibilityService().getRoleIdsForResponsibility(responsibility.getId()));
        }

        if (roleIds.isEmpty()) {
            roleIds.add("NOTFOUND"); // this forces a blank return.
        }

        return roleIds;
    }

    protected Collection<String> getGroupCriteriaRoleIds(Map<String,String> groupCrit) {
        List<Predicate> criteria = new ArrayList<Predicate>();

        for (Entry<String, String> entry : groupCrit.entrySet()) {
            if ( StringUtils.isNotBlank(entry.getValue()) ) {
                String propertyName = entry.getKey();
                String lookupValue = entry.getValue().replace('*', '%');
                if (propertyName.equals(KimConstants.AttributeConstants.GROUP_NAME)) {
                    criteria.add( likeIgnoreCase(KimConstants.AttributeConstants.NAME, lookupValue));
                } else { // the namespace code for the group field is named something besides the default. Set it to the default.
                    criteria.add( like(KimConstants.AttributeConstants.NAMESPACE_CODE, lookupValue));
                }
            }
       }
        if(criteria.isEmpty()){
            return Collections.singletonList("NOTFOUND");  // this forces a blank return.
        }

       List<String> groupIds = KimApiServiceLocator.getGroupService().findGroupIds(QueryByCriteria.Builder.fromPredicates(criteria));

       if(groupIds.isEmpty()){
           return Collections.singletonList("NOTFOUND");  // this forces a blank return.
       }

       // Get roles to which this person has been added directly or via a group
       QueryByCriteria roleMemberCriteria = QueryByCriteria.Builder.fromPredicates(
               equal(KIMPropertyConstants.RoleMember.MEMBER_TYPE_CODE, MemberType.GROUP.getCode())
               , in(KIMPropertyConstants.RoleMember.MEMBER_ID, groupIds)
               );

       List<RoleMember> roleMembers = KimApiServiceLocator.getRoleService().findRoleMembers(roleMemberCriteria).getResults();

       Set<String> roleIds = new HashSet<String>();
       DateTime now = new DateTime( CoreApiServiceLocator.getDateTimeService().getCurrentDate().getTime() );
       for (RoleMember roleMbr : roleMembers ) {
           if (roleMbr.isActive( now ) ) {
               roleIds.add(roleMbr.getRoleId());
           }
       }

       return roleIds;
    }

    /**
     * This overridden method ...
     *
     * @see org.kuali.rice.kns.lookup.AbstractLookupableHelperServiceImpl#getMaintenanceDocumentTypeName()
     */
    @Override
    protected String getMaintenanceDocumentTypeName() {
        return KimConstants.KimUIConstants.KIM_ROLE_DOCUMENT_TYPE_NAME;
    }
}
