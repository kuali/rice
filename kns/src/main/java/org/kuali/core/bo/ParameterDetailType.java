package org.kuali.core.bo;

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

@IdClass(org.kuali.core.bo.ParameterDetailTypeId.class)
@Entity
@Table(name="SH_PARM_DTL_TYP_T")
public class ParameterDetailType extends PersistableBusinessObjectBase implements Inactivateable {

	@Id
	@Column(name="SH_PARM_NMSPC_CD")
	private String parameterNamespaceCode;
	@Id
	@Column(name="SH_PARM_DTL_TYP_CD")
	private String parameterDetailTypeCode;
	@Column(name="SH_PARM_DTL_TYP_NM")
	private String parameterDetailTypeName;
	@Type(type="yes_no")
	@Column(name="ACTIVE_IND")
	private boolean active = true;
    @Transient
	private boolean virtualDetailType;
	
	@ManyToOne(fetch=FetchType.LAZY, cascade={CascadeType.PERSIST})
	@JoinColumn(name="SH_PARM_NMSPC_CD", insertable=false, updatable=false)
	private ParameterNamespace parameterNamespace;

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

	public ParameterNamespace getParameterNamespace() {
		return parameterNamespace;
	}

	public void setParameterNamespace(ParameterNamespace parameterNamespace) {
		this.parameterNamespace = parameterNamespace;
	}

    /**
     * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
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
				.append( this.parameterDetailTypeName, rhs.parameterDetailTypeName )
				.append( this.parameterNamespaceCode, rhs.parameterNamespaceCode ).isEquals();
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() {
		return new HashCodeBuilder( -2037246405, 1981572401 )
				.append( this.parameterDetailTypeCode )
				.append( this.parameterDetailTypeName )
				.append( this.parameterNamespaceCode ).toHashCode();
	}
	
}

