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
package org.kuali.rice.kim.bo.types.impl;

import org.hibernate.annotations.Type;
import org.kuali.rice.kim.bo.types.dto.KimTypeAttributeInfo;
import org.kuali.rice.kim.bo.types.dto.KimTypeInfo;
import org.kuali.rice.kns.bo.PersistableBusinessObjectBase;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@Entity
@Table(name="KRIM_TYP_T")
@NamedQueries({
  @NamedQuery(name="KimTypeImpl.FindByKimTypeId", query="select kt from KimTypeImpl kt where kt.kimTypeId = :kimTypeId"),
  @NamedQuery(name="KimTypeImpl.FindByKimTypeName", query="select kt from KimTypeImpl kt where kt.name = :name and kt.namespaceCode = :namespaceCode")
})
public class KimTypeImpl extends PersistableBusinessObjectBase {

	private static final long serialVersionUID = 7752050088434254168L;
	@Id
	@Column(name="KIM_TYP_ID")
	protected String kimTypeId;
	@Column(name="NM")
	protected String name;
	@Column(name="NMSPC_CD")
	protected String namespaceCode;
	@Column(name="SRVC_NM")
	protected String kimTypeServiceName;
	@Type(type="yes_no")
	@Column(name="ACTV_IND")
	protected boolean active;

	@OneToMany(targetEntity=KimTypeAttributeImpl.class,cascade={CascadeType.ALL},fetch=FetchType.EAGER)
	@JoinColumn(name="KIM_TYP_ID", insertable=false, updatable=false)
	protected List<KimTypeAttributeImpl> attributeDefinitions;

	public KimTypeImpl() {
		attributeDefinitions = new ArrayList<KimTypeAttributeImpl> ();
	}

	public List<KimTypeAttributeImpl> getAttributeDefinitions() {
		return attributeDefinitions;
	}

	public String getKimTypeId() {
		return kimTypeId;
	}

	public String getKimTypeServiceName() {
		return kimTypeServiceName;
	}

	public String getName() {
		return name;
	}

	public void setAttributeDefinitions(List<KimTypeAttributeImpl> attributeDefinitions) {
		this.attributeDefinitions = attributeDefinitions;
	}

	public void setKimTypeServiceName(String kimTypeServiceName) {
		this.kimTypeServiceName = kimTypeServiceName;
	}

	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @see org.kuali.rice.kns.bo.Inactivateable#isActive()
	 */
	public boolean isActive() {
		return active;
	}

	/**
	 * @see org.kuali.rice.kns.bo.Inactivateable#setActive(boolean)
	 */
	public void setActive(boolean active) {
		this.active = active;
	}

	public String getNamespaceCode() {
		return this.namespaceCode;
	}

	public void setNamespaceCode(String namespaceCode) {
		this.namespaceCode = namespaceCode;
	}

	public void setKimTypeId(String kimTypeId) {
		this.kimTypeId = kimTypeId;
	}

	public KimTypeInfo toInfo() {
		KimTypeInfo info = new KimTypeInfo();
		info.setKimTypeId( kimTypeId );
		info.setName( name );
		info.setKimTypeServiceName(kimTypeServiceName);
		info.setNamespaceCode(namespaceCode);
		List<KimTypeAttributeInfo> attribs = new ArrayList<KimTypeAttributeInfo>(); 
		info.setAttributeDefinitions( attribs );
		for ( KimTypeAttributeImpl attribImpl : getAttributeDefinitions() ) {
			attribs.add( makeAttributeInfo(attribImpl) );
		}		
		return info;
	}

	protected KimTypeAttributeInfo makeAttributeInfo( KimTypeAttributeImpl attribImpl ) {
		KimTypeAttributeInfo attrib = new KimTypeAttributeInfo();
		attrib.setAttributeName( attribImpl.getKimAttribute().getAttributeName() );
		attrib.setSortCode( attribImpl.getSortCode() );
		attrib.setComponentName( attribImpl.getKimAttribute().getComponentName() );
		attrib.setNamespaceCode( attribImpl.getKimAttribute().getNamespaceCode() );
		attrib.setAttributeLabel( attribImpl.getKimAttribute().getAttributeLabel() );
		attrib.setKimAttributeId( attribImpl.getKimAttributeId() );
		return attrib;
	}
	
	/**
	 * This overridden method ...
	 * 
	 * @see org.kuali.rice.kim.bo.KimType#getAttributeDefinition(java.lang.String)
	 */
	public KimTypeAttributeInfo getAttributeDefinition(String kimAttributeId) {
		if ( kimAttributeId == null ) {
			return null;
		}
		for ( KimTypeAttributeImpl def : getAttributeDefinitions() ) {
			if ( def.kimAttributeId.equals( kimAttributeId ) ) {
				return makeAttributeInfo(def);
			}
		}
		return null;
	}
}
