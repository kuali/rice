/*
 * Copyright 2007-2008 The Kuali Foundation
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
package org.kuali.rice.kim.bo.role.impl;

import org.apache.log4j.Logger;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.Type;
import org.kuali.rice.core.api.mo.common.Attributes;
import org.kuali.rice.core.util.AttributeSet;
import org.kuali.rice.kim.api.common.template.Template;
import org.kuali.rice.kim.api.permission.Permission;
import org.kuali.rice.kim.api.services.KimApiServiceLocator;
import org.kuali.rice.kim.api.type.KimType;
import org.kuali.rice.kim.api.type.KimTypeAttribute;
import org.kuali.rice.kim.api.type.KimTypeInfoService;
import org.kuali.rice.kim.bo.role.KimPermission;
import org.kuali.rice.kim.impl.permission.PermissionTemplateBo;
import org.kuali.rice.kim.impl.role.RolePermissionBo;
import org.kuali.rice.kim.util.KimConstants;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;
import org.kuali.rice.krad.service.DataDictionaryService;
import org.kuali.rice.krad.service.KRADServiceLocatorWeb;
import org.springframework.util.AutoPopulatingList;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.util.Iterator;
import java.util.List;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@SuppressWarnings("unchecked")
@Entity
@Table(name="KRIM_PERM_T")
public class KimPermissionImpl extends PersistableBusinessObjectBase implements KimPermission {
	private static final Logger LOG = Logger.getLogger(KimPermissionImpl.class);	
	
	private static final long serialVersionUID = 1L;
	
	@Id
	@Column(name="PERM_ID")
	protected String permissionId;
	@Column(name="NMSPC_CD")
	protected String namespaceCode;
	@Column(name="NM")
	protected String name;
	@Column(name="DESC_TXT", length=400)
	protected String description;
	@Type(type="yes_no")
	@Column(name="ACTV_IND")
	protected boolean active;
	
	@OneToMany(targetEntity=PermissionAttributeDataImpl.class,cascade={CascadeType.ALL},fetch=FetchType.EAGER)
	@Fetch(value = FetchMode.SELECT)
	@JoinColumn(name="PERM_ID", insertable=false, updatable=false)
	protected List<PermissionAttributeDataImpl> detailObjects = new AutoPopulatingList(PermissionAttributeDataImpl.class);

	@Column(name="PERM_TMPL_ID")
	protected String templateId;
	
	@OneToOne(targetEntity=KimPermissionTemplateImpl.class,cascade={},fetch=FetchType.EAGER)
    @JoinColumn(name="PERM_TMPL_ID", insertable=false, updatable=false)
	protected PermissionTemplateBo template;

	@OneToMany(targetEntity=RolePermissionBo.class,cascade={CascadeType.ALL},fetch=FetchType.EAGER)
    @Fetch(value = FetchMode.SELECT)
	@JoinColumn(name="PERM_ID", insertable=false, updatable=false)
	protected List<RolePermissionBo> rolePermissions = new AutoPopulatingList(RolePermissionBo.class);

	/**
	 * @see org.kuali.rice.krad.bo.Inactivatable#isActive()
	 */
	public boolean isActive() {
		return active;
	}

	/**
	 * @see org.kuali.rice.krad.bo.Inactivatable#setActive(boolean)
	 */
	public void setActive(boolean active) {
		this.active = active;
	}

	/**
	 * @see org.kuali.rice.kim.bo.role.KimPermission#getDescription()
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @see org.kuali.rice.kim.bo.role.KimPermission#getPermissionId()
	 */
	public String getPermissionId() {
		return permissionId;
	}

	/**
	 * @see org.kuali.rice.kim.bo.role.KimPermission#getName()
	 */
	public String getName() {
		return name;
	}

	public void setDescription(String permissionDescription) {
		this.description = permissionDescription;
	}

	public void setName(String permissionName) {
		this.name = permissionName;
	}

	public Permission toSimpleInfo() {
		Permission.Builder builder = Permission.Builder.create(getNamespaceCode(),getName(), Template.Builder.create(getTemplate()));
        builder.setId(getPermissionId());
        builder.setDescription(getDescription());
        builder.setActive(isActive());
		return builder.build();
	}
	
	public List<PermissionAttributeDataImpl> getDetailObjects() {
		return this.detailObjects;
	}

	public void setDetails(List<PermissionAttributeDataImpl> detailObjects) {
		this.detailObjects = detailObjects;
	}
	
	public PermissionTemplateBo getTemplate() {
		return this.template;
	}

	public void setTemplate(PermissionTemplateBo template) {
		this.template = template;
	}

	public String getTemplateId() {
		return this.templateId;
	}

	public void setTemplateId(String templateId) {
		this.templateId = templateId;
	}

	protected transient AttributeSet detailsAsAttributeSet = null;

	public Attributes getDetails() {
		if ( detailsAsAttributeSet == null ) {
			KimType kimType = getTypeInfoService().getKimType( getTemplate().getKimTypeId() );
			AttributeSet m = new AttributeSet();
			for ( PermissionAttributeDataImpl data : getDetailObjects() ) {
				KimTypeAttribute attribute = null;
				if ( kimType != null ) {
					attribute = kimType.getAttributeDefinitionById( data.getKimAttributeId() );
				} else {
					LOG.warn( "Unable to get KimTypeInfo for permission: " + this + "\nKim Type ID: " + getTemplate().getKimTypeId() );
				}
				if ( attribute != null ) {
					m.put( attribute.getKimAttribute().getAttributeName(), data.getAttributeValue() );
				} else {
					LOG.warn( "Unable to get attribute for ID: " + data.getKimAttributeId() + " from KimTypeInfo: " + kimType );
					m.put( data.getKimAttribute().getAttributeName(), data.getAttributeValue() );
				}
			}
			detailsAsAttributeSet = m;
		}
		return Attributes.fromMap(detailsAsAttributeSet);
	}
	
	public boolean hasDetails() {
		return !getDetailObjects().isEmpty();
	}

	public String getNamespaceCode() {
		return this.namespaceCode;
	}

	public void setNamespaceCode(String namespaceCode) {
		this.namespaceCode = namespaceCode;
	}

	public void setPermissionId(String permissionId) {
		this.permissionId = permissionId;
	}

	public void setDetailObjects(List<PermissionAttributeDataImpl> detailObjects) {
		this.detailObjects = detailObjects;
	}

	/**
	 * @return the rolePermissions
	 */
	public List<RolePermissionBo> getRolePermissions() {
		return this.rolePermissions;
	}

	/**
	 * @param rolePermissions the rolePermissions to set
	 */
	public void setRolePermissions(List<RolePermissionBo> rolePermissions) {
		this.rolePermissions = rolePermissions;
	}
	
	public String getDetailObjectsValues(){
		StringBuffer detailObjectsToDisplay = new StringBuffer();
		Iterator<PermissionAttributeDataImpl> permIter = getDetailObjects().iterator();
		while ( permIter.hasNext() ) {
			PermissionAttributeDataImpl permissionAttributeData = permIter.next();
			detailObjectsToDisplay.append( permissionAttributeData.getAttributeValue() );
			if ( permIter.hasNext() ) {
				detailObjectsToDisplay.append( KimConstants.KimUIConstants.COMMA_SEPARATOR );
			}
		}
		return detailObjectsToDisplay.toString();
	}

	public String getDetailObjectsToDisplay() {
		KimType kimType = getTypeInfoService().getKimType( getTemplate().getKimTypeId() );
		StringBuffer detailObjectsToDisplay = new StringBuffer();
		Iterator<PermissionAttributeDataImpl> permIter = getDetailObjects().iterator();
		while ( permIter.hasNext() ) {
			PermissionAttributeDataImpl permissionAttributeData = permIter.next();
			detailObjectsToDisplay.append( getKimAttributeLabelFromDD(kimType.getAttributeDefinitionById(permissionAttributeData.getKimAttributeId())));
			detailObjectsToDisplay.append( KimConstants.KimUIConstants.NAME_VALUE_SEPARATOR );
			detailObjectsToDisplay.append( permissionAttributeData.getAttributeValue() );
			if ( permIter.hasNext() ) {
				detailObjectsToDisplay.append( KimConstants.KimUIConstants.COMMA_SEPARATOR );
			}
		}
		return detailObjectsToDisplay.toString();
	}
	
	protected String getKimAttributeLabelFromDD( KimTypeAttribute attribute ){
    	return getDataDictionaryService().getAttributeLabel(attribute.getKimAttribute().getComponentName(), attribute.getKimAttribute().getAttributeName() );
    }

	private transient static DataDictionaryService dataDictionaryService;
	
	protected DataDictionaryService getDataDictionaryService() {
		if(dataDictionaryService == null){
			dataDictionaryService = KRADServiceLocatorWeb.getDataDictionaryService();
		}
		return dataDictionaryService;
	}

	private transient static KimTypeInfoService kimTypeInfoService;
	protected KimTypeInfoService getTypeInfoService() {
		if(kimTypeInfoService == null){
			kimTypeInfoService = KimApiServiceLocator.getKimTypeInfoService();
		}
		return kimTypeInfoService;
	}
}
