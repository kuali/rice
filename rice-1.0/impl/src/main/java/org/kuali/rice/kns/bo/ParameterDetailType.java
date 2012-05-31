/*
 * Copyright 2008-2009 The Kuali Foundation
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
package org.kuali.rice.kns.bo;

import java.util.LinkedHashMap;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.hibernate.annotations.Type;

@IdClass(org.kuali.rice.kns.bo.ParameterDetailTypeId.class)
@Entity
@Table(name="KRNS_PARM_DTL_TYP_T")
public class ParameterDetailType extends PersistableBusinessObjectBase implements Inactivateable {

	@Id
	@Column(name="NMSPC_CD")
	private String parameterNamespaceCode;
	@Id
	@Column(name="PARM_DTL_TYP_CD")
	private String parameterDetailTypeCode;
	@Column(name="NM")
	private String parameterDetailTypeName;
	@Type(type="yes_no")
	@Column(name="ACTV_IND")
	private boolean active = true;
    @Transient
	private boolean virtualDetailType;
	
	@ManyToOne(fetch=FetchType.LAZY, cascade={CascadeType.PERSIST})
	@JoinColumn(name="NMSPC_CD", insertable=false, updatable=false)
	private Namespace parameterNamespace;

	public ParameterDetailType() {
	}

	public ParameterDetailType( String parameterNamespaceCode, String parameterDetailTypeCode, String parameterDetailTypeName ) {
		this.parameterNamespaceCode  = parameterNamespaceCode;
		this.parameterDetailTypeCode = parameterDetailTypeCode;
		this.parameterDetailTypeName = parameterDetailTypeName;		
		virtualDetailType = true;
	}
	
	public String getParameterNamespaceCode() {
		return parameterNamespaceCode;
	}

	public void setParameterNamespaceCode(String parameterNamespaceCode) {
		this.parameterNamespaceCode = parameterNamespaceCode;
	}

	public String getParameterDetailTypeCode() {
		return parameterDetailTypeCode;
	}

	public void setParameterDetailTypeCode(String parameterDetailTypeCode) {
		this.parameterDetailTypeCode = parameterDetailTypeCode;
	}

	public String getParameterDetailTypeName() {
		return parameterDetailTypeName;
	}

	public void setParameterDetailTypeName(String parameterDetailTypeName) {
		this.parameterDetailTypeName = parameterDetailTypeName;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public Namespace getParameterNamespace() {
		return parameterNamespace;
	}

	public void setParameterNamespace(Namespace parameterNamespace) {
		this.parameterNamespace = parameterNamespace;
	}

    /**
     * @see org.kuali.rice.kns.bo.BusinessObjectBase#toStringMapper()
     */
    final protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();

        m.put("parameterNamespaceCode", getParameterNamespaceCode());
        m.put("parameterDetailTypeCode", getParameterDetailTypeCode());
        m.put("parameterDetailTypeName", getParameterDetailTypeName());
        
        return m;
    }

	public boolean isVirtualDetailType() {
		return this.virtualDetailType;
	}

	public void setVirtualDetailType(boolean virtualDetailType) {
		this.virtualDetailType = virtualDetailType;
	}

	/**
	 * @see java.lang.Object#equals(Object)
	 */
	public boolean equals(Object object) {
		if ( !(object instanceof ParameterDetailType) ) {
			return false;
		}
		ParameterDetailType rhs = (ParameterDetailType)object;
		return new EqualsBuilder()
				.append( this.parameterDetailTypeCode, rhs.parameterDetailTypeCode )
				.append( this.parameterNamespaceCode, rhs.parameterNamespaceCode ).isEquals();
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() {
		return new HashCodeBuilder( -2037246405, 1981572401 )
				.append( this.parameterDetailTypeCode )
				.append( this.parameterNamespaceCode ).toHashCode();
	}
	
}

