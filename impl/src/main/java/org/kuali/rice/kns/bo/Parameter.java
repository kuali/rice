/*
 * Copyright 2006-2007 The Kuali Foundation. Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License. You may obtain a copy of the License at
 * http://www.opensource.org/licenses/ecl1.php Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing permissions and limitations under the License.
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
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

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

	@Column(name="TXT")
	private String parameterValue;

	@Column(name="PARM_DESC_TXT", length=2048)
	private String parameterDescription;

	@Column(name="PARM_TYP_CD")
	private String parameterTypeCode;

	@Column(name="CONS_CD")
	private String parameterConstraintCode;

	@Column(name="GRP_NM")
	private String parameterWorkgroupName;

	@ManyToOne(fetch=FetchType.LAZY, cascade={CascadeType.PERSIST})
	@JoinColumn(name="NMSPC_CD", insertable=false, updatable=false)
	private ParameterNamespace parameterNamespace;

	@OneToOne(fetch=FetchType.LAZY, cascade={CascadeType.PERSIST})
	@JoinColumn(name="PARM_TYP_CD", insertable=false, updatable=false)
	private ParameterType parameterType;

	@ManyToOne(fetch=FetchType.LAZY, cascade={CascadeType.PERSIST})
	@JoinColumns({@JoinColumn(name="NMSPC_CD", insertable=false, updatable=false), @JoinColumn(name="PARM_DTL_TYP_CD", insertable=false, updatable=false)})
	private ParameterDetailType parameterDetailType;

	public Parameter() {
		
	}
	
	public Parameter( String parameterName, String parameterValue, String parameterConstraintCode ) {
		this.parameterName = parameterName;
		this.parameterValue = parameterValue;
		this.parameterConstraintCode = parameterConstraintCode;
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

	public String getParameterWorkgroupName() {
		return parameterWorkgroupName;
	}

	public void setParameterWorkgroupName(String parameterWorkgroupName) {
		this.parameterWorkgroupName = parameterWorkgroupName;
	}

	/**
	 * @see org.kuali.rice.kns.bo.BusinessObjectBase#toStringMapper()
	 */
	protected LinkedHashMap toStringMapper() {
		LinkedHashMap m = new LinkedHashMap();
		m.put( "parameterNamespaceCode", this.parameterNamespaceCode );
		m.put( "parameterDetailTypeCode", this.parameterDetailTypeCode );
		m.put( "parameterName", this.parameterName );
		m.put( "parameterValue", this.parameterValue );
		m.put( "parameterConstraintCode", this.getParameterConstraintCode() );
		return m;
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

	public ParameterNamespace getParameterNamespace() {
		return parameterNamespace;
	}

	public void setParameterNamespace(ParameterNamespace parameterNamespace) {
		this.parameterNamespace = parameterNamespace;
	}

	public ParameterDetailType getParameterDetailType() {
		return parameterDetailType;
	}

	public void setParameterDetailType(ParameterDetailType parameterDetailType) {
		this.parameterDetailType = parameterDetailType;
	}

}

