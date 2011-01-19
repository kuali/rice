/*
 * Copyright 2006-2009 The Kuali Foundation
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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.kuali.rice.kns.util.KNSConstants;

/**
 * 
 */
@IdClass(org.kuali.rice.kns.bo.ParameterId.class)
@Entity
@Table(name="KRNS_PARM_T")
public class Parameter extends PersistableBusinessObjectBase {

	private static final long serialVersionUID = 4874830226334867797L;

	@Id
	@Column(name="NMSPC_CD")
	private String parameterNamespaceCode;

	@Id
	@Column(name="PARM_DTL_TYP_CD")
	private String parameterDetailTypeCode;

	@Id
	@Column(name="PARM_NM")
	private String parameterName;
	
	@Id
    @Column(name="APPL_NMSPC_CD")
    private String parameterApplicationNamespaceCode;

	@Column(name="TXT")
	private String parameterValue;

	@Column(name="PARM_DESC_TXT", length=2048)
	private String parameterDescription;

	@Column(name="PARM_TYP_CD")
	private String parameterTypeCode;

	@Column(name="CONS_CD")
	private String parameterConstraintCode;

	

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="NMSPC_CD", insertable=false, updatable=false)
	private Namespace parameterNamespace;

	@OneToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="PARM_TYP_CD", insertable=false, updatable=false)
	private ParameterType parameterType;

	/*@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumns({
		@JoinColumn(name="NMSPC_CD", referencedColumnName="NMSPC_CD", insertable=false, updatable=false),
		@JoinColumn(name="PARM_DTL_TYP_CD", referencedColumnName="PARM_DTL_TYP_CD", insertable=false, updatable=false)
	})*/
	@Transient
	private ParameterDetailType parameterDetailType;

	public Parameter() {
		
	}
	
	public Parameter( String parameterName, String parameterValue, String parameterConstraintCode ) {
		this.parameterName = parameterName;
		this.parameterValue = parameterValue;
		this.parameterConstraintCode = parameterConstraintCode;
		this.parameterApplicationNamespaceCode = KNSConstants.DEFAULT_APPLICATION_CODE;
	}
	
	public Parameter( String parameterName, String parameterValue, String parameterConstraintCode, String parameterApplicationNamespaceCode ) {
        this.parameterName = parameterName;
        this.parameterValue = parameterValue;
        this.parameterConstraintCode = parameterConstraintCode;
        this.parameterApplicationNamespaceCode = parameterApplicationNamespaceCode;
    }
	
	
	public String getParameterName() {
		return parameterName;
	}

	public void setParameterName(String parameterName) {
		this.parameterName = parameterName;
	}

	public String getParameterValue() {
		return parameterValue;
	}

	public void setParameterValue(String parameterValue) {
		this.parameterValue = parameterValue;
	}

	public String getParameterDescription() {
		return parameterDescription;
	}

	public void setParameterDescription(String parameterDescription) {
		this.parameterDescription = parameterDescription;
	}

	public String getParameterTypeCode() {
		return parameterTypeCode;
	}

	public void setParameterTypeCode(String parameterTypeCode) {
		this.parameterTypeCode = parameterTypeCode;
	}

	public String getParameterConstraintCode() {
		return parameterConstraintCode;
	}

	public void setParameterConstraintCode(String parameterConstraintCode) {
		this.parameterConstraintCode = parameterConstraintCode;
	}

	public ParameterType getParameterType() {
		return parameterType;
	}

	public void setParameterType(ParameterType parameterType) {
		this.parameterType = parameterType;
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

	public Namespace getParameterNamespace() {
		return parameterNamespace;
	}

	public void setParameterNamespace(Namespace parameterNamespace) {
		this.parameterNamespace = parameterNamespace;
	}

	public ParameterDetailType getParameterDetailType() {
		//Special JPA stuff for @Transient.  Commenting out for the OJB reversion
		//if (!StringUtils.isBlank(parameterDetailTypeCode) && !StringUtils.isBlank(parameterNamespaceCode) && (parameterDetailType == null || !parameterDetailType.getParameterDetailTypeCode().equals(this.parameterDetailTypeCode))) {
		//	ParameterDetailTypeId id = new ParameterDetailTypeId();
		//	id.setParameterNamespaceCode(parameterNamespaceCode);
		//	id.setParameterDetailTypeCode(parameterDetailTypeCode);
		//	parameterDetailType = KNSServiceLocatorInternal.getBusinessObjectService().findBySinglePrimaryKey(ParameterDetailType.class, id);
		//}
		return parameterDetailType;
	}

	public void setParameterDetailType(ParameterDetailType parameterDetailType) {
		this.parameterDetailType = parameterDetailType;
	}

    public String getParameterApplicationNamespaceCode() {
        return this.parameterApplicationNamespaceCode;
    }

    public void setParameterApplicationNamespaceCode(String parameterApplicationNamespaceCode) {
        this.parameterApplicationNamespaceCode = parameterApplicationNamespaceCode;
    }

}

